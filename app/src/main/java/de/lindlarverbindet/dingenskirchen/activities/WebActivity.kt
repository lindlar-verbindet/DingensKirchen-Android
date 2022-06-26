package de.lindlarverbindet.dingenskirchen.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginBottom
import com.google.android.material.snackbar.BaseTransientBottomBar
import de.lindlarverbindet.dingenskirchen.R

class WebActivity : AppCompatActivity() {

    private lateinit var urlString: String
    private lateinit var parentActivityString: String
    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        parentActivityString = intent.getStringExtra("parent") ?: ""

        webView = findViewById(R.id.web_view)

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
                val url = request?.url.toString()
                return if (url.contains("mailto:")) {
                    webView.stopLoading()
                    val mail = url.replace("mailto:", "")
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.type = "text/plain"
                    intent.data = Uri.parse(url)
                    intent.putExtra(Intent.EXTRA_EMAIL, mail)

                    startActivity(intent)

                    false
                } else {
                    webView.loadUrl(request?.url.toString())
                    true
                }
            }
        }

        if (parentActivityString == "VillageActivity") {
            val param = webView.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(0,0,0,0)
            webView.layoutParams = param
        }

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.setSupportZoom(true)

        urlString = intent.getStringExtra("url") ?: ""
        if (urlString != "" && !urlString.contains("mailto:")) {
            webView.loadUrl(urlString)
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
            return
        }
        super.onBackPressed()
    }

    private fun siteLoaded() {
        this.supportActionBar?.title = webView.title
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
            "VillageActivity" -> {
                val intent = Intent(applicationContext, VillageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                intent
            }
            "MainActivity" -> {
                val intent = Intent(applicationContext, MainActivity::class.java)
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