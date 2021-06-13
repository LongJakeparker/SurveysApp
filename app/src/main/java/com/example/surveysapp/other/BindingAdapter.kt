package com.example.surveysapp.other

import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import coil.transform.CircleCropTransformation


/**
 * @author longtran
 * @since 13/06/2021
 */

@BindingAdapter("android:avatarUrl")
fun setAvatarUrl(imageView: ImageView, url: String?) {
    imageView.load(url) {
        crossfade(true)
        transformations(CircleCropTransformation())
    }
}

@BindingAdapter("android:textWatcher")
fun setTextWatcher(view: EditText, textWatcher: TextWatcher) {
    view.addTextChangedListener(textWatcher)
}

@BindingAdapter("android:viewVisible")
fun setViewVisible(view: View, visible: Boolean) {
    if (visible) {
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}