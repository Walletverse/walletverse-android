package com.walletverse.ui.ui.fragment

import android.view.View
import com.walletverse.ui.R
import com.walletverse.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_dapp.*


/**
 * ================================================================
 * Create_time：2022/7/29  12:22
 * Author：solomon
 * Detail：
 * ================================================================
 */
class DAppFragment : BaseFragment() {
    override fun getLayoutId(): Int = R.layout.fragment_dapp

    override fun initData() {
    }

    override fun initView() {
        v_dapp.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.v_dapp -> {

            }
        }
    }
}