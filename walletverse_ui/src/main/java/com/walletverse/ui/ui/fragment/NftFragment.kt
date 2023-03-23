package com.walletverse.ui.ui.fragment

import android.view.View
import com.walletverse.ui.R
import com.walletverse.ui.adapter.NftCollectionAdapter
import com.walletverse.ui.base.BaseFragment
import com.walletverse.ui.ui.activity.WalletverseCollectionDetailActivity
import com.walletverse.ui.util.ActivityUtil
import kotlinx.android.synthetic.main.fragment_nft.*


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
                ActivityUtil.goActivity(
                    requireActivity(),
                    WalletverseCollectionDetailActivity::class.java
                )
            }
        }
    }
//
//    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
//        ActivityUtil.goActivity(requireActivity(), WalletverseCollectionDetailActivity::class.java)
//    }


}

















