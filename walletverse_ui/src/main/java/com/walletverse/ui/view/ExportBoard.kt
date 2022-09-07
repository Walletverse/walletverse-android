package com.walletverse.ui.view

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.walletverse.ui.R
import com.walletverse.ui.adapter.ExportBoardAdapter
import com.walletverse.ui.util.ToastUtil
import com.blankj.utilcode.util.ClipboardUtils
import com.lxj.xpopup.core.CenterPopupView
import kotlinx.android.synthetic.main.view_center_common_board.view.*
import kotlinx.android.synthetic.main.view_export_board.view.*


/**
 * ================================================================
 * Create_time：2022/8/9  13:29
 * Author：solomon
 * Detail：
 * ================================================================
 */
@SuppressLint("ViewConstructor")
class ExportBoard(context: Context, content: MutableList<String>) :
    CenterPopupView(context) {

    private val mContent = content
    private var copyText: String = ""

    override fun getImplLayoutId(): Int {
        return R.layout.view_export_board
    }


    override fun onCreate() {
        super.onCreate()

        mContent.forEach {
            copyText = "$copyText${it}\n"
        }

        v_recycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        val exportBoardAdapter = ExportBoardAdapter(R.layout.item_export)
        v_recycler.adapter = exportBoardAdapter
        exportBoardAdapter.setNewInstance(mContent)

        v_copy.setOnClickListener {
            ClipboardUtils.copyText(copyText)
            ToastUtil.showSuccess(context.getString(R.string.copy_success))
            this.dismiss()
        }
    }


}