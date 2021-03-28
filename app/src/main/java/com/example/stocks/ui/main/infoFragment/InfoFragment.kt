package com.example.stocks.ui.main.infoFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.stocks.databinding.InfoFragmentBinding
import com.example.stocks.ui.main.infoFragment.infoFragments.chart.ChartFragment
import com.example.stocks.network.StockInfo
import com.example.stocks.ui.main.infoFragment.infoFragments.news.NewsFragment


class InfoFragment : Fragment() {
    private lateinit var companyInfo: StockInfo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val application = requireNotNull(activity).application
        val binding = InfoFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = this
        companyInfo = InfoFragmentArgs.fromBundle(arguments!!).selectedProperty
        val viewModelFactory = InfoViewModelFactory(companyInfo, application)
        binding.infoModel = ViewModelProvider(
            this, viewModelFactory
        ).get(InfoViewModel::class.java)
        binding.viewPager2.adapter = ViewPagerAdapter(this, companyInfo.companyInfo.symbol)
        return binding.root
    }

    class ViewPagerAdapter(fm: Fragment, private val company: String) :
        FragmentStateAdapter(fm) {

        override fun getItemCount(): Int = 2
        override fun createFragment(position: Int): Fragment =
            if (position == 0) ChartFragment(company) else NewsFragment.newInstance(company)

    }
}