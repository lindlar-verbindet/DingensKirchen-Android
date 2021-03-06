package de.lindlarverbindet.dingenskirchen.helper

import de.lindlarverbindet.dingenskirchen.models.News
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class RSSHelper {

    fun getRecentPosts(): List<News>? {
        val urlString = "https://lindlar.de/rss.html"
        val url = URL(urlString)

        val dateParser = SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z", Locale.US)
        val http = url.openConnection() as HttpURLConnection
        http.doInput = true
        try {
            http.connect()
            val input: InputStream = http.inputStream

            val factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true

            val xpp = factory.newPullParser()
            xpp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true)
            xpp.setInput(input, null)

            val result: ArrayList<News> = arrayListOf()
            var news = News("", "", Date(), "", null)
            var text = ""

            var eventType = xpp.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (xpp.eventType) {
                    XmlPullParser.START_TAG -> {
                        if (xpp.name == "item") {
                            news = News("", "", Date(), "", null)
                        }
                    }
                    XmlPullParser.TEXT -> {
                        text = xpp.text
                    }
                    XmlPullParser.END_TAG -> {
                        when (xpp.name) {
                            "title" -> news.title = text
                            "encoded" -> {
                                news.content = text
                                news.imageURL = ImageURLGetter.getImageURL(text)
                            }
                            "pubDate" -> news.date = dateParser.parse(text) ?: Date()
                            "link" -> news.link = text
                            "item" -> result.add(news)
                            else -> {}
                        }
                    }
                }
                eventType = xpp.next()
            }
            return result.toList()
        } catch (e: Exception) {
            return null
        }
    }



}