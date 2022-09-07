package com.walletverse.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager


/**
 * ================================================================
 * Create_time：2022/7/29  12:37
 * Author：solomon
 * Detail：
 * ================================================================
 */
class NoScrollViewPager : ViewPager {

    private var isScroll: Boolean = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    fun setScroll(scroll: Boolean) {
        isScroll = scroll
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return this.isScroll && super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return this.isScroll && super.onInterceptTouchEvent(event)
    }
}