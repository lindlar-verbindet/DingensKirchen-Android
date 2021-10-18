package de.lindlarverbindet.dingenskirchen.models

import android.media.Image
import java.io.Serializable
import java.util.*

data class News(var title: String, var content: String,  var date: Date, var link: String, var imageURL: String?): Serializable