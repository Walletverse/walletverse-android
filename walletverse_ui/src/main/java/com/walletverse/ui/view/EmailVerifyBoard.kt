package com.walletverse.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.walletverse.core.Walletverse
import com.walletverse.core.bean.EmailCodeParams
import com.walletverse.core.bean.EmailVerifyParams
import com.walletverse.ui.R
import com.walletverse.ui.base.BaseActivity
import com.walletverse.ui.util.DialogUtil
import com.walletverse.ui.util.ToastUtil
import com.caverock.androidsvg.SVG
import com.lxj.xpopup.core.CenterPopupView
import kotlinx.android.synthetic.main.view_email_board.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * ================================================================
 * Create_time：2022/8/9  13:29
 * Author：solomon
 * Detail：
 * ================================================================
 */
@SuppressLint("ViewConstructor")
class EmailVerifyBoard(context: Context, email:String, onResultListener: DialogUtil.OnResultListener) :
    CenterPopupView(context) {

    private val mOnResultListener = onResultListener
    private val mEmail=email
    private var step = 1
    private var codeText=""
    private var currentMillis=0L

    override fun getImplLayoutId(): Int {
        return R.layout.view_email_board
    }

    override fun onCreate() {
        super.onCreate()

        refreshCode()

        v_image.setOnClickListener {

            if(System.currentTimeMillis()-currentMillis>3000) {
                currentMillis=System.currentTimeMillis()
                refreshCode()
            }
        }

        v_cancel.setOnClickListener {
            this.dismiss()
            mOnResultListener.onCancel()
        }
        v_confirm.setOnClickListener {
            if (step == 1) {
                if (v_code.text.toString().isEmpty())
                    return@setOnClickListener
                (context as BaseActivity).showLoading()
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        val success=Walletverse.sInstance.getEmailCodeAsync(EmailCodeParams(v_code.text.toString(),codeText,"email",mEmail))
                        withContext(Dispatchers.Main){
                            (context as BaseActivity).hideLoading()
                            if(success) {
                                step = 2
                                v_code.setText("")
                                v_code.hint = context.getString(R.string.email_verification_code)
                                v_image.visibility=View.GONE
                            }else{
                                ToastUtil.showError(context.getString(R.string.graphics_verification_error))
                            }
                        }
                    } catch (e: Exception) {
                        (context as BaseActivity).hideLoading()
                        e.printStackTrace()
                    }

                }
            } else {
                if (v_code.text.toString().isEmpty())
                    return@setOnClickListener
                (context as BaseActivity).showLoading()
                lifecycleScope.launch(Dispatchers.IO){
                    try {
                        val auth=Walletverse.sInstance.requestEmailVerifyAsync(EmailVerifyParams(v_code.text.toString(),mEmail))
                        withContext(Dispatchers.Main){
                            (context as BaseActivity).hideLoading()
                            if(auth.isNotEmpty()){
                                mOnResultListener.onConfirm(auth)
                                dismiss()
                            }else{
                                ToastUtil.showError(context.getString(R.string.email_verification_code_error))
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            }


        }
    }

    private fun refreshCode() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val emailCode = Walletverse.sInstance.getGraphicsCodeAsync()
                codeText = emailCode.text
                val svg = SVG.getFromString(emailCode.data)
                val drawable: Drawable = PictureDrawable(svg.renderToPicture())
                withContext(Dispatchers.Main) {
                    v_image.setImageDrawable(drawable)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }


}