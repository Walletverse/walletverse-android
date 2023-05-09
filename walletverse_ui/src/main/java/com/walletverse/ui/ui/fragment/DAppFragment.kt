package com.walletverse.ui.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.SPUtils
import com.walletverse.core.Walletverse
import com.walletverse.core.bean.DApp
import com.walletverse.core.bean.GetCoinParams
import com.walletverse.core.bean.GetWalletCoinParams
import com.walletverse.core.bean.SaveCoinParams
import com.walletverse.core.enums.EChain
import com.walletverse.ui.R
import com.walletverse.ui.base.BaseFragment
import com.walletverse.ui.constant.Constants
import com.walletverse.ui.event.RefreshEvent
import com.walletverse.ui.ui.activity.WalletverseDAppActivity
import com.walletverse.ui.util.ActivityUtil
import com.walletverse.ui.util.DialogUtil
import com.walletverse.ui.util.EventBusUtil
import com.walletverse.ui.util.ToastUtil
import kotlinx.android.synthetic.main.fragment_dapp.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * ================================================================
 * Create_time：2022/7/29  12:22
 * Author：solomon
 * Detail：
 * ================================================================
 */
class DAppFragment : BaseFragment() {
    override fun getLayoutId(): Int = R.layout.fragment_dapp

    override fun initData() {
    }

    override fun initView() {
        v_dapp.setOnClickListener(this)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.v_dapp -> {
                lifecycleScope.launch(Dispatchers.Main) {
                    val wid = SPUtils.getInstance().getString(Constants.CURRENT_WID)
                    val walletCoins =
                        Walletverse.sInstance.queryWalletCoinsAsync(GetWalletCoinParams(wid))
                    //must added main chain,if not ,please add main chain first
                    val has = walletCoins!!.any { it.chainId == EChain.BNB.chainId }
                    if (!has) {
                        showTipDialog(getString(R.string.add_chain_tip, EChain.BNB.contract),
                            object : DialogUtil.OnCommonListener {
                                override fun onConfirm() {
                                    lifecycleScope.launch(Dispatchers.Main) {
                                        try {
                                            showLoading()
                                            val pwd = SPUtils.getInstance().getString(Constants.PWD)
                                            val chain =Walletverse.sInstance.queryCoinAsync(GetCoinParams(EChain.BNB.chainId,EChain.BNB.contract,EChain.BNB.symbol))
                                            val result =
                                                Walletverse.sInstance.saveWalletCoinAsync(
                                                    SaveCoinParams(wid, pwd, chain!!)
                                                )
                                            if (result) {

                                                EventBusUtil.post(RefreshEvent(WalletFragment.REFRESH_EVENT))

                                                val bundle = Bundle()
//                val dapp= DApp(SPUtils.getInstance().getString(Constants.CURRENT_WID),"Planet Hares","https://0xhares.io/", EChain.ETH)
//                val dapp= DApp(SPUtils.getInstance().getString(Constants.CURRENT_WID),"UniSwap","https://uniswap.token.im/", EChain.ETH)
//                val dapp= DApp(SPUtils.getInstance().getString(Constants.CURRENT_WID),"Opensea","https://opensea.io/", EChain.ETH)
                val dapp= DApp(SPUtils.getInstance().getString(Constants.CURRENT_WID),"Pancake","https://pancakeswap.finance/", EChain.BNB)
//                val dapp= DApp(SPUtils.getInstance().getString(Constants.CURRENT_WID),"QuickSwap","https://quickswap.exchange/#/swap", EChain.MATIC)
//                                                val dapp = DApp(SPUtils.getInstance().getString(Constants.CURRENT_WID), "Abey",
////                                                    "https://nft.revoland.com/RevoMarketplace",
//                                                    "https://abeynft.revoland.com/RevoMarketplace",
//                                                    EChain.ABEY
//                                                )

                                                bundle.putSerializable("dapp", dapp)
                                                ActivityUtil.goActivity(
                                                    requireActivity(),
                                                    WalletverseDAppActivity::class.java,
                                                    bundle
                                                )
                                            }

                                            hideLoading()
                                        } catch (e: Exception) {
                                            hideLoading()
                                            e.printStackTrace()
                                        }
                                    }
                                }

                                override fun onCancel() {
                                }

                            })
                        return@launch
                    }


                    val bundle = Bundle()
//                val dapp= DApp(SPUtils.getInstance().getString(Constants.CURRENT_WID),"Planet Hares","https://0xhares.io/", EChain.ETH)
//                val dapp= DApp(SPUtils.getInstance().getString(Constants.CURRENT_WID),"UniSwap","https://uniswap.token.im/", EChain.ETH)
//                val dapp= DApp(SPUtils.getInstance().getString(Constants.CURRENT_WID),"Opensea","https://opensea.io/", EChain.ETH)
                val dapp= DApp(SPUtils.getInstance().getString(Constants.CURRENT_WID),"Pancake","https://pancakeswap.finance/", EChain.BNB)
//                val dapp= DApp(SPUtils.getInstance().getString(Constants.CURRENT_WID),"MAPO DAO","https://newspace.idavoll.network/project-staking-detail/MAPProtocol", EChain.MAPO)
//                val dapp= DApp(SPUtils.getInstance().getString(Constants.CURRENT_WID),"QuickSwap","https://quickswap.exchange/#/swap", EChain.MATIC)

//                    val dapp = DApp(
//                        SPUtils.getInstance().getString(Constants.CURRENT_WID),
//                        "Abey",
////                        "https://nft.revoland.com/RevoMarketplace",
//                        "https://abeynft.revoland.com/RevoMarketplace",
//                        EChain.ABEY
//                    )

                    bundle.putSerializable("dapp", dapp)
                    ActivityUtil.goActivity(
                        requireActivity(),
                        WalletverseDAppActivity::class.java,
                        bundle
                    )
                }
            }
        }
    }
}