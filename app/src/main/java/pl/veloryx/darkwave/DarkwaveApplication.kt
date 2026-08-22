package pl.veloryx.darkwave

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class DarkwaveApplication : Application() {
    lateinit var api: ApiClient
        private set
    lateinit var calls: NativeCallManager
        private set

    override fun onCreate() {
        super.onCreate()
        api = ApiClient(this)
        calls = NativeCallManager(this, api)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(
                NotificationChannel(
                    DarkwaveMessagingService.CHANNEL_MESSAGES,
                    getString(R.string.notification_channel_messages),
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply {
                    description = "Darkwave Community private channel alerts"
                    enableVibration(true)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) setAllowBubbles(true)
                },
            )
        }
    }
}
