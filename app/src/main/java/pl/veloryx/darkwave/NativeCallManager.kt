package pl.veloryx.darkwave

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.webrtc.AudioSource
import org.webrtc.AudioTrack
import org.webrtc.Camera2Enumerator
import org.webrtc.CameraEnumerator
import org.webrtc.DataChannel
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.JavaAudioDeviceModule
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.MediaStreamTrack
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.RtpReceiver
import org.webrtc.RtpTransceiver
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import org.webrtc.SurfaceTextureHelper
import org.webrtc.VideoCapturer
import org.webrtc.VideoSource
import org.webrtc.VideoTrack
import java.time.Instant
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

data class NativeCallState(
    val call: ChatCall? = null,
    val phase: String = "idle",
    val microphoneMuted: Boolean = false,
    val cameraOff: Boolean = false,
    val speakerOn: Boolean = true,
    val connected: Boolean = false,
    val localVideoTrack: VideoTrack? = null,
    val remoteVideoTrack: VideoTrack? = null,
    val errorEn: String? = null,
    val errorPl: String? = null,
)

class NativeCallManager(context: Context, private val api: ApiClient) {
    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val audioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val eglBase = EglBase.create()
    val eglContext: EglBase.Context get() = eglBase.eglBaseContext

    private val factory: PeerConnectionFactory
    private val peers = mutableMapOf<Long, PeerConnection>()
    private val pendingIce = mutableMapOf<Long, MutableList<IceCandidate>>()
    private var pollJob: Job? = null
    private var ringJob: Job? = null
    private var lastSignalId = 0L
    private var localAudioSource: AudioSource? = null
    private var localAudioTrack: AudioTrack? = null
    private var localVideoSource: VideoSource? = null
    private var localVideoTrack: VideoTrack? = null
    private var videoCapturer: VideoCapturer? = null
    private var surfaceTextureHelper: SurfaceTextureHelper? = null

    private val _state = MutableStateFlow(NativeCallState())
    val state: StateFlow<NativeCallState> = _state.asStateFlow()

    init {
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(appContext)
                .setEnableInternalTracer(false)
                .createInitializationOptions(),
        )
        val audioModule = JavaAudioDeviceModule.builder(appContext)
            .setUseHardwareAcousticEchoCanceler(true)
            .setUseHardwareNoiseSuppressor(true)
            .createAudioDeviceModule()
        factory = PeerConnectionFactory.builder()
            .setAudioDeviceModule(audioModule)
            .setVideoEncoderFactory(DefaultVideoEncoderFactory(eglBase.eglBaseContext, true, true))
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBase.eglBaseContext))
            .createPeerConnectionFactory()
        audioModule.release()
    }

    fun startPolling() {
        if (pollJob?.isActive == true) return
        pollJob = scope.launch {
            while (isActive) {
                pollOnce()
                delay(1_500)
            }
        }
    }

    fun stopPolling() {
        pollJob?.cancel()
        pollJob = null
        cleanupCall()
    }

    fun startCall(conversationId: Long, mode: String) {
        if (_state.value.call != null || _state.value.phase in listOf("starting", "answering")) return
        scope.launch {
            _state.value = NativeCallState(phase = "starting")
            if (!prepareLocalMedia(mode)) return@launch
            val result = api.callAction("start", conversationId = conversationId, mode = mode)
            if (result.value != true) {
                setError("The call could not be started.", "Nie udało się rozpocząć połączenia.")
                releaseMedia()
                return@launch
            }
            playTone(ToneGenerator.TONE_CDMA_DIAL_TONE_LITE, 240)
            pollOnce()
        }
    }

    fun answer() {
        val call = _state.value.call ?: return
        scope.launch {
            _state.value = _state.value.copy(phase = "answering", errorEn = null, errorPl = null)
            if (!prepareLocalMedia(call.mode)) return@launch
            val result = api.callAction("answer", callId = call.id)
            if (result.value != true) {
                setError("The call could not be answered.", "Nie udało się odebrać połączenia.")
                return@launch
            }
            stopRinging()
            playTone(ToneGenerator.TONE_PROP_ACK, 180)
            pollOnce()
        }
    }

    fun decline() = finish("decline")

    fun end() = finish(if (_state.value.call?.status == "ringing") "end" else "end")

    fun toggleMicrophone() {
        val next = !_state.value.microphoneMuted
        localAudioTrack?.setEnabled(!next)
        _state.value = _state.value.copy(microphoneMuted = next)
        playTone(if (next) ToneGenerator.TONE_PROP_NACK else ToneGenerator.TONE_PROP_ACK, 120)
    }

    fun toggleCamera() {
        val next = !_state.value.cameraOff
        localVideoTrack?.setEnabled(!next)
        _state.value = _state.value.copy(cameraOff = next)
    }

    @Suppress("DEPRECATION")
    fun toggleSpeaker() {
        val next = !_state.value.speakerOn
        audioManager.isSpeakerphoneOn = next
        _state.value = _state.value.copy(speakerOn = next)
    }

    fun permissionDenied() {
        setError(
            "Microphone access is required to speak during a call.",
            "Dostęp do mikrofonu jest wymagany, aby rozmawiać.",
        )
    }

    private fun finish(action: String) {
        val call = _state.value.call ?: return
        scope.launch {
            api.callAction(action, callId = call.id)
            val elapsed = call.answeredAt?.let {
                runCatching { ((System.currentTimeMillis() - Instant.parse(it).toEpochMilli()) / 1_000).coerceAtLeast(0) }.getOrDefault(0)
            } ?: 0
            val body = if (call.status == "ringing") "[[DW_CALL_MISSED]]" else "[[DW_CALL_ENDED:$elapsed]]"
            api.send(call.conversationId, body)
            playTone(ToneGenerator.TONE_PROP_NACK, 220)
            cleanupCall()
        }
    }

    private suspend fun pollOnce() {
        val result = api.pollCall(lastSignalId)
        val envelope = result.value ?: return
        val call = envelope.call
        if (call == null) {
            if (_state.value.call != null) cleanupCall()
            return
        }
        if (_state.value.call?.id != call.id) {
            cleanupCall(resetState = false)
            lastSignalId = 0L
        }
        val incoming = call.status == "ringing" && call.initiatorUserId != call.viewerUserId
        _state.value = _state.value.copy(
            call = call,
            phase = when {
                incoming -> "incoming"
                call.status == "ringing" -> "dialing"
                call.status == "active" -> "active"
                else -> call.status
            },
            errorEn = null,
            errorPl = null,
        )
        if (incoming) startRinging() else stopRinging()
        for (signal in call.signals) {
            if (signal.id <= lastSignalId) continue
            handleSignal(call, signal)
            lastSignalId = maxOf(lastSignalId, signal.id)
        }
        if (call.status == "active" && localAudioTrack != null) {
            for (participant in call.participants.filter { it.userId != call.viewerUserId && it.status == "joined" }) {
                if (call.viewerUserId < participant.userId && !peers.containsKey(participant.userId)) {
                    createPeer(call, participant.userId, shouldOffer = true)
                }
            }
        }
    }

    private suspend fun handleSignal(call: ChatCall, signal: CallSignal) {
        try {
            val peer = createPeer(call, signal.fromUserId, shouldOffer = false)
            when (signal.type) {
                "offer" -> {
                    peer.setRemote(SessionDescription(SessionDescription.Type.OFFER, signal.payload.optString("sdp")))
                    flushIce(signal.fromUserId, peer)
                    val answer = peer.createAnswerSuspend()
                    peer.setLocal(answer)
                    sendDescription(call.id, signal.fromUserId, "answer", answer)
                }
                "answer" -> {
                    peer.setRemote(SessionDescription(SessionDescription.Type.ANSWER, signal.payload.optString("sdp")))
                    flushIce(signal.fromUserId, peer)
                }
                "ice" -> {
                    val candidate = IceCandidate(
                        signal.payload.optString("sdpMid").takeIf { it.isNotBlank() },
                        signal.payload.optInt("sdpMLineIndex"),
                        signal.payload.optString("candidate"),
                    )
                    if (peer.remoteDescription != null) peer.addIceCandidate(candidate)
                    else pendingIce.getOrPut(signal.fromUserId) { mutableListOf() }.add(candidate)
                }
            }
        } catch (_: Throwable) {
            setError("One participant could not be connected.", "Nie udało się połączyć jednego z uczestników.")
        }
    }

    private suspend fun createPeer(call: ChatCall, remoteUserId: Long, shouldOffer: Boolean): PeerConnection {
        peers[remoteUserId]?.let { return it }
        val servers = (call.iceServers.ifEmpty {
            listOf(CallIceServer(listOf("stun:stun.cloudflare.com:3478", "stun:stun.l.google.com:19302")))
        }).map { server ->
            PeerConnection.IceServer.builder(server.urls)
                .setUsername(server.username)
                .setPassword(server.credential)
                .createIceServer()
        }
        val configuration = PeerConnection.RTCConfiguration(servers).apply {
            sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
            continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
        }
        val observer = object : PeerConnection.Observer {
            override fun onSignalingChange(state: PeerConnection.SignalingState?) = Unit
            override fun onIceConnectionChange(state: PeerConnection.IceConnectionState?) {
                if (state == PeerConnection.IceConnectionState.FAILED) scope.launch {
                    setError(
                        "The audio route could not be established. Check the TURN server configuration.",
                        "Nie udało się zestawić kanału audio. Sprawdź konfigurację serwera TURN.",
                    )
                }
            }
            override fun onIceConnectionReceivingChange(receiving: Boolean) = Unit
            override fun onIceGatheringChange(state: PeerConnection.IceGatheringState?) = Unit
            override fun onIceCandidate(candidate: IceCandidate?) {
                if (candidate == null) return
                scope.launch {
                    val payload = JSONObject()
                        .put("candidate", candidate.sdp)
                        .put("sdpMid", candidate.sdpMid)
                        .put("sdpMLineIndex", candidate.sdpMLineIndex)
                    api.callAction("signal", callId = call.id, targetUserId = remoteUserId, signalType = "ice", signalPayload = payload)
                }
            }
            override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>?) = Unit
            override fun onAddStream(stream: MediaStream?) = Unit
            override fun onRemoveStream(stream: MediaStream?) = Unit
            override fun onDataChannel(channel: DataChannel?) = Unit
            override fun onRenegotiationNeeded() = Unit
            override fun onAddTrack(receiver: RtpReceiver?, streams: Array<out MediaStream>?) {
                when (val track = receiver?.track()) {
                    is AudioTrack -> track.setEnabled(true)
                    is VideoTrack -> _state.value = _state.value.copy(remoteVideoTrack = track)
                }
            }
            override fun onTrack(transceiver: RtpTransceiver?) {
                when (val track = transceiver?.receiver?.track()) {
                    is AudioTrack -> track.setEnabled(true)
                    is VideoTrack -> _state.value = _state.value.copy(remoteVideoTrack = track)
                }
            }
            override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
                _state.value = _state.value.copy(connected = newState == PeerConnection.PeerConnectionState.CONNECTED)
            }
        }
        val peer = factory.createPeerConnection(configuration, observer) ?: error("Peer unavailable")
        localAudioTrack?.let { peer.addTrack(it, listOf("darkwave-audio")) }
        localVideoTrack?.let { peer.addTrack(it, listOf("darkwave-video")) }
        peers[remoteUserId] = peer
        if (shouldOffer) {
            val offer = peer.createOfferSuspend()
            peer.setLocal(offer)
            sendDescription(call.id, remoteUserId, "offer", offer)
        }
        return peer
    }

    private suspend fun sendDescription(callId: Long, targetUserId: Long, type: String, description: SessionDescription) {
        api.callAction(
            "signal",
            callId = callId,
            targetUserId = targetUserId,
            signalType = type,
            signalPayload = JSONObject().put("type", description.type.canonicalForm()).put("sdp", description.description),
        )
    }

    private fun flushIce(remoteUserId: Long, peer: PeerConnection) {
        pendingIce.remove(remoteUserId)?.forEach(peer::addIceCandidate)
    }

    @Suppress("DEPRECATION")
    private fun prepareAudioRoute() {
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION
        audioManager.isSpeakerphoneOn = true
    }

    private fun prepareLocalMedia(mode: String): Boolean {
        return try {
            if (localAudioTrack == null) {
                localAudioSource = factory.createAudioSource(MediaConstraints())
                localAudioTrack = factory.createAudioTrack("DW_AUDIO", localAudioSource).apply { setEnabled(true) }
            }
            if (mode == "video" && localVideoTrack == null) startCamera()
            prepareAudioRoute()
            _state.value = _state.value.copy(
                phase = if (_state.value.call == null) "starting" else _state.value.phase,
                localVideoTrack = localVideoTrack,
                speakerOn = true,
                microphoneMuted = false,
                cameraOff = false,
                errorEn = null,
                errorPl = null,
            )
            true
        } catch (_: SecurityException) {
            permissionDenied()
            false
        } catch (_: Throwable) {
            setError("The microphone or camera could not be started.", "Nie udało się uruchomić mikrofonu lub kamery.")
            false
        }
    }

    private fun startCamera() {
        val enumerator: CameraEnumerator = Camera2Enumerator(appContext)
        val cameraName = enumerator.deviceNames.firstOrNull(enumerator::isFrontFacing)
            ?: enumerator.deviceNames.firstOrNull()
            ?: error("Camera unavailable")
        val capturer = enumerator.createCapturer(cameraName, null) ?: error("Camera unavailable")
        val helper = SurfaceTextureHelper.create("DarkwaveCamera", eglBase.eglBaseContext)
        val source = factory.createVideoSource(false)
        capturer.initialize(helper, appContext, source.capturerObserver)
        capturer.startCapture(720, 1280, 24)
        videoCapturer = capturer
        surfaceTextureHelper = helper
        localVideoSource = source
        localVideoTrack = factory.createVideoTrack("DW_VIDEO", source).apply { setEnabled(true) }
    }

    private fun startRinging() {
        if (ringJob?.isActive == true) return
        ringJob = scope.launch {
            while (isActive) {
                playTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 520)
                delay(2_800)
            }
        }
    }

    private fun stopRinging() {
        ringJob?.cancel()
        ringJob = null
    }

    private fun playTone(tone: Int, duration: Int) {
        runCatching {
            ToneGenerator(AudioManager.STREAM_VOICE_CALL, 72).apply {
                startTone(tone, duration)
                scope.launch { delay((duration + 80).toLong()); release() }
            }
        }
    }

    private fun setError(english: String, polish: String) {
        _state.value = _state.value.copy(errorEn = english, errorPl = polish)
    }

    @Suppress("DEPRECATION")
    private fun releaseMedia() {
        runCatching { videoCapturer?.stopCapture() }
        videoCapturer?.dispose()
        surfaceTextureHelper?.dispose()
        localVideoTrack?.dispose()
        localVideoSource?.dispose()
        localAudioTrack?.dispose()
        localAudioSource?.dispose()
        videoCapturer = null
        surfaceTextureHelper = null
        localVideoTrack = null
        localVideoSource = null
        localAudioTrack = null
        localAudioSource = null
        audioManager.isSpeakerphoneOn = false
        audioManager.mode = AudioManager.MODE_NORMAL
    }

    private fun cleanupCall(resetState: Boolean = true) {
        stopRinging()
        peers.values.forEach(PeerConnection::dispose)
        peers.clear()
        pendingIce.clear()
        lastSignalId = 0L
        releaseMedia()
        if (resetState) _state.value = NativeCallState()
    }
}

private suspend fun PeerConnection.createOfferSuspend(): SessionDescription = suspendCancellableCoroutine { continuation ->
    createOffer(object : SdpObserver {
        override fun onCreateSuccess(description: SessionDescription?) {
            if (description != null && continuation.isActive) continuation.resume(description)
            else if (continuation.isActive) continuation.resumeWithException(IllegalStateException("Offer unavailable"))
        }
        override fun onCreateFailure(error: String?) { if (continuation.isActive) continuation.resumeWithException(IllegalStateException(error ?: "Offer failed")) }
        override fun onSetSuccess() = Unit
        override fun onSetFailure(error: String?) = Unit
    }, MediaConstraints())
}

private suspend fun PeerConnection.createAnswerSuspend(): SessionDescription = suspendCancellableCoroutine { continuation ->
    createAnswer(object : SdpObserver {
        override fun onCreateSuccess(description: SessionDescription?) {
            if (description != null && continuation.isActive) continuation.resume(description)
            else if (continuation.isActive) continuation.resumeWithException(IllegalStateException("Answer unavailable"))
        }
        override fun onCreateFailure(error: String?) { if (continuation.isActive) continuation.resumeWithException(IllegalStateException(error ?: "Answer failed")) }
        override fun onSetSuccess() = Unit
        override fun onSetFailure(error: String?) = Unit
    }, MediaConstraints())
}

private suspend fun PeerConnection.setLocal(description: SessionDescription): Unit = suspendCancellableCoroutine { continuation ->
    setLocalDescription(object : SdpObserver {
        override fun onSetSuccess() { if (continuation.isActive) continuation.resume(Unit) }
        override fun onSetFailure(error: String?) { if (continuation.isActive) continuation.resumeWithException(IllegalStateException(error ?: "Local SDP failed")) }
        override fun onCreateSuccess(description: SessionDescription?) = Unit
        override fun onCreateFailure(error: String?) = Unit
    }, description)
}

private suspend fun PeerConnection.setRemote(description: SessionDescription): Unit = suspendCancellableCoroutine { continuation ->
    setRemoteDescription(object : SdpObserver {
        override fun onSetSuccess() { if (continuation.isActive) continuation.resume(Unit) }
        override fun onSetFailure(error: String?) { if (continuation.isActive) continuation.resumeWithException(IllegalStateException(error ?: "Remote SDP failed")) }
        override fun onCreateSuccess(description: SessionDescription?) = Unit
        override fun onCreateFailure(error: String?) = Unit
    }, description)
}
