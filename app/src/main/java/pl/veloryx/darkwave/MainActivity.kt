package pl.veloryx.darkwave

import android.Manifest
import android.content.Intent
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.io.File
import java.time.Instant
import java.time.temporal.ChronoUnit

class MainActivity : ComponentActivity() {
    private val model: AppViewModel by viewModels {
        AppViewModel.Factory((application as DarkwaveApplication).api)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 217)
        }
        setContent { DarkwaveTheme { DarkwaveApp(model) } }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.getLongExtra("conversationId", 0L).takeIf { it > 0 }?.let(model::openConversationById)
    }
}

@Composable
private fun DarkwaveApp(model: AppViewModel) {
    val state by model.state.collectAsStateWithLifecycle()
    Surface(Modifier.fillMaxSize(), color = Ink) {
        AnimatedContent(
            targetState = when { state.splash -> "splash"; state.profile == null -> "auth"; else -> "home" },
            transitionSpec = { (fadeIn() + slideInHorizontally { it / 8 }) togetherWith (fadeOut() + slideOutHorizontally { -it / 8 }) },
            label = "darkwave-screen",
        ) { page ->
            when (page) {
                "splash" -> Splash()
                "auth" -> AuthScreen(state, model)
                else -> HomeScreen(state, model)
            }
        }
    }
}

@Composable
private fun Splash() {
    Box(
        Modifier.fillMaxSize().background(Brush.radialGradient(listOf(Color(0xFF4A170F), Ink), radius = 900f)).padding(28.dp),
    ) {
        Box(Modifier.fillMaxSize().border(1.dp, Line, CutCornerShape(topEnd = 34.dp, bottomStart = 34.dp)))
        Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("DW", color = SignalGold, fontSize = 64.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
            Text("DARKWAVE", color = Bone, fontSize = 30.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            Text("INTERACTIVE", color = SignalRed, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 7.sp)
            Spacer(Modifier.height(34.dp))
            LinearProgressIndicator(
                modifier = Modifier.width(180.dp).height(3.dp),
                color = SignalGold, trackColor = PanelRaised,
            )
            Spacer(Modifier.height(12.dp))
            Text("CONNECTING TO CHANNEL 02:17", color = Muted, fontSize = 8.sp, fontFamily = FontFamily.Monospace, letterSpacing = 1.sp)
        }
    }
}

@Composable
private fun AuthScreen(state: AppUiState, model: AppViewModel) {
    val context = LocalContext.current
    var identifier by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmation by remember { mutableStateOf("") }
    var captchaAnswer by remember(state.captcha?.id) { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(true) }
    var terms by remember { mutableStateOf(false) }
    var newsletter by remember { mutableStateOf(false) }
    val t = { en: String, pl: String -> if (state.language == "pl") pl else en }

    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF21100C), Ink, Ink)))) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 52.dp, bottom = 34.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Row(Modifier.fillMaxWidth().widthIn(max = 520.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("DARKWAVE", color = Bone, fontWeight = FontWeight.Black, fontSize = 24.sp)
                        Text("MOBILE ACCESS / 02:17", color = SignalGold, fontFamily = FontFamily.Monospace, fontSize = 8.sp)
                    }
                    LanguageToggle(state.language, model::setLanguage)
                }
                Spacer(Modifier.height(22.dp))
            }
            item {
                Column(
                    Modifier.fillMaxWidth().widthIn(max = 520.dp)
                        .border(1.dp, Line, CutCornerShape(topEnd = 24.dp, bottomStart = 24.dp))
                        .background(Brush.linearGradient(listOf(PanelRaised, Ink))).padding(18.dp),
                ) {
                    Row(Modifier.fillMaxWidth().background(Ink).padding(4.dp)) {
                        AuthTab(t("SIGN IN", "LOGOWANIE"), state.authMode != AuthMode.REGISTER) { model.setAuthMode(AuthMode.LOGIN) }
                        AuthTab(t("CREATE ACCOUNT", "NOWE KONTO"), state.authMode == AuthMode.REGISTER) { model.setAuthMode(AuthMode.REGISTER) }
                    }
                    Spacer(Modifier.height(24.dp))
                    Text("DW_IDENTITY / PRIVATE CHANNEL", color = SignalGold, fontSize = 8.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    Text(
                        when (state.authMode) { AuthMode.REGISTER -> t("JOIN THE SIGNAL.", "DOŁĄCZ DO SYGNAŁU."); AuthMode.TWO_FACTOR -> t("SECOND SIGNAL.", "DRUGI SYGNAŁ."); else -> t("WELCOME BACK.", "WITAJ PONOWNIE.") },
                        color = Bone, fontSize = 38.sp, fontWeight = FontWeight.Black, lineHeight = 38.sp,
                        modifier = Modifier.padding(top = 8.dp, bottom = 20.dp),
                    )
                    when (state.authMode) {
                        AuthMode.LOGIN -> {
                            Field(t("EMAIL OR USERNAME", "EMAIL LUB NICK"), identifier, { identifier = it }, "operator@veloryx.pl")
                            Field(t("PASSWORD", "HASŁO"), password, { password = it }, "••••••••••", password = true)
                            CheckLine(t("Remember me on this device", "Zapamiętaj mnie na tym urządzeniu"), rememberMe) { rememberMe = it }
                            CaptchaBox(state.captcha, captchaAnswer, { captchaAnswer = it }, model::refreshCaptcha)
                            SignalButton(t("OPEN CHANNEL", "OTWÓRZ KANAŁ"), state.loading) { model.login(identifier, password, rememberMe, captchaAnswer) }
                            TextButton(onClick = { openWeb(context, "https://veloryx.pl/konto", state.language) }) { Text(t("FORGOT YOUR PASSWORD?", "NIE PAMIĘTASZ HASŁA?"), color = SignalGold, fontSize = 9.sp) }
                        }
                        AuthMode.REGISTER -> {
                            Field(t("USERNAME", "NICK"), username, { username = it }, "RAVEN_0217")
                            Field("EMAIL", email, { email = it }, "operator@veloryx.pl", KeyboardType.Email)
                            Field(t("PASSWORD", "HASŁO"), password, { password = it }, "••••••••••", password = true)
                            Field(t("REPEAT PASSWORD", "POWTÓRZ HASŁO"), confirmation, { confirmation = it }, "••••••••••", password = true)
                            CheckLine(t("I accept the account terms", "Akceptuję regulamin konta"), terms) { terms = it }
                            CheckLine(t("Send me development news", "Chcę otrzymywać nowości"), newsletter) { newsletter = it }
                            CaptchaBox(state.captcha, captchaAnswer, { captchaAnswer = it }, model::refreshCaptcha)
                            SignalButton(t("CREATE PASS", "UTWÓRZ PRZEPUSTKĘ"), state.loading) { model.register(username, email, password, confirmation, terms, newsletter, captchaAnswer) }
                        }
                        AuthMode.TWO_FACTOR -> {
                            Text(t("Enter the code from your authenticator application.", "Wpisz kod z aplikacji uwierzytelniającej."), color = Muted, fontSize = 12.sp)
                            Field(t("AUTHENTICATION CODE", "KOD UWIERZYTELNIANIA"), code, { code = it.filter(Char::isDigit).take(8) }, "000000", KeyboardType.Number)
                            SignalButton(t("VERIFY IDENTITY", "POTWIERDŹ TOŻSAMOŚĆ"), state.loading) { model.verifyTwoFactor(code) }
                        }
                    }
                    AnimatedVisibility(state.notice != null) {
                        Text(
                            state.notice.orEmpty(),
                            color = if (state.noticeError) Color(0xFFFF8B79) else Success,
                            fontFamily = FontFamily.Monospace, fontSize = 9.sp, lineHeight = 14.sp,
                            modifier = Modifier.fillMaxWidth().padding(top = 14.dp).background(if (state.noticeError) Color(0xFF2B100C) else Color(0xFF102116)).padding(12.dp),
                        )
                    }
                }
            }
            item { Text("VELORYX.PL / DARKWAVE INTERACTIVE", color = Muted, fontSize = 7.sp, modifier = Modifier.padding(top = 28.dp), letterSpacing = 1.sp) }
        }
    }
}

@Composable
private fun RowScope.AuthTab(label: String, active: Boolean, onClick: () -> Unit) {
    Box(
        Modifier.weight(1f).clickable(onClick = onClick).background(if (active) SignalRed else Ink).padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) { Text(label, color = if (active) Bone else Muted, fontFamily = FontFamily.Monospace, fontSize = 9.sp, fontWeight = FontWeight.Black) }
}

@Composable
private fun Field(label: String, value: String, change: (String) -> Unit, placeholder: String, keyboard: KeyboardType = KeyboardType.Text, password: Boolean = false) {
    Text(label, color = Muted, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 8.sp, modifier = Modifier.padding(top = 9.dp, bottom = 6.dp))
    OutlinedTextField(
        value = value, onValueChange = change, singleLine = true, modifier = Modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = Muted.copy(alpha = .55f), fontSize = 12.sp) },
        visualTransformation = if (password) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboard),
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SignalGold, unfocusedBorderColor = Line, focusedContainerColor = Ink, unfocusedContainerColor = Ink),
        shape = CutCornerShape(topEnd = 8.dp),
    )
}

@Composable
private fun CheckLine(label: String, checked: Boolean, change: (Boolean) -> Unit) {
    Row(Modifier.fillMaxWidth().clickable { change(!checked) }.padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked, change, colors = CheckboxDefaults.colors(checkedColor = SignalGold, checkmarkColor = Ink, uncheckedColor = Line))
        Text(label, color = Bone.copy(alpha = .75f), fontSize = 11.sp)
    }
}

@Composable
private fun CaptchaBox(captcha: Captcha?, answer: String, change: (String) -> Unit, refresh: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 10.dp).border(1.dp, Line).background(Ink).padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text("HUMAN CHECK", color = Muted, fontSize = 7.sp, fontFamily = FontFamily.Monospace)
            Text(captcha?.question ?: "SIGNAL UNAVAILABLE", color = SignalGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        OutlinedTextField(value = answer, onValueChange = { change(it.take(8)) }, modifier = Modifier.width(74.dp), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SignalGold, unfocusedBorderColor = Line), shape = CutCornerShape(0.dp))
        TextButton(onClick = refresh, contentPadding = PaddingValues(0.dp), modifier = Modifier.width(40.dp)) { Text("↻", color = SignalGold, fontSize = 20.sp) }
    }
}

@Composable
private fun SignalButton(label: String, busy: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick, enabled = !busy,
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(56.dp),
        shape = CutCornerShape(topEnd = 18.dp, bottomStart = 18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = SignalRed, contentColor = Bone),
    ) { Text(if (busy) "CONNECTING…" else label, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black, letterSpacing = 1.sp) }
}

@Composable
private fun HomeScreen(state: AppUiState, model: AppViewModel) {
    Column(Modifier.fillMaxSize().background(Ink).windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top)).imePadding()) {
        TopBar(state, model)
        Box(Modifier.weight(1f).fillMaxWidth()) {
            AnimatedContent(state.selected?.id ?: state.tab, label = "app-tab") {
                when {
                    state.selected != null -> ConversationScreen(state, model)
                    state.tab == AppTab.CHATS -> ChatsScreen(state, model)
                    state.tab == AppTab.FRIENDS -> FriendsScreen(state, model)
                    state.tab == AppTab.FORUM -> ForumScreen(state)
                    else -> ProfileScreen(state, model)
                }
            }
        }
        if (state.selected == null) BottomBar(state, model)
    }
}

@Composable
private fun TopBar(state: AppUiState, model: AppViewModel) {
    Row(Modifier.fillMaxWidth().height(66.dp).background(Color(0xFF0B0806)).border(width = 1.dp, color = Line).padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(38.dp).background(SignalRed, CutCornerShape(topEnd = 13.dp)), contentAlignment = Alignment.Center) { Text("DW", color = Bone, fontWeight = FontWeight.Black, fontSize = 12.sp) }
        Column(Modifier.weight(1f).padding(start = 11.dp)) {
            Text("DARKWAVE", color = Bone, fontWeight = FontWeight.Black, fontSize = 18.sp)
            Text("COMMUNITY / LIVE", color = SignalGold, fontFamily = FontFamily.Monospace, fontSize = 7.sp)
        }
        LanguageToggle(state.language, model::setLanguage)
    }
}

@Composable
private fun LanguageToggle(language: String, change: (String) -> Unit) {
    Row(Modifier.border(1.dp, Line).background(Ink)) {
        listOf("EN", "PL").forEach { item ->
            Text(item, color = if (language == item.lowercase()) Ink else Muted, fontSize = 8.sp, fontWeight = FontWeight.Black,
                modifier = Modifier.clickable { change(item.lowercase()) }.background(if (language == item.lowercase()) SignalGold else Color.Transparent).padding(horizontal = 9.dp, vertical = 8.dp))
        }
    }
}

@Composable
private fun BottomBar(state: AppUiState, model: AppViewModel) {
    val unread = state.inbox.conversations.sumOf { it.unreadCount }
    val requests = state.inbox.requests.count { it.direction == "incoming" }
    Row(Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)).height(66.dp).background(Color(0xFF0B0806)).border(1.dp, Line)) {
        NavItem("▣", tr(state, "CHATS", "CZATY"), state.tab == AppTab.CHATS, unread) { model.setTab(AppTab.CHATS) }
        NavItem("◇", tr(state, "FRIENDS", "ZNAJOMI"), state.tab == AppTab.FRIENDS, requests) { model.setTab(AppTab.FRIENDS) }
        NavItem("≡", "FORUM", state.tab == AppTab.FORUM, 0) { model.setTab(AppTab.FORUM) }
        NavItem("○", tr(state, "PROFILE", "PROFIL"), state.tab == AppTab.PROFILE, 0) { model.setTab(AppTab.PROFILE) }
    }
}

@Composable
private fun RowScope.NavItem(icon: String, label: String, active: Boolean, badge: Int, click: () -> Unit) {
    Box(Modifier.weight(1f).fillMaxHeight().clickable(onClick = click), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, color = if (active) SignalGold else Muted, fontSize = 21.sp)
            Text(label, color = if (active) SignalGold else Muted, fontSize = 6.sp, fontWeight = FontWeight.Black, letterSpacing = .5.sp)
        }
        if (badge > 0) Badge(Modifier.align(Alignment.TopEnd).offset(x = (-14).dp, y = 7.dp), containerColor = SignalRed) { Text(if (badge > 9) "9+" else badge.toString()) }
    }
}

@Composable
private fun ChatsScreen(state: AppUiState, model: AppViewModel) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(15.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { SectionTitle("PRIVATE CHANNEL", tr(state, "MESSAGES", "WIADOMOŚCI")) }
        if (state.inbox.conversations.isEmpty()) item { EmptyState("◌", tr(state, "NO CONVERSATIONS", "BRAK ROZMÓW"), tr(state, "Add a friend to start a secure channel.", "Dodaj znajomego, aby rozpocząć rozmowę.")) }
        items(state.inbox.conversations, key = { it.id }) { conversation -> ConversationRow(conversation, state.language) { model.selectConversation(conversation) } }
    }
}

@Composable
private fun ConversationRow(conversation: Conversation, language: String, click: () -> Unit) {
    val name = if (conversation.kind == "group") conversation.title ?: "GROUP CHANNEL" else conversation.friend?.username ?: "PRIVATE CHANNEL"
    Row(Modifier.fillMaxWidth().clickable(onClick = click).border(1.dp, Line, CutCornerShape(topEnd = 12.dp)).background(Brush.horizontalGradient(listOf(PanelRaised, Ink))).padding(11.dp), verticalAlignment = Alignment.CenterVertically) {
        Avatar(conversation.friend, conversation.unreadCount)
        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(name, color = Bone, fontSize = 13.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (conversation.friend != null) Text(presenceText(conversation.friend, language), color = if (conversation.friend.isOnline) Success else Muted, fontSize = 7.sp)
            Text(conversation.lastMessage ?: "Start a conversation", color = Muted, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Text(conversation.lastMessageAt?.takeLast(8)?.take(5).orEmpty(), color = Muted, fontSize = 8.sp)
    }
}

@Composable
private fun ConversationScreen(state: AppUiState, model: AppViewModel) {
    val context = LocalContext.current
    val conversation = state.selected ?: return
    var draft by remember(conversation.id) { mutableStateOf("") }
    var emojiOpen by remember(conversation.id) { mutableStateOf(false) }
    var optionsOpen by remember(conversation.id) { mutableStateOf(false) }
    var reportOpen by remember(conversation.id) { mutableStateOf(false) }
    var themeOpen by remember(conversation.id) { mutableStateOf(false) }
    var reportDetails by remember(conversation.id) { mutableStateOf("") }
    var recorder by remember(conversation.id) { mutableStateOf<MediaRecorder?>(null) }
    var recordingFile by remember(conversation.id) { mutableStateOf<File?>(null) }
    val name = if (conversation.kind == "group") conversation.title ?: "GROUP CHANNEL" else conversation.friend?.username ?: "PRIVATE CHANNEL"
    fun beginRecording() {
        if (recorder != null) return
        runCatching {
            val file = File.createTempFile("darkwave-voice-", ".m4a", context.cacheDir)
            @Suppress("DEPRECATION")
            val next = if (Build.VERSION.SDK_INT >= 31) MediaRecorder(context) else MediaRecorder()
            next.setAudioSource(MediaRecorder.AudioSource.MIC)
            next.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            next.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            next.setAudioEncodingBitRate(96_000)
            next.setAudioSamplingRate(44_100)
            next.setOutputFile(file.absolutePath)
            next.prepare(); next.start()
            recordingFile = file; recorder = next
        }.onFailure { model.clearNotice() }
    }
    val recordPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> if (granted) beginRecording() }
    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri -> uri?.let { model.sendAttachment(it, draft); draft = "" } }
    DisposableEffect(conversation.id) {
        onDispose { runCatching { recorder?.stop() }; recorder?.release(); recordingFile?.delete() }
    }
    fun toggleRecording() {
        val active = recorder
        if (active != null) {
            runCatching { active.stop() }; active.release(); recorder = null
            recordingFile?.let { model.sendRecording(it, draft); draft = "" }
            recordingFile = null
        } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) beginRecording()
        else recordPermission.launch(Manifest.permission.RECORD_AUDIO)
    }

    Column(Modifier.fillMaxSize().background(chatBackground(conversation.theme)).windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom))) {
        Row(Modifier.fillMaxWidth().height(66.dp).background(Panel).border(1.dp, Line).padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { model.selectConversation(null) }) { Icon(Icons.Rounded.ArrowBack, null, tint = Bone) }
            Avatar(conversation.friend)
            Column(Modifier.weight(1f).padding(start = 10.dp)) { Text(name, color = Bone, fontSize = 13.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis); Text(if (conversation.friend != null) presenceText(conversation.friend, state.language) else "${conversation.participants.size} ${tr(state, "participants", "uczestników")}", color = if (conversation.friend?.isOnline == true) Success else Muted, fontSize = 7.sp) }
            IconButton(onClick = { openWeb(context, "https://veloryx.pl/wiadomosci?user=${conversation.friend?.username.orEmpty()}", state.language) }) { Icon(Icons.Rounded.Phone, tr(state, "Call", "Zadzwoń"), tint = SignalGold) }
            Box {
                IconButton(onClick = { optionsOpen = true }) { Icon(Icons.Rounded.MoreVert, tr(state, "Conversation options", "Opcje rozmowy"), tint = Bone) }
                DropdownMenu(optionsOpen, { optionsOpen = false }, modifier = Modifier.background(PanelRaised)) {
                    DropdownMenuItem({ Text(tr(state, if (conversation.muted) "Unmute" else "Mute", if (conversation.muted) "Włącz dźwięk" else "Wycisz")) }, { optionsOpen = false; model.chatAction("toggle-mute") }, leadingIcon = { Icon(Icons.Rounded.VolumeOff, null) })
                    DropdownMenuItem({ Text(tr(state, "Change chat theme", "Zmień motyw czatu")) }, { optionsOpen = false; themeOpen = true }, leadingIcon = { Icon(Icons.Rounded.Palette, null) })
                    if (conversation.kind == "direct") DropdownMenuItem({ Text(tr(state, "Restrict / unrestrict", "Ogranicz / cofnij")) }, { optionsOpen = false; model.chatAction("toggle-restrict") }, leadingIcon = { Icon(Icons.Rounded.VisibilityOff, null) })
                    DropdownMenuItem({ Text(tr(state, "Delete conversation", "Usuń rozmowę")) }, { optionsOpen = false; model.chatAction("delete-conversation") }, leadingIcon = { Icon(Icons.Rounded.DeleteOutline, null) })
                    if (conversation.kind == "direct") {
                        DropdownMenuItem({ Text(tr(state, "Remove friend", "Usuń znajomego")) }, { optionsOpen = false; model.chatAction("remove-friend") }, leadingIcon = { Icon(Icons.Rounded.PersonRemove, null) })
                        DropdownMenuItem({ Text(tr(state, "Block user", "Zablokuj użytkownika")) }, { optionsOpen = false; model.chatAction("block") }, leadingIcon = { Icon(Icons.Rounded.Block, null) })
                        DropdownMenuItem({ Text(tr(state, "Report user", "Zgłoś użytkownika")) }, { optionsOpen = false; reportOpen = true }, leadingIcon = { Icon(Icons.Rounded.Report, null) })
                    }
                    DropdownMenuItem({ Text(tr(state, "Open all chat tools", "Otwórz wszystkie narzędzia")) }, { optionsOpen = false; openWeb(context, "https://veloryx.pl/wiadomosci?user=${conversation.friend?.username.orEmpty()}", state.language) }, leadingIcon = { Icon(Icons.Rounded.OpenInNew, null) })
                }
            }
        }
        LazyColumn(Modifier.weight(1f).fillMaxWidth(), contentPadding = PaddingValues(12.dp), verticalArrangement = Arrangement.spacedBy(9.dp), reverseLayout = true) {
            items(state.messages.reversed(), key = { it.id }) { message -> MessageBubble(message, state, model) }
        }
        Column(Modifier.fillMaxWidth().background(Panel).border(1.dp, Line).padding(9.dp)) {
            AnimatedVisibility(emojiOpen) { Row(Modifier.fillMaxWidth().padding(bottom = 6.dp), horizontalArrangement = Arrangement.SpaceEvenly) { listOf("😀","😂","😍","😮","😢","🔥","👍","❤️").forEach { emoji -> Text(emoji, fontSize = 23.sp, modifier = Modifier.clip(CircleShape).clickable { draft += emoji }.padding(4.dp)) } } }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                IconButton(onClick = { emojiOpen = !emojiOpen }) { Icon(Icons.Rounded.InsertEmoticon, tr(state, "Emoji", "Emotki"), tint = SignalGold) }
                IconButton(onClick = { filePicker.launch(arrayOf("image/*", "audio/*", "application/pdf", "text/plain", "application/zip")) }) { Icon(Icons.Rounded.AttachFile, tr(state, "Attach file", "Dołącz plik"), tint = SignalGold) }
                IconButton(onClick = ::toggleRecording) { Icon(if (recorder == null) Icons.Rounded.Mic else Icons.Rounded.Stop, tr(state, "Voice message", "Wiadomość głosowa"), tint = if (recorder == null) SignalGold else SignalRed) }
                OutlinedTextField(
                    value = draft, onValueChange = { draft = it }, modifier = Modifier.weight(1f), maxLines = 4,
                    placeholder = { Text(tr(state, "Write a message…", "Napisz wiadomość…"), color = Muted) },
                    shape = RoundedCornerShape(22.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SignalGold, unfocusedBorderColor = Line, focusedContainerColor = Ink, unfocusedContainerColor = Ink),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { if (draft.isNotBlank()) { model.sendMessage(draft); draft = "" } }),
                )
                Spacer(Modifier.width(6.dp))
                FloatingActionButton(onClick = { if (draft.isNotBlank()) { model.sendMessage(draft); draft = "" } }, containerColor = SignalGold, contentColor = Ink, modifier = Modifier.size(48.dp)) { Icon(Icons.Rounded.Send, tr(state, "Send", "Wyślij")) }
            }
        }
    }
    if (reportOpen) AlertDialog(onDismissRequest = { reportOpen = false }, title = { Text(tr(state, "Report user", "Zgłoś użytkownika")) }, text = { OutlinedTextField(reportDetails, { reportDetails = it.take(1000) }, placeholder = { Text(tr(state, "Describe the problem", "Opisz problem")) }) }, confirmButton = { TextButton(onClick = { if (reportDetails.isNotBlank()) model.chatAction("report", details = reportDetails); reportOpen = false }) { Text(tr(state, "SEND REPORT", "WYŚLIJ ZGŁOSZENIE")) } }, dismissButton = { TextButton(onClick = { reportOpen = false }) { Text(tr(state, "CANCEL", "ANULUJ")) } })
    if (themeOpen) AlertDialog(onDismissRequest = { themeOpen = false }, title = { Text(tr(state, "Chat theme", "Motyw czatu")) }, text = { Column { listOf("nocturne" to tr(state, "Nocturne Archive", "Archiwum Nocturne"), "inferno" to tr(state, "Inferno Relay", "Piekielny Przekaźnik"), "cold-signal" to tr(state, "Cold Signal", "Zimny Sygnał"), "violet-void" to tr(state, "Violet Void", "Fioletowa Pustka"), "ember-tape" to tr(state, "Ember Tape", "Taśma Żaru")).forEach { (key, label) -> TextButton(onClick = { model.chatAction("set-theme", theme = key); themeOpen = false }, modifier = Modifier.fillMaxWidth()) { Text(label, color = if (conversation.theme == key) SignalGold else Bone) } } } }, confirmButton = {})
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun MessageBubble(message: ChatMessage, state: AppUiState, model: AppViewModel) {
    var menuOpen by remember(message.id) { mutableStateOf(false) }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (message.mine) Arrangement.End else Arrangement.Start) {
        if (message.recalledAt != null) {
            Row(Modifier.widthIn(max = 280.dp).border(1.dp, Line, RoundedCornerShape(10.dp)).background(Panel.copy(alpha = .76f)).padding(horizontal = 12.dp, vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Undo, null, tint = Muted, modifier = Modifier.size(16.dp)); Spacer(Modifier.width(8.dp)); Column { Text(tr(state, "Message recalled", "Wiadomość cofnięta"), color = Muted, fontSize = 10.sp, fontFamily = FontFamily.Monospace); Text(messageTime(message.createdAt), color = Muted.copy(alpha = .65f), fontSize = 7.sp) }
            }
            return@Row
        }
        Box {
        Column(
            Modifier.widthIn(max = 310.dp).combinedClickable(onClick = {}, onLongClick = { menuOpen = true }).clip(if (message.mine) RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp) else RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp))
                .background(if (message.mine) Brush.linearGradient(listOf(Color(0xFF8F2B20), SignalRed)) else Brush.linearGradient(listOf(PanelRaised, Panel))).border(1.dp, if (message.mine) SignalRed else Line, RoundedCornerShape(16.dp)).padding(11.dp),
        ) {
            if (!message.mine && message.sender != null) Text(message.sender.username, color = SignalGold, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            if (!message.body.isNullOrBlank()) Text(callMessageText(message.body, state.language) ?: message.body, color = Bone, fontSize = 13.sp, lineHeight = 18.sp)
            message.attachment?.let { attachment -> Row(Modifier.padding(top = 7.dp).border(1.dp, Bone.copy(alpha = .25f), RoundedCornerShape(10.dp)).padding(9.dp), verticalAlignment = Alignment.CenterVertically) { Icon(if (attachment.kind == "audio") Icons.Rounded.GraphicEq else if (attachment.kind == "image") Icons.Rounded.Image else Icons.Rounded.Description, null, tint = SignalGold); Spacer(Modifier.width(8.dp)); Column { Text(attachment.name, color = Bone, fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis); Text("${(attachment.size / 1024).coerceAtLeast(1)} KB", color = Muted, fontSize = 7.sp) } } }
            if (message.reactions.isNotEmpty()) Row(Modifier.padding(top = 7.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) { message.reactions.forEach { reaction -> Surface(onClick = { model.chatAction("react", message.id, reaction.emoji) }, shape = CircleShape, color = if (reaction.mine) SignalGold.copy(alpha = .25f) else Ink.copy(alpha = .45f), border = androidx.compose.foundation.BorderStroke(1.dp, if (reaction.mine) SignalGold else Line)) { Text("${reaction.emoji} ${reaction.count}", fontSize = 10.sp, modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)) } } }
            Row(Modifier.align(Alignment.End).padding(top = 5.dp), verticalAlignment = Alignment.CenterVertically) { Text(messageTime(message.createdAt) + if (message.mine) " · " + if (message.readAt != null) tr(state, "READ", "ODCZYTANO") else if (message.deliveredAt != null) tr(state, "DELIVERED", "DOSTARCZONO") else tr(state, "SENT", "WYSŁANO") else "", color = Bone.copy(alpha = .58f), fontSize = 7.sp); IconButton(onClick = { menuOpen = true }, modifier = Modifier.size(26.dp)) { Icon(Icons.Rounded.MoreHoriz, null, tint = Bone.copy(alpha = .72f), modifier = Modifier.size(16.dp)) } }
        }
            DropdownMenu(menuOpen, { menuOpen = false }, modifier = Modifier.background(PanelRaised)) {
                Row(Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) { listOf("👍","❤️","😂","😮","🔥").forEach { emoji -> Text(emoji, fontSize = 21.sp, modifier = Modifier.clickable { model.chatAction("react", message.id, emoji); menuOpen = false }.padding(5.dp)) } }
                HorizontalDivider(color = Line)
                DropdownMenuItem({ Text(tr(state, "Delete for me", "Usuń u mnie")) }, { menuOpen = false; model.chatAction("delete-message", message.id) }, leadingIcon = { Icon(Icons.Rounded.DeleteOutline, null) })
                if (message.mine) DropdownMenuItem({ Text(tr(state, "Recall for everyone", "Cofnij u wszystkich")) }, { menuOpen = false; model.chatAction("recall-message", message.id) }, leadingIcon = { Icon(Icons.Rounded.Undo, null) })
            }
        }
    }
}

@Composable
private fun FriendsScreen(state: AppUiState, model: AppViewModel) {
    var query by remember { mutableStateOf("") }
    val incoming = state.inbox.requests.filter { it.direction == "incoming" }
    val friends = state.inbox.conversations.filter { it.kind == "direct" }.mapNotNull { it.friend }.distinctBy { it.username }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(15.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
        item { SectionTitle("COMMUNITY INDEX", tr(state, "FRIENDS", "ZNAJOMI")) }
        item {
            OutlinedTextField(
                query, { query = it; model.search(it) }, Modifier.fillMaxWidth(), singleLine = true,
                placeholder = { Text(tr(state, "Search username or #tag", "Szukaj nicku lub #tagu"), color = Muted) }, leadingIcon = { Text("⌕", color = SignalGold, fontSize = 20.sp) },
                shape = CutCornerShape(topEnd = 12.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SignalGold, unfocusedBorderColor = Line),
            )
        }
        if (incoming.isNotEmpty()) {
            item { SmallLabel(tr(state, "INVITATIONS", "ZAPROSZENIA")) }
            items(incoming, key = { "request-${it.id}" }) { request -> PersonRow(request.user, state) {
                Row { MiniAction("✓") { model.social("accept-request", request.user, request.id) }; MiniAction("×", danger = true) { model.social("reject-request", request.user, request.id) } }
            } }
        }
        if (query.trim().length >= 2) {
            item { SmallLabel(tr(state, "SEARCH RESULTS", "WYNIKI WYSZUKIWANIA")) }
            items(state.searchResults, key = { "search-${it.username}" }) { member -> PersonRow(member, state) {
                when (member.relationship) {
                    "friends" -> Text("FRIEND", color = Success, fontSize = 8.sp)
                    "outgoing" -> Text(tr(state, "SENT", "WYSŁANO"), color = Muted, fontSize = 8.sp)
                    "self" -> Unit
                    else -> MiniAction("+") { model.social("send-request", member) }
                }
            } }
        } else {
            item { SmallLabel(tr(state, "YOUR CONTACTS", "TWOJE KONTAKTY")) }
            items(friends, key = { "friend-${it.username}" }) { member -> PersonRow(member, state) { Text(presenceText(member, state.language), color = if (member.isOnline) Success else Muted, fontSize = 8.sp) } }
        }
    }
}

@Composable
private fun PersonRow(member: Profile, state: AppUiState, action: @Composable () -> Unit) {
    Row(Modifier.fillMaxWidth().border(1.dp, Line).background(Panel).padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
        Avatar(member)
        Column(Modifier.weight(1f).padding(horizontal = 11.dp)) {
            Text(member.username, color = Bone, fontWeight = FontWeight.Black, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("#${member.tag} · ${roleName(member.role, state.language)}", color = Muted, fontSize = 8.sp)
        }
        action()
    }
}

@Composable
private fun MiniAction(label: String, danger: Boolean = false, click: () -> Unit) {
    Box(Modifier.padding(start = 5.dp).size(36.dp).clickable(onClick = click).background(if (danger) SignalRed else SignalGold), contentAlignment = Alignment.Center) { Text(label, color = if (danger) Bone else Ink, fontWeight = FontWeight.Black) }
}

@Composable
private fun ForumScreen(state: AppUiState) {
    val context = LocalContext.current
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(15.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { SectionTitle("COMMUNITY SIGNAL", "FORUM") }
        item { Button(onClick = { openWeb(context, "https://veloryx.pl/forum", state.language) }, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = SignalGold, contentColor = Ink), shape = CutCornerShape(topEnd = 14.dp)) { Icon(Icons.Rounded.Forum, null); Spacer(Modifier.width(8.dp)); Text(tr(state, "OPEN FULL FORUM TOOLS", "OTWÓRZ PEŁNE FORUM"), fontWeight = FontWeight.Black, fontSize = 9.sp) } }
        if (state.forum.isEmpty()) item { EmptyState("≡", tr(state, "NO THREADS", "BRAK WĄTKÓW"), tr(state, "The channel is silent.", "Kanał jest cichy.")) }
        items(state.forum, key = { it.id }) { thread ->
            Column(Modifier.fillMaxWidth().border(1.dp, Line, CutCornerShape(topEnd = 18.dp)).background(Brush.linearGradient(listOf(PanelRaised, Ink))).padding(15.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(thread.category.uppercase(), color = SignalGold, fontFamily = FontFamily.Monospace, fontSize = 8.sp); Text(if (thread.pinned) "PINNED" else "LIVE", color = if (thread.pinned) SignalRed else Muted, fontSize = 7.sp) }
                Text(if (state.language == "pl") thread.titlePl.ifBlank { thread.titleEn } else thread.titleEn.ifBlank { thread.titlePl }, color = Bone, fontSize = 23.sp, fontWeight = FontWeight.Black, lineHeight = 24.sp, modifier = Modifier.padding(vertical = 10.dp))
                Text(if (state.language == "pl") thread.bodyPl.ifBlank { thread.bodyEn } else thread.bodyEn.ifBlank { thread.bodyPl }, color = Muted, fontSize = 12.sp, lineHeight = 18.sp, maxLines = 5, overflow = TextOverflow.Ellipsis)
                HorizontalDivider(Modifier.padding(vertical = 11.dp), color = Line)
                Text("${thread.author} / ${thread.createdAt.take(10)}", color = SignalGold.copy(alpha = .7f), fontSize = 7.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
private fun ProfileScreen(state: AppUiState, model: AppViewModel) {
    val context = LocalContext.current
    val profile = state.profile ?: return
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(15.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { SectionTitle("DW_IDENTITY / PRIVATE", tr(state, "YOUR PROFILE", "TWÓJ PROFIL")) }
        item {
            Column(Modifier.fillMaxWidth().border(1.dp, SignalGold, CutCornerShape(topEnd = 28.dp, bottomStart = 28.dp)).background(Brush.radialGradient(listOf(Color(0xFF42170F), Panel), radius = 700f)).padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Avatar(profile, large = true)
                    Column(Modifier.weight(1f).padding(start = 18.dp)) {
                        Text(roleName(profile.role, state.language).uppercase(), color = SignalGold, fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                        Text(profile.username, color = Bone, fontSize = 27.sp, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("#${profile.tag} · LVL ${profile.level}", color = Muted, fontSize = 9.sp)
                    }
                }
                Spacer(Modifier.height(18.dp))
                LinearProgressIndicator(progress = { ((profile.totalXp % 1000) / 1000f).coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth().height(5.dp), color = SignalGold, trackColor = Ink)
                Text("${profile.totalXp} XP", color = SignalGold, fontSize = 8.sp, modifier = Modifier.align(Alignment.End).padding(top = 6.dp))
            }
        }
        item { SettingsRow("⚙", tr(state, "ACCOUNT SETTINGS", "USTAWIENIA KONTA"), profile.email) { openWeb(context, "https://veloryx.pl/panel", state.language) } }
        item { SettingsRow("◎", tr(state, "PUBLIC PROFILE", "PROFIL PUBLICZNY"), "veloryx.pl/u/${profile.username}") { openWeb(context, "https://veloryx.pl/u/${profile.username}", state.language) } }
        item { SettingsRow("?", tr(state, "HELP CENTER", "CENTRUM POMOCY"), "DARKWAVE SUPPORT") { openWeb(context, "https://veloryx.pl/pomoc", state.language) } }
        item { SettingsRow("×", tr(state, "SIGN OUT", "WYLOGUJ SIĘ"), tr(state, "Close this private channel", "Zamknij prywatny kanał"), danger = true) { model.logout() } }
    }
}

@Composable
private fun SettingsRow(icon: String, title: String, subtitle: String, danger: Boolean = false, click: () -> Unit) {
    Row(Modifier.fillMaxWidth().clickable(onClick = click).border(1.dp, if (danger) SignalRed.copy(alpha = .55f) else Line).background(Panel).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(42.dp).border(1.dp, if (danger) SignalRed else SignalGold, CircleShape), contentAlignment = Alignment.Center) { Text(icon, color = if (danger) SignalRed else SignalGold, fontSize = 18.sp) }
        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) { Text(title, color = Bone, fontSize = 10.sp, fontWeight = FontWeight.Black); Text(subtitle, color = Muted, fontSize = 8.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) }
        Text("→", color = SignalGold)
    }
}

@Composable
private fun Avatar(member: Profile?, unread: Int = 0, large: Boolean = false) {
    val size = if (large) 82.dp else 48.dp
    Box(Modifier.size(size)) {
        Box(Modifier.fillMaxSize().border(if (large) 3.dp else 1.dp, roleColor(member?.role), CircleShape).padding(if (large) 5.dp else 3.dp).clip(CircleShape).background(Brush.radialGradient(listOf(Color(0xFFF0D98B), SignalGold))), contentAlignment = Alignment.Center) {
            Text(member?.username?.firstOrNull()?.uppercase() ?: "D", color = Ink, fontWeight = FontWeight.Black, fontSize = if (large) 25.sp else 16.sp)
        }
        if (member?.isOnline == true) Box(Modifier.align(Alignment.BottomEnd).size(if (large) 17.dp else 12.dp).border(2.dp, Ink, CircleShape).background(Success, CircleShape))
        if (unread > 0) Badge(Modifier.align(Alignment.TopEnd), containerColor = SignalRed) { Text(if (unread > 9) "9+" else unread.toString()) }
    }
}

@Composable
private fun SectionTitle(signal: String, title: String) {
    Column(Modifier.fillMaxWidth().padding(bottom = 9.dp)) { Text(signal, color = SignalGold, fontFamily = FontFamily.Monospace, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp); Text(title, color = Bone, fontSize = 34.sp, lineHeight = 36.sp, fontWeight = FontWeight.Black); HorizontalDivider(Modifier.padding(top = 12.dp), color = Line) }
}

@Composable private fun SmallLabel(text: String) { Text(text, color = SignalGold, fontSize = 8.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black, letterSpacing = 1.sp, modifier = Modifier.padding(top = 8.dp)) }

@Composable
private fun EmptyState(icon: String, title: String, copy: String) {
    Column(Modifier.fillMaxWidth().heightIn(min = 230.dp).border(1.dp, Line, CutCornerShape(topEnd = 22.dp)).background(Panel).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Box(Modifier.size(64.dp).border(1.dp, SignalGold, CircleShape), contentAlignment = Alignment.Center) { Text(icon, color = SignalGold, fontSize = 24.sp) }
        Text(title, color = Bone, fontWeight = FontWeight.Black, fontSize = 11.sp, modifier = Modifier.padding(top = 14.dp))
        Text(copy, color = Muted, fontSize = 10.sp, lineHeight = 15.sp, modifier = Modifier.padding(top = 8.dp))
    }
}

private fun tr(state: AppUiState, en: String, pl: String) = if (state.language == "pl") pl else en
private fun roleName(role: String, language: String) = when (role) { "owner" -> if (language == "pl") "Właściciel" else "Owner"; "admin" -> "Administrator"; "moderator" -> "Moderator"; else -> if (language == "pl") "Użytkownik" else "Member" }
private fun roleColor(role: String?) = when (role) { "owner" -> SignalRed; "admin" -> SignalGold; "moderator" -> Color(0xFF83B7C6); else -> Color(0xFF8D7655) }
private fun chatBackground(theme: String): Brush = when (theme) {
    "inferno" -> Brush.radialGradient(listOf(Color(0xFF45160D), Color(0xFF110503)), radius = 900f)
    "cold-signal" -> Brush.verticalGradient(listOf(Color(0xFF09232B), Color(0xFF040E12)))
    "violet-void" -> Brush.radialGradient(listOf(Color(0xFF2A103D), Color(0xFF09040D)), radius = 900f)
    "ember-tape" -> Brush.verticalGradient(listOf(Color(0xFF30200E), Color(0xFF100B06)))
    else -> Brush.verticalGradient(listOf(Ink, Color(0xFF110D09)))
}

private fun messageTime(value: String): String = runCatching {
    val instant = Instant.parse(value)
    java.time.ZoneId.systemDefault().let { zone -> java.time.format.DateTimeFormatter.ofPattern("HH:mm").withZone(zone).format(instant) }
}.getOrElse { value.takeLast(8).take(5) }

private fun callMessageText(body: String, language: String): String? {
    if (body == "[[DW_CALL_MISSED]]") return if (language == "pl") "Nieodebrane połączenie" else "Missed call"
    val total = Regex("^\\[\\[DW_CALL_ENDED:(\\d+)]]$").find(body)?.groupValues?.getOrNull(1)?.toLongOrNull() ?: return null
    val hours = total / 3600; val minutes = (total % 3600) / 60; val seconds = total % 60
    val duration = if (hours > 0) "%02d:%02d:%02d".format(hours, minutes, seconds) else "%02d:%02d".format(minutes, seconds)
    return (if (language == "pl") "Zakończone połączenie" else "Call ended") + " · " + duration
}

private fun presenceText(member: Profile, language: String): String {
    if (member.isOnline) return when (member.presenceMode) {
        "busy" -> if (language == "pl") "zajęty" else "busy"
        "away" -> if (language == "pl") "zaraz wracam" else "away"
        else -> "online"
    }
    if (member.presenceMode == "invisible" || member.lastActiveAt.isNullOrBlank()) return "offline"
    val minutes = runCatching { ChronoUnit.MINUTES.between(Instant.parse(member.lastActiveAt), Instant.now()).coerceAtLeast(0) }.getOrElse { return "offline" }
    return when {
        minutes < 1 -> if (language == "pl") "aktywny przed chwilą" else "active just now"
        minutes < 60 -> if (language == "pl") "aktywny ${minutes} min temu" else "active ${minutes}m ago"
        minutes < 1_440 -> if (language == "pl") "aktywny ${minutes / 60} godz. temu" else "active ${minutes / 60}h ago"
        minutes < 10_080 -> if (language == "pl") "aktywny ${minutes / 1_440} dni temu" else "active ${minutes / 1_440}d ago"
        else -> if (language == "pl") "aktywny ${minutes / 10_080} tyg. temu" else "active ${minutes / 10_080}w ago"
    }
}

private fun openWeb(context: Context, url: String, language: String) {
    val target = url + if (url.contains("?")) "&language=$language" else "?language=$language"
    context.startActivity(Intent(context, PortalActivity::class.java).putExtra("url", target))
}
