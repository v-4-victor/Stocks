package com.example.stocks.ui.main.infoFragment.infoFragments.news

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.marsrealestate.network.StocksApi
import com.example.stocks.network.PriceInfo
import com.example.stocks.network.StockInfo
import kotlinx.coroutines.launch

class NewsViewModel : ViewModel() {
    enum class ApiStatus { LOADING, ERROR, DONE }
    private val _status = MutableLiveData<ApiStatus>()

    val status: LiveData<ApiStatus>
        get() = _status

    // The internal MutableLiveData String that stores the most recent response
    private val _response = MutableLiveData<String>()
    private val _properties = MutableLiveData<List<News>>()
    val properties: LiveData<List<News>>
        get() = _properties
    private val _prices = MutableList<MutableLiveData<PriceInfo>>(50){ MutableLiveData(PriceInfo(-1.0,-1.0)) }

    // The external immutable LiveData for the response String
    val response: LiveData<String>
        get() = _response

    private val _navigateToSelectedProperty = MutableLiveData<StockInfo>()
    val navigateToSelectedProperty: LiveData<StockInfo>
        get() = _navigateToSelectedProperty
    val prices: List<LiveData<PriceInfo>>
        get() = _prices
    val listStocks = MutableLiveData<List<StockInfo>>()
    /**l
     * Call getMarsRealEstateProperties() on init so we can display status immediately.
     */

    /**
     * Sets the value of the status LiveData to the Mars API status.
     */
    fun getMarsRealEstateProperties(company:String) =
            viewModelScope.launch {
                _status.value = ApiStatus.LOADING
                _response.value = "fds"
                try {
                    val list = StocksApi.retrofitService.getNews(company,"2020-01-01","2021-01-31").distinctBy { it.headline }
                    _properties.value = list
                    _response.value = "Success: Mars properties retrieved"
                    _status.value = ApiStatus.DONE
                } catch (e: Exception) {
                    _response.value =e.message
                    _status.value = ApiStatus.ERROR
                    _properties.value = ArrayList()
                }

            }

    fun updateFilter() {
       // getMarsRealEstateProperties("f")
    }
    fun displayPropertyDetails(stockInfo: StockInfo) {
        _navigateToSelectedProperty.value = stockInfo
    }
    @SuppressLint("NullSafeMutableLiveData")
    fun displayPropertyDetailsComplete() {
        _navigateToSelectedProperty.value = null
    }
}