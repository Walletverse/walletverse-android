package com.walletverse.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.walletverse.ui.R
import com.walletverse.ui.bean.TransferParams
import com.walletverse.ui.util.ContractUtil
import com.walletverse.ui.util.DialogUtil
import com.lxj.xpopup.core.BottomPopupView
import com.walletverse.ui.bean.DAppTransferParams
import kotlinx.android.synthetic.main.view_transfer_board.view.*


/**
 * ================================================================
 * Create_time：2022/8/9  13:29
 * Author：solomon
 * Detail：
 * ================================================================
 */
@SuppressLint("ViewConstructor")
class DAppTransferBoard(
    context: Context,
    dAppTransferParams: DAppTransferParams,
    onDAppActionListener: DialogUtil.OnDAppActionListener
) :
    BottomPopupView(context) {

    private val mOnDAppActionListener = onDAppActionListener
    private val mDAppTransferBean = dAppTransferParams

    override fun getImplLayoutId(): Int {
        return R.layout.view_dapp_transfer_board
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate() {
        super.onCreate()

        v_value.text = "${mDAppTransferBean.value} ${mDAppTransferBean.symbol}"

        if (mDAppTransferBean.action.isNullOrEmpty()) {
            v_contract_address_layout.visibility = View.GONE
        } else {
            v_contract_address_layout.visibility = View.VISIBLE
            v_contract_address.text = mDAppTransferBean.action
        }
        v_to_address.text = mDAppTransferBean.to
        v_from_address.text = mDAppTransferBean.from
        v_fee.text = "${mDAppTransferBean.gasFee} ${ContractUtil.getContract(mDAppTransferBean.contract)}"
        v_gas.text = mDAppTransferBean.gas

        v_confirm.setOnClickListener {
            mOnDAppActionListener.onConfirm()
            dismiss()
        }
    }


    override fun onDismiss() {
        super.onDismiss()
        mOnDAppActionListener.onCancel()
    }
}