package de.lindlarverbindet.dingenskirchen.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import de.lindlarverbindet.dingenskirchen.R

class MainActivity : AppCompatActivity() {

    private lateinit var eventWidget: View
    private lateinit var councilWidget: View
    private lateinit var mobilWidget: View
    private lateinit var villageWidget: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

    }
}