package pl.veloryx.darkwave

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class PortalActivity : ComponentActivity() {
    private lateinit var webView: WebView
    private var loaded = false
    private val permissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { loadPortal() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        setContentView(webView)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() { if (webView.canGoBack()) webView.goBack() else finish() }
        })
        val missing = listOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA).filter { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
        if (missing.isEmpty()) loadPortal() else permissions.launch(missing.toTypedArray())
    }

    private fun loadPortal() {
        if (loaded) return
        loaded = true
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        getSharedPreferences("darkwave-cookies", MODE_PRIVATE).all.values.forEach { raw ->
            (raw as? String)?.let { cookieManager.setCookie("https://veloryx.pl", it) }
        }
        cookieManager.flush()
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.settings.userAgentString = webView.settings.userAgentString + " DarkwaveAndroid/0.2"
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) = runOnUiThread { request.grant(request.resources) }
        }
        webView.loadUrl(intent.getStringExtra("url")?.takeIf { it.startsWith("https://veloryx.pl/") } ?: "https://veloryx.pl/wiadomosci")
    }

    override fun onDestroy() {
        webView.stopLoading(); webView.destroy()
        super.onDestroy()
    }
}
