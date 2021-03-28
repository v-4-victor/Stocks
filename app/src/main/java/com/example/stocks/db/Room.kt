package com.example.stocks.db

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CompanyDao {
    @Query("select symbol, description, currentPrice, openPrice from DBCompanyInfo")
    fun getLiveCompanies(): LiveData<List<DatabasePrices>>

    @Query("select * from DBCompanyInfo")
    fun getCompanies(): List<DBCompanyInfo>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(videos: List<DBCompanyInfo>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(company: DBCompanyInfo)

    @Query("update DBCompanyInfo set timeUpdated = :newTime where symbol = :symbol")
    fun updateTime(newTime: Long, symbol: String)

    @Query("update DBCompanyInfo set openPrice = :openPrice , currentPrice = :currentPrice where symbol = :symbol")
    fun updatePrice(openPrice: Double, currentPrice: Double, symbol: String)

    @Query("select symbol, description, favourite from DBCompanyInfo ")
    fun getStaticCompInfo(): LiveData<List<Favourites>>

    @Query("select openPrice from DBCompanyInfo where symbol =:symbol")
    fun getOpenPrice(symbol: String): LiveData<Double>

    @Query("update DBCompanyInfo set favourite = :favourite where symbol = :symbol")
    fun updateFavourite(symbol: String, favourite: Boolean)

    @Query("delete from DBCompanyInfo where symbol = :symbol")
    fun delete(symbol: String)
}


@Database(entities = [DBCompanyInfo::class], version = 1)
abstract class CompanyDB : RoomDatabase() {
    abstract val companyDao: CompanyDao
}

private lateinit var INSTANCE: CompanyDB

fun getDatabase(context: Context): CompanyDB {
    synchronized(CompanyDB::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                CompanyDB::class.java,
                "companies.db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}