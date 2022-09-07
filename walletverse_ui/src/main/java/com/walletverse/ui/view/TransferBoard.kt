package com.walletverse.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.walletverse.ui.R
import com.walletverse.ui.bean.TransferParams
import com.walletverse.ui.util.ContractUtil
import com.walletverse.ui.util.DialogUtil
import com.lxj.xpopup.core.BottomPopupView
import kotlinx.android.synthetic.main.view_transfer_board.view.*


/**
 * ================================================================
 * Create_time：2022/8/9  13:29
 * Author：solomon
 * Detail：
 * ================================================================
 */
@SuppressLint("ViewConstructor")
class TransferBoard(
    context: Context,
    transferParams: TransferParams,
    onCommonListener: DialogUtil.OnCommonListener
) :
    BottomPopupView(context) {

    private val mOnCommonListener = onCommonListener
    private val mTransferBean = transferParams

    override fun getImplLayoutId(): Int {
        return R.layout.view_transfer_board
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate() {
        super.onCreate()

        v_value.text = "${mTransferBean.value} ${mTransferBean.symbol}"

        if (mTransferBean.contractAddress.isEmpty()) {
            v_contract_address_layout.visibility = View.GONE
        } else {
            v_contract_address_layout.visibility = View.VISIBLE
            v_contract_address.text = mTransferBean.contractAddress
        }
        v_to_address.text = mTransferBean.to
        v_from_address.text = mTransferBean.from
        v_fee.text = "${mTransferBean.gasFee} ${ContractUtil.getContract(mTransferBean.contract)}"
        v_gas.text = mTransferBean.gas

        v_confirm.setOnClickListener {
            mOnCommonListener.onConfirm()
            dismiss()
        }
    }


    override fun onDismiss() {
        super.onDismiss()
        mOnCommonListener.onCancel()
    }
}