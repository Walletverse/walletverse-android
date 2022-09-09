package com.walletverse.ui.ui.activity

import android.view.View
import androidx.lifecycle.lifecycleScope
import com.walletverse.core.Walletverse
import com.walletverse.core.bean.Identity
import com.walletverse.ui.R
import com.walletverse.ui.base.BaseActivity
import com.walletverse.ui.util.ActivityUtil
import com.gyf.immersionbar.ktx.immersionBar
import kotlinx.android.synthetic.main.activity_enter.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * activity for develop
 */
class EnterActivity : BaseActivity() {

    override fun getLayoutId(): Int = R.layout.activity_enter

    override fun initData() {

    }

    override fun initView() {
        immersionBar {
            statusBarColor(R.color.transparent)
            navigationBarColor(R.color.transparent)
            fitsSystemWindows(false)
        }

        btn_web2.setOnClickListener(this)
        btn_web3.setOnClickListener(this)
        btn_dev.setOnClickListener(this)

//        ThreadExecutor.instance?.execute {
//            kotlin.run {
//                val result:MutableList<Coin> = WalletverseDBHelper.mInstance!!.queryWalletCoins("1234")
//                runOnUiThread {
//                    ToastUtil.showSuccess(result.size.toString())
//                }
//            }
//        }

    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.btn_web2 -> {
                lifecycleScope.launch(Dispatchers.IO) {
                    val identities: MutableList<Identity>? =
                        Walletverse.sInstance.queryIdentitiesAsync()
                    if (identities == null || identities.isEmpty()) {
                        ActivityUtil.goActivity(
                            this@EnterActivity,
                            WalletverseWeb2LoginActivity::class.java
                        )
                    } else {
                        ActivityUtil.goNewActivity(
                            this@EnterActivity,
                            HomeActivity::class.java
                        )
                    }
                }
            }
            R.id.btn_web3 -> {
                lifecycleScope.launch(Dispatchers.IO) {
                    val identities: MutableList<Identity>? =
                        Walletverse.sInstance.queryIdentitiesAsync()
                    if (identities == null || identities.isEmpty()) {
                        ActivityUtil.goActivity(
                            this@EnterActivity,
                            WalletverseWeb3LoginActivity::class.java
                        )
                    } else {
                        ActivityUtil.goNewActivity(
                            this@EnterActivity,
                            HomeActivity::class.java
                        )
                    }
                }
            }
            R.id.btn_dev -> {
                ActivityUtil.goActivity(this, DevelopActivity::class.java)
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}