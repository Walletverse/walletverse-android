package com.walletverse.ui.util

import android.os.Handler
import android.os.Looper


/**
 * ================================================================
 * Create_time：2022/7/26  14:00
 * Author：solomon
 * Detail：
 * ================================================================
 */
class GlobalHandler : Handler(Looper.getMainLooper()) {

    companion object {
        val sInstance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            GlobalHandler()
        }
    }

}