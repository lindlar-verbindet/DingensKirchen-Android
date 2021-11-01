package de.lindlarverbindet.dingenskirchen.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import de.lindlarverbindet.dingenskirchen.R
import android.content.Intent
import android.net.Uri


class InfoActivity : AppCompatActivity() {

    private lateinit var imprintTextView: TextView
    private lateinit var dataTextView: TextView
    private lateinit var feedbackTextView: TextView
    private lateinit var openTextView: TextView
    private lateinit var ownerTextView: TextView
    private lateinit var conceptTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        this.supportActionBar?.title = getString(R.string.info_navigation_title)

        imprintTextView = findViewById(R.id.imprint_imprint)
        dataTextView = findViewById(R.id.imprint_dataprotection)
        feedbackTextView = findViewById(R.id.imprint_feedback)
        openTextView = findViewById(R.id.imprint_open)
        ownerTextView = findViewById(R.id.imprint_owner)
        conceptTextView = findViewById(R.id.imprint_concept)

        imprintTextView.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.lindlar-verbindet.de/impressum/"))
            startActivity(browserIntent)
        }

        dataTextView.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.lindlar-verbindet.de/datenschutz/"))
            startActivity(browserIntent)
        }

        feedbackTextView.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.lindlar-verbindet.de/feedback-anregungen-ideen"))
            startActivity(browserIntent)
        }

        openTextView.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/lindlar-verbindet/DingensKirchen-Android"))
            startActivity(browserIntent)
        }

        ownerTextView.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.lindlar-verbindet.de/"))
            startActivity(browserIntent)
        }

        conceptTextView.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://pixelskull.de/"))
            startActivity(browserIntent)
        }
    }
}