package com.walletverse.ui.util

import android.app.Activity
import android.content.Context
import android.graphics.RectF
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager


/**
 * ================================================================
 * Create_time：2022/8/4  22:56
 * Author：solomon
 * Detail：
 * ================================================================
 */
object KeyboardUtil {
    fun hideSoftKeyboard(activity: Activity, event: MotionEvent) {
        try {
            if (event.action == MotionEvent.ACTION_DOWN) {
                val focusView: View? = activity.currentFocus
                if (focusView?.windowToken != null && !isTouchView(
                        event,
                        focusView
                    )
                ) {
                    hideSoftInputFromWindow(activity)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isTouchView(event: MotionEvent?, focusView: View?): Boolean {
        if (null == event || null == focusView) {
            return false
        }
        val x = event.x
        val y = event.y
        val outLocation = IntArray(2)
        focusView.getLocationOnScreen(outLocation)
        val rectF = RectF(
            outLocation[0].toFloat(),
            outLocation[1].toFloat(),
            (outLocation[0] + focusView.width).toFloat(),
            (outLocation[1] + focusView.height).toFloat()
        )
        return x >= rectF.left && x <= rectF.right && y >= rectF.top && y <= rectF.bottom
    }


    private fun hideSoftInputFromWindow(activity: Activity) {
        val parentImm: InputMethodManager =
            activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (parentImm.isActive) {
            val view = activity.currentFocus
            if (null != view) {
                parentImm.hideSoftInputFromWindow(
                    view.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        }
    }
}