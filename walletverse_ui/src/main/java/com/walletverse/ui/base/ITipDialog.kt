package com.walletverse.ui.base

import com.walletverse.ui.util.DialogUtil

interface ITipDialog {
    fun showTipDialog(msg:String, onCommonListener: DialogUtil.OnCommonListener)
}