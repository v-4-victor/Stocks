package com.example.stocks

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.stocks.ui.main.mainFragment.MainViewModel

class MainActivity : AppCompatActivity() {
    var viewModel: MainViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
    }

}