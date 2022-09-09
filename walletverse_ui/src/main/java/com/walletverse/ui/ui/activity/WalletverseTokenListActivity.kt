package com.walletverse.ui.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.walletverse.core.Walletverse
import com.walletverse.core.bean.*
import com.walletverse.core.enums.Type
import com.walletverse.ui.R
import com.walletverse.ui.adapter.TokenListAdapter
import com.walletverse.ui.base.BaseActivity
import com.walletverse.ui.constant.Constants
import com.walletverse.ui.event.RefreshEvent
import com.walletverse.ui.ui.fragment.WalletFragment
import com.walletverse.ui.util.ActivityUtil
import com.walletverse.ui.util.DialogUtil
import com.walletverse.ui.util.EventBusUtil
import com.walletverse.ui.util.ToastUtil
import com.blankj.utilcode.util.SPUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.hjq.bar.TitleBar
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener
import kotlinx.android.synthetic.main.activity_walletverse_token_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WalletverseTokenListActivity : BaseActivity(), OnItemClickListener, OnLoadMoreListener {

    private lateinit var mTokenListAdapter: TokenListAdapter
    private lateinit var mChain: Coin
    private var walletCoins: MutableList<Coin>? = null
    private var mTokenList: MutableList<Coin> = arrayListOf()
    private lateinit var wid: String
    private var mPage = 1

    override fun getLayoutId(): Int {
        return R.layout.activity_walletverse_token_list
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initData() {

        wid = SPUtils.getInstance().getString(Constants.CURRENT_WID)

        v_refresh.setEnableRefresh(false)
        v_refresh.setOnLoadMoreListener(this)
        (v_recycler.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        v_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mTokenListAdapter = TokenListAdapter(R.layout.item_token)
        v_recycler.adapter = mTokenListAdapter
        mTokenListAdapter.setOnItemClickListener(this)




        getCoinWithDb()
        getData()


    }

    private fun getCoinWithDb() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                walletCoins = Walletverse.sInstance.queryWalletCoinsAsync(GetWalletCoinParams(wid))

                val dbCoins = Walletverse.sInstance.queryCoinsAsync(
                    GetCoinParams(
                        chainId = mChain.chainId,
                    )
                )?.filter { it.type == Type.COIN }

                dbCoins?.forEach {
                    walletCoins?.forEach { it2 ->
                        if (it2.contractAddress == it.contractAddress) {
                            it.isAdd = true
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    mTokenListAdapter.setList(dbCoins)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getData() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val result =
                    Walletverse.sInstance.getTokenListAsync(TokenParams(mPage, 50, mChain.chainId))
                withContext(Dispatchers.Main) {
                    if (mPage == 1 && result.isEmpty()) {
                        mTokenListAdapter.setEmptyView(R.layout.view_empty)
                        v_refresh.finishLoadMoreWithNoMoreData()
                    } else {
                        if (result.isNotEmpty()) {
                            mTokenList.addAll(result)
                            mTokenListAdapter.setList(mTokenList)
                            v_refresh.finishLoadMore()
                        } else {
                            mPage--
                            v_refresh.finishLoadMoreWithNoMoreData()
                        }

                        mTokenList.forEach {
                            if (mPage == 1) {
                                this.async {
                                    Walletverse.sInstance.insertCoinAsync(it)
                                }
                            }
                            this.async {
                                walletCoins?.forEach { it2 ->
                                    if (it2.contractAddress == it.contractAddress) {
                                        it.isAdd = true
                                    }
                                }
                            }
                        }
                        mTokenListAdapter.setList(mTokenList)
                    }


                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    override fun initView() {
        val bundle = intent.extras
        mChain = bundle?.getSerializable("chain") as Coin
        initTitleBar(mChain.contract, rightIcon = R.mipmap.icon_search)
    }

    override fun onRightClick(titleBar: TitleBar?) {
        val bundle = Bundle()
        bundle.putSerializable("chain", mChain)
        ActivityUtil.goActivity(this, WalletverseTokenSearchActivity::class.java, bundle)
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        if (walletCoins == null || walletCoins!!.isEmpty())
            return

        val has = walletCoins!!.any { it.chainId == mChain.chainId }
        if (!has) {
            showTipDialog(getString(R.string.add_chain_tip, mChain.contract),
                object : DialogUtil.OnCommonListener {
                    override fun onConfirm() {
                        lifecycleScope.launch(Dispatchers.Main) {
                            try {
                                showLoading()
                                val pwd = SPUtils.getInstance().getString(Constants.PWD)
                                val result =
                                    Walletverse.sInstance.saveWalletCoinAsync(SaveCoinParams(wid, pwd, mChain))
                                walletCoins =
                                    Walletverse.sInstance.queryWalletCoinsAsync(GetWalletCoinParams(wid))
                                if (result) {
                                    val tokenResult = Walletverse.sInstance.saveWalletCoinAsync(
                                        SaveCoinParams(
                                            wid,
                                            pwd,
                                            mTokenListAdapter.data[position]
                                        )
                                    )
                                    if (tokenResult) {
                                        mTokenListAdapter.data[position].isAdd = true
                                        mTokenListAdapter.notifyItemChanged(position)
                                        ToastUtil.showSuccess(getString(R.string.add_success))
                                    } else {
                                        ToastUtil.showSuccess(getString(R.string.add_fail))
                                    }
                                } else {
                                    ToastUtil.showSuccess(getString(R.string.add_fail))
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
            return
        }

        if (mTokenListAdapter.data[position].isAdd)
            return

        lifecycleScope.launch(Dispatchers.Main) {
            try {
                showLoading()
                val pwd = SPUtils.getInstance().getString(Constants.PWD)
                val result = Walletverse.sInstance.saveWalletCoinAsync(
                    SaveCoinParams(
                        wid,
                        pwd,
                        mTokenListAdapter.data[position]
                    )
                )
                if (result) {
                    mTokenListAdapter.data[position].isAdd = true
                    mTokenListAdapter.notifyItemChanged(position)
                    ToastUtil.showSuccess(getString(R.string.add_success))
                } else {
                    ToastUtil.showSuccess(getString(R.string.add_fail))
                }
                hideLoading()
            } catch (e: Exception) {
                hideLoading()
                e.printStackTrace()
            }

        }
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mPage++
        getData()
    }


    override fun onDestroy() {
        super.onDestroy()
        EventBusUtil.post(RefreshEvent(WalletFragment.REFRESH_EVENT))
    }
}