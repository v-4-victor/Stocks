/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.example.stocks.network

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ApiCompanyInfo(
        val description: String,
        val displaySymbol: String,
        val symbol: String,
        val type: String,
): Parcelable {
    val isRental
        get() = type == "rent"
}
@Parcelize
data class PriceInfo(
    @Json(name = "o")val openPrice: Double,
    @Json(name = "c")var currentPrice: Double,
): Parcelable
@Parcelize
data class StockInfo(
    val companyInfo: ApiCompanyInfo,
    var priceInfo: PriceInfo
): Parcelable