package com.walletverse.ui.util


/**
 * ================================================================
 * Create_time：2022/8/22  19:01
 * Author：solomon
 * Detail：
 * ================================================================
 */
object ContractUtil {

    fun getContract(symbol:String):String{
        if(symbol=="KCC"){
            return "KCS"
        }
        return symbol
    }
}