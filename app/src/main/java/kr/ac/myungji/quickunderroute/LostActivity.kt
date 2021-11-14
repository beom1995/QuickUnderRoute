package kr.ac.myungji.quickunderroute

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class LostActivity : AppCompatActivity() {
    private lateinit var webView: WebView

    // 유실물센터 메뉴 클릭 시
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lost)

        webView = findViewById(R.id.webView)        // html로 UI를 구현하기 위해서 사용

        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        webView.settings.allowContentAccess = true
        webView.settings.setAppCacheEnabled(true)
        webView.settings.domStorageEnabled = true
        webView.settings.useWideViewPort = true

        webView.loadUrl("https://m.lost112.go.kr/")
    }
}