package com.walletverse.ui.ui.activity

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.walletverse.core.Walletverse
import com.walletverse.core.bean.*
import com.walletverse.ui.R
import com.walletverse.ui.base.BaseActivity
import com.walletverse.ui.constant.Constants
import com.walletverse.ui.util.ActivityUtil
import com.walletverse.ui.util.ToastUtil
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.StringUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_walletverse_web2_create.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WalletverseWeb2CreateActivity : BaseActivity() {

    private lateinit var mFederatedParams: FederatedParams
    private var mShards: ArrayList<String> = arrayListOf()
    private var mWallets: ArrayList<Coin> = arrayListOf()
    private lateinit var mUserType: String
    private lateinit var mMnemonic: String
    private var walletPin = ""

    override fun getLayoutId(): Int {
        return R.layout.activity_walletverse_web2_create
    }

    override fun initData() {
        val bundle = intent.extras
        mUserType = bundle?.getString("userType")!!
        mFederatedParams = (bundle.getSerializable("federated") as FederatedParams?)!!
        if (mUserType == "new") {
            initTitleBar(getString(R.string.create_wallet))
            mMnemonic = bundle.getString("mnemonic")!!
            v_create.setText(getString(R.string.create))
            v_wallet_password_tip1.visibility = View.VISIBLE
            v_wallet_password_tip2.visibility = View.VISIBLE
            v_check_wallet_password.visibility = View.VISIBLE
        } else {
            initTitleBar(getString(R.string.restore_wallet))
            mShards = bundle.getStringArrayList("shards")!!

            val walletsJson = bundle.getString("wallets")
            mWallets = Gson().fromJson<ArrayList<Coin>>(
                walletsJson,
                object : TypeToken<ArrayList<Coin>>() {}.type
            )
            v_create.setText(getString(R.string.restore))
            v_wallet_password_tip1.visibility = View.GONE
            v_wallet_password_tip2.visibility = View.GONE
            v_check_wallet_password.visibility = View.GONE
        }

    }

    override fun initView() {
        walletPin =SPUtils.getInstance().getString(Constants.PWD)
        if (walletPin.isNotEmpty()) {
            v_pin_tip.visibility = View.GONE
            v_pin.visibility = View.GONE
            v_check_pin.visibility = View.GONE
        }
        v_create.setOnClickListener(this)
        v_wallet_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.isNullOrEmpty()) {
                    v_wallet_password_tip1.helper.textColorNormal = getColor(R.color.color_tv2)
                    v_wallet_password_tip1.helper.iconNormalLeft =
                        ContextCompat.getDrawable(this@WalletverseWeb2CreateActivity, R.mipmap.icon_close)
                    v_wallet_password_tip2.helper.textColorNormal = getColor(R.color.color_tv2)
                    v_wallet_password_tip2.helper.iconNormalLeft =
                        ContextCompat.getDrawable(this@WalletverseWeb2CreateActivity, R.mipmap.icon_close)
                } else {
                    if (p0.length < 8) {
                        v_wallet_password_tip1.helper.textColorNormal = getColor(R.color.color_red)
                        v_wallet_password_tip1.helper.iconNormalLeft = ContextCompat.getDrawable(
                            this@WalletverseWeb2CreateActivity,
                            R.mipmap.icon_close_red
                        )
                        v_wallet_password_tip2.helper.textColorNormal = getColor(R.color.color_red)
                        v_wallet_password_tip2.helper.iconNormalLeft = ContextCompat.getDrawable(
                            this@WalletverseWeb2CreateActivity,
                            R.mipmap.icon_close_red
                        )
                    } else {
                        v_wallet_password_tip1.helper.textColorNormal =
                            getColor(R.color.color_green)
                        v_wallet_password_tip1.helper.iconNormalLeft = ContextCompat.getDrawable(
                            this@WalletverseWeb2CreateActivity,
                            R.mipmap.icon_check_green
                        )

                        if (Walletverse.sInstance.validatePassword(p0.toString())) {
                            v_wallet_password_tip2.helper.textColorNormal =
                                getColor(R.color.color_green)
                            v_wallet_password_tip2.helper.iconNormalLeft =
                                ContextCompat.getDrawable(
                                    this@WalletverseWeb2CreateActivity,
                                    R.mipmap.icon_check_green
                                )
                        } else {
                            v_wallet_password_tip2.helper.textColorNormal =
                                getColor(R.color.color_red)
                            v_wallet_password_tip2.helper.iconNormalLeft =
                                ContextCompat.getDrawable(
                                    this@WalletverseWeb2CreateActivity,
                                    R.mipmap.icon_close_red
                                )
                        }
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.v_create -> {
                if (commitPre()) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        try {
                            showLoading()
                            if(walletPin.isEmpty()){
                                walletPin=Walletverse.sInstance.encodeAuthAsync(EncodeAuthParams(v_pin.text.toString(),DeviceUtils.getUniqueDeviceId()))
                            }

                            val success:Boolean
                            if (mUserType == "new") {
                                 success =Walletverse.sInstance.createWeb2WalletAsync(
                                    CreateWeb2Params(
                                        mMnemonic,
                                        mWallets,
                                        v_name.text.toString(),
                                        walletPin,
                                        v_wallet_password.text.toString(),
                                        mFederatedParams
                                    )
                                )
                            } else {
                                 success =Walletverse.sInstance.restoreWeb2WalletAsync(
                                    RestoreWeb2Params(
                                        mShards,
                                        mWallets,
                                        v_name.text.toString(),
                                        walletPin,
                                        v_wallet_password.text.toString(),
                                        mFederatedParams
                                    )
                                )

                            }
                            hideLoading()
                            if(success){
                                SPUtils.getInstance().put(Constants.PWD, walletPin)
                                SPUtils.getInstance().put(Constants.CURRENT_WID, Walletverse.sInstance.generateWidWithWeb2(mFederatedParams))
                                //go main
                                ActivityUtil.goNewActivity(
                                    this@WalletverseWeb2CreateActivity,
                                    HomeActivity::class.java
                                )
                            }else{
                                if(mUserType=="new"){
                                    ToastUtil.showError(getString(R.string.failed_to_create_wallet))
                                }else {
                                    ToastUtil.showError(getString(R.string.failed_to_restore_wallet))
                                }
                            }
                        } catch (e: Exception) {
                            hideLoading()
                            if(mUserType=="new"){
                                ToastUtil.showError(getString(R.string.failed_to_create_wallet))
                            }else {
                                ToastUtil.showError(getString(R.string.failed_to_restore_wallet))
                            }
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }


    private fun commitPre(): Boolean {
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
        if (StringUtils.isEmpty(v_wallet_password.text.toString().trim())) {
            ToastUtil.showError(getString(R.string.please_input_wallet_password))
            return false
        }
        if (mUserType == "new") {
            if (v_wallet_password.text.toString().isEmpty() ||
                v_wallet_password.text.toString().length < 8 ||
                !Walletverse.sInstance.validatePassword(v_wallet_password.text.toString())
            ) {
                ToastUtil.showError(getString(R.string.wallet_password_error))
                return false
            }

            if (v_wallet_password.text.toString().trim() != v_check_wallet_password.text.toString()
                    .trim()
            ) {
                ToastUtil.showError(getString(R.string.wallet_password_not_match))
                return false
            }
        }

        return true
    }

}