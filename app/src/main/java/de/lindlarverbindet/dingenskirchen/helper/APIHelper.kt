package de.lindlarverbindet.dingenskirchen.helper

import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
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

    fun sendPostRequest(urlString: String, json: JSONObject, callback: (success:Boolean, response:String) -> Unit) {
        val response: StringBuilder = StringBuilder()
        val url = URL(urlString)
        try {
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "POST"
                doOutput = true
                setRequestProperty("Content-Type", "application/json; utf-8")
                // sending the request
                try {
                    val outStream = DataOutputStream(outputStream)
                    outStream.write(json.toString().toByteArray())
                    outStream.flush()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                // get response for url
                if (responseCode == HttpURLConnection.HTTP_OK ||
                    responseCode == HttpURLConnection.HTTP_CREATED) {
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
                        callback(true, response.toString())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                else {
                    try {
                        BufferedReader(
                            InputStreamReader(errorStream, "utf-8")
                        ).use {
                            var responseLine: String? = ""
                            while (responseLine != null) {
                                responseLine = it.readLine()
                                response.append(responseLine?.trim())
                            }
                        }
                        callback(false, response.toString())
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}