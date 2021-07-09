package de.lindlarverbindet.dingenskirchen.activities

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import de.lindlarverbindet.dingenskirchen.R

class CouncilActivity : AppCompatActivity() {

    private lateinit var councilActionFirst: View
    private lateinit var councilActionSecond: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_council)

        councilActionFirst = findViewById(R.id.council_action_first)
        councilActionSecond = findViewById(R.id.council_action_second)

        councilActionFirst.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.primaryBackground))

        val secondTitleView = councilActionSecond.findViewById<TextView>(R.id.council_action_title)
        secondTitleView.text = "Mängelmelder"

        val firstActionButton = councilActionFirst.findViewById<Button>(R.id.council_action_button)
        firstActionButton.setOnClickListener {
            val webpage = Uri.parse("https://egov-lindlar.ssl.civitec.de/intelliform/forms/Lindlar/Buergeramt/pool/pool_meldebesch_einf/index")
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            startActivity(intent)
        }

        val secondActionButton = councilActionSecond.findViewById<Button>(R.id.council_action_button)
        secondActionButton.text = "Jetzt online melden"
        secondActionButton.setOnClickListener {
            val webpage = Uri.parse("https://egov-lindlar.ssl.civitec.de/intelliform/forms/Lindlar/Zentrale/pool/pool_maengelmelder/index")
            val intent = Intent(Intent.ACTION_VIEW, webpage)
            startActivity(intent)
        }
    }
}