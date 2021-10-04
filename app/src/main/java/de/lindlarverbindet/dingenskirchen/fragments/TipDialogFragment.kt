package de.lindlarverbindet.dingenskirchen.fragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import de.lindlarverbindet.dingenskirchen.R

class TipDialogFragment: DialogFragment() {

    private lateinit var titleView: TextView
    private lateinit var contentView: TextView
    private lateinit var doneButton: Button

    var title: String = ""
    var content: String = ""

    companion object {
        const val TAG = "TipDialogFragment"
    }

//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        title = savedInstanceState.get()
//
//        return super.onCreateDialog(savedInstanceState)
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tip_dialog, container)
        titleView = view.findViewById(R.id.tip_dialog_title)
        contentView = view.findViewById(R.id.tip_dialog_content)
        doneButton = view.findViewById(R.id.tip_done_button)

        val bundle = arguments

        titleView.text = bundle?.getString("TITLE", "")
        contentView.text = bundle?.getString("CONTENT", "")

        doneButton.setOnClickListener { this.dismiss() }

        return view
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}