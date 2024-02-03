package com.example.pizzeriaapp.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzeriaapp.adapter.OrderAdapter
import com.example.pizzeriaapp.databinding.FragmentEmployeeOrderHistoryBinding
import com.example.pizzeriaapp.viewmodel.EmployeeOrderHistoryViewModel

class EmployeeOrderHistory : BaseFragment<FragmentEmployeeOrderHistoryBinding>(FragmentEmployeeOrderHistoryBinding::inflate) {
    private lateinit var empOrderHistoryRecyclerView: RecyclerView
    private lateinit var empOrderHistoryAdapter: OrderAdapter
    private lateinit var empOrderHistoryViewModel: EmployeeOrderHistoryViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        empOrderHistoryRecyclerView = binding.empOrderHistoryRecycleView
        empOrderHistoryRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        empOrderHistoryAdapter = OrderAdapter(ArrayList(), true)
        empOrderHistoryRecyclerView.adapter = empOrderHistoryAdapter

        empOrderHistoryViewModel = ViewModelProvider(this)[EmployeeOrderHistoryViewModel::class.java]

        empOrderHistoryViewModel.ordersLiveData.observe(viewLifecycleOwner) { orderList ->
            empOrderHistoryAdapter.updateOrders(orderList)
        }
    }
}