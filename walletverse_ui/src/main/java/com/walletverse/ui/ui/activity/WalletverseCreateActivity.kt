package com.walletverse.ui.ui.activity

import android.os.Bundle
import android.view.View
import com.walletverse.ui.R
import com.walletverse.ui.base.BaseActivity
import com.walletverse.ui.constant.Constants
import com.walletverse.ui.util.ActivityUtil
import com.walletverse.ui.util.ToastUtil
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.StringUtils
import kotlinx.android.synthetic.main.activity_walletverse_create.*

class WalletverseCreateActivity : BaseActivity() {

    private var hasPin=false

    override fun getLayoutId(): Int {
        return R.layout.activity_walletverse_create
    }

    override fun initData() {
        v_create.setOnClickListener(this)

    }

    override fun initView() {
        initTitleBar(getString(R.string.create_wallet))
        hasPin=!StringUtils.isEmpty(SPUtils.getInstance().getString(Constants.PWD))
        if(hasPin){
            v_pin_tip.visibility=View.GONE
            v_pin.visibility=View.GONE
            v_check_pin.visibility=View.GONE
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.v_create -> {
                if (commitPre()) {
                    val bundle = Bundle()
                    bundle.putString("name", v_name.text.toString().trim())
                    bundle.putString("pin", v_pin.text.toString().trim())
                    ActivityUtil.goActivity(this, WalletverseMnemonicActivity::class.java, bundle)
                }
            }
        }
    }


    private fun commitPre(): Boolean {
        if (StringUtils.isEmpty(v_name.text.toString().trim())) {
            ToastUtil.showError(getString(R.string.please_enter_name))
            return false
        }
        if(!hasPin) {
            if (StringUtils.isEmpty(v_pin.text.toString().trim())) {
                ToastUtil.showError(getString(R.string.please_input_6_pin))
                return false
            }

            if (v_pin.text.toString().trim() != v_check_pin.text.toString().trim()) {
                ToastUtil.showError(getString(R.string.pins_not_match))
                return false
            }
        }

        return true
    }

}