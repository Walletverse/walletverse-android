package com.walletverse.ui.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.walletverse.core.Walletverse
import com.walletverse.core.bean.*
import com.walletverse.core.enums.Type
import com.walletverse.ui.R
import com.walletverse.ui.adapter.WalletCoinAdapter
import com.walletverse.ui.base.BaseFragment
import com.walletverse.ui.constant.Constants
import com.walletverse.ui.core.setScale
import com.walletverse.ui.event.RefreshEvent
import com.walletverse.ui.ui.activity.WalletverseCoinDetailActivity
import com.walletverse.ui.ui.activity.WalletverseTokenListActivity
import com.walletverse.ui.util.*
import com.blankj.utilcode.util.SPUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.fragment_wallet.*
import kotlinx.coroutines.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.math.BigDecimal


/**
 * ================================================================
 * Create_time：2022/7/29  12:22
 * Author：solomon
 * Detail：
 * ================================================================
 */
@BindEventBus
class WalletFragment : BaseFragment(), OnItemClickListener {
    private var coinTask: Job? = null
    private lateinit var mWalletCoinAdapter: WalletCoinAdapter
    private var mCoins: MutableList<Coin>? = null
    private var mChains: MutableList<Coin>? = null

    companion object {
        const val REFRESH_EVENT = "WalletFragment"
    }

    override fun getLayoutId(): Int = R.layout.fragment_wallet

    override fun initData() {

        v_recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mWalletCoinAdapter = WalletCoinAdapter(R.layout.item_wallet_coin)
        v_recycler.adapter = mWalletCoinAdapter
        mWalletCoinAdapter.setOnItemClickListener(this)

        getChains()
        getCoins()
    }


    override fun initView() {
        v_wallet_drawer.setOnClickListener(this)
        v_add.setOnClickListener(this)
        v_refresh.setEnableLoadMore(false)
        v_refresh.setOnRefreshListener {
            getCoins()
        }
        (v_recycler.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }


    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.v_wallet_drawer -> {
                DialogUtil.showWalletListDrawer(requireActivity())
            }
            R.id.v_add -> {
                if (mChains.isNullOrEmpty())
                    return
                DialogUtil.showChainBoardDialog(requireActivity(),
                    mChains!!,
                    object : DialogUtil.OnChainClickListener {
                        override fun onChainClick(chain: Coin) {
                            val bundle = Bundle()
                            bundle.putSerializable("chain", chain)
                            ActivityUtil.goActivity(
                                requireActivity(),
                                WalletverseTokenListActivity::class.java,
                                bundle
                            )
                        }
                    })
            }
        }
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        val bundle = Bundle()
        bundle.putSerializable("coin", mWalletCoinAdapter.data[position])
        ActivityUtil.goActivity(requireActivity(), WalletverseCoinDetailActivity::class.java, bundle)
    }

    private fun getChains() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                mChains = Walletverse.sInstance.queryCoinsAsync()
                    ?.filter { it.type == Type.CHAIN } as MutableList<Coin>
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    private fun getCoins() {
        if (coinTask!=null&&coinTask!!.isActive){
            coinTask?.cancel()
        }

        coinTask = lifecycleScope.launch(Dispatchers.Default) {
            try {
                val wid = SPUtils.getInstance().getString(Constants.CURRENT_WID)
                val identity: Identity? = Walletverse.sInstance.queryIdentityAsync(wid)
                mCoins = Walletverse.sInstance.queryWalletCoinsAsync(GetWalletCoinParams(identity!!.wid))
                withContext(Dispatchers.Main) {
                    v_wallet_drawer.text = identity.name
                    mWalletCoinAdapter.setList(mCoins)
                    if (!mCoins.isNullOrEmpty()) {
                        for (i in mCoins!!.indices) {
                            try {
                                val coin = mCoins!![i]
                                //balance
                                val taskBalance = this.async(SupervisorJob()) {
                                    Walletverse.sInstance.balanceAsync(
                                        GetParams(
                                            coin.chainId, coin.address, coin.contractAddress
                                        )
                                    )
                                }
                                //price
                                val taskPrice = this.async(SupervisorJob()) {
                                    Walletverse.sInstance.getPriceAsync(
                                        GetPriceParams(
                                            coin.symbol,
                                            coin.contractAddress
                                        )
                                    )
                                }

                                val balanceResult =
                                    kotlin.runCatching { taskBalance.await() }.onFailure {
                                        Logger.e("balance:${coin.chainId}-${coin.contract}-${coin.symbol}-$it")
                                    }
                                val priceResult =
                                    kotlin.runCatching { taskPrice.await() }.onFailure {
                                        Logger.e("price:${coin.chainId}-${coin.contract}-${coin.symbol}-$it")
                                    }
                                withContext(Dispatchers.IO) {

                                    val balance=balanceResult.getOrNull()
                                    if (balance!=null&&balance.isNotEmpty()) {
                                        coin.balance = setScale(balance)
                                    } else {
                                        if (coin.balance.isEmpty()) {
                                            coin.balance = "0.00"
                                        }
                                    }
                                    val price =priceResult.getOrNull()
                                    if (price!=null&&price.isNotEmpty()) {
                                        coin.price = setScale(price)
                                    } else {
                                        if (coin.price.isEmpty()) {
                                            coin.price = "0.00"
                                        }
                                    }

                                    val totalPrice =
                                        setScale(BigDecimal(coin.balance).multiply(BigDecimal(coin.price)))
                                    coin.totalPrice = totalPrice
                                    Walletverse.sInstance.updateWalletCoinAsync(coin)
                                }
                                mWalletCoinAdapter.notifyItemChanged(i)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                    v_refresh.finishRefresh()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshData(event: RefreshEvent) {
        if (event.pageName == REFRESH_EVENT)
            getCoins()
    }

}