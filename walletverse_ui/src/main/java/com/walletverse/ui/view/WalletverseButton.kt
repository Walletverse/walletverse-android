package com.walletverse.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.walletverse.ui.R
import com.blankj.utilcode.util.SizeUtils
import com.ruffian.library.widget.RTextView
import kotlin.properties.Delegates


/**
 * ================================================================
 * Create_time：2022/8/3  15:31
 * Author：solomon
 * Detail：
 * ================================================================
 */
class WalletverseButton(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {

    private var mEnable: Boolean = true
    private var mColorNormal by Delegates.notNull<Int>()
    private var mColorUnable by Delegates.notNull<Int>()
    private lateinit var mBtn: RTextView
    private var round: Float = SizeUtils.dp2px(24f).toFloat()
    private var leftTopRound: Float = 0.0f
    private var rightTopRound: Float = 0.0f
    private var rightBottomRound: Float = 0.0f
    private var leftBottomRound: Float = 0.0f
    private var text: String? = ""

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.view_btn, this, true)
        initAttrs(attributeSet)
        initData(view)
    }

    private fun initAttrs(attrs: AttributeSet) {
        val obtainStyledAttributes =
            context.obtainStyledAttributes(attrs, R.styleable.BeFiButton)
        round = obtainStyledAttributes.getDimension(R.styleable.BeFiButton_befi_round, round)
        leftTopRound =
            obtainStyledAttributes.getDimension(
                R.styleable.BeFiButton_befi_leftTopRound,
                leftTopRound
            )
        rightTopRound =
            obtainStyledAttributes.getDimension(
                R.styleable.BeFiButton_befi_rightTopRound,
                rightTopRound
            )
        rightBottomRound = obtainStyledAttributes.getDimension(
            R.styleable.BeFiButton_befi_rightBottomRound,
            rightBottomRound
        )
        leftBottomRound = obtainStyledAttributes.getDimension(
            R.styleable.BeFiButton_befi_leftBottomRound,
            leftBottomRound
        )
        text = obtainStyledAttributes.getText(R.styleable.BeFiButton_befi_text).toString()
        mEnable = obtainStyledAttributes.getBoolean(R.styleable.BeFiButton_befi_enable, true)
        mColorNormal = obtainStyledAttributes.getColor(R.styleable.BeFiButton_befi_background_normal, ContextCompat.getColor(context,R.color.colorPrimary))
        mColorUnable = obtainStyledAttributes.getColor(R.styleable.BeFiButton_befi_background_unable, ContextCompat.getColor(context,R.color.colorPrimaryAlpha))
        if (round != 0f) {
            leftTopRound = round
            rightTopRound = round
            rightBottomRound = round
            leftBottomRound = round
        }
        obtainStyledAttributes.recycle()
    }


    private fun initData(view: View) {
        mBtn = view.findViewById<RTextView>(R.id.v_btn)
        setData()
    }

    private fun setData() {
        setText(text ?: "")
        setEnable(mEnable)
        setRound(leftTopRound, rightTopRound, leftBottomRound, rightBottomRound)
        setBtnBackgroundColor()
    }

    private fun setBtnBackgroundColor() {
        mBtn.helper.backgroundColorNormal = mColorNormal
        mBtn.helper.backgroundColorUnable = mColorUnable
    }

    fun setText(text: String) {
        mBtn.text = text
    }

    fun setEnable(enable: Boolean) {
        mBtn.isEnabled=enable
//        mBtn.isClickable=enable
    }

    fun setRound(
        leftTopRound: Float?,
        rightTopRound: Float?,
        leftBottomRound: Float?,
        rightBottomRound: Float?
    ) {
        mBtn.helper?.cornerRadiusTopLeft = leftTopRound ?: 0f
        mBtn.helper?.cornerRadiusTopRight = rightTopRound ?: 0f
        mBtn.helper?.cornerRadiusBottomLeft = leftBottomRound ?: 0f
        mBtn.helper?.cornerRadiusBottomRight = rightBottomRound ?: 0f
    }

}