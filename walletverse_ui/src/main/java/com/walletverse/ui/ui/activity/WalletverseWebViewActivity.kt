package com.walletverse.ui.ui.activity

import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.walletverse.ui.R
import com.walletverse.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_walletverse_web_view.*

class WalletverseWebViewActivity : BaseActivity() {


    override fun getLayoutId(): Int {
        return R.layout.activity_walletverse_web_view
    }

    override fun initData() {
        val bundle = intent.extras
        val url = bundle?.getString("url")


        v_webview?.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                view?.loadUrl(url!!)
                return true
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            }

            override fun onPageFinished(view: WebView?, url: String?) {

            }
        }
        v_webview?.webChromeClient = WebChromeClient()
        v_webview.loadUrl(url!!)
    }

    override fun initView() {
        initTitleBar("")
    }
}