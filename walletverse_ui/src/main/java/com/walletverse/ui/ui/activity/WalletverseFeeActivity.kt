package com.walletverse.ui.ui.activity

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.walletverse.ui.R
import com.walletverse.ui.adapter.FeeAdapter
import com.walletverse.ui.base.BaseActivity
import com.walletverse.ui.bean.FeeBean
import com.walletverse.ui.core.getFee
import com.walletverse.ui.core.getGasPrice
import com.walletverse.ui.core.setScale
import com.walletverse.ui.util.ContractUtil
import com.walletverse.ui.util.EventBusUtil
import kotlinx.android.synthetic.main.activity_walletverse_fee.*
import java.math.BigDecimal
import java.math.RoundingMode

class WalletverseFeeActivity : BaseActivity() {

    private lateinit var feeAdapter: FeeAdapter
    private var mGwei: Double = 0.0
    private lateinit var mSpeed: String
    private lateinit var mGasPrice: String
    private lateinit var mGasLimit: String
    private var mDecimals: Int = 18
    private lateinit var mFee: FeeBean

    override fun getLayoutId(): Int {
        return R.layout.activity_walletverse_fee
    }

    override fun initData() {
        val bundle = intent.extras
        mFee = bundle?.getSerializable("fee") as FeeBean
        mGwei = mFee.gwei
        mSpeed = mFee.speed
        mGasPrice = mFee.gasPrice
        mGasLimit = mFee.gasLimit
        mDecimals = mFee.decimals!!

        val mBasicGwei = mFee.basicGwei!!
        val mBasicGasPrice = mFee.basicGasPrice!!
        val mBasicGasLimit = mFee.basicGasLimit!!
//        val balance = bundle.getString("balance")!!

//        v_balance.text=getString(R.string.available_balance,balance)

        val mFees = arrayListOf(
            FeeBean(
                setScale(mBasicGwei.toBigDecimal().multiply(BigDecimal(1.5)),RoundingMode.UP).toDouble(),
                setScale(mBasicGasPrice.toBigDecimal().multiply(BigDecimal(1.5)),RoundingMode.UP),
                mBasicGasLimit,
                "Fastest",
                speedName = getString(R.string.fastest)
            ),
            FeeBean(
                setScale(mBasicGwei.toBigDecimal().multiply(BigDecimal(1.2)),RoundingMode.UP).toDouble(),
                setScale(mBasicGasPrice.toBigDecimal().multiply(BigDecimal(1.2)),RoundingMode.UP),
                mBasicGasLimit,
                "Fast",
                speedName = getString(R.string.fast)
            ),
            FeeBean(
                mBasicGwei,
                mBasicGasPrice,
                mBasicGasLimit,
                "General",
                speedName = getString(R.string.general)
            )
        )

        v_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        feeAdapter = FeeAdapter(R.layout.item_fee)
        v_recycler.adapter = feeAdapter
        feeAdapter.setNewInstance(mFees)

        notifyFeeList()

        feeAdapter.setOnItemClickListener { adapter, view, position ->
            mGasPrice = feeAdapter.data[position].gasPrice
            mGasLimit = feeAdapter.data[position].gasLimit
            mGwei = feeAdapter.data[position].gwei
            mSpeed = feeAdapter.data[position].speed
            notifyFeeList()
        }
    }

    override fun initView() {
        initTitleBar(getString(R.string.gas_fee))
        v_custom_all.setOnClickListener(this)
        v_confirm.setOnClickListener(this)
    }


    override fun onClick(v: View) {
        super.onClick(v)
        when(v.id){
            R.id.v_custom_all->{
                mSpeed="Custom"
                notifyFeeList()
            }
            R.id.v_confirm->{
                val feeBean=FeeBean(mGwei,mGasPrice,mGasLimit,mSpeed)
                EventBusUtil.post(feeBean)
                finish()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun notifyFeeList() {
        notifyFee()
        feeAdapter.setSpeed(mSpeed)
        feeAdapter.notifyDataSetChanged()
        if(mSpeed=="Custom"){
            v_custom_layout.visibility=View.VISIBLE
            v_custom_gwei.setText(mGwei.toString())
            v_custom_gas_limit.setText(mGasLimit)
            v_custom_check.setImageResource(R.mipmap.icon_coin_check)
            v_custom_gwei.addTextChangedListener(GweiTextWatcher())
            v_custom_gas_limit.addTextChangedListener(GasLimitTextWatcher())
        }else{
            v_custom_layout.visibility=View.GONE
            v_custom_check.setImageResource(R.mipmap.icon_unchecked_new)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun notifyFee() {
        v_fee.text = "${getFee(mGasPrice, mGasLimit, mDecimals)} ${ContractUtil.getContract(mFee.contract!!)}"
        v_fee_price.text = "GasPrice(${mGwei}GWEI)*GasLimit($mGasLimit)"
    }


    inner class GweiTextWatcher : TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            mGwei = if(!p0.isNullOrEmpty()){
                p0.toString().toDouble()
            }else{
                0.0
            }
            mGasPrice= getGasPrice(mGwei)
            notifyFee()
        }

        override fun afterTextChanged(p0: Editable?) {
        }

    }

    inner class GasLimitTextWatcher : TextWatcher{
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            mGasLimit = if(!p0.isNullOrEmpty()){
                p0.toString()
            }else{
                "0"
            }
            notifyFee()
        }

        override fun afterTextChanged(p0: Editable?) {
        }

    }

}