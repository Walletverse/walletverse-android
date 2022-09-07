package com.walletverse.ui.adapter

import com.walletverse.ui.R
import com.walletverse.ui.bean.FeeBean
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class FeeAdapter(layoutResId: Int) : BaseQuickAdapter<FeeBean, BaseViewHolder>(layoutResId) {

    private var mSpeed=""

    fun setSpeed(speed:String){
        mSpeed=speed
    }

    override fun convert(holder: BaseViewHolder, item: FeeBean) {
        holder.setText(R.id.v_speed_name, item.speedName)
        holder.setText(R.id.v_gwei, "${item.gwei} GWEI")
        val time = when (item.speed) {
            "Fastest" -> "< 0.5min"
            "Fast" -> "< 2min"
            "General" -> "< 5min"
            else -> ""
        }
        holder.setText(R.id.v_time, time)
        if(mSpeed==item.speed){
            holder.setImageResource(R.id.v_check,R.mipmap.icon_coin_check)
        }else{
            holder.setImageResource(R.id.v_check,R.mipmap.icon_unchecked_new)
        }
    }
}