package de.lindlarverbindet.dingenskirchen.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import de.lindlarverbindet.dingenskirchen.R

class WebActivity : AppCompatActivity() {

    private lateinit var urlString: String
    private lateinit var parentActivityString: String

    private lateinit var webView: WebView
    private lateinit var backButton: ImageButton
    private lateinit var forwardButton: ImageButton

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        parentActivityString = intent.getStringExtra("parent") ?: ""

        webView = findViewById(R.id.web_view)
        backButton = findViewById(R.id.web_back)
        forwardButton = findViewById(R.id.web_forward)

        webView.settings.javaScriptEnabled = true

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress == 100) {
                    siteLoaded()
                }
            }
        }

        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?,
                                                  request: WebResourceRequest?): Boolean {
                webView.loadUrl(request?.url.toString())
                return true
            }
        }

        urlString = intent.getStringExtra("url") ?: ""
        if (urlString != "") {
            webView.loadUrl(urlString)
        }

        backButton.setOnClickListener {
            goBack()
        }

        forwardButton.setOnClickListener {
            goForward()
        }
    }

    private fun siteLoaded() {
        backButton.isEnabled = webView.canGoBack()
        forwardButton.isEnabled = webView.canGoForward()
        this.supportActionBar?.title = webView.title
    }

    private fun goBack() {
        if (webView.canGoBack()) {
            webView.goBack()
        }
    }

    private fun goForward() {
        if (webView.canGoForward()) {
            webView.goForward()
        }
    }

    private fun getParentActivityIntentImplement(): Intent {
        return when(parentActivityString) {
            "EventActivity" -> {
                val intent = Intent(applicationContext, EventActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                intent
            }
            "CouncilActivity" -> {
                val intent = Intent(applicationContext, CouncilActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                intent
            }
            else ->  {
                val intent = Intent(applicationContext, NewsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                intent
            }
        }
    }

    override fun getParentActivityIntent(): Intent {
        return getParentActivityIntentImplement()
    }

    override fun getSupportParentActivityIntent(): Intent {
        return getParentActivityIntentImplement()
    }
}