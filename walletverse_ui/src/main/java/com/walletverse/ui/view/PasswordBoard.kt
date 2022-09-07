package com.walletverse.ui.view

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.walletverse.core.Walletverse
import com.walletverse.core.bean.EncodeAuthParams
import com.walletverse.ui.R
import com.walletverse.ui.adapter.PasswordImgAdapter
import com.walletverse.ui.adapter.PasswordNumberAdapter
import com.walletverse.ui.util.DialogUtil
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.StringUtils
import com.lxj.xpopup.core.BottomPopupView
import kotlinx.android.synthetic.main.view_password_board.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**
 * ================================================================
 * Create_time：2022/8/9  13:29
 * Author：solomon
 * Detail：
 * ================================================================
 */
@SuppressLint("ViewConstructor")
class PasswordBoard(context: Context, onCompletedListener: DialogUtil.OnCompletedListener) :
    BottomPopupView(context) {

    private lateinit var mPasswordImgAdapter: PasswordImgAdapter
    private lateinit var mPasswordNumberAdapter: PasswordNumberAdapter
    private val mOnCompletedListener = onCompletedListener
    private var mCircles = arrayListOf<Boolean>()
    private var mNumbers = arrayListOf<String>()
    private var password = ""

    override fun getImplLayoutId(): Int {
        return R.layout.view_password_board
    }

    override fun onCreate() {
        super.onCreate()
        for (i in 0..5) {
            mCircles.add(false)
        }
        v_image_recycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        mPasswordImgAdapter = PasswordImgAdapter(R.layout.item_password_img)
        v_image_recycler.adapter = mPasswordImgAdapter
        mPasswordImgAdapter.setNewInstance(mCircles)

        for (i in 0..8) {
            mNumbers.add("${i + 1}")
        }
        mNumbers.add("Clean")
        mNumbers.add("0")
        mNumbers.add("Delete")
        v_board_recycler.layoutManager =
            GridLayoutManager(context, 3)
        mPasswordNumberAdapter = PasswordNumberAdapter(R.layout.item_password_number)
        v_board_recycler.adapter = mPasswordNumberAdapter
        mPasswordNumberAdapter.setNewInstance(mNumbers)
        mPasswordNumberAdapter.setOnItemClickListener { adapter, view, position ->
            if (mNumbers[position] == "Clean") {
                password = ""
//                Logger.e(password)
            } else if (mNumbers[position] == "Delete") {
                if (password.isEmpty())
                    return@setOnItemClickListener
                password = password.substring(0, password.length - 1)
//                Logger.e(password)
            } else {
                if (password.length >= 6) {
                    return@setOnItemClickListener
                }
                password += mNumbers[position]
                if (password.length == 6) {
                    lifecycleScope.launch(Dispatchers.Main) {
                        val pwd = Walletverse.sInstance.encodeAuthAsync(
                            EncodeAuthParams(
                                password,
                                DeviceUtils.getUniqueDeviceId()
                            )
                        )
                        mOnCompletedListener.passwordComplete(pwd, this@PasswordBoard)
                    }
                }
//                Logger.e(password)
            }
            setCircleStatus(if (StringUtils.isEmpty(password)) 0 else password.length)
        }
    }

    private fun setCircleStatus(numTrue: Int) {
        for (i in 0..5) {
            mCircles[i] = false
        }
        for (i in 0 until numTrue) {
            mCircles[i] = true
        }
        mPasswordImgAdapter.notifyDataSetChanged()
    }

    override fun onDismiss() {
        super.onDismiss()
//        mOnCompletedListener.passwordComplete("")
    }
}