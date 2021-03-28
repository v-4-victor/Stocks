package com.example.stocks.ui.main.mainFragment

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.android.marsrealestate.network.StocksApi
import com.example.stocks.db.DBCompanyInfo
import com.example.stocks.db.Repository
import com.example.stocks.db.getDatabase
import com.example.stocks.network.StockInfo
import com.example.stocks.search.Search
import com.example.stocks.websocket.Websocket.Companion.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class MainViewModel(app: Application) : AndroidViewModel(app) {
    enum class ApiStatus { LOADING, ERROR, DONE }


    val _status = MutableLiveData<ApiStatus>()
    private val db = Repository(getDatabase(app))
    val status: LiveData<ApiStatus>
        get() = _status

    /**
     * _prices map for current and open price of stock
     */
    val _prices =
        mutableMapOf<String, LivePrice>()
    data class LivePrice(
        val openPrice: MutableLiveData<Double>,
        val currentPrice: MutableLiveData<Double>
    )

    private val _navigateToSelectedProperty = MutableLiveData<StockInfo>()
    val navigateToSelectedProperty: LiveData<StockInfo>
        get() = _navigateToSelectedProperty
    val listStocks2 = MutableLiveData<MutableMap<String, AdaptData>>(mutableMapOf())

    init {
        mapOf(
            "BINANCE:BTCUSDT" to "Binance BTC/USDT",
            "AAPL" to "APPLE INC",
            "AMZN" to "AMAZON.COM INC",
            "TSLA" to "TESLA INC",
            "MSFT" to "MICROSOFT CORP",
            "GOOGL" to "ALPHABET INC-CL A"
        ).forEach {
            viewModelScope.launch {
                db.Insert(
                    DBCompanyInfo(
                        it.value,
                        it.key,
                        it.key,
                        -1.0,
                        -1.0,
                        0L
                    )
                )
            }
        }

    }
    fun firstInit() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val time = Calendar.getInstance()
                val currentTime = Calendar.getInstance()
                val list = db.getCompanies()
                Log.d("Help", list.size.toString())
                val listForUpdate = mutableListOf<String>()
                list.forEach { dat ->
                    time.timeInMillis = dat.timeUpdated
                    if (
                        (dat.openPrice == -1.0 || dat.currentPrice == -1.0) ||
                        (currentTime.timeInMillis - time.timeInMillis > 24 * 60 * 60 * 1000) ||
                        ((time.get(Calendar.HOUR) !in 16..23) && (currentTime.get(Calendar.HOUR) in 16..23))
                    )
                        listForUpdate.add(dat.symbol)
                            .also { Log.d("Help", dat.symbol) }
                }
                updatePrice(listForUpdate)
                updateTime(currentTime.timeInMillis, listForUpdate)

            }
        }
    }
    fun invert(symbol: String): Boolean {
        listStocks2.value?.let {
            it[symbol]?.favourite = !(it[symbol]?.favourite ?: false)
        }
        return listStocks2.value?.get(symbol)?.favourite ?: false
    }
    /**
     * Databinding functions
     */
    fun getPrice(symbol: String) = Transformations.map(_prices[symbol]?.currentPrice ?: MutableLiveData()) { if (it < 0.0) "0.00" else String.format("%.2f", it) }

    fun myFormat(difference: Double, price: Double): String {
        return "${if (difference < 0) "▼" else "▲"}${
            String.format(
                "%.2f",
                (difference / price) * 100
            )
        }%(${String.format("%.2f", difference)})"
    }

    fun getDifference(symbol: String) =
        Transformations.map(_prices[symbol]?.currentPrice ?: MutableLiveData()) {
            val openPrice = _prices[symbol]?.openPrice?.value ?: 0.0
            val difference = it - openPrice
            if (it == -1.0) "" else myFormat(difference, it)
        }

    fun getColor(symbol: String) =
        Transformations.map(_prices[symbol]?.currentPrice ?: MutableLiveData()) {
            val openPrice = _prices[symbol]?.openPrice?.value ?: 0.0
            it - openPrice < 0
        }

    fun getFavourite(symbol: String) = Transformations.map(favourites) { it[symbol] }

    val favourites = MutableLiveData<MutableMap<String, Boolean>>(mutableMapOf())

    /**
     * Navigation functions
     */
    fun displayPropertyDetails(marsProperty: StockInfo) {
        _navigateToSelectedProperty.value = marsProperty
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun displayPropertyDetailsComplete() {
        _navigateToSelectedProperty.value = null
    }
    /**
     * Database functions
     */
    fun addNewTicker(tick: Search) {
        viewModelScope.launch {
            db.Insert(
                DBCompanyInfo(
                    tick.description,
                    tick.displaySymbol,
                    tick.symbol,
                    -1.0,
                    -1.0,
                    0L
                )
            )
        }
        _prices[tick.symbol] = LivePrice(MutableLiveData(0.0), MutableLiveData(0.0))
    }

    fun deleteTicker(ticker: String) = viewModelScope.launch { db.Delete(ticker) }

    fun updateFavourite(symbol: String, favourite: Boolean) =
        viewModelScope.launch { db.updateFavourite(symbol, favourite) }

    fun updatePrice(symbols: List<String>) = viewModelScope.launch {
        withContext(Dispatchers.IO)
        {
            symbols.forEach {
                try {
                    val price = StocksApi.retrofitService.getPrice(it)
                    db.updatePrice(price.openPrice, price.currentPrice, it)
                } catch (e: java.lang.Exception) {
                    Log.d(TAG, e.message.toString())
                }
            }

        }

    }

    fun updateTime(newTime: Long, symbols: List<String>) =
        viewModelScope.launch { db.updateTime(newTime, symbols) }

    fun getLiveCompanies() = db.getLiveCompanies()
    fun getStaticCompInfo() = db.getStaticCompInfo()

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}
