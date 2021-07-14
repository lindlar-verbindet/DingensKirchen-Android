package de.lindlarverbindet.dingenskirchen.helper

import de.lindlarverbindet.dingenskirchen.models.WPPost
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class WordpressHelper {

    private val apiHelper = APIHelper()

    private val appNewsID = "13"

    fun getRecentPosts(): List<WPPost> {
        val urlString = "https://www.lindlar-verbindet.de/wp-json/wp/v2/posts?categories=$appNewsID"

        val dateParser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMAN)
        val response = apiHelper.sendGetRequest(urlString)
        try {
            val posts = JSONArray(response)
            val result: ArrayList<WPPost> = arrayListOf()
            for (i in 0 until posts.length()) {
                val post = posts.getJSONObject(i)
                val title = (post.get("title") as JSONObject).get("rendered") as String
                val content = (post.get("content") as JSONObject).get("rendered") as String
                val date = dateParser.parse(post.get("date") as String)
                val link = post.get("link") as String

                val wpPost = WPPost(title, content, date, link)
                result.add(wpPost)
            }
            return result
        } catch (e:JSONException) {
            e.printStackTrace()
            return listOf()
        }

    }

}