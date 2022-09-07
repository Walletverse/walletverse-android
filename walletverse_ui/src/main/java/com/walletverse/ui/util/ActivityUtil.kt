package com.walletverse.ui.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle


/**
 * ================================================================
 * Create_time：2022/8/3  13:57
 * Author：solomon
 * Detail：
 * ================================================================
 */
object ActivityUtil {

    fun goNewActivity(context: Context, cls: Class<out Activity>?) {
        val intent=Intent(context,cls)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun goActivity(context: Context, cls: Class<out Activity>?) {
        startActivity(context, cls, null, -1, -1)
    }


    fun goActivity(context: Context, cls: Class<out Activity>?, bundle: Bundle?) {
        startActivity(context, cls, bundle, -1, -1)
    }


    fun goActivityForResult(context: Context, cls: Class<out Activity>?, requestCode: Int) {
        startActivity(context, cls, null, requestCode, -1)
    }


    fun goActivityForResult(
        context: Context,
        cls: Class<out Activity>?,
        bundle: Bundle?,
        requestCode: Int
    ) {
        startActivity(context, cls, bundle, requestCode, -1)
    }


    private fun startActivity(
        context: Context,
        cls: Class<out Activity>?,
        bundle: Bundle?,
        requestCode: Int,
        resultCode: Int
    ) {
        val intent: Intent = cls?.let { Intent(context, it) } ?: Intent()
        if (null != bundle) {
            intent.putExtras(bundle)
        }
        if (-1 != resultCode) {
            (context as Activity).setResult(resultCode, intent)
        } else {
            if (-1 != requestCode) {
                (context as Activity).startActivityForResult(intent, requestCode)
            } else {
                context.startActivity(intent)
            }
        }
    }
}