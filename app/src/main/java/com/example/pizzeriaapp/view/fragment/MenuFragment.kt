package com.example.pizzeriaapp.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzeriaapp.adapter.MenuAdapter
import com.example.pizzeriaapp.databinding.FragmentMenuBinding
import com.example.pizzeriaapp.viewmodel.MenuViewModel

class MenuFragment : BaseFragment<FragmentMenuBinding>(FragmentMenuBinding::inflate) {
    private lateinit var menuRecyclerView: RecyclerView
    private lateinit var menuViewModel: MenuViewModel
    private lateinit var menuAdapter: MenuAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        menuRecyclerView = binding.recycleView
        menuRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
        menuAdapter = MenuAdapter(ArrayList())
        menuRecyclerView.adapter = menuAdapter

        menuViewModel = ViewModelProvider(this)[MenuViewModel::class.java]

        menuViewModel.menuLiveData.observe(viewLifecycleOwner) { products ->
            menuAdapter.updateProducts(products)
        }
    }
}