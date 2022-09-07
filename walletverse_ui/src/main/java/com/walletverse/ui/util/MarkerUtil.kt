package com.walletverse.ui.util

import com.walletverse.ui.R

object MarkerUtil {
    fun getMarkerWithChainId(chainId:String):Int{
        when(chainId){
            "0x1"->return R.mipmap.icon_local_eth_unchecked_light
            "0x38"->return R.mipmap.icon_local_bnb_unchecked_light
            "0x58f8"->return R.mipmap.icon_local_map_unchecked_light
            "0xfa"->return R.mipmap.icon_local_ftm_unchecked_light
            "0x141"->return R.mipmap.icon_local_kcs_unchecked_light
            "0x71a"->return R.mipmap.icon_local_cube_unchecked_light
            "0x42"->return R.mipmap.icon_local_okt_unchecked_light
            "0x2019"->return R.mipmap.icon_local_klay_unchecked_light
            "0x4b82"->return R.mipmap.icon_local_true_unchecked_light
            "0xa86a"->return R.mipmap.icon_local_avax_unchecked_light
            "0x89"->return R.mipmap.icon_local_polygon_unchecked_light
        }
        return R.mipmap.icon_circle_placeholder
    }
}