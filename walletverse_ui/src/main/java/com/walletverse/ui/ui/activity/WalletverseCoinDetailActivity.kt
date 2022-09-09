package com.walletverse.ui.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.walletverse.core.Walletverse
import com.walletverse.core.bean.Coin
import com.walletverse.core.bean.TransactionRecord
import com.walletverse.core.bean.TransactionRecordParams
import com.walletverse.core.enums.Condition
import com.walletverse.core.enums.Type
import com.walletverse.ui.R
import com.walletverse.ui.adapter.TransactionAdapter
import com.walletverse.ui.base.BaseActivity
import com.walletverse.ui.event.RefreshEvent
import com.walletverse.ui.ui.fragment.WalletFragment
import com.walletverse.ui.util.*
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.SPUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gyf.immersionbar.ktx.immersionBar
import com.gyf.immersionbar.ktx.statusBarHeight
import com.hjq.bar.TitleBar
import com.ruffian.library.widget.RTextView
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import kotlinx.android.synthetic.main.activity_walletverse_coin_detail.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@BindEventBus
class WalletverseCoinDetailActivity : BaseActivity(), OnRefreshLoadMoreListener {

    private lateinit var vEmpty: View
    private lateinit var mCoin: Coin
    private lateinit var mTransactionAdapter: TransactionAdapter
    private var mPage = 1
    private var mRecordList = arrayListOf<TransactionRecord>()

    companion object {
        const val REFRESH_EVENT = "WalletverseCoinDetailActivity"
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_walletverse_coin_detail
    }

    override fun initData() {

        val header = LayoutInflater.from(this).inflate(R.layout.view_coin_detail_header, null)
        val titleBar=header.findViewById<TitleBar>(R.id.v_title_bar)
        titleBar.setOnTitleBarListener(this)
        val top=header.findViewById<View>(R.id.v_top)
        top.layoutParams=LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,statusBarHeight)
        titleBar.setBackgroundColor(getColor(R.color.transparent))
        titleBar.title=mCoin.symbol
        titleBar.setTitleColor(getColor(R.color.white))
        if(mCoin.type==Type.COIN) {
            titleBar.rightIcon=ContextCompat.getDrawable(this,R.mipmap.icon_operation_white)
        }
        vEmpty=header.findViewById<View>(R.id.v_empty)

        header.findViewById<RTextView>(R.id.v_address).text =
            StringUtil.formatAddress(mCoin.address)
        header.findViewById<ImageView>(R.id.v_icon)
            .loadImageCenterCrop(mCoin.iconUrl, R.mipmap.icon_circle_placeholder)
        header.findViewById<TextView>(R.id.v_balance).text = mCoin.balance.ifEmpty { "0.00" }
        header.findViewById<TextView>(R.id.v_price).text = mCoin.totalPrice.ifEmpty { "0.00" }
        header.findViewById<RTextView>(R.id.v_address).setOnClickListener(this)

        val linearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        v_recycler.layoutManager = linearLayoutManager
        mTransactionAdapter = TransactionAdapter(R.layout.item_transaction)
        v_recycler.adapter = mTransactionAdapter
        mTransactionAdapter.addHeaderView(header)

//        transactionAdapter.setEmptyView(R.layout.view_empty)

        initRecorderSP()
        getTransactionRecorder()



        mTransactionAdapter.setOnItemClickListener { adapter, view, position ->
            val bundle = Bundle()
            val recorder = mTransactionAdapter.data[position]
            recorder.symbol = mCoin.symbol
            bundle.putSerializable("recorder", recorder)
            ActivityUtil.goActivity(this, WalletverseTransactionDetailActivity::class.java, bundle)
        }


    }

    private fun initRecorderSP() {
        lifecycleScope.launch(Dispatchers.IO){
            val spRecorder =
                SPUtils.getInstance().getString("Transaction_${mCoin.wid}${mCoin.contract}_${mCoin.symbol}")
            if (spRecorder.isNotEmpty()) {
                val recordList = Gson().fromJson<ArrayList<TransactionRecord>>(
                    spRecorder,
                    object : TypeToken<ArrayList<TransactionRecord>>() {}.type
                )
                withContext(Dispatchers.Main) {
                    mTransactionAdapter.setList(recordList)
                }
            }
        }
    }

    private fun getTransactionRecorder() {
        lifecycleScope.launch(Dispatchers.IO) {

            try {
                val result = Walletverse.sInstance.getTransactionRecordsAsync(
                    TransactionRecordParams(
                        mPage,
                        50,
                        mCoin.chainId,
                        mCoin.address,
                        Condition.OUT,
                        mCoin.contractAddress
                    )
                )

                withContext(Dispatchers.Main) {
                    if (mPage == 1 && result.isEmpty()) {
    //                    mTransactionAdapter.setEmptyView(R.layout.view_empty)
                        vEmpty.visibility=View.VISIBLE
                        v_refresh.finishLoadMoreWithNoMoreData()
                        SPUtils.getInstance().put("Transaction_${mCoin.wid}${mCoin.contract}_${mCoin.symbol}", "")
                        mTransactionAdapter.setList(arrayListOf())
                    } else {
                        vEmpty.visibility=View.GONE
                        if (result.isNotEmpty()) {
                            mRecordList.addAll(result)
                            mTransactionAdapter.setList(mRecordList)
                            v_refresh.finishLoadMore()
                            if (mPage == 1) {
                                SPUtils.getInstance().put(
                                    "Transaction_${mCoin.wid}${mCoin.contract}_${mCoin.symbol}",
                                    Gson().toJson(mRecordList)
                                )
                            }
                        } else {
                            mPage--
                            v_refresh.finishLoadMoreWithNoMoreData()
                        }
                    }
                    v_refresh.finishRefresh()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun initView() {
        immersionBar {
            statusBarColor(R.color.transparent)
            navigationBarColor(R.color.transparent)
            fitsSystemWindows(false)
        }

        val bundle = intent.extras
        mCoin = bundle?.getSerializable("coin") as Coin

        v_send.setOnClickListener(this)
        v_receive.setOnClickListener(this)
        v_refresh.setOnRefreshLoadMoreListener(this)
//        v_address.setOnClickListener(this)
    }

    override fun onRightClick(titleBar: TitleBar?) {
        DialogUtil.showBottomListDialog(
            this,
            list = arrayOf<String>(getString(R.string.hide))
        ) { pos, text ->
            when (pos) {
                0 -> {
                    showLoading()
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            Walletverse.sInstance.deleteWalletCoinAsync(mCoin)
                            EventBusUtil.post(RefreshEvent(WalletFragment.REFRESH_EVENT))
                            withContext(Dispatchers.Main) {
                                hideLoading()
                            }
                            finish()
                        } catch (e: Exception) {
                            hideLoading()
                            e.printStackTrace()
                        }
                    }

                }
            }
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.v_send -> {
                val bundle = Bundle()
                bundle.putSerializable("coin", mCoin)
                ActivityUtil.goActivity(this, WalletverseTransferActivity::class.java, bundle)
            }
            R.id.v_receive -> {
                val bundle = Bundle()
                bundle.putSerializable("coin", mCoin)
                ActivityUtil.goActivity(this, WalletverseReceiveActivity::class.java, bundle)
            }
            R.id.v_address -> {
                ClipboardUtils.copyText(mCoin.address)
                ToastUtil.showSuccess(getString(R.string.copy_success))
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshData(event: RefreshEvent) {
        if (event.pageName == REFRESH_EVENT) {
            mPage = 1
            mRecordList.clear()
            getTransactionRecorder()
        }
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        mPage = 1
        mRecordList.clear()
        getTransactionRecorder()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        mPage++
        getTransactionRecorder()
    }
}