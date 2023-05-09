package com.walletverse.ui.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.walletverse.core.Walletverse
import com.walletverse.core.bean.*
import com.walletverse.core.enums.Type
import com.walletverse.ui.R
import com.walletverse.ui.base.BaseActivity
import com.walletverse.ui.bean.FeeBean
import com.walletverse.ui.bean.TransferParams
import com.walletverse.ui.constant.Constants
import com.walletverse.ui.core.getFee
import com.walletverse.ui.core.getGwei
import com.walletverse.ui.core.setScale
import com.walletverse.ui.event.RefreshEvent
import com.walletverse.ui.util.*
import com.walletverse.ui.view.PasswordBoard
import com.blankj.utilcode.util.SPUtils
import com.hjq.bar.TitleBar
import com.king.zxing.CameraScan
import com.king.zxing.CaptureActivity
import com.orhanobut.logger.Logger
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.activity_walletverse_transfer.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.math.BigDecimal


@BindEventBus
class WalletverseTransferActivity : BaseActivity() {

    private var mFee: String = ""
    private var mGwei: Double = 0.0
    private var mGasPrice: String = ""
    private var mGasLimit: String = ""

    private var mBasicGwei: Double = 0.0
    private var mBasicGasPrice: String = ""
    private var mBasicGasLimit: String = ""

    private lateinit var mCoin: Coin
    private var mSpeed = "General"
    private var cs = SPUtils.getInstance().getString(Constants.CURRENT_CURRENCY_SYMBOL)
    private var mPrice = "0.0"
    private var inputData = ""

    private lateinit var mRegister: ActivityResultLauncher<Intent>


    override fun getLayoutId(): Int {
        return R.layout.activity_walletverse_transfer
    }

    override fun initData() {
        v_balance.text = getString(R.string.available_balance, mCoin.balance.ifEmpty { "0.00" })
        notifyPrice("0.0")


        mRegister =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.data != null) {
                    val address: String? = CameraScan.parseScanResult(result.data)
                    address?.let { v_address.setText(it) }
                }
            }

    }

    override fun initView() {
        val bundle = intent.extras
        mCoin = bundle?.getSerializable("coin") as Coin
        initTitleBar(mCoin.symbol, rightIcon = R.mipmap.icon_scan)
        mPrice = mCoin.price.ifEmpty { "0.0" }

        v_gas_fee_layout.setOnClickListener(this)
        v_transfer.setOnClickListener(this)
        v_address.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                getGasPriceAndLimit(if (p0.isNullOrEmpty()) "" else p0.toString(),
                    v_count.text.toString().ifEmpty { "0" })
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        v_count.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val count = if (p0.isNullOrEmpty()) {
                    "0"
                } else {
                    p0.toString()
                }
                notifyPrice(count)
                getGasPriceAndLimit(v_address.text.toString().ifEmpty { "" }, count)
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

    }

    private fun getGasPriceAndLimit(toAddress: String, toAmount: String) {
        lifecycleScope.launch(Dispatchers.Main) {

            try {
                if (toAddress.isEmpty()) {
                    gasFeeGone()
                    return@launch
                }

                if (mCoin.type == Type.COIN && toAmount.toDouble() == 0.0) {
                    gasFeeGone()
                    return@launch
                }


                val validateAddress: Boolean =
                    Walletverse.sInstance.validateAddressAsync(
                        ValidateAddressParams(
                            mCoin.chainId,
                            toAddress
                        )
                    )

                if (!validateAddress) {
                    gasFeeGone()
                    return@launch
                }


                if (mCoin.type == Type.COIN) {
                    val hexValue =
                        Walletverse.sInstance.toHexAsync(HexParams(toAmount, mCoin.decimals))
//                    Logger.e("${toAmount},${hexValue}")
                    inputData = Walletverse.sInstance.encodeERC20ABIAsync(
                        EncodeERC20ABIParams(
                            mCoin.chainId,
                            "transfer",
                            mCoin.contractAddress,
                            arrayListOf(
                                v_address.text.toString(),
                                hexValue
                            )
                        )
                    )
                }


                val feeParams = FeeParams(
                    mCoin.chainId,
                    mCoin.address,
                    if (mCoin.type == Type.CHAIN) toAddress else mCoin.contractAddress,
                    if (mCoin.type == Type.CHAIN) toAmount else "0",
                    mCoin.decimals.toString(),
                    inputData
                )

                val fee: GasFee? = Walletverse.sInstance.feeAsync(
                    feeParams
                )


                if (fee == null) {
                    gasFeeGone()
                    return@launch
                }


                v_gas_fee_layout.visibility = View.VISIBLE

                mBasicGasPrice = fee.gasPrice
                mBasicGasLimit = fee.gasLimit
                mBasicGwei = getGwei(mBasicGasPrice)

                mGasPrice = fee.gasPrice
                mGasLimit = fee.gasLimit
                mGwei = getGwei(mGasPrice)

                notifyFee()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun gasFeeGone() {
        v_gas_fee_layout.visibility = View.GONE
        mGasPrice = "0"
        mGasLimit = "0"
        mGwei = 0.0
        mFee = "0"
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.v_transfer -> {
                if (commitPre()) {
                    DialogUtil.showPasswordDialog(this, object : DialogUtil.OnCompletedListener {
                        override fun passwordComplete(pass: String, dialog: PasswordBoard) {
                            val pwd = SPUtils.getInstance().getString(Constants.PWD)
                            if (pwd == pass) {
                                dialog.dismiss()

                                DialogUtil.showTransferDialog(this@WalletverseTransferActivity,
                                    TransferParams(
                                        v_count.text.toString(),
                                        mCoin.symbol,
                                        mCoin.contract,
                                        mCoin.contractAddress,
                                        mCoin.address,
                                        v_address.text.toString(),
                                        mFee,
                                        v_fee_price.text.toString()
                                    ),
                                    object : DialogUtil.OnCommonListener {
                                        override fun onConfirm() {
                                            transfer()
                                        }

                                        override fun onCancel() {
                                        }

                                    })
                            } else {
                                ToastUtil.showError(getString(R.string.wrong_pin))
                            }
                        }

                        override fun onCancel() {

                        }
                    })
                }


            }
            R.id.v_gas_fee_layout -> {
                if (mBasicGwei == 0.0 || mBasicGasPrice.isEmpty() || mBasicGasPrice == "0" || mBasicGasLimit.isEmpty() || mBasicGasLimit == "0") {
                    return
                }
                val bundle = Bundle()
                val fee = FeeBean(
                    mGwei,
                    mGasPrice,
                    mGasLimit,
                    mSpeed,
                    decimals = mCoin.decimals,
                    contract = mCoin.contract,
                    basicGwei = mBasicGwei,
                    basicGasPrice = mBasicGasPrice,
                    basicGasLimit = mBasicGasLimit
                )
                bundle.putSerializable("fee", fee)
//                bundle.putString("balance", mCoin.balance.ifEmpty { "0.00" })
                ActivityUtil.goActivity(this, WalletverseFeeActivity::class.java, bundle)
            }
        }
    }

    private fun transfer() {
        lifecycleScope.launch(Dispatchers.Main) {
            try {
//                showLoading()
//                val nonce = Walletverse.sInstance.nonceAsync(
//                    GetParams(
//                        mCoin.chainId,
//                        mCoin.address,
//                        mCoin.contractAddress
//                    )
//                )
//                if (nonce.isEmpty()) {
//                    ToastUtil.showError(getString(R.string.transfer_failed))
//                    hideLoading()
//                    return@launch
//                }
//
//                val privateKey = Walletverse.sInstance.decodeMessageAsync(
//                    DecodeMessageParams(
//                        mCoin.privateKey,
//                        SPUtils.getInstance().getString(Constants.PWD)
//                    )
//                )
//
//                val signTransactionParams = SignTransactionParams(
//                    mCoin.chainId,
//                    privateKey,
//                    v_address.text.toString(),
//                    v_count.text.toString(),
//                    mCoin.decimals,
//                    mGasPrice,
//                    mGasLimit,
//                    nonce,
//                    inputData,
//                    mCoin.contractAddress
//                )
//
//                Logger.e(signTransactionParams.toString())
//
//                val signData = Walletverse.sInstance.signTransactionAsync(
//                    signTransactionParams
//                )
//
//                Logger.e(signData)
//                if (signData.isEmpty()) {
//                    ToastUtil.showError(getString(R.string.transfer_failed))
//                    hideLoading()
//                    return@launch
//                }
//
//                val hash = Walletverse.sInstance.transactionAsync(
//                    TransactionParams(
//                        mCoin.chainId,
//                        mCoin.address,
//                        v_address.text.toString(),
//                        signData,
//                        v_count.text.toString(),
//                        mCoin.contractAddress
//                    )
//                )

                showLoading()


                val walletPin = SPUtils.getInstance().getString(Constants.PWD)

                val hash = Walletverse.sInstance.signAndTransactionAsync(
                    SignAndTransactionParams(
                        mCoin.chainId,
                        mCoin.privateKey,
                        mCoin.address,
                        v_address.text.toString(),
                        v_count.text.toString(),
                        mCoin.decimals,
                        mGasPrice,
                        mGasLimit,
                        inputData,
                        mCoin.contractAddress,
                        walletPin
                    )
                )

                Logger.e(hash)

                hideLoading()
                if (hash.isNotEmpty()) {
                    ToastUtil.showSuccess(getString(R.string.transfer_success))
                    GlobalHandler.sInstance.postDelayed(Runnable {
                        EventBusUtil.post(RefreshEvent(WalletverseCoinDetailActivity.REFRESH_EVENT))
                    }, 2000)
                    finish()
                } else {
                    ToastUtil.showError(getString(R.string.transfer_failed))
                }
            } catch (e: Exception) {
                hideLoading()
                e.printStackTrace()
            }

        }
    }

    override fun onRightClick(titleBar: TitleBar?) {
        PermissionX.init(this)
            .permissions(Manifest.permission.CAMERA)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
//                    ActivityUtil.goActivityForResult(this, CaptureActivity::class.java, 100)
                    mRegister.launch(Intent(this, CaptureActivity::class.java))
                } else {
                    ToastUtil.showError(getString(R.string.open_camera_failed))
                }
            }
    }

    @SuppressLint("SetTextI18n")
    private fun notifyPrice(count: String) {
        v_price.text =
            "≈${cs}${setScale(count.toBigDecimal().multiply(BigDecimal(mPrice)))}"
    }

    private fun commitPre(): Boolean {
        if (v_address.text.toString().isEmpty()) {
            ToastUtil.showError(getString(R.string.hint_input_address))
            return false
        }

        if (v_count.text.toString().isEmpty() || v_count.text.toString().toDouble() == 0.0) {
            ToastUtil.showError(getString(R.string.input_amount))
            return false
        }

        if (mCoin.type == Type.CHAIN) {
            if (v_count.text.toString().toBigDecimal()
                    .add(BigDecimal(mFee)) > BigDecimal(mCoin.balance)
            ) {
                ToastUtil.showError(getString(R.string.insufficient_transfer_balance))
                return false
            }
        }

        if (mGasPrice.isEmpty() || mGasLimit.isEmpty() || (inputData.isEmpty() && mCoin.type == Type.COIN))
            return false

        return true
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateFee(fee: FeeBean) {
        mGwei = fee.gwei
        mGasPrice = fee.gasPrice
        mGasLimit = fee.gasLimit
        mSpeed = fee.speed

        notifyFee()
    }

    @SuppressLint("SetTextI18n")
    private fun notifyFee() {
        mFee = getFee(mGasPrice, mGasLimit, mCoin.decimals)
        v_fee.text = "$mFee ${ContractUtil.getContract(mCoin.contract)}"
        v_fee_price.text = "GasPrice(${mGwei}GWEI)*GasLimit($mGasLimit)"
    }

}