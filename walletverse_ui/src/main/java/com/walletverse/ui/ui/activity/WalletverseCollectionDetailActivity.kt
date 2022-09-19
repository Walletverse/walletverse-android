package com.walletverse.ui.ui.activity

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.SizeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.walletverse.core.bean.NFT
import com.walletverse.ui.R
import com.walletverse.ui.adapter.NftAdapter
import com.walletverse.ui.base.BaseActivity
import com.walletverse.ui.util.ActivityUtil
import com.walletverse.ui.util.ToastUtil
import kotlinx.android.synthetic.main.activity_walletverse_collection_detail.*

class WalletverseCollectionDetailActivity : BaseActivity(), OnItemClickListener, OnRefreshLoadMoreListener {


    private lateinit var mHeaderView: View
    private lateinit var mNftAdapter: NftAdapter

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
//        mHeaderView.findViewById<TextView>(R.id.v_nft_icon).text=""
//        mHeaderView.findViewById<TextView>(R.id.v_publish).text=""
//        mHeaderView.findViewById<TextView>(R.id.v_nft_count).text=""
//        mHeaderView.findViewById<TextView>(R.id.v_nft_intro).text=""
//        mHeaderView.findViewById<TextView>(R.id.v_contract_address).text=""
        mNftAdapter.addHeaderView(mHeaderView)


        val list = arrayListOf<NFT>()
        for (i in 0 until 1) {
            list.add(NFT(""))
        }

        mNftAdapter.setList(list)

    }

    override fun initView() {
        initTitleBar("NFT")

        v_refresh.setOnRefreshLoadMoreListener(this)
        v_refresh.setEnableLoadMoreWhenContentNotFull(false)
        v_receive.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when(v.id){
            R.id.v_receive->{
                val bundle = Bundle()
                bundle.putString("address","0x1111111111111111111")
                bundle.putString("contract","MAP")
                bundle.putString("symbol","DDD")
                ActivityUtil.goActivity(this, WalletverseReceiveActivity::class.java, bundle)
            }
            R.id.v_contract_address_layout->{
                ClipboardUtils.copyText("mCoin.address")
                ToastUtil.showSuccess(getString(R.string.copy_success))
            }
        }
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        val bundle = Bundle()
//        bundle.putString("address","0x1111111111111111111")
//        bundle.putString("contract","MAP")
//        bundle.putString("symbol","DDD")
        ActivityUtil.goActivity(this, WalletverseNftDetailActivity::class.java, bundle)

    }

    override fun onRefresh(refreshLayout: RefreshLayout) {

    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {

    }



    class SpacesItemDecoration(val space: Int) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            if (parent.layoutManager is GridLayoutManager) {
                outRect.top = space
                outRect.right = space
            }
        }
    }


}
















