package com.walletverse.ui.ui.activity

import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.walletverse.core.Walletverse
import com.walletverse.core.bean.*
import com.walletverse.core.enums.EChain
import com.walletverse.ui.R
import com.walletverse.ui.base.BaseActivity
import com.walletverse.ui.constant.Constants
import com.walletverse.ui.util.ActivityUtil
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.StringUtils
import kotlinx.android.synthetic.main.activity_walletverse_verify.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class WalletverseVerifyActivity : BaseActivity() {

    private lateinit var mFirstVerifyMnemonic: VerifyMnemonic
    private lateinit var mSecondVerifyMnemonic: VerifyMnemonic

    private lateinit var walletName: String
    private lateinit var walletPin: String
    private lateinit var mnemonic: String

    override fun getLayoutId(): Int {
        return R.layout.activity_walletverse_verify
    }

    override fun initData() {
        val bundle = intent.extras
        val userCreateData: UserCreateData =
            bundle?.getSerializable("user_create_data") as UserCreateData

        walletName = userCreateData.name
        walletPin = userCreateData.pin
        lifecycleScope.launch(Dispatchers.Main) {
            walletPin = if (StringUtils.isEmpty(walletPin)) {
                SPUtils.getInstance().getString(Constants.PWD)
            } else {
                Walletverse.sInstance.encodeAuthAsync(EncodeAuthParams(walletPin, DeviceUtils.getUniqueDeviceId()))
            }
        }
        mnemonic = userCreateData.mnemonic
        mFirstVerifyMnemonic = userCreateData.verifyMnemonics[0]
        mSecondVerifyMnemonic = userCreateData.verifyMnemonics[1]

//        Logger.e("$walletName\n$walletPin\n${mFirstVerifyMnemonic}\n${mSecondVerifyMnemonic}")

        v_first_num.text = Html.fromHtml(
            String.format(
                getString(
                    R.string.please_input_word,
                    mFirstVerifyMnemonic.num
                )
            )
        )
        v_second_num.text = Html.fromHtml(
            String.format(
                getString(
                    R.string.please_input_word,
                    mSecondVerifyMnemonic.num
                )
            )
        )

    }

    override fun initView() {
        initTitleBar(getString(R.string.verify_mnemonic))
        v_confirm.setOnClickListener(this)
        v_confirm.setEnable(false)
        v_confirm.isClickable = false

        v_first_input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                v_first_input.helper.borderColorNormal =
                    ContextCompat.getColor(this@WalletverseVerifyActivity, R.color.color_bg3)

                if (!StringUtils.isEmpty(s.toString()) && !StringUtils.isEmpty(
                        v_second_input.text?.toString()?.trim()
                    )
                ) {
                    v_confirm.setEnable(true)
                    v_confirm.isClickable = true
                } else {
                    v_confirm.setEnable(false)
                    v_confirm.isClickable = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        v_second_input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                v_second_input.helper.borderColorNormal =
                    ContextCompat.getColor(this@WalletverseVerifyActivity, R.color.color_bg3)

                if (!StringUtils.isEmpty(s.toString()) && !StringUtils.isEmpty(
                        v_first_input.text?.toString()?.trim()
                    )
                ) {
                    v_confirm.setEnable(true)
                    v_confirm.isClickable = true
                } else {
                    v_confirm.setEnable(false)
                    v_confirm.isClickable = false
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }


    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.v_confirm -> {

                if (mFirstVerifyMnemonic.word != v_first_input.text?.toString()?.trim()) {
                    v_first_input.helper.borderColorNormal =
                        ContextCompat.getColor(this, R.color.color_red)
                    return
                }
                if (mSecondVerifyMnemonic.word != v_second_input.text?.toString()?.trim()) {
                    v_second_input.helper.borderColorNormal =
                        ContextCompat.getColor(this, R.color.color_red)
                    return
                }


                lifecycleScope.launch(Dispatchers.Main) {
                    try {
                        showLoading()

                        val privateKey =
                            Walletverse.sInstance.getPrivateKeyAsync(GetPrivateKeyParams("0x1", mnemonic))

                        if (privateKey.isEmpty()) {
                            hideLoading()
                            return@launch
                        }

                        val address =
                            Walletverse.sInstance.getAddressAsync(GetAddressParams("0x1", privateKey))

                        if (privateKey.isEmpty()) {
                            hideLoading()
                            return@launch
                        }

                        val token =Walletverse.sInstance.encodeMessageAsync(EncodeMessageParams(mnemonic, walletPin))

                        if (token.isEmpty()) {
                            hideLoading()
                            return@launch
                        }

                        val wid = Walletverse.sInstance.generateWidAsync(Constants.APPID)
                        if(wid.isNullOrEmpty()) {
                            hideLoading()
                            return@launch
                        }

                        val identity = Identity()
                        identity.wid = wid
                        identity.name = walletName
                        identity.tokenType = "mnemonic"
                        identity.token = token


                        val coin = Walletverse.sInstance.initChainAsync(InitChainParams(wid, address, privateKey,walletPin, EChain.ETH))

                        if (coin==null||coin.chainId.isEmpty()) {
                            hideLoading()
                            return@launch
                        }


                        withContext(Dispatchers.IO) {
                            Walletverse.sInstance.insertIdentityAsync(identity)
                        }

                        val result =Walletverse.sInstance.saveWalletCoinAsync(SaveCoinParams(wid,walletPin,coin))
                        if (!result) {
                            hideLoading()
                            return@launch
                        }
                        SPUtils.getInstance().put(Constants.PWD, walletPin)
                        SPUtils.getInstance().put(Constants.CURRENT_WID, wid)


                        hideLoading()
                        //go main
                        ActivityUtil.goNewActivity(
                            this@WalletverseVerifyActivity,
                            HomeActivity::class.java
                        )
                    } catch (e: Exception) {
                        hideLoading()
                        e.printStackTrace()
                    }

                }

            }
        }
    }

}