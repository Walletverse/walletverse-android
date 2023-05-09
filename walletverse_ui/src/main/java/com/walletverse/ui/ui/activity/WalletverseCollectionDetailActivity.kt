package com.walletverse.ui.ui.activity

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.SizeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.walletverse.core.Walletverse
import com.walletverse.core.bean.GetWalletCoinParams
import com.walletverse.core.bean.NFT
import com.walletverse.core.bean.NFTParams
import com.walletverse.core.enums.EChain
import com.walletverse.ui.R
import com.walletverse.ui.adapter.NftAdapter
import com.walletverse.ui.base.BaseActivity
import com.walletverse.ui.constant.Constants
import com.walletverse.ui.util.ActivityUtil
import com.walletverse.ui.util.StringUtil
import com.walletverse.ui.util.ToastUtil
import kotlinx.android.synthetic.main.activity_walletverse_collection_detail.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WalletverseCollectionDetailActivity : BaseActivity(), OnItemClickListener {


    private lateinit var mHeaderView: View
    private lateinit var vEmptyView: View
    private lateinit var vTotal: TextView
    private lateinit var mNftAdapter: NftAdapter
    private var mNftList: MutableList<NFT> = arrayListOf()

    private lateinit var address: String

    override fun getLayoutId(): Int {
        return R.layout.activity_walletverse_collection_detail
    }

    override fun initData() {
        v_recycler.layoutManager = GridLayoutManager(this, 2)
        mNftAdapter = NftAdapter(R.layout.item_nft)
        v_recycler.adapter = mNftAdapter
        mNftAdapter.setOnItemClickListener(this)
        val space = SizeUtils.dp2px(10f)
        v_recycler.addItemDecoration(SpacesItemDecoration(space))
        v_recycler.setPadding(space, 0, 0, 0)

        mHeaderView = LayoutInflater.from(this).inflate(R.layout.view_nft_detail_header, null)
        mHeaderView.findViewById<View>(R.id.v_contract_address_layout).setOnClickListener(this)
        vEmptyView = mHeaderView.findViewById<View>(R.id.v_empty)
//        mHeaderView.findViewById<TextView>(R.id.v_publish).text=""
        vTotal = mHeaderView.findViewById<TextView>(R.id.v_nft_count)
//        mHeaderView.findViewById<TextView>(R.id.v_nft_intro).text=""
        mHeaderView.findViewById<TextView>(R.id.v_contract_address).text =
            "Contract Address: ${StringUtil.formatAddress(Constants.NFT_CONTRACT)}"
        mNftAdapter.addHeaderView(mHeaderView)

        lifecycleScope.launch(Dispatchers.IO) {
            val wid = SPUtils.getInstance().getString(Constants.CURRENT_WID)
            val walletCoins = Walletverse.sInstance.queryWalletCoinsAsync(GetWalletCoinParams(wid))
            val coin =
                walletCoins?.filter { it.contract == EChain.MAPO.contract && it.symbol == EChain.MAPO.symbol }
            address = coin?.get(0)?.address.toString()
            getData()
        }


    }

    override fun initView() {
        initTitleBar("NFT")

//        v_refresh.setOnRefreshLoadMoreListener(this)
        v_refresh.setEnableLoadMoreWhenContentNotFull(false)
        v_refresh.setEnableRefresh(false)
        v_refresh.setEnableLoadMore(false)
        v_receive.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.v_receive -> {
                val bundle = Bundle()
                bundle.putString("address", address)
                bundle.putString("contract", EChain.MAPO.contract)
                bundle.putString("symbol", EChain.MAPO.symbol)
                ActivityUtil.goActivity(this, WalletverseReceiveActivity::class.java, bundle)
            }
            R.id.v_contract_address_layout -> {
                ClipboardUtils.copyText(Constants.NFT_CONTRACT)
                ToastUtil.showSuccess(getString(R.string.copy_success))
            }
        }
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        val bundle = Bundle()
        bundle.putString("tokenId", mNftList[position].tokenId)
        bundle.putString("fromAddress", address)
        ActivityUtil.goActivity(this, WalletverseNftDetailActivity::class.java, bundle)

    }

//    override fun onRefresh(refreshLayout: RefreshLayout) {
//
//    }
//
//    override fun onLoadMore(refreshLayout: RefreshLayout) {
//
//    }


    class SpacesItemDecoration(val space: Int) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            if (parent.layoutManager is GridLayoutManager) {
                outRect.top = space
                outRect.right = space
            }
        }
    }


    private fun getData() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val result = Walletverse.sInstance.requestNFTListAsync(
                    NFTParams(
                        address,
                        Constants.NFT_CONTRACT,
                        EChain.MAPO.chainId
                    )
                )
                withContext(Dispatchers.Main) {
                    if (result != null && result.items.isNotEmpty()) {
                        vEmptyView.visibility = View.GONE
                        mNftList = result.items
                        vTotal.text = "${result.total} Pieces"
                        mNftAdapter.setList(mNftList)
                    } else {
                        vEmptyView.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}
















