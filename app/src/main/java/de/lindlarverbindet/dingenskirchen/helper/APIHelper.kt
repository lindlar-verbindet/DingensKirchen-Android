package de.lindlarverbindet.dingenskirchen.helper

import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class APIHelper {

    fun sendGetRequest(urlString: String): String {
        val response: StringBuilder = StringBuilder()
        val url = URL(urlString)
        try {
            with(url.openConnection() as HttpURLConnection) {
                setRequestProperty("Content-Type", "application/json; utf-8")

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try {
                        BufferedReader(
                            InputStreamReader(inputStream, "utf-8")
                        ).use {
                            var responseLine: String? = ""
                            while (responseLine != null) {
                                responseLine = it.readLine()
                                response.append(responseLine?.trim())
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
        return response.toString()
    }
}