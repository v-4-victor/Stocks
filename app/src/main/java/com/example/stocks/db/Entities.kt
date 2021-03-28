package com.example.stocks.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DBCompanyInfo (

    val description: String,
    val displaySymbol: String,
    @PrimaryKey
    val symbol: String,
    val openPrice: Double,
    val currentPrice: Double,
    val timeUpdated:Long,
    val favourite: Boolean = false)

data class DatabasePrices(
    val symbol: String,
    val description: String,
    val openPrice: Double,
    val currentPrice: Double
)
data class Favourites(
    val description: String,
    val symbol: String,
    val favourite: Boolean
)