package com.kiddo.testapp

import android.os.AsyncTask
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import retrofit2.Response
import retrofit2.http.GET
import java.net.HttpURLConnection
import java.net.URL

interface ImageApi {
    @GET("/images")
    suspend fun getAllImages() : Response<List<ImageData>>
}
