package de.lindlarverbindet.dingenskirchen.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TableLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import de.lindlarverbindet.dingenskirchen.R
import de.lindlarverbindet.dingenskirchen.helper.RSSHelper
import de.lindlarverbindet.dingenskirchen.helper.WordpressHelper
import de.lindlarverbindet.dingenskirchen.models.News
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NewsActivity : AppCompatActivity() {

    private val wpHelper = WordpressHelper()
    private lateinit var tableLayout: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        tableLayout = findViewById(R.id.news_table)
        getLatestNews()
    }

    private fun getLatestNews() {
        GlobalScope.launch {
            val recentNews = wpHelper.getRecentPosts()
            recentNews.addAll(RSSHelper().getRecentPosts())

            recentNews.sortByDescending { it.date }
            Log.d("APP", recentNews.joinToString { "${it.title} | ${it.content} | ${it.link}"} )
            runOnUiThread {
                configureTableRows(recentNews)
            }
        }
    }

    private fun configureTableRows(data: List<News>) {
        var backgroundGreen = false
        for (element in data) {
            Log.d("ELEMENT", element.title + " " + element.content + " " + element.link)

            val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN)
            val row = View.inflate(this, R.layout.news_layout, null) as ConstraintLayout
            if (backgroundGreen) {
                row.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primaryHighlight)
            } else {
                row.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primaryBackground)
            }
            backgroundGreen = !backgroundGreen
            // load subviews
            val dateView = row.findViewById<TextView>(R.id.news_date)
            val titleView = row.findViewById<TextView>(R.id.news_title)
            val descView = row.findViewById<TextView>(R.id.news_desc)
            // configure subviews
            dateView.text = "${dateFormatter.format(element.date)}"
            titleView.text = element.title
            descView.text = HtmlCompat.fromHtml(element.content, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
            // Set Margin for dynamic row
            val rowParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT)
            rowParams.setMargins(20, 20, 20, 0)
            row.layoutParams = rowParams

            row.setOnClickListener {
                val webpage = Uri.parse(element.link)
//                val intent = Intent(Intent.ACTION_VIEW, webpage)
                val intent = Intent(this, WebActivity::class.java)
                intent.putExtra("url", element.link)
                intent.putExtra("parent", "NewsActivity")
                startActivity(intent)
            }
            // add row to table
            tableLayout.addView(row)
        }
    }
}