//package com.ovasta.logisticsapp.base.ext
//
//class OrderVibrator(private val context: Context) {
//
//    fun vibrateNewOrder() {
//        val vibrator =
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                val manager =
//                    context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE)
//                            as VibratorManager
//                manager.defaultVibrator
//            } else {
//                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
//            }
//
//        if (!vibrator.hasVibrator()) return
//
//        val effect = VibrationEffect.createWaveform(
//            longArrayOf(0, 200, 100, 200), // delay, vibrate, pause, vibrate
//            -1
//        )
//
//        vibrator.vibrate(effect)
//    }
//}
