package com.example.pizzeriaapp.view.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzeriaapp.adapter.EmployeeOrderAdapter
import com.example.pizzeriaapp.databinding.FragmentEmployeeOrderListBinding
import com.example.pizzeriaapp.viewmodel.EmployeeOrderListViewModel

class EmployeeOrderListFragment  : BaseFragment<FragmentEmployeeOrderListBinding>(FragmentEmployeeOrderListBinding::inflate) {
    private lateinit var empOrderRecyclerView: RecyclerView
    private lateinit var empOrderAdapter: EmployeeOrderAdapter
    private lateinit var empOrderListViewModel: EmployeeOrderListViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
//        orderRecyclerView = binding.orderListRecycleView
//        orderRecyclerView.layoutManager = LinearLayoutManager(requireContext())
//        orderAdapter = OrderAdapter(ArrayList())
//        orderRecyclerView.adapter = orderAdapter
//
//        orderListViewModel = ViewModelProvider(this)[OrderListViewModel::class.java]
//
//        orderListViewModel.ordersLiveData.observe(viewLifecycleOwner) { orderList ->
//            orderAdapter.updateOrders(orderList)
//        }
    }
}