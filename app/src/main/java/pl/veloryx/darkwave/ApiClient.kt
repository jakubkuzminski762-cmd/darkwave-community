package pl.veloryx.darkwave

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

class PersistentCookieJar(context: Context) : CookieJar {
    private val store = context.getSharedPreferences("darkwave-cookies", Context.MODE_PRIVATE)

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val editor = store.edit()
        cookies.forEach { cookie ->
            if (cookie.expiresAt < System.currentTimeMillis()) editor.remove(cookie.name)
            else editor.putString(cookie.name, cookie.toString())
        }
        editor.apply()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val expired = mutableListOf<String>()
        val cookies = store.all.mapNotNull { (name, raw) ->
            val cookie = Cookie.parse(url, raw as? String ?: return@mapNotNull null)
            if (cookie == null || cookie.expiresAt < System.currentTimeMillis()) { expired += name; null }
            else if (cookie.matches(url)) cookie else null
        }
        if (expired.isNotEmpty()) store.edit().also { editor -> expired.forEach(editor::remove) }.apply()
        return cookies
    }

    fun clear() = store.edit().clear().apply()
}

class ApiClient(context: Context) {
    private val appContext = context.applicationContext
    private val preferences = appContext.getSharedPreferences("darkwave-preferences", Context.MODE_PRIVATE)
    private val cookieJar = PersistentCookieJar(context.applicationContext)
    private val client = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()
    private val jsonType = "application/json; charset=utf-8".toMediaType()
    private val base = "https://veloryx.pl"

    fun savedLanguage(): String = preferences.getString("language", "en")?.takeIf { it == "pl" } ?: "en"

    fun saveLanguage(language: String) {
        preferences.edit().putString("language", if (language == "pl") "pl" else "en").apply()
    }

    private suspend fun request(path: String, method: String = "GET", payload: JSONObject? = null): ApiResult<JSONObject> = withContext(Dispatchers.IO) {
        try {
            val url = if (path.startsWith("https://")) path else base + path
            val builder = Request.Builder().url(url).header("X-DW-Request", "account-form").header("Accept", "application/json")
            when (method) {
                "POST" -> builder.post((payload ?: JSONObject()).toString().toRequestBody(jsonType))
                else -> builder.get()
            }
            client.newCall(builder.build()).execute().use { response ->
                val body = response.body?.string().orEmpty()
                val json = runCatching { JSONObject(body) }.getOrElse { JSONObject() }
                ApiResult(if (response.isSuccessful) json else null, json.nullableString("message"), response.code)
            }
        } catch (error: Exception) {
            ApiResult(message = error.message ?: "Connection unavailable")
        }
    }

    suspend fun session(): ApiResult<Profile> {
        val result = request("/api/auth/session")
        return ApiResult(result.value?.optJSONObject("profile")?.toProfile(), result.message, result.status)
    }

    suspend fun captcha(): ApiResult<Captcha> {
        val result = request("/api/auth/captcha")
        val json = result.value
        return ApiResult(if (json != null) Captcha(json.optString("challengeId"), json.optString("question")) else null, result.message, result.status)
    }

    suspend fun login(identifier: String, password: String, remember: Boolean, captcha: Captcha, answer: String): ApiResult<String> {
        val result = request("/api/auth/login", "POST", JSONObject().put("identifier", identifier).put("password", password).put("remember", remember).put("captchaId", captcha.id).put("captchaAnswer", answer))
        val token = result.value?.nullableString("loginToken")
        return ApiResult(token ?: if (result.value != null) "ok" else null, result.message, result.status)
    }

    suspend fun loginTwoFactor(token: String, code: String): ApiResult<Boolean> {
        val result = request("/api/auth/login-2fa", "POST", JSONObject().put("loginToken", token).put("code", code))
        return ApiResult(result.value != null, result.message, result.status)
    }

    suspend fun register(username: String, email: String, password: String, confirmation: String, newsletter: Boolean, language: String, captcha: Captcha, answer: String): ApiResult<Boolean> {
        val payload = JSONObject().put("username", username).put("email", email).put("password", password).put("passwordConfirmation", confirmation).put("termsAccepted", true).put("newsletter", newsletter).put("language", language).put("termsVersion", "2026-08-20-v1").put("captchaId", captcha.id).put("captchaAnswer", answer)
        val result = request("/api/auth/register", "POST", payload)
        return ApiResult(result.value != null, result.message, result.status)
    }

    suspend fun logout() { request("/api/auth/logout", "POST"); cookieJar.clear() }

    suspend fun inbox(): ApiResult<Inbox> {
        val result = request("/api/chat/inbox")
        val json = result.value ?: return ApiResult(message = result.message, status = result.status)
        return ApiResult(Inbox(json.optJSONArray("conversations").toList { it.toConversation() }, json.optJSONArray("requests").toList { item -> FriendRequest(item.optLong("id"), item.optString("direction"), item.optJSONObject("user").toProfile()) }), status = result.status)
    }

    suspend fun messages(conversationId: Long): ApiResult<List<ChatMessage>> {
        val result = request("/api/chat/messages?conversationId=$conversationId")
        return ApiResult(result.value?.optJSONArray("messages").toList { it.toMessage() }, result.message, result.status)
    }

    suspend fun send(conversationId: Long, text: String): ApiResult<Boolean> {
        val result = request("/api/chat/send", "POST", JSONObject().put("conversationId", conversationId).put("body", text))
        return ApiResult(result.value != null, result.message, result.status)
    }

    suspend fun upload(conversationId: Long, text: String, uri: Uri): ApiResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            val resolver = appContext.contentResolver
            val bytes = resolver.openInputStream(uri)?.use { it.readBytes() } ?: return@withContext ApiResult(message = "File unavailable")
            if (bytes.isEmpty() || bytes.size > 25_000_000) return@withContext ApiResult(message = "File may be up to 25 MB")
            val mime = resolver.getType(uri) ?: "application/octet-stream"
            var name = "attachment"
            resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) name = cursor.getString(0) ?: name
            }
            uploadBytes(conversationId, text, bytes, mime, name)
        } catch (error: Exception) { ApiResult(message = error.message ?: "Upload unavailable") }
    }

    suspend fun upload(conversationId: Long, text: String, file: File, mime: String): ApiResult<Boolean> = withContext(Dispatchers.IO) {
        try { uploadBytes(conversationId, text, file.readBytes(), mime, file.name) }
        catch (error: Exception) { ApiResult(message = error.message ?: "Upload unavailable") }
    }

    private fun uploadBytes(conversationId: Long, text: String, bytes: ByteArray, mime: String, name: String): ApiResult<Boolean> {
        val body = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("conversationId", conversationId.toString())
            .addFormDataPart("body", text)
            .addFormDataPart("file", name, bytes.toRequestBody(mime.toMediaType()))
            .build()
        val request = Request.Builder().url("$base/api/chat/upload")
            .header("X-DW-Request", "account-form").header("Accept", "application/json").post(body).build()
        client.newCall(request).execute().use { response ->
            val json = runCatching { JSONObject(response.body?.string().orEmpty()) }.getOrElse { JSONObject() }
            return ApiResult(if (response.isSuccessful) true else null, json.nullableString("message"), response.code)
        }
    }

    suspend fun chatAction(action: String, conversationId: Long, messageId: Long? = null, emoji: String? = null, details: String? = null, theme: String? = null, username: String? = null): ApiResult<Boolean> {
        val payload = JSONObject().put("action", action).put("conversationId", conversationId)
        messageId?.let { payload.put("messageId", it) }
        emoji?.let { payload.put("emoji", it) }
        details?.let { payload.put("details", it).put("reason", "other") }
        theme?.let { payload.put("theme", it) }
        username?.let { payload.put("username", it) }
        val result = request("/api/chat/action", "POST", payload)
        return ApiResult(result.value != null, result.message, result.status)
    }

    suspend fun pollCall(afterSignalId: Long = 0, conversationId: Long? = null): ApiResult<CallEnvelope> {
        val query = buildList {
            if (afterSignalId > 0) add("after=$afterSignalId")
            if (conversationId != null) add("conversationId=$conversationId")
        }.joinToString("&")
        val result = request("/api/chat/call${if (query.isBlank()) "" else "?$query"}")
        val json = result.value ?: return ApiResult(message = result.message, status = result.status)
        return ApiResult(CallEnvelope(json.optJSONObject("call")?.toCall()), result.message, result.status)
    }

    suspend fun callAction(
        action: String,
        callId: Long? = null,
        conversationId: Long? = null,
        mode: String? = null,
        targetUserId: Long? = null,
        signalType: String? = null,
        signalPayload: JSONObject? = null,
    ): ApiResult<Boolean> {
        val payload = JSONObject().put("action", action)
        callId?.let { payload.put("callId", it) }
        conversationId?.let { payload.put("conversationId", it) }
        mode?.let { payload.put("mode", it) }
        targetUserId?.let { payload.put("targetUserId", it) }
        signalType?.let { payload.put("type", it) }
        signalPayload?.let { payload.put("payload", it) }
        val result = request("/api/chat/call", "POST", payload)
        return ApiResult(result.value != null, result.message, result.status)
    }

    suspend fun markRead(conversationId: Long) { request("/api/chat/read", "POST", JSONObject().put("conversationId", conversationId)) }

    suspend fun members(query: String): ApiResult<List<Profile>> {
        val result = request("/api/community/members?q=${java.net.URLEncoder.encode(query, "UTF-8")}")
        return ApiResult(result.value?.optJSONArray("members").toList { it.toProfile() }, result.message, result.status)
    }

    suspend fun social(action: String, username: String, friendshipId: Long? = null): ApiResult<Boolean> {
        val payload = JSONObject().put("action", action).put("username", username)
        if (friendshipId != null) payload.put("friendshipId", friendshipId)
        val result = request("/api/social/action", "POST", payload)
        return ApiResult(result.value != null, result.message, result.status)
    }

    suspend fun forum(): ApiResult<List<ForumThread>> {
        val result = request("/api/forum/feed")
        return ApiResult(result.value?.optJSONArray("threads").toList { it.toForumThread() }, result.message, result.status)
    }

    suspend fun registerPushToken(token: String) {
        request("/api/mobile/push/register", "POST", JSONObject().put("token", token).put("platform", "android"))
    }

    suspend fun latestUpdate(): ApiResult<AppUpdate> {
        val result = request("https://raw.githubusercontent.com/jakubkuzminski762-cmd/darkwave-community/main/android-latest.json")
        val json = result.value ?: return ApiResult(message = result.message, status = result.status)
        val versionCode = json.optInt("versionCode")
        val apkUrl = json.nullableString("apkUrl")
        if (versionCode <= 0 || apkUrl == null) return ApiResult(message = "Invalid update manifest", status = result.status)
        return ApiResult(
            value = AppUpdate(
                versionCode = versionCode,
                versionName = json.nullableString("versionName") ?: versionCode.toString(),
                apkUrl = apkUrl,
                sha256 = json.nullableString("sha256") ?: "",
                notesEn = json.nullableString("notesEn") ?: "A new Darkwave Community build is ready.",
                notesPl = json.nullableString("notesPl") ?: "Nowa wersja Darkwave Community jest gotowa.",
                required = json.optBoolean("required"),
            ),
            status = result.status,
        )
    }
}

private fun JSONObject?.toProfile(): Profile {
    if (this == null) return Profile("Unknown")
    val progression = optJSONObject("progression")
    return Profile(
        username = optString("username", "Unknown"), email = nullableString("email") ?: "", tag = nullableString("tag") ?: "", role = optString("role", "member"),
        avatarUrl = nullableString("avatarUrl")?.let { if (it.startsWith("http")) it else "https://veloryx.pl${if (it.startsWith("/")) it else "/$it"}" },
        isOnline = optBoolean("isOnline"), presenceMode = optString("presenceMode", "auto"),
        lastActiveAt = nullableString("lastActiveAt"), statusMessage = nullableString("statusMessage"),
        level = progression?.optInt("level", 1) ?: 1, totalXp = progression?.optInt("totalXp", 0) ?: 0,
        relationship = optString("relationship", "none"),
        friendshipId = if (has("friendshipId") && !isNull("friendshipId")) optLong("friendshipId") else null,
        memberCode = optString("memberCode"),
    )
}

private fun JSONObject.toConversation() = Conversation(
    id = optLong("id"), kind = optString("kind", "direct"), title = nullableString("title"),
    friend = optJSONObject("friend")?.toProfile(), lastMessage = nullableString("lastMessage"),
    lastMessageAt = nullableString("lastMessageAt"), unreadCount = optInt("unreadCount"),
    muted = optBoolean("muted"), restricted = optBoolean("restricted"), theme = optString("theme", "nocturne"),
    ownerUsername = nullableString("ownerUsername"),
    participants = optJSONArray("participants").toList { it.toProfile() },
)

private fun JSONObject.toMessage() = ChatMessage(
    id = optLong("id"), mine = optBoolean("mine"), sender = optJSONObject("sender")?.toProfile(),
    body = nullableString("body"), createdAt = optString("createdAt"),
    deliveredAt = nullableString("deliveredAt"), readAt = nullableString("readAt"), recalledAt = nullableString("recalledAt"),
    attachment = optJSONObject("attachment")?.let { item -> Attachment(item.optLong("id"), item.nullableString("kind") ?: "file", item.nullableString("name") ?: "attachment", item.nullableString("mime") ?: "application/octet-stream", item.optLong("size"), item.nullableString("url") ?: "") },
    reactions = optJSONArray("reactions").toList { item -> ChatReaction(item.optString("emoji"), item.optInt("count"), item.optBoolean("mine"), item.optJSONArray("users").toList { it.toProfile() }) },
)

private fun JSONObject.toForumThread() = ForumThread(
    id = optLong("id"), category = optString("category"), titleEn = optString("titleEn"), titlePl = optString("titlePl"),
    bodyEn = optString("bodyEn"), bodyPl = optString("bodyPl"), pinned = optBoolean("isPinned"), locked = optBoolean("isLocked"),
    author = optString("author"), createdAt = optString("createdAt"),
)

private fun JSONObject.toCall(): ChatCall = ChatCall(
    id = optLong("id"),
    conversationId = optLong("conversationId"),
    conversationTitle = nullableString("conversationTitle") ?: "Private channel",
    mode = optString("mode", "audio"),
    status = optString("status", "ringing"),
    initiatorUserId = optLong("initiatorUserId"),
    viewerUserId = optLong("viewerUserId"),
    createdAt = nullableString("createdAt") ?: "",
    answeredAt = nullableString("answeredAt"),
    endedAt = nullableString("endedAt"),
    participants = optJSONArray("participants").toList { item ->
        CallParticipant(
            userId = item.optLong("userId"),
            username = item.nullableString("username") ?: "Participant",
            avatarUrl = item.nullableString("avatarUrl")?.let { if (it.startsWith("http")) it else "https://veloryx.pl${if (it.startsWith("/")) it else "/$it"}" },
            status = item.optString("status", "ringing"),
        )
    },
    signals = optJSONArray("signals").toList { item ->
        CallSignal(
            id = item.optLong("id"),
            fromUserId = item.optLong("fromUserId"),
            type = item.optString("type"),
            payload = item.optJSONObject("payload") ?: JSONObject(),
        )
    },
    iceServers = optJSONArray("iceServers").toList { item ->
        val urlsValue = item.opt("urls")
        val urls = when (urlsValue) {
            is JSONArray -> buildList { for (index in 0 until urlsValue.length()) urlsValue.optString(index).takeIf { it.isNotBlank() }?.let(::add) }
            is String -> listOf(urlsValue).filter { it.isNotBlank() }
            else -> emptyList()
        }
        CallIceServer(urls, item.nullableString("username") ?: "", item.nullableString("credential") ?: "")
    }.filter { it.urls.isNotEmpty() },
)

private inline fun <T> JSONArray?.toList(transform: (JSONObject) -> T): List<T> {
    if (this == null) return emptyList()
    return buildList { for (index in 0 until length()) optJSONObject(index)?.let { add(transform(it)) } }
}

private fun JSONObject.nullableString(key: String): String? {
    if (!has(key) || isNull(key)) return null
    return optString(key).trim().takeIf { it.isNotEmpty() && !it.equals("null", ignoreCase = true) }
}
