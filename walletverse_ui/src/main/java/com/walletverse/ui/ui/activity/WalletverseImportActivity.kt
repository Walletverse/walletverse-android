package com.walletverse.ui.ui.activity

import android.view.View
import androidx.lifecycle.lifecycleScope
import com.walletverse.core.Walletverse
import com.walletverse.core.bean.*
import com.walletverse.core.enums.EChain
import com.walletverse.ui.R
import com.walletverse.ui.base.BaseActivity
import com.walletverse.ui.constant.Constants
import com.walletverse.ui.util.ActivityUtil
import com.walletverse.ui.util.ToastUtil
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.StringUtils
import kotlinx.android.synthetic.main.activity_walletverse_import.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WalletverseImportActivity : BaseActivity() {
    private var tokenType = ""
    private var content = ""
    private var walletPin = ""
    private var walletName = ""

    override fun getLayoutId(): Int {
        return R.layout.activity_walletverse_import
    }

    override fun initData() {
    }

    override fun initView() {
        initTitleBar(getString(R.string.import_wallet))
        walletPin = SPUtils.getInstance().getString(Constants.PWD)
        if (walletPin.isNotEmpty()) {
            v_pin_tip.visibility = View.GONE
            v_pin.visibility = View.GONE
            v_check_pin.visibility = View.GONE
        }
        v_clear.setOnClickListener(this)
        v_import.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.v_clear -> {
                v_content.setText("")
            }
            R.id.v_import -> {
                if (commitPre()) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        try {
                            showLoading()
                            content = v_content.text.toString()
                            walletName = v_name.text.toString()
                            tokenType = if (content.split(" ").size > 1) {
                                "mnemonic"
                            } else {
                                "private"
                            }

                            if (walletPin.isEmpty()) {
                                walletPin =
                                    Walletverse.sInstance.encodeAuthAsync(
                                        EncodeAuthParams(
                                            v_pin.text.toString(),
                                            DeviceUtils.getUniqueDeviceId()
                                        )
                                    )
                            }

                            if (walletPin.isEmpty()) {
                                hideLoading()
                                return@launch
                            }

                            //if mnemonic need getPrivateKey
                            var privateKey = content
                            if (tokenType == "mnemonic") {
                                privateKey = Walletverse.sInstance.getPrivateKeyAsync(
                                    GetPrivateKeyParams(
                                        "0x1",
                                        content
                                    )
                                )
                            }

                            if (privateKey.isEmpty()) {
                                hideLoading()
                                ToastUtil.showError(getString(R.string.import_error_tip))
                                return@launch
                            }

                            val address =
                                Walletverse.sInstance.getAddressAsync(GetAddressParams("0x1", privateKey))

                            if (address.isEmpty()) {
                                hideLoading()
                                ToastUtil.showError(getString(R.string.import_error_tip))
                                return@launch
                            }

                            val token = Walletverse.sInstance.encodeMessageAsync(
                                EncodeMessageParams(
                                    content,
                                    walletPin
                                )
                            )

                            if (token.isEmpty()) {
                                hideLoading()
                                ToastUtil.showError(getString(R.string.import_error_tip))
                                return@launch
                            }

                            val wid: String? = Walletverse.sInstance.generateWidAsync(Constants.APPID)

                            if (wid.isNullOrEmpty()) {
                                hideLoading()
                                return@launch
                            }

                            val identity = Identity()
                            identity.wid = wid
                            identity.name = walletName
                            identity.tokenType = tokenType
                            identity.token = token


                            //init chain
                            val coin = Walletverse.sInstance.initChainAsync(
                                InitChainParams(
                                    wid,
                                    address,
                                    privateKey,
                                    walletPin,
                                    EChain.ETH
                                )
                            )

                            if (coin == null || coin.chainId.isEmpty()) {
                                hideLoading()
                                ToastUtil.showError(getString(R.string.import_error_tip))
                                return@launch
                            }

                            withContext(Dispatchers.IO) {
                                Walletverse.sInstance.insertIdentityAsync(identity)
                            }

                            val result =
                                Walletverse.sInstance.saveWalletCoinAsync(SaveCoinParams(wid, walletPin, coin))

                            if (!result) {
                                hideLoading()
                                ToastUtil.showError(getString(R.string.import_error_tip))
                                return@launch
                            }

                            SPUtils.getInstance().put(Constants.PWD, walletPin)
                            SPUtils.getInstance().put(Constants.CURRENT_WID, wid)


                            hideLoading()
                            //go main
                            ActivityUtil.goNewActivity(
                                this@WalletverseImportActivity,
                                HomeActivity::class.java
                            )

                        } catch (e: Exception) {
                            hideLoading()
                            ToastUtil.showError(getString(R.string.import_error_tip))
                        }
                    }
                }
            }
        }
    }

    private fun commitPre(): Boolean {
        if (StringUtils.isEmpty(v_content.text.toString().trim())) {
            ToastUtil.showError(getString(R.string.please_enter_mnemonic_or_private_key))
            return false
        }
        if (StringUtils.isEmpty(v_name.text.toString().trim())) {
            ToastUtil.showError(getString(R.string.please_enter_name))
            return false
        }
        if (walletPin.isEmpty()) {
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