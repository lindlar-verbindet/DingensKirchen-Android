package de.lindlarverbindet.dingenskirchen.widgets

import android.app.Activity
import android.os.Bundle
import de.lindlarverbindet.dingenskirchen.R

class EventsWidget: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.widget_events)
    }
}