package de.lindlarverbindet.dingenskirchen.activities.villageservices

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import de.lindlarverbindet.dingenskirchen.R
import de.lindlarverbindet.dingenskirchen.helper.APIHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

class DigitalActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var lastSelectedTopic: String = ""

    private lateinit var givenNameTextView: EditText
    private lateinit var nameTextView: EditText
    private lateinit var addressTextView: EditText
    private lateinit var phoneTextView: EditText
    private lateinit var mailTextView: EditText
    private lateinit var topicSelectView: Spinner
    private lateinit var moreInfoLabel: TextView
    private lateinit var moreInfoView: EditText
    private lateinit var detailInfoView: EditText
    private lateinit var homeCheckBox: CheckBox
    private lateinit var termsCheckBox: CheckBox
    private lateinit var sendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_digital)

        givenNameTextView = findViewById(R.id.digital_given_name_field)
        nameTextView = findViewById(R.id.digital_name_field)
        addressTextView  = findViewById(R.id.digital_address_field)
        phoneTextView = findViewById(R.id.digital_tel_field)
        mailTextView = findViewById(R.id.digital_mail_field)
        topicSelectView = findViewById(R.id.digital_topic_spinner)
        moreInfoLabel = findViewById(R.id.digital_other_topic_label)
        moreInfoView = findViewById(R.id.digital_other_topic_field)
        detailInfoView = findViewById(R.id.digital_detail_topic_field)
        homeCheckBox = findViewById(R.id.digital_home_order)
        termsCheckBox = findViewById(R.id.digital_agreement_checkbox)
        sendButton = findViewById(R.id.digital_button)

        moreInfoLabel.visibility = View.GONE
        moreInfoView.visibility = View.GONE

        sendButton.isEnabled = false
        sendButton.setOnClickListener {
            sendForm()
        }

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

    private fun sendForm() {
        val json = JSONObject()
        try {
            json.put("form", "digital")
            json.put("name", givenNameTextView.text ?: "")
            json.put("nachname", nameTextView.text ?: "")
            json.put("strasse", addressTextView.text ?: "")
            json.put("plz", "51789") // TODO: Add field here
            json.put("ort", "Lindlar") // TODO: Add field here
            json.put("fon", phoneTextView.text ?: "")
            json.put("mail", mailTextView.text ?: "")
            json.put("aufgabe", lastSelectedTopic)
            json.put("aufgabe_beschreibung", moreInfoView.text ?: "")
            json.put("freitext", detailInfoView.text ?: "")
            json.put("datenschutz", termsCheckBox.isChecked)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        // send it
        GlobalScope.launch {
            val response = APIHelper().sendPostRequest(getString(R.string.api_url), json)
            Log.d("RESPONSE", response)
        }
    }
}