package de.lindlarverbindet.dingenskirchen.activities.villageservices

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.core.content.ContextCompat
import de.lindlarverbindet.dingenskirchen.R
import de.lindlarverbindet.dingenskirchen.helper.APIHelper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

class LimoActivity : AppCompatActivity() {

    private lateinit var givenNameTextView: EditText
    private lateinit var nameTextView: EditText
    private lateinit var phoneTextView: EditText
    private lateinit var mailTextView: EditText
    private lateinit var termsCheckBox: CheckBox
    private lateinit var sendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_limo)

        this.supportActionBar?.title = getString(R.string.limo_navigation_title)

        givenNameTextView = findViewById(R.id.limo_given_name_field)
        nameTextView = findViewById(R.id.limo_name_field)
        phoneTextView = findViewById(R.id.limo_tel_field)
        mailTextView = findViewById(R.id.limo_mail_field)
        termsCheckBox = findViewById(R.id.limo_agreement_checkbox)
        sendButton = findViewById(R.id.limo_button)

        sendButton.isEnabled = false
        sendButton.setOnClickListener {
            sendForm()
        }
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

    private fun sendForm() {
        val json = JSONObject()
        try {
            json.put("form", "limo")
            json.put("name", givenNameTextView.text ?: "")
            json.put("nachname", nameTextView.text ?: "")
            json.put("fon", phoneTextView.text ?: "")
            json.put("mail", mailTextView.text ?: "")
            json.put("datenschutz", termsCheckBox.isChecked)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        // send it
        GlobalScope.launch {
            val response = APIHelper().sendPostRequest(getString(R.string.tool_api_url), json)
            Log.d("RESPONSE", response)
        }
    }
}