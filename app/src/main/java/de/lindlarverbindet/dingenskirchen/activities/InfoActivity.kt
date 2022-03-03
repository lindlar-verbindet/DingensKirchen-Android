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
    private lateinit var sponsorTextView: TextView
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
        sponsorTextView = findViewById(R.id.imprint_sponsor)
        openTextView = findViewById(R.id.imprint_open)
        ownerTextView = findViewById(R.id.imprint_owner)
        conceptTextView = findViewById(R.id.imprint_concept)

        imprintTextView.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.info_imprint_url)))
            startActivity(browserIntent)
        }

        dataTextView.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.info_datapolicy_url)))
            startActivity(browserIntent)
        }

        feedbackTextView.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.info_feedback_url)))
            startActivity(browserIntent)
        }

        sponsorTextView.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.info_sponsor_url)))
            startActivity(browserIntent)
        }

        openTextView.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.info_opensource_url)))
            startActivity(browserIntent)
        }

        ownerTextView.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.info_owner_url)))
            startActivity(browserIntent)
        }

        conceptTextView.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.info_concept_url)))
            startActivity(browserIntent)
        }
    }
}