package com.walletverse.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import com.walletverse.core.Walletverse
import com.walletverse.core.core.UserConfig
import com.walletverse.core.core.VoidCallback
import com.walletverse.core.enums.Currency
import com.walletverse.core.enums.Language
import com.walletverse.core.enums.Unit
import com.walletverse.ui.constant.Constants
import com.walletverse.ui.constant.GlobalConstants
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.LanguageUtils
import com.blankj.utilcode.util.SPUtils
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator


/**
 * ================================================================
 * Create_time：2022/7/25  16:59
 * Author：solomon
 * Detail：
 * ================================================================
 */
class WalletverseApplication : Application() {

    private lateinit var mELanguage: Language
    private lateinit var mECurrency: Currency
    private lateinit var mEUnit: Unit

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext


        initLanguage()
        initCurrency()
        initUnit()
        initGlobal()

        val userConfig = UserConfig(DeviceUtils.getUniqueDeviceId(), mELanguage, mECurrency, mEUnit)

        Walletverse.install(this, Constants.APPID, Constants.APPKEY, userConfig, object : VoidCallback {
            override fun onResult() {
                //init success
                Log.d("WalletverseApplication", "sdk init success")
            }

            override fun onError(error: Exception) {
                Log.e("WalletverseApplication", "onError: ${error.toString()}")
            }
        })


        SmartRefreshLayout.setDefaultRefreshHeaderCreator(DefaultRefreshHeaderCreator { context, layout ->
            layout.setPrimaryColorsId(R.color.colorPrimary, R.color.white)
            MaterialHeader(context)
        })
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
            ClassicsFooter(context)
        }


        Logger.addLogAdapter(object : AndroidLogAdapter() {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })


    }


    private fun initUnit() {
        val spUnit = SPUtils.getInstance().getString(Constants.CURRENT_UNIT)
        if (spUnit.isNullOrEmpty()) {
            SPUtils.getInstance().put(Constants.CURRENT_UNIT, "USDT")
        }
        when (SPUtils.getInstance().getString(Constants.CURRENT_UNIT)) {
            "USD" -> {
                mEUnit = Unit.USD
            }
            "USDT" -> {
                mEUnit = Unit.USDT
            }
        }
    }

    private fun initCurrency() {
        val spCurrency = SPUtils.getInstance().getString(Constants.CURRENT_CURRENCY)
        if (spCurrency.isNullOrEmpty()) {
            SPUtils.getInstance().put(Constants.CURRENT_CURRENCY, "USD")
            SPUtils.getInstance().put(Constants.CURRENT_CURRENCY_SYMBOL, "$")
        }
        when (SPUtils.getInstance().getString(Constants.CURRENT_CURRENCY)) {
            "USD" -> {
                mECurrency = Currency.USD
            }
            "CNY" -> {
                mECurrency = Currency.CNY
            }
            "KRW" -> {
                mECurrency = Currency.KRW
            }
        }
    }


    private fun initLanguage() {
        val spLanguage = SPUtils.getInstance().getString(Constants.CURRENT_LANGUAGE)
//        val spCountry=SPUtils.getInstance().getString(Constants.CURRENT_COUNTRY)
        var language = LanguageUtils.getAppContextLanguage().language
        var country = ""
        if (spLanguage.isNullOrEmpty()) {
            when (language) {
                "zh" -> {
                    country = "HK"
                    mELanguage = Language.ZH
                }
                "en" -> {
                    country = "US"
                    mELanguage = Language.EN
                }
                "ko" -> {
                    country = "KR"
                    mELanguage = Language.KO
                }
            }
        } else {
            language = "en"
            country = "US"
            mELanguage = Language.EN
        }
        SPUtils.getInstance().put(Constants.CURRENT_LANGUAGE, language)
        SPUtils.getInstance().put(Constants.CURRENT_COUNTRY, country)

    }


    private fun initGlobal() {
        GlobalConstants.CURRENT_CURRENCY_SYMBOL =
            SPUtils.getInstance().getString(Constants.CURRENT_CURRENCY_SYMBOL)
        GlobalConstants.CURRENT_LANGUAGE =
            SPUtils.getInstance().getString(Constants.CURRENT_LANGUAGE)
    }

}