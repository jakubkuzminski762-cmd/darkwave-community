package pl.veloryx.darkwave

data class Profile(
    val username: String,
    val email: String = "",
    val tag: String = "",
    val role: String = "member",
    val avatarUrl: String? = null,
    val isOnline: Boolean = false,
    val presenceMode: String = "auto",
    val lastActiveAt: String? = null,
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
    val muted: Boolean = false,
    val restricted: Boolean = false,
    val theme: String = "nocturne",
    val ownerUsername: String? = null,
    val participants: List<Profile> = emptyList(),
)

data class Attachment(
    val id: Long,
    val kind: String,
    val name: String,
    val mime: String,
    val size: Long,
    val url: String,
)

data class ChatReaction(
    val emoji: String,
    val count: Int,
    val mine: Boolean,
    val users: List<Profile>,
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
    val attachment: Attachment? = null,
    val reactions: List<ChatReaction> = emptyList(),
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
