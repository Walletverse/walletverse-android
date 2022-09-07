package com.walletverse.ui.adapter

import com.walletverse.ui.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class ExportBoardAdapter(layoutResId: Int) :BaseQuickAdapter<String,BaseViewHolder>(layoutResId) {

    override fun convert(holder: BaseViewHolder, item: String) {
        holder.setText(R.id.v_content,item)
    }
}