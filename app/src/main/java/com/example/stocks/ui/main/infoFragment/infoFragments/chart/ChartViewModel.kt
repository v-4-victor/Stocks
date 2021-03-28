package com.example.stocks.ui.main.infoFragment.infoFragments.chart

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.marsrealestate.network.StocksApi
import kotlinx.coroutines.launch
import java.util.*

class ChartViewModel : ViewModel() {
    enum class ApiStatus { LOADING, ERROR, DONE }

    private val _status = MutableLiveData<ApiStatus>()

    val status: LiveData<ApiStatus>
        get() = _status

    // The internal MutableLiveData String that stores the most recent response
    private val _response = MutableLiveData<String>()
    private val _properties = MutableLiveData<Chart>()
    val properties: LiveData<Chart>
        get() = _properties

    fun getChartData(company: String, scale: String) =
        viewModelScope.launch {
            val toDate = Calendar.getInstance().unixTime().toString()
            val fromDate = Calendar.getInstance()
            when (scale) {
                "D" -> {
                    fromDate.add(Calendar.DAY_OF_YEAR, -1)
                }
                "W" -> {
                    fromDate.add(Calendar.WEEK_OF_YEAR, -1)
                }
                "M" -> {
                    fromDate.add(Calendar.MONTH, -1)
                }
                "6M" -> {
                    fromDate.add(Calendar.MONTH, -6)
                }
                "Y" -> {
                    fromDate.add(Calendar.YEAR, -1)
                }
            }
            Log.d("DATE", fromDate.unixTime().toString())
            Log.d("DATE", toDate)
            val map = mapOf(
                "D" to "60",
                "W" to "D",
                "M" to "D",
                "6M" to "W",
                "Y" to "M"
            )
            _status.value = ApiStatus.LOADING
            _response.value = "fds"
            try {
                val list = StocksApi.retrofitService.getCandles(
                    company,
                    map[scale] ?: "",
                    fromDate.unixTime().toString(),
                    toDate
                )
                _properties.value = list
                _response.value = "Success: Mars properties retrieved"
                _status.value = ApiStatus.DONE
            } catch (e: Exception) {
                _response.value = e.message
                _status.value = ApiStatus.ERROR

            }

        }

    fun Calendar.unixTime() = this.timeInMillis / 1000L
}