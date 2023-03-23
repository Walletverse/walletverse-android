package com.walletverse.ui.ui.activity

import android.Manifest
import android.content.Intent
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.SPUtils
import com.hjq.bar.TitleBar
import com.king.zxing.CameraScan
import com.king.zxing.CaptureActivity
import com.orhanobut.logger.Logger
import com.permissionx.guolindev.PermissionX
import com.walletverse.core.Walletverse
import com.walletverse.core.bean.*
import com.walletverse.core.enums.EChain
import com.walletverse.core.enums.Type
import com.walletverse.ui.R
import com.walletverse.ui.base.BaseActivity
import com.walletverse.ui.bean.TransferParams
import com.walletverse.ui.constant.Constants
import com.walletverse.ui.event.RefreshEvent
import com.walletverse.ui.util.*
import com.walletverse.ui.view.PasswordBoard
import kotlinx.android.synthetic.main.activity_walletverse_nft_transfer.*
import kotlinx.android.synthetic.main.activity_walletverse_nft_transfer.v_address
import kotlinx.android.synthetic.main.activity_walletverse_nft_transfer.v_transfer
import kotlinx.android.synthetic.main.activity_walletverse_transfer.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal

class WalletverseNftTransferActivity : BaseActivity() {

    private lateinit var mRegister: ActivityResultLauncher<Intent>
    lateinit var tokenId: String
    lateinit var fromAddress: String
    lateinit var name: String
    lateinit var image: String

    override fun getLayoutId(): Int {
        return R.layout.activity_walletverse_nft_transfer
    }

    override fun initData() {
        mRegister =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.data != null) {
                    val address: String? = CameraScan.parseScanResult(result.data)
                    address?.let { v_address.setText(it) }
                }
            }

        val bundle = intent.extras
        tokenId = bundle?.getString("tokenId").toString()
        fromAddress = bundle?.getString("fromAddress").toString()
        name = bundle?.getString("name").toString()
        image = bundle?.getString("image").toString()

        v_nft_name.text = name
        v_nft_icon.loadImage(image, R.mipmap.ic_launcher)
        v_chain_name.text = EChain.MAP.contract
    }

    override fun initView() {
        initTitleBar("", rightIcon = R.mipmap.icon_scan)
        v_transfer.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.v_transfer -> {
                if (commitPre()) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        withContext(Dispatchers.Main) {
                            showLoading()
                        }
                        val inputData = Walletverse.sInstance.requestNFTTransferDataAsync(
                            NFTParams(
                                tokenId = tokenId,
                                contractAddress = Constants.NFT_CONTRACT,
                                chainId = EChain.MAP.chainId,
                                from = fromAddress,
                                to = v_address.text.toString().trim()
                            )
                        )
                        Logger.d(inputData)

                        val feeParams = FeeParams(
                            EChain.MAP.chainId,
                            fromAddress,
                            Constants.NFT_CONTRACT,
                            "0",
                            "18",
                            inputData
                        )

                        val fee: GasFee? = Walletverse.sInstance.feeAsync(
                            feeParams
                        )

                        val nonce = Walletverse.sInstance.nonceAsync(
                            GetParams(
                                EChain.MAP.chainId,
                                fromAddress,
                                Constants.NFT_CONTRACT
                            )
                        )
                        if (nonce.isEmpty()) {
                            ToastUtil.showError(getString(R.string.transfer_failed))
                            hideLoading()
                            return@launch
                        }
                        val identity :Identity?= Walletverse.sInstance.queryIdentityAsync(
                            SPUtils.getInstance().getString(Constants.CURRENT_WID)
                        )
                        var privateKey = ""
                        if (identity?.tokenType == "private") {
                            privateKey = Walletverse.sInstance.decodeMessageAsync(
                                DecodeMessageParams(
                                    identity.token,
                                    SPUtils.getInstance().getString(Constants.PWD)
                                )
                            )
                        } else {
                            val mnemonic = Walletverse.sInstance.decodeMessageAsync(
                                DecodeMessageParams(
                                    identity!!.token,
                                    SPUtils.getInstance().getString(Constants.PWD)
                                )
                            )
                            privateKey = Walletverse.sInstance.getPrivateKeyAsync(
                                GetPrivateKeyParams(
                                    EChain.MAP.chainId,
                                    mnemonic
                                )
                            )
                        }
                        Logger.d(privateKey)


//                        val signTransactionParams = NFTSignTransactionParams(
//                            EChain.MAP.chainId,
//                            privateKey,
//                            v_address.text.toString(),
//                            fee?.gasPrice,
//                            fee?.gasLimit,
//                            nonce,
//                            inputData,
//                            Constants.NFT_CONTRACT
//                        )
//
//                        Logger.d(signTransactionParams)
//
//                        val signData = Walletverse.sInstance.signNFTTransactionAsync(
//                            signTransactionParams
//                        )
//
//                        Logger.d(signData)
//
//                        if (signData.isEmpty()) {
//                            ToastUtil.showError(getString(R.string.transfer_failed))
//                            hideLoading()
//                            return@launch
//                        }
//
//                        val hash = Walletverse.sInstance.transactionNFTAsync(
//                            NFTTransactionParams(
//                                EChain.MAP.chainId,
//                                fromAddress,
//                                v_address.text.toString(),
//                                signData,
//                                Constants.NFT_CONTRACT
//                            )
//                        )


                        val hash = Walletverse.sInstance.signAndNFTTransactionAsync(
                            SignAndNFTTransactionParams(
                                EChain.MAP.chainId,
                                privateKey,
                                fromAddress,
                                v_address.text.toString(),
                                fee?.gasPrice,
                                fee?.gasLimit,
                                inputData,
                                Constants.NFT_CONTRACT,
                            )
                        )

                        Logger.d(hash)

                        withContext(Dispatchers.Main) {
                            hideLoading()
                            if (hash.isNotEmpty()) {
                                ToastUtil.showSuccess(getString(R.string.transfer_success))
                                finish()
                            } else {
                                ToastUtil.showError(getString(R.string.transfer_failed))
                            }
                        }

                    }
                }
            }
        }
    }


    override fun onRightClick(titleBar: TitleBar?) {
        PermissionX.init(this)
            .permissions(Manifest.permission.CAMERA)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    mRegister.launch(Intent(this, CaptureActivity::class.java))
                } else {
                    ToastUtil.showError(getString(R.string.open_camera_failed))
                }
            }
    }

    private fun commitPre(): Boolean {
        if (v_address.text.toString().isEmpty()) {
            ToastUtil.showError(getString(R.string.hint_input_address))
            return false
        }

        return true
    }

}