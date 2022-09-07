package com.walletverse.ui.util

object StringUtil {
    fun formatAddress(address:String) :String{
        if(address.isEmpty())
            return ""
        val start=address.substring(0,8)
        val end=address.substring(address.length-8,address.length)
        return "$start....$end"
    }
}