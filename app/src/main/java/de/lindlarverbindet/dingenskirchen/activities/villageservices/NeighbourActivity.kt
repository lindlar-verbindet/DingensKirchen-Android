package de.lindlarverbindet.dingenskirchen.activities.villageservices

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
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

class NeighbourActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val hintColor = "#d72b22"

    private var lastSelectedTopic: String = ""
    private var lastSelectedDistrict: String = ""

    private lateinit var givenNameTextView: EditText
    private lateinit var nameTextView: EditText
    private lateinit var addressTextView: EditText
    private lateinit var districtSpinner: Spinner
    private lateinit var phoneTextView: EditText
    private lateinit var mailTextView: EditText
    private lateinit var topicSelectView: Spinner
    private lateinit var moreInfoLabel: TextView
    private lateinit var moreInfoView: EditText
    private lateinit var detailTextView: EditText
    private lateinit var termsCheckBox: CheckBox
    private lateinit var sendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_neighbour)

        this.supportActionBar?.title = getString(R.string.neighbour_navigation_title)

        givenNameTextView = findViewById(R.id.neighbour_given_name_field)
        nameTextView = findViewById(R.id.neighbour_name_field)
        addressTextView = findViewById(R.id.neighbour_address_field)
        districtSpinner = findViewById(R.id.neighbour_district_spinner)
        phoneTextView = findViewById(R.id.neighbour_tel_field)
        mailTextView = findViewById(R.id.neighbour_mail_field)
        topicSelectView = findViewById(R.id.neighbour_topic_spinner)
        moreInfoLabel = findViewById(R.id.neighbour_other_topic_label)
        moreInfoView = findViewById(R.id.neighbour_other_topic_field)
        detailTextView = findViewById(R.id.neighbour_detail_topic_field)
        termsCheckBox = findViewById(R.id.neighbour_agreement_checkbox)
        sendButton = findViewById(R.id.neighbour_button)

        moreInfoLabel.visibility = View.GONE
        moreInfoView.visibility = View.GONE

        sendButton.isEnabled = false
        sendButton.setOnClickListener {
            sendForm()
        }

        districtSpinner.onItemSelectedListener = this
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
        if (parent == topicSelectView) {
            lastSelectedTopic = parent.getItemAtPosition(position).toString()
            if (position == parent.adapter?.count?.minus(1)) {
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
        } else {
            lastSelectedDistrict = parent?.getItemAtPosition(position).toString()
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        lastSelectedTopic = ""
        runOnUiThread {
            moreInfoLabel.visibility = View.GONE
            moreInfoView.visibility = View.GONE
        }
    }

    private fun sendForm() {
        if (!checkFields()) {
            return
        }
        val json = JSONObject()
        try {
            json.put("form", "nachbarschaft")
            json.put("name", givenNameTextView.text ?: "")
            json.put("nachname", nameTextView.text ?: "")
            json.put("strasse", addressTextView.text ?: "")
            json.put("kdorf", lastSelectedDistrict)
            json.put("fon", phoneTextView.text ?: "")
            json.put("mail", mailTextView.text ?: "")
            json.put("aufgabe", lastSelectedTopic)
            json.put("aufgabe_beschreibung", moreInfoView.text ?: "")
            json.put("freitext", detailTextView.text ?: "")
            json.put("datenschutz", termsCheckBox.isChecked)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        // send it
        lifecycleScope.launch(Dispatchers.IO) {
            APIHelper().sendPostRequest(getString(R.string.tool_api_url), json) { success, _ ->
                runOnUiThread {
                    val title = if (success) R.string.form_alert_success_title
                    else R.string.form_alert_failure_title
                    val text = if (success) R.string.form_alert_success_text
                    else R.string.form_alert_failure_text

                    val alertDialogBuilder = AlertDialog.Builder(this@NeighbourActivity)
                    alertDialogBuilder.setTitle(title)
                    alertDialogBuilder.setMessage(text)
                    alertDialogBuilder.setPositiveButton(R.string.form_alert_button) { _, _ ->
                        if (success) {
                            this@NeighbourActivity.finish()
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