package com.walletverse.ui.core

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.pow


fun getGwei(gasPrice: String): Double {
    if(gasPrice.isEmpty())
        return 0.0
    return gasPrice.toBigDecimal().divide(BigDecimal(1000000000)).setScale(18, RoundingMode.UP)
        .stripTrailingZeros()
        .toDouble()
}

fun getGasPrice(gwei: Double): String {
    return gwei.toBigDecimal().multiply(BigDecimal(1000000000)).setScale(18, RoundingMode.UP)
        .stripTrailingZeros().toString()
}

fun getValue(value: String,decimals: Int): String {
    if(value.isEmpty())
        return "0"
    return "0x${value.toBigDecimal().multiply(
        10.0.pow(
            decimals
        ).toBigDecimal()
    ).toLong().toString(16)}"
}


fun getFee(gasPrice: String, gasLimit: String, decimals: Int): String {
    if(gasPrice.isEmpty()||gasLimit.isEmpty())
        return "0"
    return gasPrice.toBigDecimal().multiply(gasLimit.toBigDecimal()).divide(
        10.0.pow(
            decimals
        ).toBigDecimal()
    ).setScale(8, RoundingMode.UP).stripTrailingZeros().toPlainString()
}

fun setScale(num: String,roundingMode: RoundingMode?=RoundingMode.DOWN): String {
    return num.toBigDecimal().setScale(8, roundingMode).stripTrailingZeros().toPlainString()
}

fun setScale(num: BigDecimal,roundingMode: RoundingMode?=RoundingMode.DOWN): String {
    return num.setScale(8, roundingMode).stripTrailingZeros().toPlainString()
}
