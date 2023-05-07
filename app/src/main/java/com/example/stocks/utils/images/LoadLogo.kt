package com.example.stocks.utils.images

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.RequestOptions.overrideOf
import com.bumptech.glide.request.target.Target
import com.example.stocks.R

fun loadLogo(context: Context, url: String, symbol: String, view: ImageView) {
    Glide.with(context).load(url)
        .diskCacheStrategy(DiskCacheStrategy.DATA)
        .skipMemoryCache(true)
        .error(R.drawable.ic_warning)
        .centerCrop()
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                if (symbol == "ADDYY" || symbol == "AAPL" ||
                    symbol == "NKE" || symbol == "IBM"
                    || symbol == "V") {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        resource?.colorFilter = BlendModeColorFilter(
                            Color.BLACK, BlendMode.SRC_IN
                        )
                    } else {
                        resource?.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN)
                    }
                }
                return false
            }
        })
        .into(view)
}

fun loadNewsImage(context: Context, url: String, view: ImageView) {
    Glide.with(context)
        .load(url)
        //.apply(RequestOptions.overrideOf(1200, 1000))
        .diskCacheStrategy(DiskCacheStrategy.DATA)
        .skipMemoryCache(true)
        .error(R.drawable.ic_warning)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                var image = resource?.toBitmap()!!
                val y = image.height.toLong()
                val x = image.width.toLong()
                val ratio = x / y
                var height = 800.0
                if (ratio > 1.6) {
                    height *= 1.7
                }
                image = Bitmap.createScaledBitmap(image, (1200 * ratio).toInt(), height.toInt(), false)
                view.setImageDrawable(image.toDrawable(context.resources))
                return false
            }
        })
        .preload()
}