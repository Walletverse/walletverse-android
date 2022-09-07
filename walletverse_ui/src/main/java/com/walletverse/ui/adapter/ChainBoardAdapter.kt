package com.walletverse.ui.adapter

import android.widget.ImageView
import com.walletverse.core.bean.Coin
import com.walletverse.ui.R
import com.walletverse.ui.util.loadImageCenterCrop
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class ChainBoardAdapter(layoutResId: Int) : BaseQuickAdapter<Coin, BaseViewHolder>(layoutResId) {

    override fun convert(holder: BaseViewHolder, item: Coin) {
        holder.setText(R.id.v_name, item.chainName)
        holder.getView<ImageView>(R.id.v_icon).loadImageCenterCrop(item.iconUrl,R.mipmap.icon_circle_placeholder)
    }
}