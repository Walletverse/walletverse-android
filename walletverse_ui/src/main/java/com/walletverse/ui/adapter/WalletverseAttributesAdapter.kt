package com.walletverse.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.walletverse.core.bean.Attribute
import com.walletverse.core.bean.NFT
import com.walletverse.core.bean.NameValue
import com.walletverse.ui.R


/**
 * ================================================================
 * Create_time：2022/8/6  14:56
 * Author：solomon
 * Detail：
 * ================================================================
 */
class WalletverseAttributesAdapter(layoutResId: Int) : BaseQuickAdapter<Attribute, BaseViewHolder>(layoutResId) {


    override fun convert(holder: BaseViewHolder, item: Attribute) {
        holder.setText(R.id.v_key,item.trait_type)
        holder.setText(R.id.v_name,item.value)
    }

}