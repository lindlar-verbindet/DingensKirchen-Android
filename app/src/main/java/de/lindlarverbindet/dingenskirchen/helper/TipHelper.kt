package de.lindlarverbindet.dingenskirchen.helper

import de.lindlarverbindet.dingenskirchen.models.Tip
import org.json.JSONArray
import org.json.JSONException
import kotlin.collections.ArrayList

class TipHelper {

    private val apiHelper = APIHelper()

    fun getTips(): List<Tip> {
        val url = "https://wildflowers.bplaced.net/tippdtages.json" // TODO: Use the right url

        val response = apiHelper.sendGetRequest(url)
        try {
            val tips = JSONArray(response)
            val result: ArrayList<Tip> = arrayListOf()
            for (i in 0 until tips.length()) {
                val post = tips.getJSONObject(i)

                val id = post.get("id") as Int
                val title = post.get("headline") as String
                val content = post.get("text") as String

                result.add(Tip(id, title, content))
            }
            return result
        } catch (e: JSONException) {
            e.printStackTrace()
            return arrayListOf()
        }
    }
}