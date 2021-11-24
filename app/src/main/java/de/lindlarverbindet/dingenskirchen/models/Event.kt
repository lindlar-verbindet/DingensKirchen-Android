package de.lindlarverbindet.dingenskirchen.models

import java.io.Serializable
import java.util.*

data class Event(val title: String,
                 val desc: String,
                 val date: Date,
                 val start: String,
                 val end: String,
                 val location: String,
                 val link: String) : Serializable