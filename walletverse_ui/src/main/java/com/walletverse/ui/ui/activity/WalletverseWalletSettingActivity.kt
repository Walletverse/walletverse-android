package com.walletverse.ui.ui.activity

import android.annotation.SuppressLint
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.walletverse.core.Walletverse
import com.walletverse.core.bean.*
import com.walletverse.core.enums.Type
import com.walletverse.ui.R
import com.walletverse.ui.base.BaseActivity
import com.walletverse.ui.constant.Constants
import com.walletverse.ui.event.CloseEvent
import com.walletverse.ui.event.RefreshEvent
import com.walletverse.ui.ui.fragment.WalletFragment
import com.walletverse.ui.util.ActivityUtil
import com.walletverse.ui.util.DialogUtil
import com.walletverse.ui.util.EventBusUtil
import com.walletverse.ui.util.ToastUtil
import com.walletverse.ui.view.PasswordBoard
import com.blankj.utilcode.util.SPUtils
import kotlinx.android.synthetic.main.activity_walletverse_wallet_setting.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class WalletverseWalletSettingActivity : BaseActivity() {

    private var identity: Identity? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_walletverse_wallet_setting
    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        val bundle = intent.extras
        identity = bundle?.getSerializable("identity") as Identity
        v_wallet_id.text = "id:${identity?.wid}"
        if (identity?.tokenType == "private") {
            v_private_key.visibility = View.VISIBLE
            v_private_key.setOnClickListener(this)
        } else {
            v_private_key.visibility = View.VISIBLE
            v_private_key.setOnClickListener(this)
            v_mnemonic.visibility = View.VISIBLE
            v_mnemonic.setOnClickListener(this)
        }
    }

    override fun initView() {
        initTitleBar(getString(R.string.wallet_management))
        v_modify_name.setOnClickListener(this)
        v_delete_wallet.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.v_modify_name -> {
                DialogUtil.showInputDialog(this, object : DialogUtil.OnInputConfirmListener {
                    override fun onConfirm(text: String) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            try {
                                identity?.name = text
                                Walletverse.sInstance.updateIdentityAsync(identity!!)
                                EventBusUtil.post(RefreshEvent(WalletFragment.REFRESH_EVENT))
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                })
            }
            R.id.v_private_key -> {
                showPrivateAction()
            }
            R.id.v_mnemonic -> {
                showMnemonicAction()
            }
            R.id.v_delete_wallet -> {
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val identities: MutableList<Identity>? =
                            Walletverse.sInstance.queryIdentitiesAsync()

                        if (identities == null || identities.isEmpty())
                            return@launch

                        withContext(Dispatchers.Main) {
                            if (identities.size == 1) {    //only one
                                DialogUtil.showCenterCommonDialog(
                                    this@WalletverseWalletSettingActivity,
                                    getString(R.string.delete_last_wallet_tip),
                                    object : DialogUtil.OnCommonListener {
                                        override fun onConfirm() {
                                            confirmDelete(true)
                                        }

                                        override fun onCancel() {
                                        }

                                    })
                            } else {
                                confirmDelete(false)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }


                }
            }
        }
    }

    private fun showMnemonicAction() {
        DialogUtil.showPasswordDialog(this, object : DialogUtil.OnCompletedListener {
            override fun passwordComplete(pass: String, dialog: PasswordBoard) {
                val pwd = SPUtils.getInstance().getString(Constants.PWD)
                if (pwd == pass) {
                    dialog.dismiss()
                    lifecycleScope.launch(Dispatchers.Main){
                        val mnemonic =
                            Walletverse.sInstance.decodeMessageAsync(
                                DecodeMessageParams(
                                    identity!!.token,
                                    pass
                                )
                            )
                        DialogUtil.showExportDialog(
                            this@WalletverseWalletSettingActivity,
                            arrayListOf(mnemonic)
                        )
                    }
                } else {
                    ToastUtil.showError(getString(R.string.wrong_pin))
                }
            }

        })
    }

    private fun showPrivateAction() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val chains = arrayListOf<Coin>()
                val coins = Walletverse.sInstance.queryWalletCoinsAsync(GetWalletCoinParams(identity!!.wid))
                coins?.forEach {
                    if (it.type==Type.CHAIN) {
                        chains.add(it)
                    }
                }
                withContext(Dispatchers.Main) {
                    DialogUtil.showChainBoardDialog(this@WalletverseWalletSettingActivity,
                        chains,
                        object : DialogUtil.OnChainClickListener {
                            override fun onChainClick(coin: Coin) {

                                DialogUtil.showPasswordDialog(this@WalletverseWalletSettingActivity,
                                    object : DialogUtil.OnCompletedListener {
                                        override fun passwordComplete(
                                            pass: String,
                                            dialog: PasswordBoard
                                        ) {
                                            val pwd = SPUtils.getInstance().getString(Constants.PWD)
                                            if (pwd == pass) {
                                                dialog.dismiss()
                                                lifecycleScope.launch(Dispatchers.IO) {
                                                    try {
                                                        Walletverse.sInstance.queryWalletCoinAsync(GetWalletCoinParams(coin.wid,coin.contract,coin.symbol))
                                                        withContext(Dispatchers.Main) {
                                                            val privateKey =
                                                                Walletverse.sInstance.decodeMessageAsync(
                                                                    DecodeMessageParams(
                                                                        coin.privateKey,
                                                                        pass
                                                                    )
                                                                )
                                                            DialogUtil.showExportDialog(
                                                                this@WalletverseWalletSettingActivity, arrayListOf(
                                                                    privateKey
                                                                )
                                                            )
                                                        }
                                                    } catch (e: Exception) {
                                                        e.printStackTrace()
                                                    }
                                                }
                                            } else {
                                                ToastUtil.showError(getString(R.string.wrong_pin))
                                            }
                                        }

                                    })

                            }
                        })
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }

    private fun confirmDelete(lastOne: Boolean) {
        DialogUtil.showPasswordDialog(this@WalletverseWalletSettingActivity,
            object : DialogUtil.OnCompletedListener {
                override fun passwordComplete(
                    pass: String,
                    dialog: PasswordBoard
                ) {
                    val pwd = SPUtils.getInstance().getString(Constants.PWD)
                    if (pwd == pass) {
                        dialog.dismiss()
                        showLoading()
                        lifecycleScope.launch(Dispatchers.IO) {
                            try {//delete coins
                                val coins: MutableList<Coin>? =
                                    Walletverse.sInstance.queryWalletCoinsAsync(GetWalletCoinParams(identity!!.wid))
                                coins?.forEach {
                                    Walletverse.sInstance.deleteWalletCoinAsync(it)
                                }
                                //delete identity
                                Walletverse.sInstance.deleteIdentityAsync(identity!!)

                                if (lastOne) {
                                    SPUtils.getInstance().put(Constants.PWD, "")
                                    ActivityUtil.goNewActivity(
                                        this@WalletverseWalletSettingActivity,
                                        EnterActivity::class.java
                                    )
                                } else {
                                    val identities: MutableList<Identity>? =
                                        Walletverse.sInstance.queryIdentitiesAsync()
                                    if (identities != null && identities.isNotEmpty()) {
                                        SPUtils.getInstance()
                                            .put(Constants.CURRENT_WID, identities[0].wid)
                                    }

                                    EventBusUtil.post(RefreshEvent(WalletFragment.REFRESH_EVENT))
                                    EventBusUtil.post(CloseEvent(WalletverseChainManageActivity.CLOSE_EVENT))

    //                                ActivityUtil.goActivity(
    //                                    this@BeFiWalletSettingActivity,
    //                                    HomeActivity::class.java,
    //                                )
                                    finish()
                                }
                                withContext(Dispatchers.Main) {
                                    hideLoading()
                                }
                            } catch (e: Exception) {
                                hideLoading()
                                e.printStackTrace()
                            }
                        }
                    } else {
                        ToastUtil.showError(getString(R.string.wrong_pin))
                    }
                }
            })
    }
}