package de.lindlarverbindet.dingenskirchen.activities.villageservices

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import androidx.core.content.ContextCompat
import de.lindlarverbindet.dingenskirchen.R

class LimoActivity : AppCompatActivity() {

    private lateinit var termsCheckBox: CheckBox
    private lateinit var sendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_limo)

        termsCheckBox = findViewById(R.id.limo_agreement_checkbox)
        sendButton = findViewById(R.id.limo_button)

        sendButton.isEnabled = false
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
}