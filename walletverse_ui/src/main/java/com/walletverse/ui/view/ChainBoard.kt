package com.walletverse.ui.view

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager
import com.walletverse.core.bean.Coin
import com.walletverse.ui.R
import com.walletverse.ui.adapter.ChainBoardAdapter
import com.walletverse.ui.util.DialogUtil
import com.lxj.xpopup.core.BottomPopupView
import kotlinx.android.synthetic.main.view_chain_board.view.*


/**
 * ================================================================
 * Create_time：2022/8/9  13:29
 * Author：solomon
 * Detail：
 * ================================================================
 */
@SuppressLint("ViewConstructor")
class ChainBoard(
    context: Context,
    chains: MutableList<Coin>,
    onChainClickListener: DialogUtil.OnChainClickListener
) :
    BottomPopupView(context) {

    private val mOnChainClickListener = onChainClickListener
    private val mChains=chains

    override fun getImplLayoutId(): Int {
        return R.layout.view_chain_board
    }

    override fun onCreate() {
        super.onCreate()
        v_recycler.layoutManager = GridLayoutManager(context, 4)
        val chainBoardAdapter = ChainBoardAdapter(R.layout.item_chain)
        v_recycler.adapter = chainBoardAdapter
        chainBoardAdapter.setNewInstance(mChains)

        chainBoardAdapter.setOnItemClickListener { adapter, view, position ->
            this.dismiss()
            mOnChainClickListener.onChainClick(chainBoardAdapter.data[position])
        }
    }


}