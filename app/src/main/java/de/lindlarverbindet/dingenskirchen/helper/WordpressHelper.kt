package de.lindlarverbindet.dingenskirchen.helper

import android.util.Log
import de.lindlarverbindet.dingenskirchen.models.WPEvent
import de.lindlarverbindet.dingenskirchen.models.News
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.ClassCastException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WordpressHelper {

    private val apiHelper = APIHelper()

    private val appNewsID = "13"

    fun getRecentPosts(): ArrayList<News> {
        val urlString = "https://www.lindlar-verbindet.de/wp-json/wp/v2/posts?categories=$appNewsID"

        val dateParser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMAN)
        val response = apiHelper.sendGetRequest(urlString)
        try {
            val posts = JSONArray(response)
            val result: ArrayList<News> = arrayListOf()
            for (i in 0 until posts.length()) {
                val post = posts.getJSONObject(i)
                val title = (post.get("title") as JSONObject).get("rendered") as String
                val content = (post.get("content") as JSONObject).get("rendered") as String
                val date = dateParser.parse(post.get("date") as String)
                val link = post.get("link") as String

                val imageURL = ImageURLGetter.getImageURL(content)
                val wpPost = News(title, content, date ?: Date(), link, imageURL)
                result.add(wpPost)
            }
            return result
        } catch (e:JSONException) {
            e.printStackTrace()
            return arrayListOf()
        }
    }

    fun getRecentEvents(): ArrayList<WPEvent> {
        val urlString = "https://lindlar-verbindet.de/wp-json/mecexternal/v1/calendar/2525"

        val dateParser = SimpleDateFormat("yyyy-MM-dd", Locale.GERMAN)
        val response = apiHelper.sendGetRequest(urlString)
        try {
            val json = JSONObject(response)
            val result: ArrayList<WPEvent> = arrayListOf()

            try {
                val content = json.get("content_json") as JSONObject
                for (key in content.keys()) {
                    val date = dateParser.parse(key)
                    val appointmentArray = content.get(key) as JSONArray

                    val entry = appointmentArray[0] as JSONObject // just pull the first dont know why this is an array
                    val data = entry.get("data") as JSONObject

                    val title = data.get("title") as String
                    val content = data.get("content") as String
                    val startTime = (data.get("time") as JSONObject).get("start_raw") as String
                    val endTime = (data.get("time") as JSONObject).get("end_raw") as String
                    val locations = (data.get("locations") as JSONObject)
                    var location = ""
                    for (key in locations.keys()) {
                        location = (locations.get(key) as JSONObject).get("address") as String
                    }
                    val link = data.get("permalink") as String

                    if (date != null) {
                        val appointment = WPEvent(title, content, date, startTime, endTime, location, link)
                        result.add(appointment)
                    }
                }
                return result
            } catch (e: ClassCastException) {
                e.printStackTrace()
                return arrayListOf()
            }

        } catch (e: JSONException) {
            e.printStackTrace()
            return arrayListOf()
        }
    }

}