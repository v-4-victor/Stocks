package com.example.stocks.search

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.marsrealestate.network.StocksApi
import com.example.stocks.network.PriceInfo
import com.example.stocks.network.StockInfo
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    enum class MarsApiStatus { LOADING, ERROR, DONE }

    private val _status = MutableLiveData<MarsApiStatus>()

    val status: LiveData<MarsApiStatus>
        get() = _status

    // The internal MutableLiveData String that stores the most recent response
    private val _response = MutableLiveData<String>()
    private val _properties = MutableLiveData<List<Search>>()
    val properties: LiveData<List<Search>>
        get() = _properties
    private val _prices =
        MutableList<MutableLiveData<PriceInfo>>(50) { MutableLiveData(PriceInfo(-1.0, -1.0)) }

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
    fun getMarsRealEstateProperties(company: String) =
        viewModelScope.launch {
            _status.value = MarsApiStatus.LOADING
            _response.value = "fds"
            try {
                val list = StocksApi.retrofitService.getNames(company)
                _properties.value = list.result
                _response.value = "Success: Mars properties retrieved"
                _status.value = MarsApiStatus.DONE
            } catch (e: Exception) {
                _response.value = e.message
                _status.value = MarsApiStatus.ERROR
                _properties.value = ArrayList()
            }

        }

    fun updateFilter() {
        // getMarsRealEstateProperties("f")
    }

    fun displayPropertyDetails(marsProperty: StockInfo) {
        _navigateToSelectedProperty.value = marsProperty
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun displayPropertyDetailsComplete() {
        _navigateToSelectedProperty.value = null
    }
}