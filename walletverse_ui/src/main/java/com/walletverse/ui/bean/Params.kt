package com.walletverse.ui.bean

import androidx.annotation.Keep
import java.io.Serializable


/**
 * ================================================================
 * Create_time：2022/8/29  13:45
 * Author：solomon
 * Detail：
 * ================================================================
 */

@Keep
class TransferParams(
    val value: String,
    val symbol: String,
    val contract: String,
    val contractAddress: String,
    val from: String,
    val to: String,
    val gasFee: String,
    val gas: String
): Serializable