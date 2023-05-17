package com.example.pizzeriaapp.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzeriaapp.adapter.ProductAdapter
import com.example.pizzeriaapp.databinding.FragmentProductsBinding
import com.example.pizzeriaapp.viewmodel.ProductsViewModel

class ProductsFragment : BaseFragment<FragmentProductsBinding>(FragmentProductsBinding::inflate) {
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var productsViewModel: ProductsViewModel
    private lateinit var productsAdapter: ProductAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        productsRecyclerView = binding.recycleView
        productsRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
        productsAdapter = ProductAdapter(ArrayList())
        productsRecyclerView.adapter = productsAdapter

        productsViewModel = ViewModelProvider(this)[ProductsViewModel::class.java]

        productsViewModel.productsLiveData.observe(viewLifecycleOwner) { products ->
            productsAdapter.updateProducts(products)
        }

        productsViewModel.loadProducts()
    }
}