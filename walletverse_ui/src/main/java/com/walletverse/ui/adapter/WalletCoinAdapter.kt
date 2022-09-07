package com.walletverse.ui.adapter

import android.widget.ImageView
import com.walletverse.core.bean.Coin
import com.walletverse.core.enums.Type
import com.walletverse.ui.R
import com.walletverse.ui.constant.GlobalConstants
import com.walletverse.ui.util.MarkerUtil.getMarkerWithChainId
import com.walletverse.ui.util.loadImageCenterCrop
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import kotlinx.coroutines.*


/**
 * ================================================================
 * Create_time：2022/8/6  14:56
 * Author：solomon
 * Detail：
 * ================================================================
 */
class WalletCoinAdapter(layoutResId: Int) : BaseQuickAdapter<Coin, BaseViewHolder>(layoutResId) {


    override fun convert(holder: BaseViewHolder, item: Coin) {
        holder.setText(R.id.v_symbol, item.symbol)
        holder.getView<ImageView>(R.id.v_icon)
            .loadImageCenterCrop(item.iconUrl, R.mipmap.icon_circle_placeholder)
//        v_maker
        if (item.type == Type.COIN) {
            holder.setVisible(R.id.v_marker, true)
            holder.setImageResource(R.id.v_marker, getMarkerWithChainId(item.chainId))
        } else {
            holder.setVisible(R.id.v_marker, false)
        }

        holder.setText(R.id.v_balance, item.balance.ifEmpty { "0.00" })
//        price
        holder.setText(
            R.id.v_price,
            "≈${GlobalConstants.CURRENT_CURRENCY_SYMBOL}${item.totalPrice.ifEmpty { "0.00" }}"
        )



    }

}