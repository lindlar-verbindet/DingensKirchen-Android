package de.lindlarverbindet.dingenskirchen.models

import java.io.Serializable
import java.util.*

data class News(var title: String, var content: String,  var date: Date, var link: String): Serializable