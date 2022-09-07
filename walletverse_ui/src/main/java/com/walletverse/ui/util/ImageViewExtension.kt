package com.walletverse.ui.util

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.CropTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

fun ImageView.loadImage(uri: String?) {
    Glide.with(this).load(uri).apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)).into(this)
}

fun ImageView.loadImage(uri: Uri?) {
    Glide.with(this).load(uri).apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)).into(this)
}

fun ImageView.loadImage(uri: String?, @DrawableRes holder: Int) {
    Glide.with(this).load(uri).apply(RequestOptions.placeholderOf(holder).diskCacheStrategy(DiskCacheStrategy.ALL)).into(this)
}


fun ImageView.loadImage(uri: String?, width: Int, height: Int) {
    val multi = MultiTransformation(CropTransformation(width, height))
    Glide.with(this).load(uri).apply(RequestOptions.bitmapTransform(multi).dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL)).into(this)
}

@SuppressLint("CheckResult")
fun ImageView.loadImageCenterCrop(uri: String?, @DrawableRes holder: Int? = null) {
    Glide.with(this).load(uri).apply(RequestOptions().dontAnimate().dontTransform().centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).apply {
        if (holder != null) {
            this.placeholder(holder)
        }
    }).into(this)
}

fun ImageView.loadGif(uri: String?,  centerCrop: Boolean? = null, @DrawableRes holder: Int? = null) {
    var requestOptions = RequestOptions().dontTransform().diskCacheStrategy(DiskCacheStrategy.ALL)
    if (centerCrop != null) {
        requestOptions = requestOptions.centerCrop()
    }
    if (holder != null) {
        requestOptions = requestOptions.placeholder(holder)
    }
    Glide.with(this).asGif().load(uri).apply(requestOptions).into(this)
}


fun ImageView.loadVideo(uri: String, @DrawableRes holder: Int) {
    Glide.with(this).load(uri).apply(RequestOptions().frame(0)
        .centerCrop().placeholder(holder).dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL)).into(this)
}

fun ImageView.loadSticker(uri: String?, type: String?) {
    uri?.let {
        when (type) {
            "GIF" -> {
                loadGif(uri)
            }
            else -> loadImage(uri)
        }
    }
}

fun ImageView.loadBase64(uri: ByteArray?, width: Int, height: Int, mark: Int) {
    val multi = MultiTransformation(CropTransformation(width, height))
    Glide.with(this).load(uri)
        .apply(RequestOptions().centerCrop()
            .transform(multi)
            .dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL)).into(this)
}

fun ImageView.loadCircleImage(uri: String?, @DrawableRes holder: Int? = null) {
    if (uri.isNullOrBlank()) {
        if (holder != null) {
            setImageResource(holder)
        }
    } else if (holder == null) {
        Glide.with(this).load(uri).apply(RequestOptions().circleCrop().diskCacheStrategy(DiskCacheStrategy.ALL)).into(this)
    } else {
        Glide.with(this).load(uri).apply(RequestOptions().placeholder(holder).circleCrop().diskCacheStrategy(DiskCacheStrategy.ALL)).into(this)
    }
}

fun ImageView.loadRoundImage(uri: String?, radius: Int, @DrawableRes holder: Int? = null) {
    if (uri.isNullOrBlank() && holder != null) {
        setImageResource(holder)
    } else if (holder == null) {
        Glide.with(this).load(uri).apply(RequestOptions.bitmapTransform(RoundedCornersTransformation(radius, 0)).diskCacheStrategy(DiskCacheStrategy.ALL)).into(this)
    } else {
        Glide.with(this).load(uri).apply(RequestOptions().transform(RoundedCornersTransformation(radius, 0)).diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(holder))
            .into(this)
    }
}