package com.walletverse.ui.ui.activity

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.walletverse.core.Walletverse
import com.walletverse.core.bean.DefaultParams
import com.walletverse.core.bean.UserCreateData
import com.walletverse.core.bean.VerifyMnemonic
import com.walletverse.ui.R
import com.walletverse.ui.adapter.MnemonicAdapter
import com.walletverse.ui.base.BaseActivity
import com.walletverse.ui.util.ActivityUtil
import com.blankj.utilcode.util.StringUtils
import kotlinx.android.synthetic.main.activity_walletverse_mnemonic.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class WalletverseMnemonicActivity : BaseActivity() {

    private var mnemonics = mutableListOf<String>()
    private var userCreateData: UserCreateData = UserCreateData()
    private var userDataBundle: Bundle = Bundle()

    override fun getLayoutId(): Int {
        return R.layout.activity_walletverse_mnemonic
    }

    override fun initData() {

        val bundle = intent.extras
        val name = bundle?.getString("name")
        val pin = bundle?.getString("pin")

        val one = Random(System.currentTimeMillis()).nextInt(6)
        val two = Random(System.currentTimeMillis()).nextInt(6)+6

        lifecycleScope.launch(Dispatchers.Main) {
            try {
                val mnemonic = Walletverse.sInstance.generateMnemonicAsync(
                    DefaultParams("0x1")
                )
                if (StringUtils.isEmpty(mnemonic.phrase)) {
                    return@launch
                }

                mnemonics = mnemonic.phrase.split(" ") as MutableList<String>
//                Logger.d(mnemonics.toString())

                val gridLayoutManager = GridLayoutManager(this@WalletverseMnemonicActivity, 3)
                v_recycler.layoutManager = gridLayoutManager
                val mnemonicAdapter = MnemonicAdapter(R.layout.item_mnemonic)
                v_recycler.adapter = mnemonicAdapter
                mnemonicAdapter.setNewInstance(mnemonics)

                userCreateData.name = name!!
                userCreateData.pin = pin!!
                userCreateData.mnemonic = mnemonic.phrase
                userCreateData.verifyMnemonics.add(VerifyMnemonic("${one + 1}", mnemonics[one]))
                userCreateData.verifyMnemonics.add(VerifyMnemonic("${two + 1}", mnemonics[two]))

                userDataBundle.putSerializable("user_create_data", userCreateData)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        v_backup_completed.setOnClickListener(this)
    }

    override fun initView() {
        initTitleBar(getString(R.string.mnemonic))
    }


    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.v_backup_completed -> {
                if (userCreateData.verifyMnemonics.isNotEmpty()) {
                    ActivityUtil.goActivity(this, WalletverseVerifyActivity::class.java, userDataBundle)
                }
            }
        }
    }

}