package de.lindlarverbindet.dingenskirchen.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import de.lindlarverbindet.dingenskirchen.R

class MainActivity : AppCompatActivity() {

    private lateinit var mobilWidget: View
    private lateinit var villageWidget: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mobilWidget = findViewById(R.id.main_mobil_widget)
        villageWidget = findViewById(R.id.main_village_widget)

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