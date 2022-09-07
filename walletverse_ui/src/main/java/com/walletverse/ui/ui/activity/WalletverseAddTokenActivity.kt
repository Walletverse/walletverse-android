package com.walletverse.ui.ui.activity

import com.walletverse.ui.R
import com.walletverse.ui.base.BaseActivity

class WalletverseAddTokenActivity : BaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_walletverse_add_token
    }

    override fun initData() {

    }

    override fun initView() {
        initTitleBar(getString(R.string.custom_token))
    }


}