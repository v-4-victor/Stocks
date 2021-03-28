package com.example.stocks.ui.main.infoFragment

import android.app.Application
import androidx.lifecycle.*
import com.example.stocks.network.StockInfo

class InfoViewModel ( marsProperty: StockInfo,
                      app: Application) : AndroidViewModel(app) {
    private val _selectedProperty = MutableLiveData<StockInfo>()
    val selectedProperty: LiveData<StockInfo>
        get() = _selectedProperty
    init {
        _selectedProperty.value = marsProperty
    }
    val displayPropertyPrice = Transformations.map(selectedProperty) {
//        app.applicationContext.getString(
//                when (it.isRental) {
//                    true -> R.string.display_price_monthly_rental
//                    false -> R.string.display_price
//                }, it.price)
    }
    val displayPropertyType = Transformations.map(selectedProperty) {
//        app.applicationContext.getString(R.string.display_type,
//                app.applicationContext.getString(
//                        when (it.isRental) {
//                            true -> R.string.type_rent
//                            false -> R.string.type_sale
//                        }))
    }
}
class InfoViewModelFactory(
        private val marsProperty: StockInfo,
        private val application: Application) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InfoViewModel::class.java)) {
            return InfoViewModel(marsProperty, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}