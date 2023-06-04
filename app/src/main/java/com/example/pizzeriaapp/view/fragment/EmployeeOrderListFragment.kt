package com.example.pizzeriaapp.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzeriaapp.adapter.EmployeeOrderAdapter
import com.example.pizzeriaapp.adapter.OrderAdapter
import com.example.pizzeriaapp.databinding.FragmentEmployeeOrderListBinding
import com.example.pizzeriaapp.viewmodel.EmployeeOrderListViewModel
import com.example.pizzeriaapp.viewmodel.OrderListViewModel

class EmployeeOrderListFragment  : BaseFragment<FragmentEmployeeOrderListBinding>(FragmentEmployeeOrderListBinding::inflate) {
    private lateinit var empOrderRecyclerView: RecyclerView
    private lateinit var empOrderAdapter: EmployeeOrderAdapter
    private lateinit var empOrderListViewModel: EmployeeOrderListViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        empOrderRecyclerView = binding.orderListRecycleView
        empOrderRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        empOrderAdapter = EmployeeOrderAdapter(ArrayList())
        empOrderRecyclerView.adapter = empOrderAdapter

        empOrderListViewModel = ViewModelProvider(this)[EmployeeOrderListViewModel::class.java]

        empOrderListViewModel.ordersLiveData.observe(viewLifecycleOwner) { orderList ->
            empOrderAdapter.updateOrders(orderList)
        }
    }
}