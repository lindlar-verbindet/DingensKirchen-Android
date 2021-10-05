package de.lindlarverbindet.dingenskirchen.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TableLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import de.lindlarverbindet.dingenskirchen.R
import de.lindlarverbindet.dingenskirchen.models.CouncilService
import org.xmlpull.v1.XmlPullParser


class CouncilActivity : AppCompatActivity() {

    private lateinit var councilTrashService: View
    private lateinit var tableLayout: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_council)

        this.supportActionBar?.title = "Rathaus"

        councilTrashService = findViewById(R.id.council_action_trash)
        tableLayout = findViewById(R.id.council_table)

        councilTrashService.setOnClickListener {
            val intent = Intent(applicationContext, WebActivity::class.java)
            intent.putExtra("url", "https://abfallnavi.de/lindlar/")
            intent.putExtra("parent", "CouncilActivity")
            startActivity(intent)
        }

        val services = loadCouncilServices()
        configureTableRows(services)
    }

    private fun loadCouncilServices(): List<CouncilService> {
        val items = ArrayList<CouncilService>()
        val xpp: XmlPullParser = resources.getXml(R.xml.council_services)

        var serviceName = ""
        var serviceDesc = ""
        var serviceLink = ""
        var buttonText  = ""
        // parsing services
        while (xpp.eventType != XmlPullParser.END_DOCUMENT) {
            when (xpp.eventType) {
                XmlPullParser.START_TAG -> {
                    if (xpp.name == "service") {
                        serviceName = xpp.getAttributeValue(null, "name")
                        serviceDesc = xpp.getAttributeValue(null, "desc")
                        serviceLink = xpp.getAttributeValue(null, "link")
                        buttonText  = xpp.getAttributeValue(null, "btn")
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (xpp.name == "service") {
                        items.add(CouncilService(serviceName, serviceDesc, serviceLink, buttonText))
                    }
                }
            }
            xpp.next()
        }
        return items.toList()
    }

    private fun configureTableRows(data: List<CouncilService>) {
        var backgroundGreen = false
        for (element in data) {
            Log.d("ELEMENT", element.name + " " + element.desc + " " + element.link)
            val row = View.inflate(this, R.layout.council_action, null) as ConstraintLayout
            if (backgroundGreen) {
                row.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primaryHighlight)
            } else {
                row.backgroundTintList = ContextCompat.getColorStateList(this, R.color.secondaryHighlight)
            }
            backgroundGreen = !backgroundGreen
            // load subviews
            val titleView = row.findViewById<TextView>(R.id.council_action_title)
            val descView = row.findViewById<TextView>(R.id.council_action_desc)
            val button = row.findViewById<Button>(R.id.council_action_button)
            // configure subviews
            titleView.text = element.name
            descView.text = element.desc
            button.text = element.buttonText
            button.setOnClickListener {
                val intent = Intent(applicationContext, WebActivity::class.java)
                intent.putExtra("url", element.link)
                intent.putExtra("parent", "CouncilActivity")
                startActivity(intent)
            }
            // Set Margin for dynamic row
            val rowParams = TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,
                                                     TableLayout.LayoutParams.WRAP_CONTENT)
            rowParams.setMargins(20, 20, 20, 0)
            row.layoutParams = rowParams
            // add row to table
            tableLayout.addView(row)
        }
    }
}