package com.walletverse.ui.ui.activity

import android.view.KeyEvent
import androidx.fragment.app.Fragment
import com.walletverse.ui.R
import com.walletverse.ui.adapter.FragmentAdapter
import com.walletverse.ui.base.BaseActivity
import com.walletverse.ui.ui.fragment.MeFragment
import com.walletverse.ui.ui.fragment.WalletFragment
import com.walletverse.ui.util.ToastUtil
import kotlinx.android.synthetic.main.activity_home.*
import kotlin.system.exitProcess


class HomeActivity : BaseActivity() {

    private val fragments = mutableListOf<Fragment>()

    override fun getLayoutId(): Int = R.layout.activity_home

    override fun initData() {

        val bundle =intent.extras
        val event=bundle?.getString("event")
        event?.let { ToastUtil.showSuccess(it) }

        fragments.add(WalletFragment())
//        fragments.add(DAppFragment())
        fragments.add(MeFragment())

        val fragmentAdapter = FragmentAdapter(fragments, supportFragmentManager)
        viewPager.adapter = fragmentAdapter
        viewPager.setScroll(false)
        viewPager.offscreenPageLimit = 3

//        val bottomStringArray = resources.getStringArray(R.array.bottom_navigation)
//        for (i in bottomStringArray.indices) {
//            bottomNavigation.menu.getItem(i).title = bottomStringArray[i]
//        }
        bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.nav_wallet -> viewPager.setCurrentItem(0, false)
//                R.id.nav_dapp -> viewPager.setCurrentItem(1, false)
                R.id.nav_me -> viewPager.setCurrentItem(2, false)
            }
            true
        }
//        viewPager.currentItem=0
    }


    override fun initView() {
    }


    private var exitTime: Long = 0

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit()
            return false
        }
        return super.onKeyDown(keyCode, event)
    }


    private fun exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            ToastUtil.showSuccess(getString(R.string.go_back_again), withIcon = false)
            exitTime = System.currentTimeMillis()
        } else {
            finish()
            exitProcess(0)
        }
    }
}