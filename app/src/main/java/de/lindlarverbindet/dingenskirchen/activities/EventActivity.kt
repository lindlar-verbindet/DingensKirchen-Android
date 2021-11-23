package de.lindlarverbindet.dingenskirchen.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.lindlarverbindet.dingenskirchen.R
import de.lindlarverbindet.dingenskirchen.helper.WordpressHelper
import de.lindlarverbindet.dingenskirchen.models.WPEvent
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EventActivity : AppCompatActivity(){

    private val wpHelper = WordpressHelper()

    private lateinit var tableLayout: TableLayout
    private lateinit var refreshView: SwipeRefreshLayout

    private lateinit var recentEvents: ArrayList<WPEvent>

    override fun onCreate(savedInstanceState: Bundle?) {

        recentEvents = intent.getSerializableExtra("EVENTS") as ArrayList<WPEvent>

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)

        this.supportActionBar?.title = getString(R.string.event_navigation_title)

        tableLayout = findViewById(R.id.event_table)
        refreshView = findViewById(R.id.event_refresh)

        refreshView.setOnRefreshListener {
            getLatestAppointments()
        }

        configureTableRows(recentEvents)
    }

    private fun getLatestAppointments() {
        GlobalScope.launch {
            recentEvents = wpHelper.getRecentEvents()
            Log.d("APP", recentEvents.joinToString { "${it.title} | ${it.desc} | ${it.link}"} )
            runOnUiThread {
                tableLayout.removeAllViews()
                configureTableRows(recentEvents)
                refreshView.isRefreshing = false
            }
        }
    }

    private fun formatDate(date: Date, start: String, end: String): String {
        val dateFormatter = SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN)
        val result = when (end != "") {
            true -> "${dateFormatter.format(date)} Von: $start Bis: $end"
            false -> "${dateFormatter.format(date)} Ab: $start"
        }
        return result
    }

    private fun configureTableRows(data: List<WPEvent>) {
        if (recentEvents.isEmpty()) {
            // maybe Show info View -> no Events available
            return
        }
        var backgroundGreen = true
        for (element in data) {
            Log.d("ELEMENT", element.title + " " + element.desc + " " + element.link)
            val row = View.inflate(this, R.layout.event_layout, null) as ConstraintLayout
            if (backgroundGreen) {
                row.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primaryHighlight)
            } else {
                row.backgroundTintList = ContextCompat.getColorStateList(this, R.color.secondaryHighlight)
            }
            backgroundGreen = !backgroundGreen
            // load subviews
            val dateView = row.findViewById<TextView>(R.id.event_date)
            val titleView = row.findViewById<TextView>(R.id.event_title)
            val descView = row.findViewById<TextView>(R.id.event_desc)
            val addressView = row.findViewById<TextView>(R.id.event_location)
            val websiteView = row.findViewById<TextView>(R.id.event_website)
            // configure subviews
            dateView.text = formatDate(element.date, element.start, element.end)
            titleView.text = HtmlCompat.fromHtml(element.title, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
            if (element.desc.isEmpty()) {
                descView.visibility = View.GONE
            } else {
                descView.text = HtmlCompat.fromHtml(element.desc, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
            }
            if (element.location.isEmpty()) {
                val addressLayout = row.findViewById<LinearLayout>(R.id.event_location_area)
                addressLayout.visibility = View.GONE
            } else {
                addressView.text = element.location
            }
            websiteView.text = URI(element.link).host.substringBefore("/")
            // Set Margin for dynamic row
            val rowParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT)
            rowParams.setMargins(20, 20, 20, 60)
            row.layoutParams = rowParams

            row.setOnClickListener {
                val intent = Intent(applicationContext, WebActivity::class.java)
                intent.putExtra("url", element.link)
                intent.putExtra("parent", "EventActivity")
                startActivity(intent)
            }
            // add row to table
            tableLayout.addView(row)
        }
    }
}