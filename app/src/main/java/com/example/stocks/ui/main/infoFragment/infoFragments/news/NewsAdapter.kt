package com.example.stocks.ui.main.infoFragment.infoFragments.news

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stocks.R
import com.example.stocks.databinding.NewsCardBinding


class NewsAdapter : ListAdapter<News, RecyclerView.ViewHolder>(REPO_COMPARATOR) {

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
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<News>() {
            override fun areItemsTheSame(oldItem: News, newItem: News): Boolean =
                    oldItem.url == newItem.url

            override fun areContentsTheSame(oldItem: News, newItem: News): Boolean =
                    oldItem == newItem
        }
    }
}
class ViewHolder private constructor(val binding: NewsCardBinding, val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
    private val imageWidthPixels = 1024;
    private val imageHeightPixels = 768;
    fun bind(item: News) {
        binding.item = item
        //val url = item.url.toUri().buildUpon().scheme("https").build()
        Glide.with(binding.imageView.context)
                .load(item.image)
                .override(binding.imageView.width, binding.imageView.height)
                .error(R.drawable.ic_launcher_background)
                .into(binding.imageView)
        binding.imageView.setOnClickListener {
            val address = Uri.parse(item.url)
            val openLinkIntent = Intent(Intent.ACTION_VIEW, address)
            startActivity(context,openLinkIntent,null)
        }
        //binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding =
                NewsCardBinding.inflate(layoutInflater, parent, false)
            return ViewHolder(binding, parent.context)
        }
    }
}