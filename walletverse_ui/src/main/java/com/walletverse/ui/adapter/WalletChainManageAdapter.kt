package com.walletverse.ui.adapter

import android.widget.ImageView
import com.walletverse.core.bean.Coin
import com.walletverse.ui.R
import com.walletverse.ui.util.StringUtil
import com.walletverse.ui.util.loadImageCenterCrop
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder


/**
 * ================================================================
 * Create_time：2022/8/6  14:56
 * Author：solomon
 * Detail：
 * ================================================================
 */
class WalletChainManageAdapter(layoutResId: Int) :
    BaseQuickAdapter<Coin, BaseViewHolder>(layoutResId) {

    override fun convert(holder: BaseViewHolder, item: Coin) {
        holder.setVisible(R.id.v_operation, item.contract != "ETH" && data.size != 1)
        holder.setText(R.id.v_symbol, item.contract)
        holder.setText(R.id.v_address, StringUtil.formatAddress(item.address))
        holder.getView<ImageView>(R.id.v_icon).loadImageCenterCrop(item.iconUrl,R.mipmap.icon_circle_placeholder)
    }
}