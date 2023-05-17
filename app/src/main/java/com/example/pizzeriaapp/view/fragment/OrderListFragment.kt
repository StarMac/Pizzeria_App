package com.example.pizzeriaapp.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzeriaapp.adapter.OrderAdapter
import com.example.pizzeriaapp.databinding.FragmentOrderListBinding
import com.example.pizzeriaapp.viewmodel.OrderListViewModel

class OrderListFragment : BaseFragment<FragmentOrderListBinding>(FragmentOrderListBinding::inflate) {
    private lateinit var orderRecyclerView: RecyclerView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var orderListViewModel: OrderListViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        orderRecyclerView = binding.orderListRecycleView
        orderRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        orderAdapter = OrderAdapter(ArrayList())
        orderRecyclerView.adapter = orderAdapter

        orderListViewModel = ViewModelProvider(this)[OrderListViewModel::class.java]

        orderListViewModel.ordersLiveData.observe(viewLifecycleOwner) { orderList ->
            orderAdapter.updateOrders(orderList)
        }

        orderListViewModel.loadOrders()
    }
}