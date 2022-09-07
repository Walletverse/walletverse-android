package com.walletverse.ui.view

import android.annotation.SuppressLint
import android.content.Context
import com.walletverse.ui.R
import com.walletverse.ui.util.DialogUtil
import com.blankj.utilcode.util.StringUtils
import com.lxj.xpopup.core.CenterPopupView
import kotlinx.android.synthetic.main.view_input_board.view.*
import kotlinx.android.synthetic.main.view_password_board.view.*


/**
 * ================================================================
 * Create_time：2022/8/9  13:29
 * Author：solomon
 * Detail：
 * ================================================================
 */
@SuppressLint("ViewConstructor")
class InputBoard(context: Context, onInputConfirmListener: DialogUtil.OnInputConfirmListener) :
    CenterPopupView(context) {

    private val mOnInputConfirmListener = onInputConfirmListener

    override fun getImplLayoutId(): Int {
        return R.layout.view_input_board
    }

    override fun onCreate() {
        super.onCreate()
        v_cancel.setOnClickListener{
            this.dismiss()
        }
        v_confirm.setOnClickListener{
            if(StringUtils.isEmpty(v_name.text.toString()))
                return@setOnClickListener
            this.dismiss()
            mOnInputConfirmListener.onConfirm(v_name.text.toString())
        }
    }


}