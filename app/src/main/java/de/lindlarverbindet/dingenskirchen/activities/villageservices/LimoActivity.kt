package de.lindlarverbindet.dingenskirchen.activities.villageservices

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import de.lindlarverbindet.dingenskirchen.R
import de.lindlarverbindet.dingenskirchen.helper.APIHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

class LimoActivity : AppCompatActivity() {

    private val hintColor = "#d72b22"

    private lateinit var givenNameTextView: EditText
    private lateinit var nameTextView: EditText
    private lateinit var dateTextView: EditText
//    private lateinit var timeTextView: EditText
//    private lateinit var destinationTextView: EditText
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
        dateTextView = findViewById(R.id.limo_date_field)
//        timeTextView = findViewById(R.id.limo_time_field)
//        destinationTextView = findViewById(R.id.limo_dest_field)
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
        if (!checkFields()) {
            return
        }
        val json = JSONObject()
        try {
            json.put("form", "limo")
            json.put("name", givenNameTextView.text ?: "")
            json.put("nachname", nameTextView.text ?: "")
            json.put("datum", dateTextView.text ?: "")
//            json.put("uhr", timeTextView.text ?: "")
//            json.put("ziel", destinationTextView.text ?: "")
            json.put("fon", phoneTextView.text ?: "")
            json.put("mail", mailTextView.text ?: "")
            json.put("datenschutz", termsCheckBox.isChecked)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        // send it
        lifecycleScope.launch(Dispatchers.IO) {
            APIHelper().sendPostRequest(getString(R.string.tool_api_url), json) {success, _ ->
                runOnUiThread {
                    val title = if (success) R.string.form_alert_success_title
                                else R.string.form_alert_failure_title
                    val text =  if (success) R.string.form_alert_success_text
                                else R.string.form_alert_failure_text

                    val alertDialogBuilder = AlertDialog.Builder(this@LimoActivity)
                    alertDialogBuilder.setTitle(title)
                    alertDialogBuilder.setMessage(text)
                    alertDialogBuilder.setPositiveButton(R.string.form_alert_button) { _, _ ->
                        if (success) {
                            this@LimoActivity.finish()
                        }
                    }
                    alertDialogBuilder.show()
                }
            }
        }
    }

    private fun setHint(text: EditText) {
        text.hint = getString(R.string.hint)
        text.setHintTextColor(Color.parseColor(hintColor))
    }

    private fun checkFields():Boolean {
        var result = true
        if (nameTextView.text.isNullOrBlank()) {
            setHint(nameTextView)
            result = false
        }
        if (phoneTextView.text.isNullOrBlank()) {
            setHint(phoneTextView)
            result = false
        }
        return result
    }
}