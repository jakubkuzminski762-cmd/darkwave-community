package pl.veloryx.darkwave

data class Profile(
    val username: String,
    val email: String = "",
    val tag: String = "",
    val role: String = "member",
    val avatarUrl: String? = null,
    val isOnline: Boolean = false,
    val statusMessage: String? = null,
    val level: Int = 1,
    val totalXp: Int = 0,
    val relationship: String = "none",
    val friendshipId: Long? = null,
    val memberCode: String = "",
)

data class Conversation(
    val id: Long,
    val kind: String,
    val title: String?,
    val friend: Profile?,
    val lastMessage: String?,
    val lastMessageAt: String?,
    val unreadCount: Int,
)

data class ChatMessage(
    val id: Long,
    val mine: Boolean,
    val sender: Profile?,
    val body: String?,
    val createdAt: String,
    val deliveredAt: String?,
    val readAt: String?,
    val recalledAt: String?,
)

data class FriendRequest(
    val id: Long,
    val direction: String,
    val user: Profile,
)

data class ForumThread(
    val id: Long,
    val category: String,
    val titleEn: String,
    val titlePl: String,
    val bodyEn: String,
    val bodyPl: String,
    val pinned: Boolean,
    val locked: Boolean,
    val author: String,
    val createdAt: String,
)

data class Captcha(val id: String, val question: String)
data class Inbox(val conversations: List<Conversation>, val requests: List<FriendRequest>)
data class ApiResult<T>(val value: T? = null, val message: String? = null, val status: Int = 0) {
    val ok: Boolean get() = value != null && status in 200..299
}
