package com.walletverse.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.walletverse.ui.util.BindEventBus
import com.walletverse.ui.util.DialogUtil
import com.walletverse.ui.util.ToastUtil
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.LoadingPopupView
import org.greenrobot.eventbus.EventBus


abstract class BaseFragment : Fragment(), IClickPresenter, IToast, ITipDialog{

    private var loadingPopupView: LoadingPopupView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return if (getLayoutId() != 0) {
            inflater.inflate(getLayoutId(), container, false)
        } else {
            super.onCreateView(inflater, container, savedInstanceState)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        initEvent()
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
        loadingPopupView = XPopup.Builder(context)
            .dismissOnTouchOutside(false)
            .dismissOnBackPressed(true)
            .asLoading()
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
        DialogUtil.showCenterCommonDialog(requireActivity(),msg,object : DialogUtil.OnCommonListener{
            override fun onConfirm() {
                onCommonListener.onConfirm()
            }

            override fun onCancel() {
                onCommonListener.onCancel()
            }
        })
    }
}
