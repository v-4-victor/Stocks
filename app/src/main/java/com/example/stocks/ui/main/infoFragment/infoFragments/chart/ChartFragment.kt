package com.example.stocks.ui.main.infoFragment.infoFragments.chart

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.stocks.R
import com.example.stocks.databinding.ChartFragmentBinding
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import java.util.*

class ChartFragment(val company: String) : Fragment() {
    private lateinit var viewModel: ChartViewModel
    private lateinit var binder: ChartFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binder = ChartFragmentBinding.inflate(inflater)
        binder.lifecycleOwner = this
        viewModel = ViewModelProvider(this).get(ChartViewModel::class.java)

        viewModel.properties.observe(viewLifecycleOwner)
        {
            initChart(binder.chart1, it)
        }
        viewModel.status.observe(viewLifecycleOwner)
        {
            if (it == ChartViewModel.ApiStatus.LOADING) {
                binder.progressBar3.visibility = View.VISIBLE
                binder.chart1.visibility = View.INVISIBLE
            }
            if (it == ChartViewModel.ApiStatus.DONE) {
                binder.progressBar3.visibility = View.INVISIBLE
                binder.chart1.visibility = View.VISIBLE
            }
            if (it == ChartViewModel.ApiStatus.ERROR) {
                binder.chart1.visibility = View.VISIBLE
                binder.progressBar3.visibility = View.INVISIBLE
            }
        }
        binder.day.setOnClickListener { viewModel.getChartData(company, "D") }
        binder.week.setOnClickListener { viewModel.getChartData(company, "W") }
        binder.month.setOnClickListener { viewModel.getChartData(company, "M") }
        binder.halfYear.setOnClickListener { viewModel.getChartData(company, "6M") }
        binder.year.setOnClickListener { viewModel.getChartData(company, "Y") }
        viewModel.getChartData(
            company,
            "Y"
        )
        return binder.root
    }

    private fun initChart(chart: CandleStickChart, data: Chart) {
        chart.setBackgroundColor(Color.WHITE)

        chart.description.isEnabled = false

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60)

        // scaling can now only be done on x- and y-axis separately
        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false)

        chart.setDrawGridBackground(false)

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)

        val leftAxis = chart.axisLeft
        leftAxis.setLabelCount(7, false)
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(false)

        val rightAxis = chart.axisRight
        rightAxis.isEnabled = false
        setData(chart, data)
        chart.legend.isEnabled = false

        chart.animateY(1000)
        chart.invalidate()
    }

    private fun setData(chart: CandleStickChart, data: Chart) {
        chart.resetTracking()
        val candles = mutableListOf<CandleEntry>()
        repeat(data.closePrice.size)
        {
            candles.add(
                CandleEntry(
                    it.toFloat(),
                    data.highPrice[it].toFloat(),
                    data.lowPrice[it].toFloat(),
                    data.openPrice[it].toFloat(),
                    data.closePrice[it].toFloat(),
                    R.drawable.ic_launcher_background
                )
            )
        }
        val set1 = CandleDataSet(candles, "Data Set")
        set1.setDrawIcons(false)
        set1.axisDependency = YAxis.AxisDependency.LEFT
        set1.shadowColor = Color.DKGRAY
        set1.shadowWidth = 0.7f
        set1.decreasingColor = Color.rgb(255, 0, 0)
        set1.decreasingPaintStyle = Paint.Style.FILL
        set1.increasingColor = Color.rgb(122, 242, 84)
        set1.increasingPaintStyle = Paint.Style.STROKE
        set1.neutralColor = Color.BLUE
        val fullData = CandleData(set1)
        chart.data = fullData
        chart.invalidate()
    }


}