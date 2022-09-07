package com.walletverse.ui.view

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.walletverse.core.Walletverse
import com.walletverse.core.bean.Identity
import com.walletverse.ui.R
import com.walletverse.ui.adapter.WalletListAdapter
import com.walletverse.ui.constant.Constants
import com.walletverse.ui.event.RefreshEvent
import com.walletverse.ui.ui.activity.WalletverseChainManageActivity
import com.walletverse.ui.ui.activity.WalletverseWeb2LoginActivity
import com.walletverse.ui.ui.fragment.WalletFragment
import com.walletverse.ui.util.ActivityUtil
import com.walletverse.ui.util.EventBusUtil
import com.blankj.utilcode.util.SPUtils
import com.lxj.xpopup.core.DrawerPopupView
import kotlinx.android.synthetic.main.view_wallet_list_drawer.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * ================================================================
 * Create_time：2022/8/6  15:12
 * Author：solomon
 * Detail：
 * ================================================================
 */
class WalletListDrawer(context: Context) : DrawerPopupView(context) {

    override fun getImplLayoutId(): Int {
        return R.layout.view_wallet_list_drawer
    }

    override fun onCreate() {
        super.onCreate()
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        v_recycler.layoutManager = linearLayoutManager
        val walletListAdapter = WalletListAdapter(R.layout.item_wallet_list)
        v_recycler.adapter = walletListAdapter


        lifecycleScope.launch(Dispatchers.IO){
            try {
                val identities:MutableList<Identity>? = Walletverse.sInstance.queryIdentitiesAsync()
                withContext(Dispatchers.Main){
                    if (identities != null && identities.isNotEmpty()) {
                        walletListAdapter.setNewInstance(identities)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        walletListAdapter.setOnItemClickListener { adapter, view, position ->

            if(SPUtils.getInstance().getString(Constants.CURRENT_WID)==walletListAdapter.data[position].wid) {
                this.dismiss()
                return@setOnItemClickListener
            }

            SPUtils.getInstance().put(Constants.CURRENT_WID,walletListAdapter.data[position].wid)
            //sendEvent update wallet
            EventBusUtil.post(RefreshEvent(WalletFragment.REFRESH_EVENT))
            this.dismiss()
        }
        walletListAdapter.addChildClickViewIds(R.id.v_operation)
        walletListAdapter.setOnItemChildClickListener { adapter, view, position ->
            this.dismiss()
            when (view.id) {
                R.id.v_operation->{
                    val bundle=Bundle()
                    bundle.putSerializable("identity",walletListAdapter.data[position])
                    ActivityUtil.goActivity(context,WalletverseChainManageActivity::class.java,bundle)
                }
            }
        }

        v_add_wallet.setOnClickListener {
            this.dismiss()

            val bundle=Bundle()
            bundle.putBoolean("canBack",true)
            ActivityUtil.goActivity(context,WalletverseWeb2LoginActivity::class.java,bundle)
        }
    }


    override fun onShow() {
        super.onShow()
    }

    override fun onDismiss() {
        super.onDismiss()
    }
}