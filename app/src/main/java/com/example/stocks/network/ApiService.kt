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

package com.example.android.marsrealestate.network

import android.os.Parcelable
import com.example.stocks.ui.main.infoFragment.infoFragments.chart.Chart
import com.example.stocks.network.ApiCompanyInfo
import com.example.stocks.network.PriceInfo
import com.example.stocks.ui.main.infoFragment.infoFragments.news.News
import com.example.stocks.search.Search
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.android.parcel.Parcelize
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://finnhub.io/api/v1/"
private const val TOKEN = "c0mmsm748v6tkq136co0"
private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .build()
@Parcelize
data class Response(val result:List<Search>):Parcelable

interface ApiService {
    @GET("stock/symbol")
    suspend fun getProperties(@Query("exchange") type: String = "US", @Query("token") token: String = TOKEN): List<ApiCompanyInfo>
    @GET("index/constituents")
    suspend fun getTop(@Query("symbol") symbol: String = "^DJI", @Query("token") token: String = TOKEN): List<String>
    @GET("quote")
    suspend fun getPrice(@Query("symbol") symbol: String, @Query("token") token: String = TOKEN): PriceInfo
    @GET("company-news")
    suspend fun getNews(@Query("symbol") symbol: String,@Query("from") fromDate: String, @Query("to") toDate: String, @Query("token") token: String = TOKEN): List<News>
    @GET("stock/candle")
    suspend fun getCandles(@Query("symbol") symbol: String, @Query("resolution") resolution:String = "D",@Query("from") fromDate: String ="1572651390", @Query("to") toDate: String = "1575243390", @Query("token") token: String = TOKEN): Chart
    @GET("search")
    suspend fun getNames(@Query("q")symbol: String = "apple",  @Query("token") token: String = TOKEN):Response
}


object StocksApi
{
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}