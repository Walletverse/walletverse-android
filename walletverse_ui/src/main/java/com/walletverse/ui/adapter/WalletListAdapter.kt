package com.walletverse.ui.adapter

import com.walletverse.core.bean.Identity
import com.walletverse.ui.R
import com.walletverse.ui.constant.Constants
import com.blankj.utilcode.util.SPUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder


/**
 * ================================================================
 * Create_time：2022/8/6  15:37
 * Author：solomon
 * Detail：
 * ================================================================
 */
class WalletListAdapter(layoutResId: Int) :BaseQuickAdapter<Identity,BaseViewHolder>(layoutResId) {

    override fun convert(holder: BaseViewHolder, item: Identity) {
        if(item.wid==SPUtils.getInstance().getString(Constants.CURRENT_WID)){
            holder.setImageResource(R.id.v_check,R.mipmap.icon_circle_check_small)
        }else{
            holder.setImageResource(R.id.v_check,R.mipmap.icon_circle_uncheck_small)
        }
        holder.setText(R.id.v_wallet_name,item.name)
    }

}