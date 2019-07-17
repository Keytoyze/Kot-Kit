/*
 * Copyright (c) 2019 Chen Mouxiang <cmx_1007@foxmail.com>
 * All Rights Reserved.
 */

package team.henrykey.minitiktok.extension

import android.net.Uri
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.facebook.drawee.view.SimpleDraweeView
import java.util.*

object Binding {

    @Suppress("DEPRECATION")
    @JvmStatic
    @BindingAdapter("imageUri")
    fun setImageUri(simpleDraweeView: SimpleDraweeView, uri: String?) {
        simpleDraweeView.setImageURI(uri)
    }

    @JvmStatic
    @BindingAdapter("hide")
    fun setHide(view: View, hide: Boolean) {
        view.visibility = if (hide) View.GONE else View.VISIBLE
    }

    @JvmStatic
    @BindingAdapter("date")
    fun setDate(textView: TextView, date: Date) {
        textView.text = date.formatFriendly(textView.context)
    }

    @JvmStatic
    @BindingAdapter("tags")
    fun setTags(textView: TextView, tag: List<String>) {
        textView.text = tag.joinToString("、")
    }
}
