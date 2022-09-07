package com.walletverse.ui.view

import android.annotation.SuppressLint
import android.content.Context
import com.walletverse.ui.R
import com.walletverse.ui.util.DialogUtil
import com.lxj.xpopup.core.CenterPopupView
import kotlinx.android.synthetic.main.view_center_common_board.view.*


/**
 * ================================================================
 * Create_time：2022/8/9  13:29
 * Author：solomon
 * Detail：
 * ================================================================
 */
@SuppressLint("ViewConstructor")
class CenterCommonBoard(context: Context, content:String, onCommonListener: DialogUtil.OnCommonListener) :
    CenterPopupView(context) {

    private val mOnResultListener = onCommonListener
    private val mContent=content

    override fun getImplLayoutId(): Int {
        return R.layout.view_center_common_board
    }

    override fun onCreate() {
        super.onCreate()
        v_content.text=mContent
        v_cancel.setOnClickListener{
            this.dismiss()
            mOnResultListener.onCancel()
        }
        v_confirm.setOnClickListener{
            this.dismiss()
            mOnResultListener.onConfirm()
        }
    }


}