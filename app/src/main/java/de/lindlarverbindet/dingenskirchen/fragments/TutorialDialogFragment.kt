package de.lindlarverbindet.dingenskirchen.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import de.lindlarverbindet.dingenskirchen.R
import de.lindlarverbindet.dingenskirchen.adapter.TutorialViewAdapter

class TutorialDialogFragment : DialogFragment() {

    lateinit var tutorialPager: ViewPager2
    lateinit var tabView: TabLayout
    lateinit var prevButton: Button
    lateinit var nextButton: Button

    lateinit var closeButton: Button

    companion object {
        const val TAG = "TutorialDialogFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tutorial, container)

        // Next, prev, close button setup...
        tutorialPager = view.findViewById(R.id.tutorial_pager)
        tabView = view.findViewById(R.id.tutorial_tablayout)
        prevButton = view.findViewById(R.id.tutorial_prev_button)
        nextButton = view.findViewById(R.id.tutorial_next_button)

        closeButton = view.findViewById(R.id.tutorial_close_button)

        tutorialPager.adapter = TutorialViewAdapter(activity as Context)
        TabLayoutMediator(tabView, tutorialPager) { tab, position ->
            // configure stuff here
        }.attach()


        closeButton.setOnClickListener {
            this.dismiss()
        }

        nextButton.setOnClickListener {
            tutorialPager.setCurrentItem(getItem(+1), true)
        }

        prevButton.setOnClickListener {
            tutorialPager.setCurrentItem(getItem(-1), true)
        }

        return view
    }

    private fun getItem(i: Int): Int = tutorialPager.currentItem + i

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
        }
    }

}