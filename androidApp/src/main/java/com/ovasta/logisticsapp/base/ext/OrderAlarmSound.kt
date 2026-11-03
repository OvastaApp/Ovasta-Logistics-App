package com.ovasta.logisticsapp.base.ext

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import com.ovasta.logisticsapp.R
import com.ovasta.logisticsapp.presentation.home.data.OrderAlarm

class OrderAlarmSound(private val context: Context) : OrderAlarm {

    private var mediaPlayer: MediaPlayer? = null

    private val isAppInForeground: Boolean
        get() = ProcessLifecycleOwner.get().lifecycle.currentState
            .isAtLeast(Lifecycle.State.RESUMED)

    override fun startAlarm() {
        if (!isAppInForeground) return
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

    override fun stopAlarm() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
    }
}
