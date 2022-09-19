package com.walletverse.ui.ui.fragment

import android.view.View
import androidx.lifecycle.lifecycleScope
import com.walletverse.core.Walletverse
import com.walletverse.core.bean.*
import com.walletverse.core.enums.Type
import com.walletverse.ui.R
import com.walletverse.ui.base.BaseFragment
import com.walletverse.ui.constant.Constants
import com.walletverse.ui.ui.activity.WalletverseSettingActivity
import com.walletverse.ui.util.ActivityUtil
import com.walletverse.ui.util.DialogUtil
import com.walletverse.ui.util.ToastUtil
import com.walletverse.ui.view.PasswordBoard
import com.blankj.utilcode.util.SPUtils
import kotlinx.android.synthetic.main.fragment_me.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * ================================================================
 * Create_time：2022/7/29  12:22
 * Author：solomon
 * Detail：
 * ================================================================
 */
class MeFragment : BaseFragment() {
    override fun getLayoutId(): Int = R.layout.fragment_me

    override fun initData() {

    }

    override fun initView() {
        v_setting.setOnClickListener(this)
        v_export.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.v_setting -> {
                ActivityUtil.goActivity(requireActivity(),WalletverseSettingActivity::class.java)
            }
            R.id.v_export -> {
                DialogUtil.showPasswordDialog(requireActivity(),
                    object : DialogUtil.OnCompletedListener {
                        override fun passwordComplete(pass: String, dialog: PasswordBoard) {
                            val pwd = SPUtils.getInstance().getString(Constants.PWD)
                            if (pwd == pass) {
                                dialog.dismiss()
                                showLoading()
                                lifecycleScope.launch(Dispatchers.IO) {
                                    try {
                                        val result = mutableListOf<String>()
                                        val identities: MutableList<Identity>? =
                                            Walletverse.sInstance.queryIdentitiesAsync()
                                        identities?.forEach { it ->
                                            val name: String = it.name
                                            val coins: MutableList<Coin>? =
                                                Walletverse.sInstance.queryWalletCoinsAsync(GetWalletCoinParams(it.wid))
                                            coins?.forEach { it2 ->
                                                if (it2.type==Type.CHAIN) {
                                                    withContext(Dispatchers.Main){
                                                        val chain = it2.contract
                                                        val privateKey = Walletverse.sInstance.decodeMessageAsync(
                                                            DecodeMessageParams(it2.privateKey,pass)
                                                        )
                                                        result.add("$name@$chain: $privateKey")
                                                    }
                                                }
                                            }
                                        }
                                        withContext(Dispatchers.Main) {
                                            hideLoading()
                                            DialogUtil.showExportDialog(requireActivity(), result)
                                        }
                                    } catch (e: Exception) {
                                        hideLoading()
                                        e.printStackTrace()
                                    }

                                }
                            }else {
                                ToastUtil.showError(getString(R.string.wrong_pin))
                            }
                        }

                        override fun onCancel() {
                        }

                    })
            }
        }
    }
}