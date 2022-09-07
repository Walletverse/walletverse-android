package com.walletverse.ui.base

import android.view.View



interface IClickPresenter : View.OnClickListener {
    override fun onClick(v: View)
}