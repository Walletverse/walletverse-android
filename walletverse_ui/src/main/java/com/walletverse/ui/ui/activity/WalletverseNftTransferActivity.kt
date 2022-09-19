package com.walletverse.ui.ui.activity

import android.Manifest
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.hjq.bar.TitleBar
import com.king.zxing.CameraScan
import com.king.zxing.CaptureActivity
import com.permissionx.guolindev.PermissionX
import com.walletverse.ui.R
import com.walletverse.ui.base.BaseActivity
import com.walletverse.ui.util.ToastUtil
import kotlinx.android.synthetic.main.activity_walletverse_nft_transfer.*

class WalletverseNftTransferActivity : BaseActivity() {

    private lateinit var mRegister: ActivityResultLauncher<Intent>

    override fun getLayoutId(): Int {
        return R.layout.activity_walletverse_nft_transfer
    }

    override fun initData() {
        mRegister =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.data != null) {
                    val address: String? = CameraScan.parseScanResult(result.data)
                    address?.let { v_address.setText(it) }
                }
            }
    }

    override fun initView() {
        initTitleBar("", rightIcon = R.mipmap.icon_scan)
    }


    override fun onRightClick(titleBar: TitleBar?) {
        PermissionX.init(this)
            .permissions(Manifest.permission.CAMERA)
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    mRegister.launch(Intent(this, CaptureActivity::class.java))
                } else {
                    ToastUtil.showError(getString(R.string.open_camera_failed))
                }
            }
    }

}