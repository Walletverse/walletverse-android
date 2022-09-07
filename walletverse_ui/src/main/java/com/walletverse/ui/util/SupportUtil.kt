package com.walletverse.ui.util

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.text.TextUtils
import com.walletverse.ui.bean.CsBean
import com.walletverse.ui.bean.CurrencyBean
import com.walletverse.ui.bean.LanguageBean
import java.util.*
import kotlin.collections.ArrayList


object SupportUtil {

    fun changeLanguage(context: Context, language: String, country: String) {
        if (TextUtils.isEmpty(language)) {
            return
        }
        val resources: Resources = context.resources
        val config: Configuration = resources.configuration
        config.setLocale(Locale(language, country))
        resources.updateConfiguration(config, null)
    }


    fun supportLanguage(): ArrayList<LanguageBean> {
        return arrayListOf(
            LanguageBean("zh", "HK", "繁體中文"),
            LanguageBean("en", "US", "English"),
            LanguageBean("ko", "KR", "한국어")
        )
    }

    fun supportCurrency(): ArrayList<CurrencyBean> {
        return arrayListOf(
            CurrencyBean("CNY", "¥"),
            CurrencyBean("USD", "$"),
            CurrencyBean("KRW", "₩"),
        )
    }

    fun supportCs(): ArrayList<CsBean> {
        return arrayListOf(
            CsBean("USDT"),
            CsBean("USD"),
        )
    }

}