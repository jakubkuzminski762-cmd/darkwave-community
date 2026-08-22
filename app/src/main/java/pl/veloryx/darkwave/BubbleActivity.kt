package pl.veloryx.darkwave

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

class BubbleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val conversationId = intent.getLongExtra("conversationId", 0L)
        val username = intent.getStringExtra("username") ?: "PRIVATE CHANNEL"
        setContent { DarkwaveTheme { BubbleContent(username) {
            startActivity(Intent(this, MainActivity::class.java).putExtra("conversationId", conversationId))
            finish()
        } } }
    }
}

@Composable
private fun BubbleContent(username: String, open: () -> Unit) {
    Column(
        Modifier.fillMaxSize().background(Ink).padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("DARKWAVE / LIVE", color = SignalGold, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(18.dp))
        Text(username, color = Bone, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(24.dp))
        Button(onClick = open) { Text("OPEN PRIVATE CHANNEL") }
    }
}
