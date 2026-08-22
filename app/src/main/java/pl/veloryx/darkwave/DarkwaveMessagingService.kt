package pl.veloryx.darkwave

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class DarkwaveMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        getSharedPreferences("darkwave-push", MODE_PRIVATE).edit().putString("pending-token", token).apply()
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.data["title"] ?: message.notification?.title ?: "Darkwave Community"
        val body = message.data["body"] ?: message.notification?.body ?: "New signal received."
        val conversationId = message.data["conversationId"]?.toLongOrNull() ?: 0L
        val username = message.data["username"] ?: title
        showMessageNotification(conversationId, username, title, body)
    }

    private fun showMessageNotification(conversationId: Long, username: String, title: String, body: String) {
        val shortcutId = "conversation-$conversationId"
        val person = Person.Builder().setName(username).setKey(username).build()
        val openIntent = Intent(this, MainActivity::class.java)
            .putExtra("conversationId", conversationId)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingOpen = PendingIntent.getActivity(
            this, conversationId.toInt(), openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val shortcut = ShortcutInfoCompat.Builder(this, shortcutId)
            .setLongLived(true)
            .setShortLabel(username.take(20))
            .setIcon(IconCompat.createWithResource(this, R.drawable.app_icon_vector))
            .setPerson(person)
            .setIntent(openIntent.setAction(Intent.ACTION_VIEW))
            .build()
        ShortcutManagerCompat.pushDynamicShortcut(this, shortcut)

        val builder = NotificationCompat.Builder(this, CHANNEL_MESSAGES)
            .setSmallIcon(R.drawable.ic_notification)
            .setColor(getColor(R.color.darkwave_red))
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.MessagingStyle(person).addMessage(body, System.currentTimeMillis(), person))
            .setContentIntent(pendingOpen)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setShortcutId(shortcutId)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val bubbleIntent = PendingIntent.getActivity(
                this, (conversationId + 200_000).toInt(),
                Intent(this, BubbleActivity::class.java).putExtra("conversationId", conversationId).putExtra("username", username),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE,
            )
            builder.setBubbleMetadata(
                NotificationCompat.BubbleMetadata.Builder(
                    bubbleIntent,
                    IconCompat.createWithResource(this, R.drawable.app_icon_vector),
                ).setDesiredHeight(640).setAutoExpandBubble(false).setSuppressNotification(false).build(),
            )
        }
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(conversationId.toInt(), builder.build())
    }

    companion object { const val CHANNEL_MESSAGES = "darkwave_messages" }
}
