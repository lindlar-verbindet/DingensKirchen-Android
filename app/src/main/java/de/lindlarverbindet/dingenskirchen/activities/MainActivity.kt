package de.lindlarverbindet.dingenskirchen.activities

import android.R.attr
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
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
import android.R.attr.data
import de.lindlarverbindet.dingenskirchen.helper.TipHelper
import de.lindlarverbindet.dingenskirchen.models.Tip
import android.R.attr.data
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import de.lindlarverbindet.dingenskirchen.fragments.TipDialogFragment


class MainActivity : AppCompatActivity() {

    private lateinit var tipWidget      : View
    private lateinit var newsWidget     : View
    private lateinit var eventWidget    : View
    private lateinit var councilWidget  : View
    private lateinit var mobilWidget    : View
    private lateinit var villageWidget  : View

    private val wpHelper = WordpressHelper()
    private var tips: List<Tip>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tipWidget = findViewById(R.id.main_tip_widget)
        newsWidget = findViewById(R.id.main_news_widget)
        eventWidget = findViewById(R.id.main_events_widget)
        councilWidget = findViewById(R.id.main_council_widget)
        mobilWidget = findViewById(R.id.main_mobil_widget)
        villageWidget = findViewById(R.id.main_village_widget)

        tipWidget.setOnClickListener {
            runOnUiThread {
                if (tips != null) {
                    val currentDay = getNumberOfDay()
                    val index = currentDay % tips!!.count()
                    val todaysTip = tips!![index]

                    val tipFragment = TipDialogFragment()
                    val bundle = Bundle()
                    bundle.putString("TITLE", todaysTip.title)
                    bundle.putString("CONTENT", todaysTip.content)
                    tipFragment.arguments = bundle
                    tipFragment.show(this.supportFragmentManager, TipDialogFragment.TAG)
                } else {
                    Toast.makeText(
                        this,
                        R.string.main_activity_no_tip,
                        Toast.LENGTH_LONG).show()
                }
            }
        }

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
        getTips()
    }

    private fun getTips() {
        GlobalScope.launch {
            tips = TipHelper().getTips()
            Log.d("TIPS:", tips.toString())
        }
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

    private fun getNumberOfDay(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.DAY_OF_YEAR)
    }
}