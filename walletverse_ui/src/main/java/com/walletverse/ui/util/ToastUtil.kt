package com.walletverse.ui.util

import com.walletverse.ui.WalletverseApplication
import es.dmoral.toasty.Toasty


/**
 * ================================================================
 * Create_time：2022/7/25  18:29
 * Author：solomon
 * Detail：
 * ================================================================
 */
object ToastUtil {

    fun showSuccess(msg: String, duration: Int = 0, withIcon: Boolean = true) {
        Toasty.success(WalletverseApplication.context, msg, duration, withIcon).show()
    }

    fun showError(msg: String, duration: Int = 0, withIcon: Boolean = true) {
        Toasty.error(WalletverseApplication.context, msg, duration, withIcon).show()
    }

}