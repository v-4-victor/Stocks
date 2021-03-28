package com.example.stocks.ui.main.infoFragment.infoFragments.news

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class News(
    val datetime: Long,
    val headline: String,
    val id: Int,
    val image: String,
    val source: String,
    val summary: String,
    val url: String):Parcelable