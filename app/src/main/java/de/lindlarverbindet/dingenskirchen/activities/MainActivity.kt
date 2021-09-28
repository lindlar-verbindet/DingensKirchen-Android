package de.lindlarverbindet.dingenskirchen.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.text.HtmlCompat
import de.lindlarverbindet.dingenskirchen.R
import de.lindlarverbindet.dingenskirchen.helper.RSSHelper
import de.lindlarverbindet.dingenskirchen.helper.WordpressHelper
import de.lindlarverbindet.dingenskirchen.models.WPEvent
import de.lindlarverbindet.dingenskirchen.models.News
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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

        newsWidget.setOnClickListener {
            runOnUiThread {
                val intent = Intent(applicationContext, NewsActivity::class.java)
                startActivity(intent)
            }
        }

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
        getLatestAppointment()
    }

    private fun getLatestNews() {
        GlobalScope.launch {
            val recentPosts: ArrayList<News> = wpHelper.getRecentPosts()
            recentPosts.addAll(RSSHelper().getRecentPosts())

            recentPosts.sortByDescending { it.date }

            runOnUiThread {
                populateNewsWidget(recentPosts.firstOrNull())
            }
        }
    }

    private fun getLatestAppointment() {
        GlobalScope.launch {
            val recentEvents = wpHelper.getRecentEvents()
            Log.d("APP", recentEvents.joinToString { "${it.title} | ${it.desc} | ${it.link}"} )
            runOnUiThread {
                populateEventWidget(recentEvents.firstOrNull())
            }
        }
    }

    private fun populateNewsWidget(post: News?) {
        val titleView:TextView      = newsWidget.findViewById(R.id.news_heading)
        val newsDateView: TextView  = newsWidget.findViewById(R.id.news_date)
        val newsDescView: TextView  = newsWidget.findViewById(R.id.news_preview)

        if (post == null) {
            titleView.text = getString(R.string.warning_no_events)
            newsDateView.text = ""
            newsDescView.text = ""
            return
        }
//        newsWidget.setOnClickListener {
//            val webpage = Uri.parse(post.link)
//            val intent = Intent(Intent.ACTION_VIEW, webpage)
//            startActivity(intent)
//        }

        val dateFormatter = SimpleDateFormat("dd.MM hh:mm", Locale.GERMAN)
        val previewText: String = HtmlCompat.fromHtml(post.content, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()

        titleView.text      = post.title
        newsDateView.text   = dateFormatter.format(post.date)
        newsDescView.text   = cutoffIfNeeded(previewText, 117)
    }

    private fun populateEventWidget(event: WPEvent?) {
        val titleView: TextView = eventWidget.findViewById(R.id.events_event_title)
        val dateView: TextView = eventWidget.findViewById(R.id.events_event_date)
        val descView: TextView = eventWidget.findViewById(R.id.events_event_desc)

        if (event == null) {
            titleView.text = getString(R.string.warning_no_events)
            dateView.text = ""
            descView.text = ""
            return
        }

        val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN)
        val descText = HtmlCompat.fromHtml(event.desc, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()

        titleView.text = event.title
        dateView.text = dateFormatter.format(event.date)
        descView.text = cutoffIfNeeded(descText, 50).replace("\n", "")
    }

    private fun cutoffIfNeeded(text: String, maxChars: Int): String {
        return if (text.length >= maxChars) {
            text.substring(0, maxChars) + "..."
        } else {
            text
        }
    }
}