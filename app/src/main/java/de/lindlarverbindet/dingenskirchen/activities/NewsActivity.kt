package de.lindlarverbindet.dingenskirchen.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.lindlarverbindet.dingenskirchen.R
import de.lindlarverbindet.dingenskirchen.helper.RSSHelper
import de.lindlarverbindet.dingenskirchen.helper.WordpressHelper
import de.lindlarverbindet.dingenskirchen.models.News
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.graphics.BitmapFactory

import android.graphics.Bitmap
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import java.net.URL


class NewsActivity : AppCompatActivity() {

    private val wpHelper = WordpressHelper()

    private lateinit var tableLayout: TableLayout
    private lateinit var refreshView: SwipeRefreshLayout

    private lateinit var recentNews: ArrayList<News>

    override fun onCreate(savedInstanceState: Bundle?) {

        recentNews = intent.getSerializableExtra("NEWS") as ArrayList<News>

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        this.supportActionBar?.title = getString(R.string.news_navigation_title)

        tableLayout = findViewById(R.id.news_table)
        refreshView = findViewById(R.id.news_refresh)
        refreshView.setOnRefreshListener {
            getLatestNews()
        }

        configureTableRows(recentNews)
    }

    private fun getLatestNews() {
        lifecycleScope.launch(Dispatchers.IO) {
            val recentNews = wpHelper.getRecentPosts()
            val posts = RSSHelper().getRecentPosts()
            if (!posts.isNullOrEmpty()) {
                recentNews.addAll(posts)
            }


            recentNews.sortByDescending { it.date }
            Log.d("APP", recentNews.joinToString { "${it.title} | ${it.content} | ${it.link}"} )
            runOnUiThread {
                tableLayout.removeAllViews()
                configureTableRows(recentNews)
                refreshView.isRefreshing = false
            }
        }
    }

    private fun configureTableRows(data: List<News>) {
        var backgroundGreen = true
        for (element in data) {
            Log.d("ELEMENT", element.title + " " + element.content + " " + element.link)

            val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN)
            val row = View.inflate(this, R.layout.news_layout, null) as ConstraintLayout
            if (backgroundGreen) {
                row.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primaryHighlight)
            } else {
                row.backgroundTintList = ContextCompat.getColorStateList(this, R.color.secondaryHighlight)
            }
            backgroundGreen = !backgroundGreen
            // load subviews
            val layout = row.findViewById<View>(R.id.news_layout)
            val imageView = row.findViewById<ImageView>(R.id.news_image)
            val dateView = row.findViewById<TextView>(R.id.news_date)
            val titleView = row.findViewById<TextView>(R.id.news_title)
            val descView = row.findViewById<TextView>(R.id.news_desc)
            // configure subviews
            if (element.imageURL != null) {
                lifecycleScope.launch(Dispatchers.IO) {
                    val url = URL(element.imageURL)
                    val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())

                    runOnUiThread {
                        layout.clipToOutline = true
                        imageView.setImageBitmap(bmp)
                        imageView.visibility = View.VISIBLE
                    }
                }
            }
            dateView.text = dateFormatter.format(element.date)
            titleView.text = element.title
            descView.text = HtmlCompat.fromHtml(element.content, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
            // Set Margin for dynamic row
            val rowParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT)
            rowParams.setMargins(20, 20, 20, 60)
            row.layoutParams = rowParams

            row.setOnClickListener {
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