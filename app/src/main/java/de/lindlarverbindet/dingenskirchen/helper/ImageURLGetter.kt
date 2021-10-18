package de.lindlarverbindet.dingenskirchen.helper

import android.util.Log

class ImageURLGetter {
    companion object {
        fun getImageURL(content: String): String? {
            val urlRegex =
                "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)".toRegex()
            val imgRegex = "<img([\\w\\W]+?)/>".toRegex()
            val imageTags = imgRegex.findAll(content)
            var url: MatchResult? = null
            for (tag in imageTags) {
                Log.d("IMG", "tag was found ${tag.value}")

                url = urlRegex.find(tag.value)
                Log.d("IMGURL", "url was found ${url?.value}")
                if (url != null) {
                    return url.value
                }
            }
            return null
        }
    }
}