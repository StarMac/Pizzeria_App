package com.example.pizzeriaapp.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzeriaapp.R
import com.example.pizzeriaapp.adapter.AdminMenuAdapter
import com.example.pizzeriaapp.adapter.MenuAdapter
import com.example.pizzeriaapp.databinding.FragmentAdminMenuBinding
import com.example.pizzeriaapp.viewmodel.AdminMenuViewModel
import com.example.pizzeriaapp.viewmodel.MenuViewModel

class AdminMenuFragment : BaseFragment<FragmentAdminMenuBinding>(FragmentAdminMenuBinding::inflate) {
    private lateinit var adminMenuRecyclerView: RecyclerView
    private lateinit var adminMenuViewModel: AdminMenuViewModel
    private lateinit var adminMenuAdapter: AdminMenuAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){
        adminMenuRecyclerView = binding.recycleView
        adminMenuRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
        adminMenuAdapter = AdminMenuAdapter(ArrayList())
        adminMenuRecyclerView.adapter = adminMenuAdapter

        adminMenuViewModel = ViewModelProvider(this)[AdminMenuViewModel::class.java]

        adminMenuViewModel.menuLiveData.observe(viewLifecycleOwner) { products ->
            adminMenuAdapter.updateProducts(products)
        }
    }
}