package de.lindlarverbindet.dingenskirchen.activities.villageservices

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import de.lindlarverbindet.dingenskirchen.R

class DigitalActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var lastSelectedTopic: String = ""

    private lateinit var topicSelectView: Spinner
    private lateinit var moreInfoLabel: TextView
    private lateinit var moreInfoView: EditText
    private lateinit var termsCheckBox: CheckBox
    private lateinit var sendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_digital)

        topicSelectView = findViewById(R.id.digital_topic_spinner)
        moreInfoLabel = findViewById(R.id.digital_other_topic_label)
        moreInfoView = findViewById(R.id.digital_other_topic_field)
        termsCheckBox = findViewById(R.id.digital_agreement_checkbox)
        sendButton = findViewById(R.id.digital_button)

        moreInfoLabel.visibility = View.GONE
        moreInfoView.visibility = View.GONE

        sendButton.isEnabled = false

        topicSelectView.onItemSelectedListener = this
    }

    fun onCheckboxClicked(view: View) {
        if (termsCheckBox == view) {
            sendButton.isEnabled = termsCheckBox.isChecked
            if (termsCheckBox.isChecked) {
                sendButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primaryHighlight)
            } else {
                sendButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.primaryBackground)
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Log.d("Selected", "currently selected $position from ${parent?.adapter?.count}")
        lastSelectedTopic = parent?.getItemAtPosition(position).toString()
        if (position == parent?.adapter?.count?.minus(1)) {
            runOnUiThread {
                moreInfoLabel.visibility = View.VISIBLE
                moreInfoView.visibility = View.VISIBLE
            }
        } else {
            runOnUiThread {
                moreInfoLabel.visibility = View.GONE
                moreInfoView.visibility = View.GONE
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        runOnUiThread {
            moreInfoLabel.visibility = View.GONE
            moreInfoView.visibility = View.GONE
        }
    }
}