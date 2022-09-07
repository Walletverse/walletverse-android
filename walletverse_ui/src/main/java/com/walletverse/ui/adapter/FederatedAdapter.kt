package com.walletverse.ui.adapter

import com.walletverse.ui.R
import com.walletverse.ui.bean.FederatedBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class FederatedAdapter(layoutResId: Int) :BaseQuickAdapter<FederatedBean,BaseViewHolder>(layoutResId) {

    override fun convert(holder: BaseViewHolder, item: FederatedBean) {
        holder.setImageResource(R.id.v_channel_image,item.channel_icon)
        holder.setText(R.id.v_channel,item.channel)
    }
}