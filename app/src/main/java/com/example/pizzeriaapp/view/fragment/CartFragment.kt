package com.example.pizzeriaapp.view.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzeriaapp.adapter.CartAdapter
import com.example.pizzeriaapp.databinding.FragmentCartBinding
import com.example.pizzeriaapp.viewmodel.CartViewModel
import com.example.pizzeriaapp.viewmodel.MenuViewModel

class CartFragment : BaseFragment<FragmentCartBinding>(FragmentCartBinding::inflate) {
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var cartViewModel: CartViewModel
    private lateinit var cartAdapter: CartAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        cartViewModel = ViewModelProvider(this)[CartViewModel::class.java]
        cartRecyclerView = binding.cartRecycleView
        cartRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
        cartAdapter = CartAdapter(ArrayList(), cartViewModel)
        cartRecyclerView.adapter = cartAdapter

        cartViewModel.cartLiveData.observe(viewLifecycleOwner) { products ->
            cartAdapter.updateList(products)
        }
        cartViewModel.totalPriceLiveData.observe(viewLifecycleOwner) { totalPrice ->
            binding.totalPriceValue.text = totalPrice.toString()
            if(totalPrice == 0) {
                binding.cardView.visibility = View.GONE
            }else{
                binding.cardView.visibility = View.VISIBLE
            }
        }

        cartViewModel.orderPlacedLiveData.observe(viewLifecycleOwner, Observer { isOrderPlaced ->
            if (isOrderPlaced) {
                // Очищаем список в адаптере
                cartAdapter.updateList(null)
                // Сбрасываем orderPlacedLiveData
                cartViewModel.orderPlacedLiveData.value = false
            }
        })

        cartViewModel.loadCart()

        binding.orderButton.setOnClickListener{cartViewModel.placeOrder()}
    }
}