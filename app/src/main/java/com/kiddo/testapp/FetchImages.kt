package com.kiddo.testapp

import android.os.AsyncTask
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.net.HttpURLConnection
import java.net.URL

class FetchImages(private val listener: (List<ImageData>) -> Unit) : AsyncTask<Void, Void, List<ImageData>>() {

    override fun doInBackground(vararg params: Void?): List<ImageData> {
        val url = URL("http://192.168.113.89:5000/images")
        val connection = url.openConnection() as HttpURLConnection

        return try {
            connection.requestMethod = "GET"
            connection.connect()

            val inputStream = connection.inputStream
            val response = inputStream.bufferedReader().use { it.readText() }

            val imageType = object : TypeToken<List<ImageData>>() {}.type
            Gson().fromJson(response, imageType)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        } finally {
            connection.disconnect()
        }
    }

    override fun onPostExecute(result: List<ImageData>) {
        listener(result)
    }
}
