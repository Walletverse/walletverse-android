package com.walletverse.ui.util

import android.text.TextUtils


/**
 * ================================================================
 * Create_time：2022/8/5  10:17
 * Author：solomon
 * Detail：
 * ================================================================
 */
object ClickUtil {

    private var lastClickTime: Long = 0
    private var lastButtonId: String? = null

    //        @MainThread
    fun isFastDoubleClick(buttonId: String?): Boolean {
        val time = System.currentTimeMillis()
        val timeD = time - lastClickTime
        if (TextUtils.equals(lastButtonId, buttonId) && 0 < timeD && timeD < 500) {
            return true
        }
        lastClickTime = time
        lastButtonId = buttonId
        return false
    }
}