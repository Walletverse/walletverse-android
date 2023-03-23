package com.walletverse.ui.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.walletverse.core.Walletverse
import com.walletverse.core.bean.NFT
import com.walletverse.core.bean.NFTParams
import com.walletverse.core.bean.NameValue
import com.walletverse.core.enums.EChain
import com.walletverse.ui.R
import com.walletverse.ui.adapter.WalletverseAttributesAdapter
import com.walletverse.ui.base.BaseActivity
import com.walletverse.ui.constant.Constants
import com.walletverse.ui.util.ActivityUtil
import com.walletverse.ui.util.loadImageCenterCrop
import kotlinx.android.synthetic.main.activity_walletverse_nft_detail.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WalletverseNftDetailActivity : BaseActivity() {


    private lateinit var mWalletverseAttributesAdapter: WalletverseAttributesAdapter
    lateinit var tokenId: String
    lateinit var fromAddress: String
    var mNft: NFT? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_walletverse_nft_detail
    }

    override fun initData() {

        val bundle = intent.extras
        tokenId = bundle?.getString("tokenId").toString()
        fromAddress = bundle?.getString("fromAddress").toString()

        v_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mWalletverseAttributesAdapter = WalletverseAttributesAdapter(R.layout.item_nft_attributes)
        v_recycler.adapter = mWalletverseAttributesAdapter

        getData()

    }

    private fun getData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val result = Walletverse.sInstance.requestNFTDetailAsync(
                NFTParams(
                    tokenId = tokenId,
                    contractAddress = Constants.NFT_CONTRACT,
                    chainId = EChain.MAP.chainId
                )
            )
            mNft = result
            withContext(Dispatchers.Main) {
                v_image.loadImageCenterCrop(result.image, R.mipmap.ic_launcher)
                v_nft_name.text = result.name
                mWalletverseAttributesAdapter.setList(result.attributes)
            }
        }
    }

    override fun initView() {
        initTitleBar("")
        v_transfer.setOnClickListener(this)
    }


    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.v_transfer -> {
                val bundle = Bundle()
                bundle.putString("tokenId", tokenId)
                bundle.putString("fromAddress", fromAddress)
                bundle.putString("name", mNft?.name)
                bundle.putString("image", mNft?.image)
                ActivityUtil.goActivity(this, WalletverseNftTransferActivity::class.java, bundle)
            }
        }
    }

}