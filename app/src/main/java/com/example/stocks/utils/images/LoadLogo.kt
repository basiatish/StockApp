package com.example.stocks.utils.images

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
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