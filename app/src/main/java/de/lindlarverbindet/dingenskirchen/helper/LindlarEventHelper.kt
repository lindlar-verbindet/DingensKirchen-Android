package de.lindlarverbindet.dingenskirchen.helper

import android.util.Log
import de.lindlarverbindet.dingenskirchen.models.WPEvent
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat

import java.util.*
import kotlin.collections.ArrayList

class LindlarEventHelper {
    companion object {
        val apiHelper = APIHelper()

        fun getRecentEvents(): ArrayList<WPEvent> {
            val currentTime = Date().time.toString().dropLast(3)
            val url = "https://lindlar.de/?eID=event_api&token=8_435!B!tV_9wuj-3P*e&mod=event&start=${currentTime}"
            Log.d("APIURL:", url)

            val response = apiHelper.sendGetRequest(url)
            val result: ArrayList<WPEvent> = arrayListOf()
            try {
                val eventsObject = JSONObject(response)
                val events = eventsObject.get("events") as JSONArray
                for (i in 0 until events.length()) {
                    val element = events.getJSONObject(i)
                    val title = element.get("title") as String
                    val desc = element.get("description") as String
                    val location = element.get("venue") as String

                    val timeFormatter = SimpleDateFormat("HH:mm", Locale.GERMAN)

                    val startTimestamp = (element.get("event_dates") as JSONObject).get("event_start") as Int
                    val startDate = Date(startTimestamp.toLong() * 1000)
                    val startTime = timeFormatter.format(startDate)

                    val endTimeStamp = (element.get("event_dates") as JSONObject).get("event_end") as Int
                    val endDate = Date(endTimeStamp.toLong() * 1000)
                    val endTime = when (endTimeStamp == 0) {
                        true -> ""
                        false -> timeFormatter.format(endDate)
                    }
                    val link = element.get("url") as String

                    val event = WPEvent(title, desc, startDate, startTime, endTime, location, link)
                    result.add(event)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return result
        }
    }
}