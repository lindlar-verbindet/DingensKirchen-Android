package de.lindlarverbindet.dingenskirchen.activities.villageservices

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import de.lindlarverbindet.dingenskirchen.R

class NeighbourActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private var lastSelectedTopic: String = ""

    private lateinit var topicSelectView: Spinner
    private lateinit var moreInfoLabel: TextView
    private lateinit var moreInfoView: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_neighbour)

        topicSelectView = findViewById(R.id.neighbour_topic_spinner)
        moreInfoLabel = findViewById(R.id.neighbour_other_topic_label)
        moreInfoView = findViewById(R.id.neighbour_other_topic_field)
        moreInfoLabel.visibility = View.GONE
        moreInfoView.visibility = View.GONE

        topicSelectView.onItemSelectedListener = this
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
        lastSelectedTopic = ""
        runOnUiThread {
            moreInfoLabel.visibility = View.GONE
            moreInfoView.visibility = View.GONE
        }
    }


}