package com.egeperk.rick_and_morty_pro.adapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load

@BindingAdapter("imageUrl")
fun ImageView.setImageUrl(url: String?) {
    load(url) { crossfade(true) }
}