package com.walletverse.ui.adapter

import com.walletverse.ui.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder


/**
 * ================================================================
 * Create_time：2022/8/5  12:20
 * Author：solomon
 * Detail：
 * ================================================================
 */
class MnemonicAdapter(layoutResId: Int) : BaseQuickAdapter<String,BaseViewHolder>(layoutResId) {

    override fun convert(holder: BaseViewHolder, item: String) {
        holder.setText(R.id.v_num,(holder.adapterPosition+1).toString())
        holder.setText(R.id.v_word,item)
    }
}