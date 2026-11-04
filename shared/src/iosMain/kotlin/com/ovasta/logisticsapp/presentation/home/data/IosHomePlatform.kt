package com.ovasta.logisticsapp.presentation.home.data

import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.Foundation.NSBundle
import platform.Foundation.NSUserDefaults

/**
 * Loops new_order_alert.caf (in the app bundle, iosApp/iosApp) while Home has new orders, like
 * Android's OrderAlarmSound. The playback category plays it even with the silent switch on.
 */
@OptIn(ExperimentalForeignApi::class)
class IosOrderAlarm : OrderAlarm {

    private var player: AVAudioPlayer? = null

    override fun startAlarm() {
        if (player?.playing == true) return
        val url = NSBundle.mainBundle.URLForResource("new_order_alert", withExtension = "caf") ?: return
        AVAudioSession.sharedInstance().apply {
            setCategory(AVAudioSessionCategoryPlayback, error = null)
            setActive(true, error = null)
        }
        player = (player ?: AVAudioPlayer(contentsOfURL = url, error = null)).apply {
            numberOfLoops = -1
            currentTime = 0.0
            play()
        }
    }

    override fun stopAlarm() {
        player?.stop()
    }
}

/** The same key as the Android SharedPreferences store. */
class UserDefaultsSeenTaskStore : SeenTaskStore {

    private val defaults = NSUserDefaults.standardUserDefaults

    override fun load(): MutableSet<Int> =
        defaults.stringArrayForKey(KEY).orEmpty()
            .mapNotNullTo(mutableSetOf()) { (it as? String)?.toIntOrNull() }

    override fun save(ids: Set<Int>) {
        defaults.setObject(ids.map { it.toString() }, forKey = KEY)
    }

    private companion object {
        const val KEY = "seen_task_ids"
    }
}
