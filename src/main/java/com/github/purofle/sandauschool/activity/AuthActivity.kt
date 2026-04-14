package com.github.purofle.sandauschool.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
import android.webkit.WebView
import android.webkit.WebView.setWebContentsDebuggingEnabled
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri

class AuthActivity : ComponentActivity() {

    lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                enableEdgeToEdge()
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            setWebContentsDebuggingEnabled(true)
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                mixedContentMode = MIXED_CONTENT_COMPATIBILITY_MODE
                            }

                            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

                            webChromeClient = WebChromeClient()
                            webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    url?.let { onMaybeTargetUrl(it) }
                                }
                            }
                            webView = this
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .imePadding()
                        .systemBarsPadding()
                        .navigationBarsPadding()
                ) { web ->
                    web.loadUrl(AUTH_URL)
                }
            }

            onBackPressedDispatcher.addCallback {
                if (::webView.isInitialized) {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        finish()
                    }
                }
            }
        }
    }

    private fun onMaybeTargetUrl(url: String) {
        val uri = url.toUri()
        val host = uri.host ?: return

        if (host.equals(TARGET_HOST, ignoreCase = true)) {
            Log.i(TAG, "onMaybeTargetUrl: $url")
            val cookieManager = CookieManager.getInstance()
            val rawCookie = cookieManager.getCookie(url)
            Log.d(TAG, "rawCookie($TARGET_HOST): $rawCookie")
            cookieManager.flush()
            // 跳转教务系统，不走跳转 URL 会导致无教务系统 SESSION 需重新登录
            webView.loadUrl(JUMP_URL)
        }

        if (host.equals(JXGL_HOST, ignoreCase = true)) {
            val cookieManager = CookieManager.getInstance()
            val rawCookie = cookieManager.getCookie(url)
            Log.d(TAG, "rawCookie($JXGL_HOST): $rawCookie")
            cookieManager.flush()
            // 这时实际已经获取到 Cookie，可以返回
            finish()
        }
    }

    companion object {
        const val AUTH_URL = "https://authserver.sandau.edu.cn/authserver/login?service=https://newehall.sandau.edu.cn/"
        const val TARGET_HOST = "newehall.sandau.edu.cn"
        const val JUMP_URL = "https://newehall.sandau.edu.cn/appShow?appId=7328727903036396"
        // JXGL=教学管理
        const val JXGL_HOST = "jxgl.sandau.edu.cn"
        const val TAG = "AuthActivity"
    }
}
