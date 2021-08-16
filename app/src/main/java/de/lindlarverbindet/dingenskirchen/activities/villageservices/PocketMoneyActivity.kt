package de.lindlarverbindet.dingenskirchen.activities.villageservices

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import de.lindlarverbindet.dingenskirchen.R

class PocketMoneyActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var lastSelectedTopic: String = ""

    private lateinit var topicSpinner: Spinner
    private lateinit var moreTopicLabel: TextView
    private lateinit var moreTopicView: EditText
    private lateinit var termsCheckBox: CheckBox
    private lateinit var sendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pocket_money)

        topicSpinner = findViewById(R.id.pocket_money_topic_spinner)
        moreTopicLabel = findViewById(R.id.pocket_money_other_topic_label)
        moreTopicView = findViewById(R.id.pocket_money_other_topic_field)
        termsCheckBox = findViewById(R.id.pocket_money_agreement_checkbox)
        sendButton = findViewById(R.id.pocket_money_button)

        moreTopicLabel.visibility = View.GONE
        moreTopicView.visibility = View.GONE
        sendButton.isEnabled = false

        topicSpinner.onItemSelectedListener = this
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
        lastSelectedTopic = parent?.getItemAtPosition(position).toString()
        if (position == parent?.adapter?.count?.minus(1)) {
            runOnUiThread {
                moreTopicLabel.visibility = View.VISIBLE
                moreTopicView.visibility = View.VISIBLE
            }
        } else {
            runOnUiThread {
                moreTopicLabel.visibility = View.GONE
                moreTopicView.visibility = View.GONE
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        lastSelectedTopic = ""
        runOnUiThread {
            moreTopicLabel.visibility = View.GONE
            moreTopicView.visibility = View.GONE
        }
    }
}