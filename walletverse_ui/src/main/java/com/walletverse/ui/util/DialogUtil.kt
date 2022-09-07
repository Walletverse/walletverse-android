package com.walletverse.ui.util

import android.content.Context
import com.walletverse.core.bean.Coin
import com.walletverse.ui.R
import com.walletverse.ui.bean.TransferParams
import com.walletverse.ui.view.*
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.interfaces.OnSelectListener


/**
 * ================================================================
 * Create_time：2022/8/4  16:05
 * Author：solomon
 * Detail：
 * ================================================================
 */
object DialogUtil {

    fun showWalletListDrawer(context: Context) {
        XPopup.Builder(context)
            .hasShadowBg(true)
            .asCustom(WalletListDrawer(context))
            .show()
    }


    fun showBottomListDialog(
        context: Context,
        title: String? = "",
        list: Array<String>,
        selectListener: OnSelectListener
    ) {
        XPopup.Builder(context)
//                .isDarkTheme(true)
            .hasShadowBg(true)
            .moveUpToKeyboard(false)
            .isCoverSoftInput(true)
            //.hasBlurBg(true)
            .isDestroyOnDismiss(true)
            .asBottomList(
                title,
                list,
                null,
                -1,
                selectListener,
                R.layout.item_bottom_list,
                R.layout.item_adapter_text_match
            ).show()
    }

    interface OnCompletedListener {
        fun passwordComplete(pass: String, dialog: PasswordBoard)
    }

    fun showPasswordDialog(
        context: Context,
        onCompletedListener: OnCompletedListener
    ) {
        XPopup.Builder(context)
            .hasShadowBg(true)
            .moveUpToKeyboard(false)
            .isCoverSoftInput(true)
            //.hasBlurBg(true)
            .isDestroyOnDismiss(true)
            .asCustom(PasswordBoard(context, onCompletedListener))
            .show()
    }

    interface OnCommonListener {
        fun onConfirm()
        fun onCancel()
    }

    fun showTransferDialog(
        context: Context,
        transferParams: TransferParams,
        onCommonListener: OnCommonListener
    ) {
        XPopup.Builder(context)
            .hasShadowBg(true)
            .moveUpToKeyboard(false)
            .isCoverSoftInput(true)
            //.hasBlurBg(true)
            .isDestroyOnDismiss(true)
            .asCustom(TransferBoard(context, transferParams, onCommonListener))
            .show()
    }

    interface OnInputConfirmListener {
        fun onConfirm(text: String)
    }

    fun showInputDialog(context: Context, onInputConfirmListener: OnInputConfirmListener) {
        XPopup.Builder(context)
            .isDestroyOnDismiss(true)
            .autoOpenSoftInput(true)
            //.hasBlurBg(true)
            .isDestroyOnDismiss(true)
            .asCustom(InputBoard(context, onInputConfirmListener))
            .show()
    }

    fun showCenterCommonDialog(
        context: Context,
        content: String,
        onCommonListener: OnCommonListener
    ) {
        XPopup.Builder(context)
            .maxWidth(ScreenUtils.getAppScreenWidth()-SizeUtils.dp2px(30f))
            .isDestroyOnDismiss(true)
            //.hasBlurBg(true)
            .isDestroyOnDismiss(true)
            .asCustom(CenterCommonBoard(context, content, onCommonListener))
            .show()
    }

    interface OnResultListener {
        fun onConfirm(result: Any)
        fun onCancel()
    }

    fun showEmailVerifyDialog(
        context: Context,
        email:String,
        onResultListener: OnResultListener
    ) {
        XPopup.Builder(context)
            .maxWidth(ScreenUtils.getAppScreenWidth()-SizeUtils.dp2px(30f))
            .isDestroyOnDismiss(true)
            .autoOpenSoftInput(true)
            //.hasBlurBg(true)
            .isDestroyOnDismiss(true)
            .asCustom(EmailVerifyBoard(context,email,onResultListener))
            .show()
    }



    interface OnChainClickListener {
        fun onChainClick(chain: Coin)
    }


    fun showChainBoardDialog(
        context: Context,
        coins: MutableList<Coin>,
        onChainClickListener: OnChainClickListener
    ) {
        XPopup.Builder(context)
            .maxHeight(SizeUtils.dp2px(450f))
            .hasShadowBg(true)
            .moveUpToKeyboard(false)
            .isCoverSoftInput(true)
            //.hasBlurBg(true)
            .isDestroyOnDismiss(true)
            .asCustom(ChainBoard(context, coins, onChainClickListener))
            .show()
    }


    fun showExportDialog(context: Context, content: MutableList<String>) {
        XPopup.Builder(context)
            .maxHeight(SizeUtils.dp2px(450f))
            .isDestroyOnDismiss(true)
            //.hasBlurBg(true)
            .isDestroyOnDismiss(true)
            .asCustom(ExportBoard(context, content))
            .show()
    }

}