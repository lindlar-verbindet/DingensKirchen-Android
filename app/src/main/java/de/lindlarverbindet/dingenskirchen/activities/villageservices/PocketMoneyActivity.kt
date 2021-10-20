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

class PocketMoneyActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var lastSelectedTopic: String = ""

    private lateinit var givenNameTextView: EditText
    private lateinit var nameTextView: EditText
    private lateinit var addressTextView: EditText
    private lateinit var phoneTextView: EditText
    private lateinit var mailTextView: EditText
    private lateinit var birthdayTextView: EditText
    private lateinit var topicSpinner: Spinner
    private lateinit var moreTopicLabel: TextView
    private lateinit var moreTopicView: EditText
    private lateinit var detailTextView: EditText
    private lateinit var fromTextView: EditText
    private lateinit var untilTextView: EditText
    private lateinit var termsCheckBox: CheckBox
    private lateinit var sendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pocket_money)

        givenNameTextView = findViewById(R.id.pocket_money_given_name_field)
        nameTextView = findViewById(R.id.pocket_money_name_field)
        addressTextView = findViewById(R.id.pocket_money_address_field)
        phoneTextView = findViewById(R.id.pocket_money_tel_field)
        mailTextView = findViewById(R.id.pocket_money_mail_field)
        birthdayTextView = findViewById(R.id.pocket_money_birthdate_field)
        topicSpinner = findViewById(R.id.pocket_money_topic_spinner)
        moreTopicLabel = findViewById(R.id.pocket_money_other_topic_label)
        moreTopicView = findViewById(R.id.pocket_money_other_topic_field)
        detailTextView = findViewById(R.id.pocket_money_detail_topic_field)
        fromTextView = findViewById(R.id.pocket_money_date_from_field)
        untilTextView = findViewById(R.id.pocket_money_date_to_field)
        termsCheckBox = findViewById(R.id.pocket_money_agreement_checkbox)
        sendButton = findViewById(R.id.pocket_money_button)

        moreTopicLabel.visibility = View.GONE
        moreTopicView.visibility = View.GONE
        sendButton.isEnabled = false
        sendButton.setOnClickListener {
            sendForm()
        }

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

    private fun sendForm() {
        val json = JSONObject()
        try {
            json.put("form", "taschengeld")
            json.put("name", givenNameTextView.text ?: "")
            json.put("nachname", nameTextView.text ?: "")
            json.put("strasse", addressTextView.text ?: "")
            json.put("plz", "51789") // TODO: Add field here
            json.put("ort", "Lindlar") // TODO: Add field here
            json.put("fon", phoneTextView.text ?: "")
            json.put("mail", mailTextView.text ?: "")
            json.put("geburtstag", birthdayTextView.text ?: "")
            json.put("aufgabe", lastSelectedTopic)
            json.put("aufgabe_beschreibung", moreTopicView.text ?: "")
            json.put("freitext", detailTextView.text ?: "")
            json.put("zeit_start", fromTextView.text ?: "")
            json.put("zeit_ende", untilTextView.text ?: "")
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