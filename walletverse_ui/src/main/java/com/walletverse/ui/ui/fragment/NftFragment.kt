package com.walletverse.ui.ui.fragment

import android.view.View
import androidx.lifecycle.lifecycleScope
import com.blankj.utilcode.util.SPUtils
import com.walletverse.core.Walletverse
import com.walletverse.core.bean.Coin
import com.walletverse.core.bean.GetWalletCoinParams
import com.walletverse.core.bean.SaveCoinParams
import com.walletverse.core.enums.EChain
import com.walletverse.core.enums.Type
import com.walletverse.ui.R
import com.walletverse.ui.adapter.NftCollectionAdapter
import com.walletverse.ui.base.BaseFragment
import com.walletverse.ui.constant.Constants
import com.walletverse.ui.event.RefreshEvent
import com.walletverse.ui.ui.activity.WalletverseCollectionDetailActivity
import com.walletverse.ui.util.ActivityUtil
import com.walletverse.ui.util.DialogUtil
import com.walletverse.ui.util.EventBusUtil
import com.walletverse.ui.util.ToastUtil
import kotlinx.android.synthetic.main.fragment_nft.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * ================================================================
 * Create_time：2022/7/29  12:22
 * Author：solomon
 * Detail：
 * ================================================================
 */
class NftFragment : BaseFragment() {
    private lateinit var mNftCollectionAdapter: NftCollectionAdapter

    override fun getLayoutId(): Int = R.layout.fragment_nft

    override fun initData() {

    }


    override fun initView() {
//        v_recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//        mNftCollectionAdapter = NftCollectionAdapter(R.layout.item_nft_colletion)
//        v_recycler.adapter = mNftCollectionAdapter
//        mNftCollectionAdapter.setOnItemClickListener(this)
        v_nft_collections.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.v_nft_collections -> {
                lifecycleScope.launch(Dispatchers.IO) {
                    val wid = SPUtils.getInstance().getString(Constants.CURRENT_WID)
                    val walletCoins =
                        Walletverse.sInstance.queryWalletCoinsAsync(GetWalletCoinParams(wid))
                    val has = walletCoins!!.any { it.chainId == EChain.MAPO.chainId }
                    if (!has) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            showTipDialog(getString(R.string.add_chain_tip, EChain.MAPO.contract),
                                object : DialogUtil.OnCommonListener {
                                    override fun onConfirm() {
                                        lifecycleScope.launch(Dispatchers.IO){
                                            try {
                                                val pwd = SPUtils.getInstance().getString(Constants.PWD)
                                                val mChains = Walletverse.sInstance.queryCoinsAsync()
                                                    ?.filter { it.type == Type.CHAIN && it.contract == EChain.MAPO.contract } as MutableList<Coin>
                                                val result = Walletverse.sInstance.saveWalletCoinAsync(
                                                    SaveCoinParams(
                                                        wid,
                                                        pwd,
                                                        mChains[0]
                                                    )
                                                )
                                                if (result) {
                                                    EventBusUtil.post(RefreshEvent(WalletFragment.REFRESH_EVENT))
                                                    ActivityUtil.goActivity(
                                                        requireActivity(),
                                                        WalletverseCollectionDetailActivity::class.java
                                                    )
                                                }
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                        }
                                    }

                                    override fun onCancel() {
                                    }

                                })
                            return@launch
                        }
                    } else {
                        ActivityUtil.goActivity(
                            requireActivity(),
                            WalletverseCollectionDetailActivity::class.java
                        )
                    }
                }
            }
        }
    }
//
//    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
//        ActivityUtil.goActivity(requireActivity(), WalletverseCollectionDetailActivity::class.java)
//    }


}

















