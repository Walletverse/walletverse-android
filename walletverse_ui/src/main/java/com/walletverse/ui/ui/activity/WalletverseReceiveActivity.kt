package com.walletverse.ui.ui.activity

import android.Manifest
import android.graphics.Bitmap
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.walletverse.core.bean.Coin
import com.walletverse.ui.R
import com.walletverse.ui.base.BaseActivity
import com.walletverse.ui.util.ShareUtil
import com.walletverse.ui.util.ToastUtil
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.SizeUtils
import com.gyf.immersionbar.ImmersionBar
import com.gyf.immersionbar.ktx.immersionBar
import com.king.zxing.util.CodeUtils
import com.permissionx.guolindev.PermissionX
import kotlinx.android.synthetic.main.activity_walletverse_receive.*

class WalletverseReceiveActivity : BaseActivity() {
    private lateinit var scanImage: Bitmap
    private lateinit var address:String
    private lateinit var mCoin: Coin

    override fun getLayoutId(): Int {
        return R.layout.activity_walletverse_receive
    }

    override fun initData() {


    }

    override fun initView() {
        immersionBar {
            transparentBar()
            fitsSystemWindows(false)
        }

        val bundle = intent.extras
        mCoin = bundle?.getSerializable("coin") as Coin
        address=mCoin.address


        initTitleBar(
            getString(R.string.receive),
            backgroundColor = ContextCompat.getColor(this, R.color.transparent)
        )

        v_receive_tip.text=getString(R.string.receive_tip,mCoin.contract)
        v_scan_receive_tip.text=getString(R.string.receive_tip2,mCoin.symbol)

        v_status_bar.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            ImmersionBar.getStatusBarHeight(this)
        )

        scanImage = CodeUtils.createQRCode(address, SizeUtils.dp2px(160f))
        v_scan_code_image.setImageBitmap(scanImage)

        v_address.text=address

        v_copy.setOnClickListener(this)
        v_share.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        when (v.id) {
            R.id.v_copy -> {
                ClipboardUtils.copyText(address)
                ToastUtil.showSuccess(getString(R.string.copy_success))
            }
            R.id.v_share -> {
                PermissionX.init(this)
                    .permissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
                    .request { allGranted, _, _ ->
                        if (allGranted) {
                            ShareUtil.shareImage(this,scanImage)
                        } else {
                            ToastUtil.showSuccess(getString(R.string.share_failed))
                        }
                    }
            }
        }
    }

}