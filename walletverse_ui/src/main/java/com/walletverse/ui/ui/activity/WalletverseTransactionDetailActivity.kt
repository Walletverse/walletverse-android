package com.walletverse.ui.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import com.walletverse.core.bean.TransactionRecord
import com.walletverse.ui.R
import com.walletverse.ui.base.BaseActivity
import com.walletverse.ui.util.ActivityUtil
import com.walletverse.ui.util.ContractUtil
import com.blankj.utilcode.util.TimeUtils
import kotlinx.android.synthetic.main.activity_walletverse_transaction_detail.*

class WalletverseTransactionDetailActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_walletverse_transaction_detail
    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        val bundle = intent.extras
        val recorder: TransactionRecord = bundle?.getSerializable("recorder") as TransactionRecord

        when (recorder.status) {
            "pending" -> {
                v_status_image.setImageResource(R.mipmap.icon_pending)
                v_status.text=getString(R.string.pending)
            }
            "success" -> {
                v_status_image.setImageResource(R.mipmap.icon_success)
                v_status.text=getString(R.string.success)
            }
            "fail" -> {
                v_status_image.setImageResource(R.mipmap.icon_fail)
                v_status.text=getString(R.string.fail)
            }
        }
        v_time.text=TimeUtils.millis2String(recorder.timestamp)
        v_amount.text="${recorder.value} ${recorder.symbol?:""}"
        v_fee.text="${recorder.gas} ${ContractUtil.getContract(recorder.contract)}"
        v_to_address.text=recorder.to
        v_from_address.text=recorder.from
        v_hash.text=recorder.hash

        v_view_details.setOnClickListener {
            val bundle =Bundle()
            bundle.putString("url",recorder.scanUrl)
            ActivityUtil.goActivity(this,WalletverseWebViewActivity::class.java,bundle)
        }
    }

    override fun initView() {
        initTitleBar("")

    }

}