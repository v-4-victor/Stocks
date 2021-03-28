package com.example.stocks.ui.main.infoFragment.infoFragments.news

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.stocks.databinding.NewsFragmentBinding

class NewsFragment(val company:String) : Fragment() {
    companion object
    {
        fun newInstance(company: String) = NewsFragment(company)
    }
    val adapter = NewsAdapter()
    private lateinit var viewModel: NewsViewModel
    private lateinit var binder : NewsFragmentBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binder= NewsFragmentBinding.inflate(inflater)
        binder.textError.visibility = View.INVISIBLE
        binder.lifecycleOwner = this
        viewModel = ViewModelProvider(this).get(NewsViewModel::class.java)
        binder.myViewModel = viewModel
        viewModel.getMarsRealEstateProperties(company)
        binder.recyclerView.adapter = adapter
        viewModel.properties.observe(viewLifecycleOwner)
        {
            adapter.submitList(it)
            //binder.button2.text = it.toString()

        }
        viewModel.status.observe(viewLifecycleOwner)
        {
            when(it)
            {
                NewsViewModel.ApiStatus.DONE -> {
                    binder.progressBar.visibility = View.GONE
                    binder.textError.visibility = View.INVISIBLE
                }
                NewsViewModel.ApiStatus.ERROR -> {
                    binder.progressBar.visibility = View.GONE
                    binder.textError.visibility = View.VISIBLE
                    binder.textError.text = "Error"
                }
                else -> {
                    binder.textError.visibility = View.VISIBLE
                    binder.textError.text = "Loading"
                }
            }
        }
        return binder.root
    }

}