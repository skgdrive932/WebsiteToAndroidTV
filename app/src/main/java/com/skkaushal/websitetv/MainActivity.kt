package com.skkaushal.websitetv

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.CookieManager
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.net.http.SslError
import android.widget.FrameLayout
import android.widget.TextView
import android.graphics.Color
import android.view.Gravity
import android.view.WindowManager
import java.net.URI

class MainActivity : Activity() {
    private lateinit var webView: WebView
    private lateinit var status: TextView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        )

        val root = FrameLayout(this)
        webView = WebView(this)
        status = TextView(this).apply {
            setTextColor(Color.WHITE)
            setBackgroundColor(0xCC202124.toInt())
            textSize = 16f
            gravity = Gravity.CENTER
            text = "Website load nahi ho rahi. Internet aur URL check karein."
            visibility = View.GONE
        }
        root.addView(webView, FrameLayout.LayoutParams(-1, -1))
        root.addView(status, FrameLayout.LayoutParams(-1, -2, Gravity.BOTTOM))
        setContentView(root)

        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.loadsImagesAutomatically = true
        settings.mediaPlaybackRequiresUserGesture = false
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true
        settings.builtInZoomControls = false
        settings.displayZoomControls = false
        settings.cacheMode = WebSettings.LOAD_DEFAULT
        CookieManager.getInstance().setAcceptCookie(true)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)

        webView.isFocusable = true
        webView.isFocusableInTouchMode = true
        webView.requestFocus()
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                status.visibility = View.GONE
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                if (request?.isForMainFrame == true) {
                    status.text = "Page load error. Internet connection check karein aur Retry ke liye OK dabayein."
                    status.visibility = View.VISIBLE
                }
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                handler?.cancel() // Never bypass certificate errors.
                status.text = "Secure connection error: website certificate valid nahi hai."
                status.visibility = View.VISIBLE
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: return true
                return !(url.startsWith("https://") || url.startsWith("http://"))
            }
        }

        val url = normalizeUrl(BuildConfig.WEBSITE_URL)
        if (url == null) {
            status.text = "Invalid website URL. GitHub Actions mein valid https:// URL dein."
            status.visibility = View.VISIBLE
        } else {
            webView.loadUrl(url)
        }
    }

    private fun normalizeUrl(raw: String): String? = try {
        val candidate = raw.trim()
        val withScheme = if (candidate.startsWith("https://") || candidate.startsWith("http://")) candidate else "https://$candidate"
        val uri = URI(withScheme)
        if ((uri.scheme == "https" || uri.scheme == "http") && !uri.host.isNullOrBlank()) withScheme else null
    } catch (_: Exception) { null }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                    if (status.visibility == View.VISIBLE) {
                        status.visibility = View.GONE
                        webView.reload()
                        return true
                    }
                }
                KeyEvent.KEYCODE_BACK -> {
                    if (::webView.isInitialized && webView.canGoBack()) {
                        webView.goBack()
                        return true
                    }
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }

    override fun onDestroy() {
        if (::webView.isInitialized) {
            webView.stopLoading()
            webView.destroy()
        }
        super.onDestroy()
    }
}
