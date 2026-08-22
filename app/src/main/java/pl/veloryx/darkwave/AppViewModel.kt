package pl.veloryx.darkwave

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

enum class AppTab { CHATS, FRIENDS, FORUM, PROFILE }
enum class AuthMode { LOGIN, REGISTER, TWO_FACTOR }

data class AppUiState(
    val splash: Boolean = true,
    val loading: Boolean = false,
    val language: String = "en",
    val profile: Profile? = null,
    val authMode: AuthMode = AuthMode.LOGIN,
    val captcha: Captcha? = null,
    val loginToken: String = "",
    val notice: String? = null,
    val noticeError: Boolean = false,
    val tab: AppTab = AppTab.CHATS,
    val inbox: Inbox = Inbox(emptyList(), emptyList()),
    val selected: Conversation? = null,
    val messages: List<ChatMessage> = emptyList(),
    val searchResults: List<Profile> = emptyList(),
    val forum: List<ForumThread> = emptyList(),
    val appUpdate: AppUpdate? = null,
)

class AppViewModel(private val api: ApiClient, private val calls: NativeCallManager) : ViewModel() {
    private val _state = MutableStateFlow(AppUiState(language = api.savedLanguage()))
    val state: StateFlow<AppUiState> = _state.asStateFlow()
    val callState: StateFlow<NativeCallState> = calls.state
    val callEglContext get() = calls.eglContext
    private var messagePoll: Job? = null
    private var inboxPoll: Job? = null

    init {
        viewModelScope.launch { checkForUpdate() }
        viewModelScope.launch {
            delay(1050)
            val session = api.session()
            if (session.value != null) {
                _state.value = _state.value.copy(splash = false, profile = session.value)
                afterLogin()
            } else {
                _state.value = _state.value.copy(splash = false)
                loadCaptcha()
            }
        }
    }

    private suspend fun checkForUpdate() {
        api.latestUpdate().value
            ?.takeIf { it.versionCode > BuildConfig.VERSION_CODE }
            ?.let { _state.value = _state.value.copy(appUpdate = it) }
    }

    fun dismissUpdate() {
        if (_state.value.appUpdate?.required != true) _state.value = _state.value.copy(appUpdate = null)
    }

    fun setLanguage(language: String) {
        val normalized = if (language == "pl") "pl" else "en"
        api.saveLanguage(normalized)
        _state.value = _state.value.copy(language = normalized)
    }

    fun setAuthMode(mode: AuthMode) {
        _state.value = _state.value.copy(authMode = mode, notice = null, loginToken = "")
        viewModelScope.launch { loadCaptcha() }
    }

    suspend fun loadCaptcha() {
        val result = api.captcha()
        _state.value = _state.value.copy(captcha = result.value)
    }

    fun refreshCaptcha() { viewModelScope.launch { loadCaptcha() } }

    fun login(identifier: String, password: String, remember: Boolean, answer: String) {
        val captcha = _state.value.captcha ?: return notice(t("Security check unavailable.", "Kontrola bezpieczeństwa jest niedostępna."), true)
        if (answer.isBlank()) return notice(t("Complete the security check.", "Uzupełnij kontrolę bezpieczeństwa."), true)
        viewModelScope.launch {
            busy(true); val result = api.login(identifier.trim(), password, remember, captcha, answer.trim())
            when {
                result.value == null -> notice(result.message ?: t("Could not sign in.", "Nie udało się zalogować."), true)
                result.value != "ok" -> _state.value = _state.value.copy(authMode = AuthMode.TWO_FACTOR, loginToken = result.value, loading = false, notice = null)
                else -> loadProfileAfterAuth()
            }
            if (result.value == null) loadCaptcha()
        }
    }

    fun verifyTwoFactor(code: String) {
        if (code.length < 6) return notice(t("Enter the authentication code.", "Wpisz kod uwierzytelniający."), true)
        viewModelScope.launch {
            busy(true); val result = api.loginTwoFactor(_state.value.loginToken, code.trim())
            if (result.value == true) loadProfileAfterAuth()
            else notice(result.message ?: t("The code is invalid.", "Kod jest nieprawidłowy."), true)
        }
    }

    fun register(username: String, email: String, password: String, confirmation: String, terms: Boolean, newsletter: Boolean, answer: String) {
        if (!terms) return notice(t("Accept the account terms.", "Zaakceptuj regulamin konta."), true)
        if (password != confirmation) return notice(t("Passwords do not match.", "Hasła nie są takie same."), true)
        val captcha = _state.value.captcha ?: return notice(t("Security check unavailable.", "Kontrola bezpieczeństwa jest niedostępna."), true)
        viewModelScope.launch {
            busy(true)
            val result = api.register(username.trim(), email.trim(), password, confirmation, newsletter, _state.value.language, captcha, answer.trim())
            if (result.value == true) {
                _state.value = _state.value.copy(authMode = AuthMode.LOGIN, loading = false, noticeError = false, notice = t("Account created. Confirm the link sent to your email.", "Konto utworzone. Potwierdź link wysłany na e-mail."))
            } else notice(result.message ?: t("Could not create the account.", "Nie udało się utworzyć konta."), true)
            loadCaptcha()
        }
    }

    private suspend fun loadProfileAfterAuth() {
        val session = api.session()
        if (session.value == null) return notice(session.message ?: t("Could not open the account.", "Nie udało się otworzyć konta."), true)
        _state.value = _state.value.copy(profile = session.value, loading = false, notice = null, authMode = AuthMode.LOGIN)
        afterLogin()
    }

    private fun afterLogin() {
        calls.startPolling()
        refreshInbox()
        loadForum()
        inboxPoll?.cancel()
        inboxPoll = viewModelScope.launch {
            while (isActive) { delay(15_000); refreshInbox() }
        }
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            viewModelScope.launch { api.registerPushToken(token) }
        }
    }

    fun setTab(tab: AppTab) { _state.value = _state.value.copy(tab = tab, selected = null); messagePoll?.cancel() }

    fun refreshInbox() {
        viewModelScope.launch {
            val result = api.inbox()
            result.value?.let { inbox ->
                val selected = _state.value.selected?.let { current -> inbox.conversations.firstOrNull { it.id == current.id } ?: current }
                _state.value = _state.value.copy(inbox = inbox, selected = selected)
            }
        }
    }

    fun selectConversation(conversation: Conversation?) {
        _state.value = _state.value.copy(selected = conversation, messages = emptyList())
        messagePoll?.cancel()
        if (conversation == null) return
        messagePoll = viewModelScope.launch {
            while (isActive) { loadMessages(conversation.id); delay(4_000) }
        }
    }

    fun openConversationById(id: Long) {
        _state.value.inbox.conversations.firstOrNull { it.id == id }?.let {
            _state.value = _state.value.copy(tab = AppTab.CHATS)
            selectConversation(it)
        }
    }

    private suspend fun loadMessages(id: Long) {
        val result = api.messages(id)
        result.value?.let {
            _state.value = _state.value.copy(messages = it)
            api.markRead(id)
            refreshInbox()
        }
    }

    fun sendMessage(body: String) {
        val conversation = _state.value.selected ?: return
        if (body.isBlank()) return
        viewModelScope.launch {
            val result = api.send(conversation.id, body.trim())
            if (result.value == true) loadMessages(conversation.id)
            else notice(result.message ?: t("Message not sent.", "Wiadomość nie została wysłana."), true)
        }
    }

    fun sendAttachment(uri: Uri, body: String = "") {
        val conversation = _state.value.selected ?: return
        viewModelScope.launch {
            val result = api.upload(conversation.id, body.trim(), uri)
            if (result.value == true) loadMessages(conversation.id)
            else notice(result.message ?: t("Could not send the attachment.", "Nie udało się wysłać załącznika."), true)
        }
    }

    fun sendRecording(file: File, body: String = "") {
        val conversation = _state.value.selected ?: return
        viewModelScope.launch {
            val result = api.upload(conversation.id, body.trim(), file, "audio/mp4")
            file.delete()
            if (result.value == true) loadMessages(conversation.id)
            else notice(result.message ?: t("Could not send the recording.", "Nie udało się wysłać nagrania."), true)
        }
    }

    fun chatAction(action: String, messageId: Long? = null, emoji: String? = null, details: String? = null, theme: String? = null) {
        val conversation = _state.value.selected ?: return
        viewModelScope.launch {
            val result = api.chatAction(action, conversation.id, messageId, emoji, details, theme, if (action == "report") conversation.friend?.username else null)
            if (result.value != true) return@launch notice(result.message ?: t("Action unavailable.", "Operacja jest niedostępna."), true)
            if (action == "toggle-mute") {
                val changed = conversation.copy(muted = !conversation.muted)
                _state.value = _state.value.copy(selected = changed)
                notice(if (changed.muted) t("Conversation notifications muted.", "Powiadomienia rozmowy zostały wyciszone.") else t("Conversation notifications enabled.", "Powiadomienia rozmowy zostały włączone."), false)
            }
            if (action == "toggle-restrict") {
                val changed = conversation.copy(restricted = !conversation.restricted)
                _state.value = _state.value.copy(selected = changed)
                notice(if (changed.restricted) t("User restricted.", "Użytkownik został ograniczony.") else t("Restriction removed.", "Ograniczenie zostało cofnięte."), false)
            }
            if (action in listOf("delete-conversation", "remove-friend", "block", "leave-group")) {
                _state.value = _state.value.copy(selected = null, messages = emptyList())
                messagePoll?.cancel()
            } else loadMessages(conversation.id)
            refreshInbox()
        }
    }

    fun search(query: String) {
        if (query.trim().length < 2) { _state.value = _state.value.copy(searchResults = emptyList()); return }
        viewModelScope.launch {
            delay(220)
            val result = api.members(query.trim())
            result.value?.let { _state.value = _state.value.copy(searchResults = it) }
        }
    }

    fun social(action: String, member: Profile, friendshipId: Long? = member.friendshipId) {
        viewModelScope.launch {
            val result = api.social(action, member.username, friendshipId)
            if (result.value == true) {
                refreshInbox()
                _state.value = _state.value.copy(searchResults = _state.value.searchResults.map {
                    if (it.username == member.username) it.copy(relationship = if (action == "send-request") "outgoing" else if (action == "accept-request") "friends" else "none") else it
                })
            } else notice(result.message ?: t("Action unavailable.", "Operacja jest niedostępna."), true)
        }
    }

    fun loadForum() {
        viewModelScope.launch { api.forum().value?.let { _state.value = _state.value.copy(forum = it) } }
    }

    fun logout() {
        viewModelScope.launch {
            api.logout(); messagePoll?.cancel(); inboxPoll?.cancel(); calls.stopPolling()
            _state.value = AppUiState(splash = false, language = _state.value.language)
            loadCaptcha()
        }
    }

    private fun busy(value: Boolean) { _state.value = _state.value.copy(loading = value, notice = null) }
    private fun notice(message: String, error: Boolean) { _state.value = _state.value.copy(loading = false, notice = message, noticeError = error) }
    fun clearNotice() { _state.value = _state.value.copy(notice = null) }
    fun startCall(mode: String) { _state.value.selected?.let { calls.startCall(it.id, mode) } }
    fun answerCall() = calls.answer()
    fun declineCall() = calls.decline()
    fun endCall() = calls.end()
    fun toggleCallMicrophone() = calls.toggleMicrophone()
    fun toggleCallCamera() = calls.toggleCamera()
    fun toggleCallSpeaker() = calls.toggleSpeaker()
    fun callPermissionDenied() {
        calls.permissionDenied()
        notice(t("Allow microphone access to make and answer calls.", "Zezwól na dostęp do mikrofonu, aby wykonywać i odbierać połączenia."), true)
    }
    private fun t(en: String, pl: String) = if (_state.value.language == "pl") pl else en

    override fun onCleared() {
        calls.stopPolling()
        super.onCleared()
    }

    class Factory(private val api: ApiClient, private val calls: NativeCallManager) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = AppViewModel(api, calls) as T
    }
}
