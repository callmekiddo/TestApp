package com.kiddo.testapp

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout
import android.widget.Switch
import android.widget.Toast
import android.content.SharedPreferences
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread
import android.widget.Button
import android.content.Intent
import androidx.preference.PreferenceManager

class MainActivity : AppCompatActivity() {

    private lateinit var libVLC: LibVLC
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var vlcVideoLayout: VLCVideoLayout
    private lateinit var notificationSwitch: Switch
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vlcVideoLayout = findViewById(R.id.vlcVideoLayout)
        notificationSwitch = findViewById(R.id.notificationSwitch)
        preferences = PreferenceManager.getDefaultSharedPreferences(this)

        libVLC = LibVLC(this)
        mediaPlayer = MediaPlayer(libVLC)

        val ipCameraUrl = "http://192.168.113.89:5000/video"
        val media = Media(libVLC, Uri.parse(ipCameraUrl))
        mediaPlayer.media = media

        notificationSwitch.isChecked = preferences.getBoolean("notifications_enabled", true)
        updateSwitchText(notificationSwitch.isChecked)
        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            preferences.edit().putBoolean("notifications_enabled", isChecked).apply()
            updateSwitchText(isChecked)
            val signal = if (isChecked) 'c' else 'd' //c->on, d-> off ( on bat push noti)
            sendSignalToEsp32(signal.toString())
            Toast.makeText(this, "Notifications ${if (isChecked) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
        }

        val viewImagesButton = findViewById<Button>(R.id.viewImagesButton)
        viewImagesButton.setOnClickListener {
            val intent = Intent(this, ImagesActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateSwitchText(isChecked: Boolean) {
        notificationSwitch.text = if (isChecked) "On" else "Off"
    }

    private fun sendSignalToEsp32(signal: String) {
        thread {
            try {
                val url = URL("http://192.168.113.145:8088")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.outputStream.write(signal.toByteArray())
                connection.outputStream.flush()
                connection.outputStream.close()
                connection.responseCode
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (vlcVideoLayout != null && mediaPlayer != null) {
            try {
                mediaPlayer.attachViews(vlcVideoLayout, null, false, false)
                mediaPlayer.play()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mediaPlayer.detachViews()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer != null) {
            mediaPlayer.release()
        }
        if (libVLC != null) {
            libVLC.release()
        }
    }
}
