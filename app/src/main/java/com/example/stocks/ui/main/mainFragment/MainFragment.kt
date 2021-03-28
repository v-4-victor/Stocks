package com.example.stocks.ui.main.mainFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.stocks.MainActivity
import com.example.stocks.R
import com.example.stocks.databinding.MainFragmentBinding
import com.example.stocks.network.ApiCompanyInfo
import com.example.stocks.network.PriceInfo
import com.example.stocks.network.StockInfo
import com.example.stocks.websocket.Websocket
import com.example.stocks.websocket.Websocket.Companion.TAG
import kotlinx.android.synthetic.main.main_fragment.*
import kotlin.collections.set


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var binder: MainFragmentBinding
    private lateinit var adapter: MyAdapter
    private lateinit var mIth:ItemTouchHelper
    private var star = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binder = MainFragmentBinding.inflate(inflater)
        binder.lifecycleOwner = viewLifecycleOwner
        binder.toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.star) {
                star = !it.isChecked
                it.setIcon(if (!it.isChecked) R.drawable.ic_star_on else R.drawable.ic_star_off)
                if (viewModel.listStocks2.value != null) {
                    if (star)
                        adapter.submitList(viewModel.listStocks2.value?.values?.filter { it.favourite }
                            ?.sortedBy { it.stock.companyInfo.symbol })
                    else
                        adapter.submitList(
                            viewModel.listStocks2.value?.values?.toMutableList()
                                ?.sortedBy { it.stock.companyInfo.symbol })
                }
                it.isChecked = !it.isChecked
            }
            if (it.itemId == R.id.search) {
                this.findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToSearchFragment()
                )
            }
            true
        }

        if ((activity as MainActivity).viewModel == null) {
            viewModel =
                ViewModelProvider(this, MainViewModel.Factory(requireActivity().application))
                    .get(MainViewModel::class.java)
            (activity as MainActivity).viewModel = viewModel
        } else
            viewModel = (activity as MainActivity).viewModel!!

        arguments?.let {
            viewModel.addNewTicker(MainFragmentArgs.fromBundle(it).selectedCompany)
            arguments = null
        }
        viewModel.status.observe(viewLifecycleOwner)
        {
            when (it) {
                MainViewModel.ApiStatus.DONE -> {

                    try {
                        binder.progressBar2.visibility = View.GONE
                        val socket = Websocket()
                        viewModel.listStocks2.value?.let { it1 -> socket.stocks.addAll(it1.keys) }
                        socket.initWebSocket()
                        socket.answer.observe(viewLifecycleOwner) { list ->
                            list.forEach { that ->
                                viewModel._prices[that.s]?.currentPrice?.postValue(
                                    that.p
                                )
                            }
                        }
                    } catch (e: Exception) {
                        Log.d(TAG, e.message.toString())
                    }
                }
                MainViewModel.ApiStatus.ERROR -> {
                    binder.progressBar2.visibility = View.GONE
                    binder.buttonRetry.visibility = View.VISIBLE
                    binder.mainError.visibility = View.VISIBLE
                }
            }
        }

        val dynamicData = viewModel.getStaticCompInfo()
        dynamicData.observe(viewLifecycleOwner)
        {
            val data = it

            data.forEach {
                viewModel.favourites.value?.set(it.symbol, it.favourite)
                viewModel.listStocks2.value?.set(
                    it.symbol,
                    AdaptData(
                        StockInfo(
                            ApiCompanyInfo(it.description, it.symbol, it.symbol, ""),
                            PriceInfo(-1.0, -1.0)
                        ), it.favourite
                    )
                )
            }
            viewModel.listStocks2.postValue(viewModel.listStocks2.value)
            viewModel.firstInit()
            dynamicData.removeObservers(viewLifecycleOwner)
        }


        val obs = viewModel.getLiveCompanies().distinctUntilChanged()
        obs.observe(viewLifecycleOwner)
        {
            val data = it
            data.forEach {
                if (viewModel._prices[it.symbol] == null) {
                    viewModel._prices[it.symbol] = MainViewModel.LivePrice(
                        MutableLiveData(it.currentPrice),
                        MutableLiveData(it.openPrice)
                    )
                }
                viewModel._prices[it.symbol]?.currentPrice?.value = it.currentPrice
                viewModel._prices[it.symbol]?.openPrice?.value = it.openPrice
                val favourite = viewModel.listStocks2.value?.get(it.symbol)?.favourite ?: false
                viewModel.listStocks2.value?.set(
                    it.symbol,
                    AdaptData(
                        StockInfo(
                            ApiCompanyInfo(it.description, it.symbol, it.symbol, ""),
                            PriceInfo(it.currentPrice, it.openPrice)
                        ), favourite
                    )
                )
            }
        }
        binder.myViewModel = viewModel
        adapter = MyAdapter(viewLifecycleOwner, viewModel, MyAdapter.OnClickListener {
            viewModel.displayPropertyDetails(it.stock)
        },
            MyAdapter.OnClickListener { adapt ->
                val bool = viewModel.invert(adapt.stock.companyInfo.symbol)
                viewModel.favourites.value?.set(adapt.stock.companyInfo.symbol, bool)
                viewModel.favourites.value = viewModel.favourites.value
                viewModel.updateFavourite(adapt.stock.companyInfo.symbol, bool)
            })
        viewModel.navigateToSelectedProperty.observe(this, {
            if (it != null) {
                this.findNavController().navigate(
                    MainFragmentDirections.actionShowDetail(it)
                )
                viewModel.displayPropertyDetailsComplete()
            }
        })
         mIth = ItemTouchHelper(
             object : ItemTouchHelper.SimpleCallback(
                 ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                 ItemTouchHelper.LEFT
             ) {
                 override fun onMove(
                     recyclerView: RecyclerView,
                     viewHolder: RecyclerView.ViewHolder,
                     target: RecyclerView.ViewHolder
                 ): Boolean {
                     return true
                 }

                 override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                     viewHolder as MyAdapter.ViewHolder
                     viewModel.deleteTicker(viewHolder.binding.ticker.text.toString())
                     viewModel._prices.remove(viewHolder.binding.ticker.text.toString())
                     viewModel.listStocks2.value?.remove(viewHolder.binding.ticker.text.toString())
                     viewModel.listStocks2.value = viewModel.listStocks2.value
                 }
             })

        viewModel.listStocks2.observe(viewLifecycleOwner) {
                if (it.values.isNotEmpty())
                {
                    if (star)
                        adapter.submitList(it.values.filter { it.favourite }
                            .sortedBy { it.stock.companyInfo.symbol })
                    else
                    adapter.submitList(
                        it.values.toMutableList().sortedBy { it.stock.companyInfo.symbol })
                    viewModel._status.value = MainViewModel.ApiStatus.DONE
                }
        }
        return binder.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recycler.adapter = adapter
        mIth.attachToRecyclerView(recycler)
    }


}