package de.lindlarverbindet.dingenskirchen.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.text.HtmlCompat
import de.lindlarverbindet.dingenskirchen.R
import de.lindlarverbindet.dingenskirchen.helper.WordpressHelper
import de.lindlarverbindet.dingenskirchen.models.WPPost
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var newsWidget     : View
    private lateinit var eventWidget    : View
    private lateinit var councilWidget  : View
    private lateinit var mobilWidget    : View
    private lateinit var villageWidget  : View

    private val wpHelper = WordpressHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        newsWidget = findViewById(R.id.main_news_widget)
        eventWidget = findViewById(R.id.main_events_widget)
        councilWidget = findViewById(R.id.main_council_widget)
        mobilWidget = findViewById(R.id.main_mobil_widget)
        villageWidget = findViewById(R.id.main_village_widget)

        eventWidget.setOnClickListener {
            runOnUiThread {
                val intent = Intent(applicationContext, EventActivity::class.java)
                startActivity(intent)
            }
        }

        councilWidget.setOnClickListener {
            runOnUiThread {
                val intent = Intent(applicationContext, CouncilActivity::class.java)
                startActivity(intent)
            }
        }

        mobilWidget.setOnClickListener {
            runOnUiThread{
                val intent = Intent(applicationContext, MapActivity::class.java)
                startActivity(intent)
            }
        }

        villageWidget.setOnClickListener {
            runOnUiThread {
                val intent = Intent(applicationContext, VillageActivity::class.java)
                startActivity(intent)
            }
        }

        getLatestNews()
    }

    private fun getLatestNews() {
        GlobalScope.launch {
            val recentPost = wpHelper.getRecentPosts().first()
            runOnUiThread {
                populateNewsWidget(recentPost)
            }
        }
    }

    private fun populateNewsWidget(post: WPPost) {
        newsWidget.setOnClickListener {
            val webpage = Uri.parse(post.link)
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            startActivity(intent)
        }

        val titleView:TextView      = newsWidget.findViewById(R.id.news_heading)
        val newsDateView: TextView  = newsWidget.findViewById(R.id.news_date)
        val newsDescView: TextView  = newsWidget.findViewById(R.id.news_preview)

        val dateFormatter = SimpleDateFormat("dd.MM hh:mm", Locale.GERMAN)
        val previewText: String = HtmlCompat.fromHtml(post.content, HtmlCompat.FROM_HTML_MODE_LEGACY).toString().substring(0, 120) + "..."

        titleView.text      = post.title
        newsDateView.text   = dateFormatter.format(post.date)
        newsDescView.text   = previewText
    }
}