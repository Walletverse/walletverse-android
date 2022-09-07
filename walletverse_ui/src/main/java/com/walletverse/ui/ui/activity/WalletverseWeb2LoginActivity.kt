package com.walletverse.ui.ui.activity

//import com.befi.core.federated.GoogleFederated

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.walletverse.core.Walletverse
import com.walletverse.core.bean.*
import com.walletverse.core.enums.Channel
import com.walletverse.ui.R
import com.walletverse.ui.adapter.FederatedAdapter
import com.walletverse.ui.base.BaseActivity
import com.walletverse.ui.bean.FederatedBean
import com.walletverse.ui.util.ActivityUtil
import com.walletverse.ui.util.DialogUtil
import com.walletverse.ui.util.ToastUtil
import com.blankj.utilcode.util.RegexUtils
import com.blankj.utilcode.util.StringUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.gyf.immersionbar.ktx.immersionBar
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_walletverse_web2_login.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList


//web2.0 login
class WalletverseWeb2LoginActivity : BaseActivity(), TextWatcher {

    private lateinit var mRegister: ActivityResultLauncher<Intent>
    private var canBack: Boolean = false
    private var mFederated = mutableListOf<FederatedBean>(
        FederatedBean(R.mipmap.icon_auth_google, "Google"),
        FederatedBean(R.mipmap.icon_auth_facebook, "FaceBook"),
        FederatedBean(R.mipmap.icon_auth_twitter, "Twitter"),
        FederatedBean(R.mipmap.icon_auth_github, "Github"),
        FederatedBean(R.mipmap.icon_auth_discord, "Discord"),
        FederatedBean(R.mipmap.icon_auth_line, "Line"),
        FederatedBean(R.mipmap.icon_auth_kakao, "KaKao"),
    )

    override fun getLayoutId(): Int {
        return R.layout.activity_walletverse_web2_login
    }

    override fun initView() {
        immersionBar {
            statusBarColor(R.color.transparent)
            navigationBarColor(R.color.transparent)
            fitsSystemWindows(false)
        }
    }

    override fun initData() {

        v_recycler.layoutManager = GridLayoutManager(this, 5)
        val federatedAdapter = FederatedAdapter(R.layout.item_federated)
        v_recycler.adapter = federatedAdapter
        federatedAdapter.setNewInstance(mFederated)
        federatedAdapter.setOnItemClickListener { adapter, view, position ->
            when (federatedAdapter.data[position].channel) {
                "Google" -> {
                    googleSignIn()
                }
                "FaceBook" -> {

                }
                "Twitter" -> {
                }
            }

        }

        v_web3.setOnClickListener(this)
        val bundle = intent.extras
        canBack = bundle?.getBoolean("canBack", false) == true
        if (canBack) {
            v_close.visibility = View.VISIBLE
            v_close.setOnClickListener(this)
        }
        v_continue.setOnClickListener(this)
        v_continue.isClickable = false
        v_email.addTextChangedListener(this)

        mRegister =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(
                    result.data
                )
                handleSignInResult(task)
            }
    }

    private fun googleSignIn() {
        showLoading()
        lifecycleScope.launch(Dispatchers.Main) {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .requestProfile()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(this@WalletverseWeb2LoginActivity, gso)

            // Log out first because if the user logs in with another email,
            // he needs to log out of the current authorized login first.
            // If your app has an explicit user logout method, please call it when logging out
            googleSignInClient.signOut().addOnCompleteListener(this@WalletverseWeb2LoginActivity) {
                val signInIntent: Intent = googleSignInClient.signInIntent
                mRegister.launch(signInIntent)
            }


        }

    }


    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val federatedParams = FederatedParams(
                account.email ?: account.displayName!!,
                account.id!!,
                Channel.Google
            )

            judgeUserType(federatedParams)

        } catch (e: ApiException) {
            hideLoading()
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Logger.e("signInResult:failed code=" + e.statusCode)
        }
    }

    private fun judgeUserType(federatedParams: FederatedParams) {
        if (Walletverse.sInstance.checkWalletExist(federatedParams)) {
            hideLoading()
            ToastUtil.showError(getString(R.string.already_exist))
            return
        }

        //  successfully
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val federated: Federated = Walletverse.sInstance.signInWeb2Async(
                    federatedParams
                )
                val bundle = Bundle()
                var userType = ""
                if (federated.shards.isEmpty()) {
                    userType = "new"
                    val mnemonicData = Walletverse.sInstance.generateMnemonicAsync(DefaultParams("0x1"))
                    if (mnemonicData.phrase.isEmpty()) {
                        hideLoading()
                        return@launch
                    }
                    bundle.putString("mnemonic", mnemonicData.phrase)
                } else {
                    userType = "old"
                    bundle.putStringArrayList("shards", federated.shards as ArrayList<String>)
                    bundle.putString("wallets", Gson().toJson(federated.wallets))
                }
                bundle.putSerializable("federated", federatedParams)
                bundle.putString("userType", userType)
                withContext(Dispatchers.Main) {
                    hideLoading()
                    ActivityUtil.goActivity(
                        this@WalletverseWeb2LoginActivity,
                        WalletverseWeb2CreateActivity::class.java,
                        bundle
                    )
                }
            } catch (e: Exception) {
                hideLoading()
                e.printStackTrace()
            }
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.v_web3 -> {
                ActivityUtil.goActivity(this, WalletverseWeb3LoginActivity::class.java)
            }
            R.id.v_close -> {
                finish()
            }
            R.id.v_continue -> {
                DialogUtil.showEmailVerifyDialog(
                    this,
                    v_email.text.toString(),
                    object : DialogUtil.OnResultListener {
                        override fun onConfirm(result: Any) {
                            val auth = result as String
                            val federatedParams = FederatedParams(
                                v_email.text.toString(),
                                v_email.text.toString(),
                                Channel.Email,
                                auth
                            )
                            showLoading()
                            judgeUserType(federatedParams)
                        }

                        override fun onCancel() {
                        }

                    })
            }
        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        v_continue.setEnable(!StringUtils.isEmpty(p0) && RegexUtils.isEmail(p0))
        v_continue.isClickable = !StringUtils.isEmpty(p0) && RegexUtils.isEmail(p0)
    }

    override fun afterTextChanged(p0: Editable?) {
    }


}