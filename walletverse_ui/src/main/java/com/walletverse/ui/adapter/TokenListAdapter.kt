package com.walletverse.ui.adapter

import android.widget.ImageView
import com.walletverse.core.bean.Coin
import com.walletverse.ui.R
import com.walletverse.ui.util.loadImageCenterCrop
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class TokenListAdapter(layoutResId: Int) : BaseQuickAdapter<Coin, BaseViewHolder>(layoutResId) {

    override fun convert(holder: BaseViewHolder, item: Coin) {
        if(item.isAdd){
            holder.setImageResource(R.id.v_check,R.mipmap.icon_coin_check)
        }else{
            holder.setImageResource(R.id.v_check,R.mipmap.icon_coin_add)
        }
        holder.getView<ImageView>(R.id.v_icon)
            .loadImageCenterCrop(item.iconUrl, R.mipmap.icon_circle_placeholder)
        holder.setText(R.id.v_symbol, item.symbol)
        holder.setText(R.id.v_contract_address, item.contractAddress)
        if (item.name.isNotEmpty()) {
            holder.setVisible(R.id.v_name, true)
            holder.setText(R.id.v_name, "(${item.name})")
        } else {
            holder.setVisible(R.id.v_name, false)
        }
    }

}