package com.walletverse.ui.adapter

import com.walletverse.core.bean.TransactionRecord
import com.walletverse.ui.R
import com.walletverse.ui.util.StringUtil
import com.blankj.utilcode.util.TimeUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder


/**
 * ================================================================
 * Create_time：2022/8/7  11:07
 * Author：solomon
 * Detail：
 * ================================================================
 */
class TransactionAdapter(layoutResId: Int) :BaseQuickAdapter<TransactionRecord,BaseViewHolder>(layoutResId) {

    override fun convert(holder: BaseViewHolder, item: TransactionRecord) {
        holder.setText(R.id.v_address,StringUtil.formatAddress(item.to))
        holder.setText(R.id.v_date,TimeUtils.millis2String(item.timestamp))
        holder.setText(R.id.v_value, if (item.value != "0") "-${item.value}" else item.value)
    }
}