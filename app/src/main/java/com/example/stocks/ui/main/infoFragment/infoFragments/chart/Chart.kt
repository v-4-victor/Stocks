package com.example.stocks.ui.main.infoFragment.infoFragments.chart

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Chart(
    @Json(name = "o")val openPrice: List<Double>,
    @Json(name = "h")val highPrice: List<Double>,
    @Json(name = "l")val lowPrice: List<Double>,
    @Json(name = "c")val closePrice: List<Double>,
    @Json(name = "t")val time: List<Long>
): Parcelable