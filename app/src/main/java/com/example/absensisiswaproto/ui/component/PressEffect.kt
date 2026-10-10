package com.example.absensisiswaproto.ui.component

import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View

object PressEffect {

    fun attach(view: View, scale: Float = 0.94f) {
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(scale).scaleY(scale).setDuration(80).start()
                    v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    false
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
                    false
                }
                else -> false
            }
        }
    }
}