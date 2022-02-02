package de.lindlarverbindet.dingenskirchen.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import de.lindlarverbindet.dingenskirchen.R
import de.lindlarverbindet.dingenskirchen.helper.RSSHelper
import de.lindlarverbindet.dingenskirchen.helper.WordpressHelper
import de.lindlarverbindet.dingenskirchen.models.Event
import de.lindlarverbindet.dingenskirchen.models.News
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import de.lindlarverbindet.dingenskirchen.helper.TipHelper
import de.lindlarverbindet.dingenskirchen.models.Tip
import android.widget.ImageView
import de.lindlarverbindet.dingenskirchen.fragments.TipDialogFragment
import de.lindlarverbindet.dingenskirchen.fragments.TutorialDialogFragment
import de.lindlarverbindet.dingenskirchen.helper.LindlarEventHelper


class MainActivity : AppCompatActivity() {

    private lateinit var tipWidget      : View
    private lateinit var newsWidget     : View
    private lateinit var eventWidget    : View
    private lateinit var councilWidget  : View
    private lateinit var mobilWidget    : View
    private lateinit var villageWidget  : View
    private lateinit var surveyWidget   : View
    private lateinit var imprintImageView: ImageView
    private lateinit var tutorialImageView: ImageView

    private lateinit var prefs: SharedPreferences

    private var recentPosts =  arrayListOf<News>()
    private var recentEvents = arrayListOf<Event>()

    private val wpHelper = WordpressHelper()
    private var tips: List<Tip>? = null

    private var handler: Handler = Handler(Looper.getMainLooper())
    private val animateTipWidget = object: Runnable {
        override fun run() {
            runOnUiThread {
                tipWidget.animate().apply {
                    duration = 333
                    rotationXBy(-10f)
                    scaleXBy(0.1f)
                    scaleYBy(0.1f)
                }.withEndAction {
                    tipWidget.animate().apply {
                        duration = 333
                        rotationXBy(10f)
                        scaleXBy(-0.1f)
                        scaleYBy(-0.1f)
                    }
                }
                handler.postDelayed(this, 10000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        prefs = getSharedPreferences("Dingenskirchen", MODE_PRIVATE)

        getLatestNews()
        getLatestAppointment()
        getTips()

        tipWidget = findViewById(R.id.main_tip_widget)
        newsWidget = findViewById(R.id.main_news_widget)
        eventWidget = findViewById(R.id.main_events_widget)
        councilWidget = findViewById(R.id.main_council_widget)
        mobilWidget = findViewById(R.id.main_mobil_widget)
        villageWidget = findViewById(R.id.main_village_widget)
        surveyWidget = findViewById(R.id.main_survey_widget)

        tutorialImageView = findViewById(R.id.main_tutorial)
        tutorialImageView.setOnClickListener { showTutorial() }

        imprintImageView = findViewById(R.id.main_imprint)

        setupClickListener()

        if (prefs.getBoolean("firstStart", true)) {
            prefs.edit().putBoolean("firstStart", false).apply()
            showTutorial()
        }
    }

    private fun setupClickListener() {
        imprintImageView.setOnClickListener {
            val intent = Intent(applicationContext, InfoActivity::class.java)
            startActivity(intent)
        }

        tipWidget.setOnClickListener {
            runOnUiThread {
                if (tips?.count() != 0) {
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
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        newsWidget.setOnClickListener {
            runOnUiThread {
                val intent = Intent(applicationContext, NewsActivity::class.java)
                intent.putExtra("NEWS", recentPosts)
                startActivity(intent)
            }
        }

        eventWidget.setOnClickListener {
            runOnUiThread {
                val intent = Intent(applicationContext, EventActivity::class.java)
                intent.putExtra("EVENTS", recentEvents)
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
            runOnUiThread {
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

        surveyWidget.setOnClickListener {
            runOnUiThread {
                val intent = Intent(applicationContext, WebActivity::class.java)
                intent.putExtra("url", "https://www.lindlar-verbindet.de/umfrage")
                intent.putExtra("parent", "MainActivity")
                startActivity(intent)
            }
        }
    }

    private fun showTutorial() = TutorialDialogFragment().show(this.supportFragmentManager, TutorialDialogFragment.TAG)

    override fun onResume() {
        handler.post(animateTipWidget)
        super.onResume()
    }

    override fun onPause() {
        handler.removeCallbacks(animateTipWidget)
        super.onPause()
    }

    private fun getTips() {
        GlobalScope.launch {
            tips = TipHelper().getTips()
            Log.d("TIPS:", tips.toString())
        }
    }

    private fun getLatestNews() {
        GlobalScope.launch {
            recentPosts = wpHelper.getRecentPosts()
            recentPosts.addAll(RSSHelper().getRecentPosts())
//            val currentDate = Date()
            recentPosts.sortByDescending { it.date }
//            recentPosts = recentPosts.filter { it.date > currentDate } as ArrayList<News>
            runOnUiThread {
                populateNewsWidget(recentPosts.firstOrNull())
            }
        }
    }

    private fun getLatestAppointment() {
        GlobalScope.launch {
            recentEvents = wpHelper.getRecentEvents()
            recentEvents += LindlarEventHelper.getRecentEvents()
            Log.d("APP", recentEvents.joinToString { "${it.title} | ${it.desc} | ${it.link}"} )
            runOnUiThread {
                recentEvents.sortBy { it.date }
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
        newsDescView.text   = cutoffIfNeeded(previewText, 100)
    }

    private fun populateEventWidget(event: Event?) {
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

        titleView.text = HtmlCompat.fromHtml(event.title, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
        dateView.text = dateFormatter.format(event.date)
        descView.text = cutoffIfNeeded(descText, 61).replace("\n", "")
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