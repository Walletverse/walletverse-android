package com.walletverse.ui.ui.activity

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.walletverse.core.Walletverse
import com.walletverse.core.bean.*
import com.walletverse.core.core.ResultCallback
import com.walletverse.core.enums.VM
import com.walletverse.ui.R
import com.walletverse.ui.base.BaseActivity
import com.blankj.utilcode.util.DeviceUtils
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_develop.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * activity for develop
 */
class DevelopActivity : BaseActivity() {
    val TAG = "DevelopActivity"

    override fun getLayoutId(): Int = R.layout.activity_develop

    override fun initData() {

    }

    @SuppressLint("SetTextI18n")
    override fun initView() {
        b_mnemonic.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val mnemonic = Walletverse.sInstance.generateMnemonicAsync(
                    DefaultParams("0x38")
                )
                tx_result.text = mnemonic.phrase
                Log.i(TAG, "Result mnemonic $mnemonic")
            }
        }

        b_balance.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val balance = Walletverse.sInstance.balanceAsync(
                    GetParams("0x38", "0xAeA144E143D90f8B8ebF5153e73813f2FCf3321E", "")
                )
                tx_result.text = balance;
                Log.i(TAG, "Result Balance $balance")
            }
        }
        b_nonce.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val nonce = Walletverse.sInstance.nonceAsync(
                        GetParams("0x38", "0xAeA144E143D90f8B8ebF5153e73813f2FCf3321E", "")
                    )
                    Log.i(TAG, "Result nonce $nonce")
                    tx_result.text = nonce;
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

//            BeFi.sInstance.nonce(GetParams("", "", ""),object :ResultCallback<String>{
//                override fun onError(error: Exception) {
//                    Log.e(TAG, "onError: $error" )
//                }
//
//                override fun onResult(data: Result<String>) {
//                }
//
//            })

        }
        b_decimals.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                val decimals = Walletverse.sInstance.decimalsAsync(
                    GetParams(
                        "0x38",
                        "0xAeA144E143D90f8B8ebF5153e73813f2FCf3321E",
                        "0x55d398326f99059ff775485246999027b3197955"
                    )
                )
                tx_result.text = decimals;
                Log.i(TAG, "Result decimals $decimals")

            }
        }
        b_encodeMessage.setOnClickListener {
            Walletverse.sInstance.encodeMessage(
                EncodeMessageParams("testpassword", "testauth"),
                object : ResultCallback<String> {
                    override fun onResult(data: Result<String>) {
                        tx_result.text = data.getOrNull();
                    }

                    override fun onError(error: Exception) {
                    }
                }
            )
//            CoroutineScope(Dispatchers.Main).launch {
//                val decimals = BeFi.sInstance.encodeMessage(
//                    EncodeMessageParams("testpassword")
//                )
//                tx_result.text = decimals;
//                Log.i(TAG, "Result decimals $decimals")
//
//            }
        }

        b_encodeAuth.setOnClickListener {
            Walletverse.sInstance.encodeAuth(
                EncodeAuthParams("testpassword", DeviceUtils.getUniqueDeviceId()),
                object : ResultCallback<String> {
                    override fun onResult(data: Result<String>) {
                        tx_result.text = data.getOrNull();
                    }

                    override fun onError(error: Exception) {
                    }
                }
            )


//            CoroutineScope(Dispatchers.Main).launch {
//                val decimals = BeFi.sInstance.encodeMessage(
//                    EncodeMessageParams("testpassword")
//                )
//                tx_result.text = decimals;
//                Log.i(TAG, "Result decimals $decimals")
//
//            }
        }
        b_test_net.setOnClickListener {
            Walletverse.sInstance.getSupportChains(GetChainsParams(VM.EVM),
                object : ResultCallback<MutableList<Coin>?> {
                    override fun onResult(data: Result<MutableList<Coin>?>) {
                        Log.i(TAG, "onResult: ${data.getOrNull()?.size}")
                    }

                    override fun onError(error: Exception) {
                        Logger.e(error.toString())
                    }
                })
        }

        b_error.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    Walletverse.sInstance.getTokenListAsync(TokenParams(1, 50, ""))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
//               BeFi.sInstance.requestTokenList(TokenParams(1,50,""),object :ResultCallback<MutableList<Coin>>{
//                   override fun onError(error: Exception) {
//                       Log.e(TAG, "onError: $error")
//                   }
//
//                   override fun onResult(data: Result<MutableList<Coin>>) {
//                   }
//
//               })
            }
        }


        b_sdk_version.setOnClickListener {
            val versionCode=Walletverse.sInstance.getSDKVersionCode()
            val versionName=Walletverse.sInstance.getSDKVersionName()
            tx_result.text="$versionCode--$versionName"
        }
    }
}