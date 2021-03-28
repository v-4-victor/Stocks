package com.example.stocks.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.stocks.databinding.ListItemBinding
import com.example.stocks.network.ApiCompanyInfo
import com.example.stocks.network.PriceInfo
import com.example.stocks.network.StockInfo

class SearchAdapter  : ListAdapter<Search, RecyclerView.ViewHolder>(REPO_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val repoItem = getItem(position)
        if (repoItem != null) {
            (holder as ViewHolder).bind(repoItem)
        }
    }

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<Search>() {
            override fun areItemsTheSame(oldItem: Search, newItem: Search): Boolean =
                oldItem.displaySymbol == newItem.displaySymbol

            override fun areContentsTheSame(oldItem: Search, newItem: Search): Boolean =
                oldItem.displaySymbol == newItem.displaySymbol && oldItem.description == newItem.description
        }
    }
}
class ViewHolder private constructor(val binding: ListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    init {
        binding.root.setOnClickListener {
            val data:ApiCompanyInfo = binding.pic?.companyInfo ?:ApiCompanyInfo("","","","")
            binding.root.findNavController().navigate(
                SearchFragmentDirections.actionSearchFragmentToMainFragment(Search(data.description,data.displaySymbol,data.symbol)))
        }
    }
    fun bind(item: Search) {
        binding.pic= StockInfo(ApiCompanyInfo(item.description,item.displaySymbol, item.symbol,""), PriceInfo(0.0,0.0))
        binding.imageView.visibility = View.GONE
        binding.curPrice.visibility = View.GONE
        binding.opPrice.visibility = View.GONE
    }

    companion object {
        fun from(parent: ViewGroup): ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding =
                ListItemBinding.inflate(layoutInflater, parent, false)
            return ViewHolder(binding)
        }
    }
}