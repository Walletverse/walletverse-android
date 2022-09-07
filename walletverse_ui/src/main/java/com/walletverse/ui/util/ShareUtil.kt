package com.walletverse.ui.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import java.util.*


/**
 * ================================================================
 * Create_time：2022/8/7  14:26
 * Author：solomon
 * Detail：
 * ================================================================
 */
object ShareUtil {

    fun shareImage(context: Context, bitmap: Bitmap) {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        val uri: Uri = Uri.parse(
            MediaStore.Images.Media.insertImage(
                context.contentResolver,
                bitmap,
                "IMG" + Calendar.getInstance().time,
                null
            )
        )
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        context.startActivity(intent)
    }

    fun shareText(context: Context,string: String){
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_TEXT, "文本内容")
        intent.type = "text/plain"
        context.startActivity(intent)
    }

}