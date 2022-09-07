package com.walletverse.ui.ui.activity

import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.walletverse.core.Walletverse
import com.walletverse.core.bean.Coin
import com.walletverse.core.bean.GetTokenParams
import com.walletverse.core.bean.GetWalletCoinParams
import com.walletverse.core.bean.SaveCoinParams
import com.walletverse.ui.R
import com.walletverse.ui.adapter.TokenListAdapter
import com.walletverse.ui.base.BaseActivity
import com.walletverse.ui.constant.Constants
import com.walletverse.ui.event.RefreshEvent
import com.walletverse.ui.ui.fragment.WalletFragment
import com.walletverse.ui.util.DialogUtil
import com.walletverse.ui.util.EventBusUtil
import com.walletverse.ui.util.ToastUtil
import com.blankj.utilcode.util.SPUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import kotlinx.android.synthetic.main.activity_walletverse_token_search.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class WalletverseTokenSearchActivity : BaseActivity(), TextView.OnEditorActionListener,
    OnItemClickListener {
    private lateinit var mTokenListAdapter: TokenListAdapter
    private lateinit var mChain: Coin
    private lateinit var wid: String
    private var walletCoins: MutableList<Coin>? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_walletverse_token_search
    }

    override fun initData() {
        val bundle = intent.extras
        mChain = bundle?.getSerializable("chain") as Coin

        wid = SPUtils.getInstance().getString(Constants.CURRENT_WID)

        v_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mTokenListAdapter = TokenListAdapter(R.layout.item_token)
        v_recycler.adapter = mTokenListAdapter
        mTokenListAdapter.setOnItemClickListener(this)


        lifecycleScope.launch(Dispatchers.IO){
            try {
                walletCoins = Walletverse.sInstance.queryWalletCoinsAsync(GetWalletCoinParams(wid))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    override fun initView() {
        v_back.setOnClickListener(this)
        v_search.setOnClickListener(this)
        v_input.setOnEditorActionListener(this)
    }


    private fun getToken() {
        lifecycleScope.launch(Dispatchers.Main) {
            try {
                showLoading()
                val coin: Coin = Walletverse.sInstance.getTokenAsync(
                    GetTokenParams(
                        mChain.chainId,
                        v_input.text.toString()
                    )
                )

                hideLoading()
                if (coin.contractAddress.isNotEmpty()) {
                    val hasCoins=walletCoins?.filter { it.chainId==mChain.chainId&& it.contractAddress==coin.contractAddress}

                    coin.isAdd=hasCoins.isNullOrEmpty()
                    val tokens: MutableList<Coin> = mutableListOf(coin)
                    mTokenListAdapter.setNewInstance(tokens)
                } else {
                    mTokenListAdapter.setNewInstance(mutableListOf())
                    mTokenListAdapter.setEmptyView(R.layout.view_empty)
                }
            } catch (e: Exception) {
                hideLoading()
                e.printStackTrace()
            }

        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.v_back -> {
                finish()
            }
            R.id.v_search -> {
                if (v_input.text.toString().isEmpty())
                    return
                getToken()
            }
        }
    }

    override fun onEditorAction(p0: TextView?, actionId: Int, p2: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            if (v_input.text.toString().isEmpty()) {
                return true
            }
            getToken()
            return true
        }
        return false

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
                                val result =Walletverse.sInstance.saveWalletCoinAsync(SaveCoinParams(wid, pwd, mChain))
                                EventBusUtil.post(RefreshEvent(WalletFragment.REFRESH_EVENT))
                                if(result) {
                                    val tokenResult =Walletverse.sInstance.saveWalletCoinAsync(SaveCoinParams(wid, pwd, mTokenListAdapter.data[position]))
                                    if(tokenResult){
                                        mTokenListAdapter.data[position].isAdd=true
                                        mTokenListAdapter.notifyItemChanged(position)
                                        ToastUtil.showSuccess(getString(R.string.add_success))
                                        EventBusUtil.post(RefreshEvent(WalletFragment.REFRESH_EVENT))
                                    }else{
                                        ToastUtil.showSuccess(getString(R.string.add_fail))
                                    }
                                }else{
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

        if(mTokenListAdapter.data[position].isAdd)
            return

        lifecycleScope.launch(Dispatchers.Main) {
            try {
                showLoading()
                val pwd = SPUtils.getInstance().getString(Constants.PWD)
                val result =Walletverse.sInstance.saveWalletCoinAsync(SaveCoinParams(wid, pwd, mTokenListAdapter.data[position]))
                if(result) {
                    mTokenListAdapter.data[position].isAdd=true
                    mTokenListAdapter.notifyItemChanged(position)
                    ToastUtil.showSuccess(getString(R.string.add_success))
                    EventBusUtil.post(RefreshEvent(WalletFragment.REFRESH_EVENT))
                }else{
                    ToastUtil.showSuccess(getString(R.string.add_fail))
                }
                hideLoading()
            } catch (e: Exception) {
                hideLoading()
                e.printStackTrace()
            }

        }
    }

}