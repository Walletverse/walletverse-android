package com.walletverse.ui.ui.activity

import android.view.View
import android.widget.LinearLayout
import com.walletverse.ui.R
import com.walletverse.ui.base.BaseActivity
import com.walletverse.ui.util.ActivityUtil
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.ktx.immersionBar
import kotlinx.android.synthetic.main.activity_walletverse_web3_login.*

//web3.0
class WalletverseWeb3LoginActivity : BaseActivity() {


    override fun getLayoutId(): Int {
        return R.layout.activity_walletverse_web3_login
    }

    override fun initData() {
        v_create_wallet.setOnClickListener(this)
        v_import.setOnClickListener(this)

        v_status_bar.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            ImmersionBar.getStatusBarHeight(this)
        )
    }

    override fun initView() {
        initTitleBar("")
        immersionBar {
            statusBarColor(R.color.transparent)
            fitsSystemWindows(false)
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.v_create_wallet -> {
                ActivityUtil.goActivity(this,WalletverseCreateActivity::class.java)
            }
            R.id.v_import -> {
                ActivityUtil.goActivity(this,WalletverseImportActivity::class.java)
//                DialogUtil.showBottomListDialog(this, list = arrayOf("1","@","3")) { position, text -> ToastUtil.showSuccess(position.toString()) }
            }

        }
    }
}