package de.lindlarverbindet.dingenskirchen.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TableLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import de.lindlarverbindet.dingenskirchen.R
import de.lindlarverbindet.dingenskirchen.models.CouncilService
import de.lindlarverbindet.dingenskirchen.models.VillageService
import org.xmlpull.v1.XmlPullParser

class VillageActivity : AppCompatActivity() {

    private lateinit var tableLayout: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_village)

        this.supportActionBar?.title = "Dorfleben"

        tableLayout = findViewById(R.id.village_table)

        val services = loadVillageServices()
        configureTableRows(services)
    }

    private fun loadVillageServices(): List<VillageService> {
        val items = ArrayList<VillageService>()
        val xpp: XmlPullParser = resources.getXml(R.xml.village_services)

        var serviceType      = ""
        var serviceTitle     = ""
        var serviceDesc      = ""
        var serviceTel       = ""
        var serviceTelBtn    = ""
        var serviceAction    = ""
        var serviceActionBtn = ""
        // parsing services
        while (xpp.eventType != XmlPullParser.END_DOCUMENT) {
            when (xpp.eventType) {
                XmlPullParser.START_TAG -> {
                    if (xpp.name == "service") {
                        serviceType = xpp.getAttributeValue(null, "type")
                        serviceTitle = xpp.getAttributeValue(null, "title")
                        serviceDesc = xpp.getAttributeValue(null, "desc")
                        serviceTel = xpp.getAttributeValue(null, "tel")
                        serviceTelBtn  = xpp.getAttributeValue(null, "telbtn")
                        serviceAction = xpp.getAttributeValue(null, "action")
                        serviceActionBtn = xpp.getAttributeValue(null, "actionbtn")
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (xpp.name == "service") {
                        items.add(VillageService(serviceType.toInt(), serviceTitle, serviceDesc, serviceTel, serviceTelBtn, serviceAction, serviceActionBtn))
                    }
                }
            }
            xpp.next()
        }
        return items.toList()
    }

    private fun configureTableRows(data: List<VillageService>) {
        var backgroundGreen = false
        for (element in data) {
            val row = when(element.type) {
                1 -> configureDoubleAction(View.inflate(this, R.layout.village_double_action, null) as ConstraintLayout, element)
                else -> configureSingleAction(View.inflate(this, R.layout.village_single_action, null) as ConstraintLayout, element)
            }
            if (backgroundGreen) {
                row.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primaryHighlight)
            } else {
                row.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primaryBackground)
            }
            backgroundGreen = !backgroundGreen
            // Set Margin for dynamic row
            val rowParams = TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT)
            rowParams.setMargins(20, 20, 20, 0)
            row.layoutParams = rowParams
            // add row to table
            tableLayout.addView(row)
        }
    }

    private fun configureSingleAction(row: ConstraintLayout, element:VillageService): ConstraintLayout {
        // load subviews
        val titleView = row.findViewById<TextView>(R.id.village_single_action_title)
        val descView = row.findViewById<TextView>(R.id.village_single_action_desc)
        val button = row.findViewById<Button>(R.id.village_single_action_button)
        // configure subviews
        titleView.text = element.title
        descView.text = element.desc
        button.text = element.actionBtn
        button.setOnClickListener {
            // TODO: Add when action is available
//            val webpage = Uri.parse(element.action)
//            val intent = Intent(Intent.ACTION_VIEW, webpage)
//            startActivity(intent)
        }
        return row
    }

    private fun configureDoubleAction(row: ConstraintLayout, element: VillageService): ConstraintLayout {
        // load subviews
        val titleView = row.findViewById<TextView>(R.id.village_double_action_title)
        val descView = row.findViewById<TextView>(R.id.village_double_action_desc)
        val button1 = row.findViewById<Button>(R.id.village_double_first_action)
        val button2 = row.findViewById<Button>(R.id.village_double_second_action)
        // configure subviews
        titleView.text = element.title
        descView.text = element.desc
        button1.text = element.telBtn
        button1.setOnClickListener {
            // TODO: Add calling when permission resolved
            val phone = Uri.parse("tel:" + element.tel)
            val intent = Intent(Intent.ACTION_DIAL, phone)
            startActivity(intent)
        }
        button2.text = element.actionBtn
        button2.setOnClickListener {
            // TODO: Add when action is available
//            val webpage = Uri.parse(element.action)
//            val intent = Intent(Intent.ACTION_VIEW, webpage)
//            startActivity(intent)
        }
        return row
    }

}