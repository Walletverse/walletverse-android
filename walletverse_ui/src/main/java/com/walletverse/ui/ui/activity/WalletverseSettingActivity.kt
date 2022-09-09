package com.walletverse.ui.ui.activity

import android.view.View
import com.walletverse.core.Walletverse
import com.walletverse.core.enums.Currency
import com.walletverse.core.enums.Language
import com.walletverse.core.enums.Unit
import com.walletverse.ui.R
import com.walletverse.ui.base.BaseActivity
import com.walletverse.ui.constant.Constants
import com.walletverse.ui.constant.GlobalConstants
import com.walletverse.ui.event.RefreshEvent
import com.walletverse.ui.ui.fragment.WalletFragment
import com.walletverse.ui.util.ActivityUtil
import com.walletverse.ui.util.DialogUtil
import com.walletverse.ui.util.EventBusUtil
import com.walletverse.ui.util.SupportUtil
import com.blankj.utilcode.util.SPUtils
import kotlinx.android.synthetic.main.activity_walletverse_setting.*

class WalletverseSettingActivity : BaseActivity() {


    private var mLanguages = emptyArray<String>()
    private var mCurrencies = emptyArray<String>()
    private var mUnits = emptyArray<String>()
    private val mSupportLanguages = SupportUtil.supportLanguage()
    private val mSupportCurrency = SupportUtil.supportCurrency()
    private val mSupportUnits = SupportUtil.supportCs()

    override fun getLayoutId(): Int {
        return R.layout.activity_walletverse_setting
    }

    override fun initData() {

        mSupportLanguages.forEach {
            if (it.language == SPUtils.getInstance().getString(Constants.CURRENT_LANGUAGE)) {
                v_language.text = it.content
            }
            mLanguages = mLanguages.plus(it.content)
        }

        mSupportCurrency.forEach {
            if (it.currency == SPUtils.getInstance().getString(Constants.CURRENT_CURRENCY)) {
                v_currency.text = it.currency
            }
            mCurrencies = mCurrencies.plus(it.currency)
        }

        mSupportUnits.forEach {
            if (it.cs == SPUtils.getInstance().getString(Constants.CURRENT_UNIT)) {
                v_unit.text = it.cs
            }
            mUnits = mUnits.plus(it.cs)
        }

    }

    override fun initView() {
        initTitleBar(getString(R.string.setting))
        v_language_layout.setOnClickListener(this)
        v_currency_layout.setOnClickListener(this)
        v_unit_layout.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.v_language_layout -> {

                DialogUtil.showBottomListDialog(this, list = mLanguages) { position, text ->
                    SPUtils.getInstance().put(
                        Constants.CURRENT_LANGUAGE,
                        mSupportLanguages[position].language
                    )
                    SPUtils.getInstance().put(
                        Constants.CURRENT_COUNTRY,
                        mSupportLanguages[position].countryCode
                    )

                    GlobalConstants.CURRENT_LANGUAGE= SPUtils.getInstance().getString(Constants.CURRENT_LANGUAGE)

                    when (mSupportLanguages[position].language) {
                        "zh" -> {
                            Walletverse.sInstance.changeLanguage(Language.ZH)
                        }
                        "en" -> {
                            Walletverse.sInstance.changeLanguage(Language.EN)
                        }
                        "ko" -> {
                            Walletverse.sInstance.changeLanguage(Language.KO)
                        }
                    }

                    ActivityUtil.goNewActivity(this@WalletverseSettingActivity, HomeActivity::class.java)
                    finish()
                }
            }
            R.id.v_currency_layout -> {
                DialogUtil.showBottomListDialog(this, list = mCurrencies) { position, text ->

                    v_currency.text = mSupportCurrency[position].currency

                    SPUtils.getInstance().put(
                        Constants.CURRENT_CURRENCY,
                        mSupportCurrency[position].currency
                    )

                    SPUtils.getInstance().put(
                        Constants.CURRENT_CURRENCY_SYMBOL,
                        mSupportCurrency[position].content
                    )

                    GlobalConstants.CURRENT_CURRENCY_SYMBOL=SPUtils.getInstance().getString(Constants.CURRENT_CURRENCY_SYMBOL)

                    when (mSupportCurrency[position].currency) {
                        "USD" -> {
                            Walletverse.sInstance.changeCurrency(Currency.USD)
                        }
                        "CNY" -> {
                            Walletverse.sInstance.changeCurrency(Currency.CNY)
                        }
                        "KRW" -> {
                            Walletverse.sInstance.changeCurrency(Currency.KRW)
                        }
                    }

                    EventBusUtil.post(RefreshEvent(WalletFragment.REFRESH_EVENT))

                }
            }
            R.id.v_unit_layout -> {
                DialogUtil.showBottomListDialog(this, list = mUnits) { position, text ->

                    v_unit.text = mUnits[position]

                    SPUtils.getInstance().put(
                        Constants.CURRENT_UNIT,
                        mUnits[position]
                    )


                    when (mUnits[position]) {
                        "USDT" -> {
                            Walletverse.sInstance.changeUnit(Unit.USDT)
                        }
                        "USD" -> {
                            Walletverse.sInstance.changeUnit(Unit.USD)
                        }

                    }

                    EventBusUtil.post(RefreshEvent(WalletFragment.REFRESH_EVENT))

                }

            }
        }
    }

}