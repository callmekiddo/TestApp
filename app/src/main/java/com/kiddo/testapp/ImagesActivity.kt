package com.kiddo.testapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.kiddo.testapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.coroutineContext

class ImagesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var imagesAdapter: ImagesAdapter
    private lateinit var images : List<ImageData>
    private lateinit var retrofit: Retrofit
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.102.10:5000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val imageApi = retrofit.create(ImageApi::class.java)

        CoroutineScope(Dispatchers.Main).launch {
            val response = withContext(Dispatchers.IO){
                imageApi.getAllImages()
            }
            if(response.isSuccessful){
                images = response.body()!!
                imagesAdapter = ImagesAdapter(this@ImagesActivity,images)
                recyclerView.adapter = imagesAdapter
            }
            else{
                Toast.makeText(this@ImagesActivity,"Can't fetch image",Toast.LENGTH_SHORT).show()
            }
        }
    }
}
