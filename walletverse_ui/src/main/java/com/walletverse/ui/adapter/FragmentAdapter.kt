package com.walletverse.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


/**
 * ================================================================
 * Create_time：2022/7/29  12:33
 * Author：solomon
 * Detail：
 * ================================================================
 */
open class FragmentAdapter(list:List<Fragment>, fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    private val mFragments: List<Fragment> = list

    override fun getCount(): Int {
        return mFragments.size
    }

    override fun getItem(position: Int): Fragment {
        return mFragments[position]
    }
}