package com.walletverse.ui.adapter

import com.walletverse.ui.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder


/**
 * ================================================================
 * Create_time：2022/8/9  13:50
 * Author：solomon
 * Detail：
 * ================================================================
 */
class PasswordImgAdapter(layoutResId: Int) :
    BaseQuickAdapter<Boolean, BaseViewHolder>(layoutResId) {

    override fun convert(holder: BaseViewHolder, item: Boolean) {
        val image = if (item) {
            R.mipmap.icon_circle_selected
        } else {
            R.mipmap.icon_circle_unselected
        }
        holder.setImageResource(R.id.v_circle,image)
    }
}