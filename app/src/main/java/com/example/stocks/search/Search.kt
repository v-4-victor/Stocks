package com.example.stocks.search

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Search(
    val description: String,
    val displaySymbol: String,
    val symbol:String
): Parcelable