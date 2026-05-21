package com.ovasta.logisticsapp.base.ext

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import com.ovasta.logisticsapp.R

class OrderAlarmSound(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null

    fun startAlarm() {
        if (mediaPlayer?.isPlaying == true) return

        mediaPlayer = MediaPlayer.create(context, R.raw.new_order_alert).apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            isLooping = true
            start()
        }
    }

    fun stopAlarm() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
    }
}
