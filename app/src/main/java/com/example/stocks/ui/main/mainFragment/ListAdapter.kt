package com.example.stocks.ui.main.mainFragment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stocks.R
import com.example.stocks.databinding.ListItemBinding
import com.example.stocks.network.StockInfo
import kotlinx.android.synthetic.main.list_item.view.*

data class AdaptData(val stock: StockInfo, var favourite:Boolean)
class MyAdapter(
    val lifecycleOwner: LifecycleOwner,
    val viewModel: MainViewModel,
    private val onClickListener: OnClickListener,
    private val onImageListener: OnClickListener
) : ListAdapter<AdaptData, RecyclerView.ViewHolder>(
    REPO_COMPARATOR
) {
    class ViewHolder private constructor(val binding: ListItemBinding, val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AdaptData, lifecycleOwner: LifecycleOwner, viewModel: MainViewModel) {
            binding.pic = item.stock
            val url = "https://finnhub.io/api/logo?symbol=${item.stock.companyInfo.symbol}"
            Glide.with(binding.imageView.context)
                .load(url)
                .error(R.drawable.ic_broken)
                .into(binding.imageView)
            binding.lifecycleOwner = lifecycleOwner
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    ListItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, parent.context)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder.from(parent)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val repoItem = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(repoItem)
        }
        holder.itemView.imageView.setOnClickListener {
            onImageListener.onClick(repoItem)
        }
        if (repoItem != null) {
            (holder as ViewHolder).bind(repoItem, lifecycleOwner,viewModel)
        }
    }

    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<AdaptData>() {
            override fun areItemsTheSame(oldItem: AdaptData, newItem: AdaptData): Boolean =
                 oldItem.stock.companyInfo.symbol == newItem.stock.companyInfo.symbol && oldItem.stock.companyInfo.description == newItem.stock.companyInfo.description

            override fun areContentsTheSame(oldItem: AdaptData, newItem: AdaptData): Boolean =
                oldItem.stock.priceInfo.currentPrice == newItem.stock.priceInfo.currentPrice && oldItem.stock.priceInfo.openPrice == newItem.stock.priceInfo.openPrice &&  oldItem.stock.companyInfo.symbol == newItem.stock.companyInfo.symbol && oldItem.stock.companyInfo.description == newItem.stock.companyInfo.description
        }
    }

    class OnClickListener(val clickListener: (marsProperty: AdaptData) -> Unit) {
        fun onClick(marsProperty: AdaptData) = clickListener(marsProperty)
    }

}