package com.walletverse.ui.ui.activity

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.walletverse.core.bean.NameValue
import com.walletverse.ui.R
import com.walletverse.ui.adapter.WalletverseAttributesAdapter
import com.walletverse.ui.base.BaseActivity
import com.walletverse.ui.util.ActivityUtil
import kotlinx.android.synthetic.main.activity_walletverse_nft_detail.*

class WalletverseNftDetailActivity : BaseActivity() {


    private lateinit var mWalletverseAttributesAdapter: WalletverseAttributesAdapter

    override fun getLayoutId(): Int {
        return R.layout.activity_walletverse_nft_detail
    }

    override fun initData() {
        v_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mWalletverseAttributesAdapter = WalletverseAttributesAdapter(R.layout.item_nft_attributes)
        v_recycler.adapter = mWalletverseAttributesAdapter

       val list= arrayListOf<NameValue>()
        for (i in 0..10){
            list.add(NameValue("name${i}","value${i}"))
        }
        mWalletverseAttributesAdapter.setList(list)
    }

    override fun initView() {
        initTitleBar("")
        v_transfer.setOnClickListener(this)
    }


    override fun onClick(v: View) {
        super.onClick(v)
        when(v.id){
            R.id.v_transfer->{
//        bundle.putString("address","0x1111111111111111111")
//        bundle.putString("contract","MAP")
//        bundle.putString("symbol","DDD")
                ActivityUtil.goActivity(this, WalletverseNftTransferActivity::class.java)
            }
        }
    }

}