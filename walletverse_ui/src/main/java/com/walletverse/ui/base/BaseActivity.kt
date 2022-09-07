package com.walletverse.ui.base

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.walletverse.ui.R
import com.walletverse.ui.constant.Constants
import com.walletverse.ui.util.*
import com.walletverse.ui.util.SupportUtil.changeLanguage
import com.blankj.utilcode.util.SPUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.LoadingPopupView
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.view_titlebar.*
import org.greenrobot.eventbus.EventBus

abstract class BaseActivity : AppCompatActivity(), IClickPresenter, IToast, ITipDialog,
    OnTitleBarListener {

    private var loadingPopupView: LoadingPopupView? = null

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        immersionBar {
            statusBarColor(R.color.white)
            navigationBarColor(R.color.white)
            fitsSystemWindows(true)
            statusBarDarkFont(true)
        }
        setLanguage()
        setContentView(getLayoutId())
//        window.decorView.findViewById<View>(android.R.id.content).setOnClickListener(this)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        initView()
        initData()
        initEvent()
    }

    fun initTitleBar(title: String = "", leftIcon: Int = 0, rightIcon: Int = 0,backgroundColor:Int =ContextCompat.getColor(this,R.color.white)) {
        title_bar?.title = title
        if (leftIcon != 0) leftIcon.let {
            title_bar?.leftIcon = ContextCompat.getDrawable(this, it)
        }
        if (rightIcon != 0) rightIcon.let {
            title_bar?.rightIcon = ContextCompat.getDrawable(this, it)
        }
        title_bar?.setBackgroundColor(backgroundColor)
        title_bar?.setOnTitleBarListener(this)
    }

    private fun setLanguage() {
        changeLanguage(
            this, SPUtils.getInstance().getString(Constants.CURRENT_LANGUAGE),
            SPUtils.getInstance().getString(Constants.CURRENT_COUNTRY)
        )
    }

    override fun onLeftClick(titleBar: TitleBar?) {
        this.finish()
    }

    override fun onRightClick(titleBar: TitleBar?) {

    }

    abstract fun getLayoutId(): Int

    abstract fun initData()

    abstract fun initView()

    private fun initEvent() {
        if (this.javaClass.isAnnotationPresent(BindEventBus::class.java) && !EventBus.getDefault()
                .isRegistered(this)
        ) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onClick(v: View) {
        if(ClickUtil.isFastDoubleClick(v.id.toString()))
            return
    }


    override fun onResume() {
        super.onResume()
        Logger.e(this.javaClass.name)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (this.javaClass.isAnnotationPresent(BindEventBus::class.java) && EventBus.getDefault()
                .isRegistered(this)
        ) {
            EventBus.getDefault().unregister(this)
        }


        if (null != loadingPopupView && loadingPopupView!!.isShow) {
            loadingPopupView?.dismiss()
        }
        loadingPopupView = null

    }


    private fun initLoadingView() {
        loadingPopupView = XPopup.Builder(this)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(true)
            .asLoading("Loading")
    }

    private fun showLoadingView() {
        if (null != loadingPopupView) {
            loadingPopupView?.show()
        } else {
            initLoadingView()
            loadingPopupView?.show()
        }
    }

    private fun hideLoadingView() {
        if (null != loadingPopupView && loadingPopupView!!.isShow) {
            loadingPopupView?.dismiss()
        }
    }


    fun showLoading() {
        showLoadingView()
    }

    fun hideLoading() {
        hideLoadingView()
    }

    override fun showToast(msg: String) {
        ToastUtil.showSuccess(msg)
    }

    override fun showErrorToast(msg: String) {
        ToastUtil.showError(msg)
    }

    override fun showTipDialog(msg: String, onCommonListener: DialogUtil.OnCommonListener) {
        DialogUtil.showCenterCommonDialog(this,msg,object :DialogUtil.OnCommonListener{
            override fun onConfirm() {
                onCommonListener.onConfirm()
            }

            override fun onCancel() {
                onCommonListener.onCancel()
            }
        })
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        KeyboardUtil.hideSoftKeyboard(this,event)
        return super.dispatchTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        KeyboardUtil.hideSoftKeyboard(this,event)
        return super.onTouchEvent(event)
    }


//    fun switchLanguage(event: SwitchLanguageEvent){
//        changeLanguage(this, event.language, event.country)
//        recreate()
//    }
}
