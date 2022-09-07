package com.walletverse.ui.adapter

import androidx.core.content.ContextCompat
import com.walletverse.ui.R
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ruffian.library.widget.RTextView


/**
 * ================================================================
 * Create_time：2022/8/9  13:50
 * Author：solomon
 * Detail：
 * ================================================================
 */
class PasswordNumberAdapter(layoutResId: Int) :
    BaseQuickAdapter<String, BaseViewHolder>(layoutResId) {

    override fun convert(holder: BaseViewHolder, item: String) {
        if (item == "Delete") {
            holder.getView<RTextView>(R.id.v_num).helper.iconNormal=ContextCompat.getDrawable(context,R.mipmap.icon_delete)
        }else{
            holder.setText(R.id.v_num,item)
        }
    }
}