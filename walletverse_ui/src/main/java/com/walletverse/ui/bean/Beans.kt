package com.walletverse.ui.bean

import androidx.annotation.Keep
import java.io.Serializable


/**
 * ================================================================
 * Create_time：2022/8/29  13:43
 * Author：solomon
 * Detail：
 * ================================================================
 */

@Keep
data class CsBean(
    val cs: String,
): Serializable

@Keep
data class CurrencyBean(
    val currency: String,
    val content: String
): Serializable

@Keep
data class FederatedBean(
    val channel_icon: Int,
    val channel: String
): Serializable

@Keep
data class FeeBean(
    val gwei: Double,
    val gasPrice: String,
    val gasLimit: String,
    val speed: String,
    val decimals: Int? = 18,
    val contract: String? = "",
    val speedName: String? = "",
    val basicGwei: Double? = 0.0,
    val basicGasPrice: String? = "",
    val basicGasLimit: String? = "",
) : Serializable

@Keep
data class LanguageBean(
    val language: String,
    val countryCode: String,
    val content: String
): Serializable