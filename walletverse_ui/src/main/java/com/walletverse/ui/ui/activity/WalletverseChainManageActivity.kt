package com.walletverse.ui.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.walletverse.core.Walletverse
import com.walletverse.core.bean.Coin
import com.walletverse.core.bean.Identity
import com.walletverse.core.bean.GetWalletCoinParams
import com.walletverse.core.enums.Type
import com.walletverse.ui.R
import com.walletverse.ui.adapter.WalletChainManageAdapter
import com.walletverse.ui.base.BaseActivity
import com.walletverse.ui.event.CloseEvent
import com.walletverse.ui.event.RefreshEvent
import com.walletverse.ui.ui.fragment.WalletFragment
import com.walletverse.ui.util.*
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.hjq.bar.TitleBar
import kotlinx.android.synthetic.main.activity_walletverse_chain_manage.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@BindEventBus
class WalletverseChainManageActivity : BaseActivity(), OnItemChildClickListener {
    private lateinit var bundle: Bundle
    private lateinit var mWalletChainManageAdapter: WalletChainManageAdapter
    private lateinit var identity: Identity

    companion object {
        const val CLOSE_EVENT = "WalletverseChainManageActivity"
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_walletverse_chain_manage
    }

    override fun initData() {

        bundle = intent.extras!!
        identity = bundle.getSerializable("identity") as Identity

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val chains: MutableList<Coin> = mutableListOf()
                val coins: MutableList<Coin>? = Walletverse.sInstance.queryWalletCoinsAsync(GetWalletCoinParams(identity.wid))
                coins?.forEach {
                    if (it.type==Type.CHAIN) {
                        chains.add(it)
                    }
                }
                mWalletChainManageAdapter.setNewInstance(chains)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    override fun initView() {
        initTitleBar(getString(R.string.chain_management), rightIcon = R.mipmap.icon_setting2)

        v_recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mWalletChainManageAdapter = WalletChainManageAdapter(R.layout.item_wallet_chain_manage)
        v_recycler.adapter = mWalletChainManageAdapter
        mWalletChainManageAdapter.addChildClickViewIds(R.id.v_operation)
        mWalletChainManageAdapter.setOnItemChildClickListener(this)
    }


    override fun onRightClick(titleBar: TitleBar?) {
        ActivityUtil.goActivity(this, WalletverseWalletSettingActivity::class.java, bundle)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        DialogUtil.showBottomListDialog(
            this,
            list = arrayOf<String>(getString(R.string.hide))
        ) { pos, text ->
            when (pos) {
                0 -> {
                    showLoading()
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            val chain =mWalletChainManageAdapter.data[position]

                            //delete chain
                            Walletverse.sInstance.deleteWalletCoinAsync(chain)

                            //delete token
                            val coins: MutableList<Coin>? =
                                Walletverse.sInstance.queryWalletCoinsAsync(GetWalletCoinParams(identity.wid))
                            coins?.forEach {
                                if(it.chainId==chain.chainId) {
                                    Walletverse.sInstance.deleteWalletCoinAsync(it)
                                }
                            }

                            mWalletChainManageAdapter.data.remove(chain)

                            EventBusUtil.post(RefreshEvent(WalletFragment.REFRESH_EVENT))

                            withContext(Dispatchers.Main) {
                                mWalletChainManageAdapter.notifyDataSetChanged()
                                hideLoading()
                            }
                        } catch (e: Exception) {
                            hideLoading()
                            e.printStackTrace()
                        }
                    }

                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun close(event: CloseEvent) {
        if (event.pageName == CLOSE_EVENT)
            finish()
    }
}