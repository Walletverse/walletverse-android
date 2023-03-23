package com.walletverse.ui.adapter

import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.walletverse.core.bean.NFT
import com.walletverse.ui.R
import com.walletverse.ui.util.loadImageCenterCrop


/**
 * ================================================================
 * Create_time：2022/8/6  14:56
 * Author：solomon
 * Detail：
 * ================================================================
 */
class NftAdapter(layoutResId: Int) : BaseQuickAdapter<NFT, BaseViewHolder>(layoutResId) {


    override fun convert(holder: BaseViewHolder, item: NFT) {
        holder.getView<ImageView>(R.id.v_image).loadImageCenterCrop(item.image,R.mipmap.ic_launcher)
        holder.getView<TextView>(R.id.v_nft_name).text=item.name
        holder.getView<TextView>(R.id.v_nft_token_id).text="Token Id：${item.tokenId}"

    }

}