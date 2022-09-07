package com.walletverse.ui.base


 data class BaseBean<T> (
     val msg: String = "",
     val code: Int = 200,
     var data: T
 )