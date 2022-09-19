package com.walletverse.ui.ui.activity

import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.SPUtils
import com.walletverse.core.Walletverse
import com.walletverse.core.bean.*
import com.walletverse.core.core.ResultCallback
import com.walletverse.core.enums.DAppMethod
import com.walletverse.ui.R
import com.walletverse.ui.base.BaseActivity
import com.walletverse.ui.bean.DAppTransferParams
import com.walletverse.ui.constant.Constants
import com.walletverse.ui.core.getFee
import com.walletverse.ui.core.getGwei
import com.walletverse.ui.util.DialogUtil
import com.walletverse.ui.util.ToastUtil
import com.walletverse.ui.view.PasswordBoard
import kotlinx.android.synthetic.main.activity_walletverse_dapp.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.util.*
import kotlin.math.pow

class WalletverseDAppActivity : BaseActivity() {

    private lateinit var dapp: DApp


    override fun getLayoutId(): Int {
        return R.layout.activity_walletverse_dapp
    }


    override fun initView() {

        val bundle = intent.extras
        dapp = bundle?.getSerializable("dapp") as DApp

        initTitleBar(dapp.name)
    }


    override fun initData() {

        v_webview.loadDApp(dapp)

        v_webview.setResultCallback(object : ResultCallback<String> {
            override fun onError(error: Exception) {

            }

            override fun onResult(data: Result<String>) {
                val dappData = data.getOrNull()
                if (!dappData.isNullOrEmpty()) {
                    val result = JSONObject(dappData)
                    val id: String? = result.optString("id")
                    val chain: String = result.optString("chain").uppercase(Locale.getDefault())
                    var symbol = chain
                    val method: String? = result.optString("method")
                    val dataStr: String? = result.optString("data")
                    val dataObj = dataStr?.let { JSONObject(it) }
                    if (!dataObj?.optString("symbol").isNullOrEmpty()) {
                        symbol = dataObj!!.optString("symbol").uppercase(Locale.getDefault())
                    }

                    Log.e("WalletverseDAppWebView", "postMessage: ----${method}")

                    when (method) {
                        DAppMethod.METHOD_DAPPS_SIGN.method -> {
                            lifecycleScope.launch(Dispatchers.Main) {
                                showLoading()
                                val decimal =
                                    Walletverse.sInstance.decimalsAsync(GetParams(dapp.chain.chainId))
                                Log.e("walletverse", "onResult: ----${decimal}----")
                                var value = "0"
                                if (!dataObj?.optString("value").isNullOrEmpty()) {
                                    value = valueTransformByDecimal(
                                        dataObj?.optString("value")!!,
                                        decimal
                                    )
                                }

                                val fee: GasFee? = Walletverse.sInstance.feeAsync(
                                    FeeParams(
                                        dapp.chain.chainId,
                                        dataObj?.optString("from"),
                                        dataObj?.optString("to"),
                                        value,
                                        decimal,
                                        dataObj?.optString("data")
                                    )
                                )
                                Log.e(
                                    "walletverse",
                                    "onResult: ----${value}----}",
                                )
                                hideLoading()

                                val dAppTransferParams = DAppTransferParams(
                                    value,
                                    symbol,
                                    chain,
                                    dataObj!!.optString("from"),
                                    dataObj.optString("to"),
                                    getFee(fee!!.gasPrice, fee.gasLimit, decimal.toInt()),
                                    "GasPrice(${getGwei(fee.gasPrice)}GWEI)*GasLimit(${fee.gasLimit})"
                                )
                                DialogUtil.showDAppTransferDialog(this@WalletverseDAppActivity,
                                    dAppTransferParams,
                                    object : DialogUtil.OnDAppActionListener {
                                        var isConfirmPwd = false
                                        override fun onConfirm() {
                                            isConfirmPwd = true
                                            DialogUtil.showPasswordDialog(this@WalletverseDAppActivity,
                                                object : DialogUtil.OnCompletedListener {
                                                    override fun passwordComplete(
                                                        pass: String,
                                                        dialog: PasswordBoard
                                                    ) {
                                                        lifecycleScope.launch(Dispatchers.Main) {
                                                            val pwd =
                                                                SPUtils.getInstance().getString(
                                                                    Constants.PWD
                                                                )
                                                            if (pwd == pass) {
                                                                dialog.dismiss()
                                                                showLoading()
                                                                val coin: Coin? =
                                                                    Walletverse.sInstance.queryWalletCoinAsync(
                                                                        GetWalletCoinParams(
                                                                            SPUtils.getInstance()
                                                                                .getString(Constants.CURRENT_WID),
                                                                            chain,
                                                                            symbol
                                                                        )
                                                                    )
                                                                val privateKey =
                                                                    Walletverse.sInstance.decodeMessageAsync(
                                                                        DecodeMessageParams(
                                                                            coin!!.privateKey,
                                                                            pass
                                                                        )
                                                                    )
                                                                val nonce =
                                                                    Walletverse.sInstance.nonceAsync(
                                                                        GetParams(
                                                                            dapp.chain.chainId,
                                                                            coin.address
                                                                        )
                                                                    )

                                                                try {

                                                                    val sign =
                                                                        Walletverse.sInstance.signDAppTransactionAsync(
                                                                            SignTransactionParams(
                                                                                dapp.chain.chainId,
                                                                                privateKey,
                                                                                dataObj.optString("to"),
                                                                                value,
                                                                                decimal.toInt(),
                                                                                fee.gasPrice,
                                                                                fee.gasLimit,
                                                                                nonce,
                                                                                dataObj.optString("data")
                                                                            )
                                                                        )

                                                                    Log.e(
                                                                        "walletverse",
                                                                        "--sign--${sign}"
                                                                    )

                                                                    Log.e(
                                                                        "walletverse",
                                                                        TransactionParams(
                                                                            dapp.chain.chainId,
                                                                            dataObj.optString("from"),
                                                                            dataObj.optString("to"),
                                                                            sign,
                                                                            value
                                                                        ).toString(),
                                                                    )

                                                                    v_webview.jsCallback(
                                                                        id!!,
                                                                        "",
                                                                        sign
                                                                    )
                                                                    hideLoading()
                                                                } catch (e: Exception) {
                                                                    v_webview.jsCallback(
                                                                        id!!,
                                                                        "",
                                                                        ""
                                                                    )
                                                                    hideLoading()
                                                                }

                                                            } else {
                                                                ToastUtil.showError(getString(R.string.wrong_pin))
                                                            }
                                                        }
                                                    }

                                                    override fun onCancel() {
                                                        v_webview.jsCallback(id!!, "", "")
                                                    }

                                                })
                                        }

                                        override fun onCancel() {
                                            if (!isConfirmPwd) {
                                                v_webview.jsCallback(id!!, "", "")
                                            }
                                        }

                                    }
                                )

                            }
                        }
                        DAppMethod.METHOD_DAPPS_SIGN_SEND.method -> {
                            lifecycleScope.launch(Dispatchers.Main) {
                                showLoading()
                                val decimal =
                                    Walletverse.sInstance.decimalsAsync(GetParams(dapp.chain.chainId))
                                Log.e("walletverse", "onResult: ----${decimal}----")
                                var value = "0"
                                if (!dataObj?.optString("value").isNullOrEmpty()) {
                                    value = valueTransformByDecimal(
                                        dataObj?.optString("value")!!,
                                        decimal
                                    )
                                }

                                val fee: GasFee? = Walletverse.sInstance.feeAsync(
                                    FeeParams(
                                        dapp.chain.chainId,
                                        dataObj?.optString("from"),
                                        dataObj?.optString("to"),
                                        value,
                                        decimal,
                                        dataObj?.optString("data"),
                                    )
                                )
                                Log.e(
                                    "walletverse",
                                    "onResult: ----${value}----}",
                                )
                                hideLoading()

                                val dAppTransferParams = DAppTransferParams(
                                    value,
                                    symbol,
                                    chain,
                                    dataObj!!.optString("from"),
                                    dataObj.optString("to"),
                                    getFee(fee!!.gasPrice, fee.gasLimit, decimal.toInt()),
                                    "GasPrice(${getGwei(fee.gasPrice)}GWEI)*GasLimit(${fee.gasLimit})"
                                )
                                DialogUtil.showDAppTransferDialog(this@WalletverseDAppActivity,
                                    dAppTransferParams,
                                    object : DialogUtil.OnDAppActionListener {
                                        var isConfirmPwd = false
                                        override fun onConfirm() {
                                            isConfirmPwd = true
                                            DialogUtil.showPasswordDialog(this@WalletverseDAppActivity,
                                                object : DialogUtil.OnCompletedListener {
                                                    override fun passwordComplete(
                                                        pass: String,
                                                        dialog: PasswordBoard
                                                    ) {
                                                        lifecycleScope.launch(Dispatchers.Main) {
                                                            val pwd =
                                                                SPUtils.getInstance().getString(
                                                                    Constants.PWD
                                                                )
                                                            if (pwd == pass) {
                                                                dialog.dismiss()
                                                                showLoading()
                                                                val coin: Coin? =
                                                                    Walletverse.sInstance.queryWalletCoinAsync(
                                                                        GetWalletCoinParams(
                                                                            SPUtils.getInstance()
                                                                                .getString(Constants.CURRENT_WID),
                                                                            chain,
                                                                            symbol
                                                                        )
                                                                    )
                                                                val privateKey =
                                                                    Walletverse.sInstance.decodeMessageAsync(
                                                                        DecodeMessageParams(
                                                                            coin!!.privateKey,
                                                                            pass
                                                                        )
                                                                    )
                                                                val nonce =
                                                                    Walletverse.sInstance.nonceAsync(
                                                                        GetParams(
                                                                            dapp.chain.chainId,
                                                                            coin.address
                                                                        )
                                                                    )

                                                                try {

                                                                    val sign =
                                                                        Walletverse.sInstance.signDAppTransactionAsync(
                                                                            SignTransactionParams(
                                                                                dapp.chain.chainId,
                                                                                privateKey,
                                                                                dataObj.optString("to"),
                                                                                value,
                                                                                decimal.toInt(),
                                                                                fee.gasPrice,
                                                                                fee.gasLimit,
                                                                                nonce,
                                                                                dataObj.optString("data")
                                                                            )
                                                                        )

                                                                    Log.e(
                                                                        "walletverse",
                                                                        "--sign--${sign}"
                                                                    )


                                                                    val hash =
                                                                        Walletverse.sInstance.transactionAsync(
                                                                            TransactionParams(
                                                                                dapp.chain.chainId,
                                                                                dataObj.optString("from"),
                                                                                dataObj.optString("to"),
                                                                                sign,
                                                                                value
                                                                            )
                                                                        )

                                                                    Log.e(
                                                                        "walletverse",
                                                                        "--hash--${hash}"
                                                                    )

                                                                    v_webview.jsCallback(
                                                                        id!!,
                                                                        "",
                                                                        hash
                                                                    )
                                                                    hideLoading()
                                                                } catch (e: Exception) {
                                                                    v_webview.jsCallback(
                                                                        id!!,
                                                                        "",
                                                                        ""
                                                                    )
                                                                    hideLoading()
                                                                }

                                                            } else {
                                                                ToastUtil.showError(getString(R.string.wrong_pin))
                                                            }
                                                        }
                                                    }

                                                    override fun onCancel() {
                                                        v_webview.jsCallback(id!!, "", "")
                                                    }

                                                })
                                        }

                                        override fun onCancel() {
                                            if (!isConfirmPwd) {
                                                v_webview.jsCallback(id!!, "", "")
                                            }
                                        }

                                    }
                                )

                            }
                        }
                        DAppMethod.METHOD_DAPPS_SIGN_MESSAGE.method -> {
                            lifecycleScope.launch(Dispatchers.Main) {
                                DialogUtil.showPasswordDialog(this@WalletverseDAppActivity,
                                    object : DialogUtil.OnCompletedListener {
                                        override fun passwordComplete(
                                            pass: String,
                                            dialog: PasswordBoard
                                        ) {
                                            lifecycleScope.launch(Dispatchers.Main) {
                                                val pwd =
                                                    SPUtils.getInstance().getString(
                                                        Constants.PWD
                                                    )
                                                if (pwd == pass) {
                                                    dialog.dismiss()
                                                    showLoading()
                                                    val coin: Coin? =
                                                        Walletverse.sInstance.queryWalletCoinAsync(
                                                            GetWalletCoinParams(
                                                                SPUtils.getInstance()
                                                                    .getString(Constants.CURRENT_WID),
                                                                chain,
                                                                symbol
                                                            )
                                                        )
                                                    val privateKey =
                                                        Walletverse.sInstance.decodeMessageAsync(
                                                            DecodeMessageParams(
                                                                coin!!.privateKey,
                                                                pass
                                                            )
                                                        )
                                                    val nonce =
                                                        Walletverse.sInstance.nonceAsync(
                                                            GetParams(
                                                                dapp.chain.chainId,
                                                                coin.address
                                                            )
                                                        )
                                                    val dAppMessage = DAppMessage(
                                                        dataObj!!.optString("data"),
                                                        dataObj.optString("__type"),
                                                        privateKey,
                                                        nonce
                                                    )
                                                    try {
                                                        val signMessageResult =
                                                            Walletverse.sInstance.signMessageAsync(
                                                                SignMessageParams(
                                                                    dapp.chain.chainId,
                                                                    privateKey,
                                                                    dAppMessage
                                                                )
                                                            )
                                                        v_webview.jsCallback(
                                                            id!!,
                                                            "",
                                                            signMessageResult
                                                        )
                                                        hideLoading()
                                                    } catch (e: Exception) {
                                                        v_webview.jsCallback(id!!, "", "")
                                                        hideLoading()
                                                    }

                                                } else {
                                                    ToastUtil.showError(getString(R.string.wrong_pin))
                                                }
                                            }
                                        }

                                        override fun onCancel() {
                                            v_webview.jsCallback(id!!, "", "")
                                        }

                                    })
                            }
                        }

                    }


                }
            }

        })
    }

    fun valueTransformByDecimal(value: String, decimal: String): String {

        try {
            return if (value.startsWith("0x")) {
                val decimalString = BigInteger(value.substring(2, value.length), 16).toString()
                BigDecimal(decimalString).divide(10.0.pow(decimal.toInt()).toBigDecimal())
                    .setScale(8, RoundingMode.UP).stripTrailingZeros().toPlainString()
            } else {
                BigDecimal(value).divide(10.0.pow(decimal.toInt()).toBigDecimal())
                    .setScale(8, RoundingMode.UP).stripTrailingZeros().toPlainString()
            }
        } catch (e: Exception) {
        }
        return "0"
    }


}