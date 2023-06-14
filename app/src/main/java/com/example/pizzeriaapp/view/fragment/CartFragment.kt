package com.example.pizzeriaapp.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzeriaapp.R
import com.example.pizzeriaapp.adapter.CartAdapter
import com.example.pizzeriaapp.databinding.FragmentCartBinding
import com.example.pizzeriaapp.model.DeliveryMethod
import com.example.pizzeriaapp.model.PaymentMethod
import com.example.pizzeriaapp.view.activity.AuthorizationActivity
import com.example.pizzeriaapp.viewmodel.CartViewModel

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
        cartRecyclerView.isNestedScrollingEnabled = false
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

        cartViewModel.orderPlacedLiveData.observe(viewLifecycleOwner) { isOrderPlaced ->
            if (isOrderPlaced) {
                // Clearing the list in the adapter
                cartAdapter.updateList(null)
                // Resetting orderPlacedLiveData
                cartViewModel.orderPlacedLiveData.value = false
            }
        }

        cartViewModel.userSignedOutLiveData.observe(viewLifecycleOwner) { isSignedOut ->
            if (isSignedOut) {
                val intent = Intent(activity, AuthorizationActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                requireActivity().startActivity(intent)
                requireActivity().overridePendingTransition(0, 0) // Turns off animation
                requireActivity().finish()
                requireActivity().overridePendingTransition(0, 0) // Turns off animation
            }
        }

        cartViewModel.loadCart()

        binding.selfDeliveryButton.isChecked = true
        binding.cashButton.isChecked = true

        binding.toggleButtonDeliveryMethod.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.self_deliveryButton -> {
                        binding.addressTextInputLayout.visibility = View.GONE // hide address input if self-delivery is selected
                    }
                    R.id.delivery_button -> {
                        binding.addressTextInputLayout.visibility = View.VISIBLE // show address input if delivery is selected
                    }
                }
            }
        }

        binding.orderButton.setOnClickListener{
            val deliveryMethod = when(binding.toggleButtonDeliveryMethod.checkedButtonId) {
                R.id.self_deliveryButton -> DeliveryMethod.PICK_UP
                R.id.delivery_button -> DeliveryMethod.DELIVERY
                else -> null // this case should not happen now, as we have a default selection
            }

            val paymentMethod = when(binding.toggleButtonPaymentMethod.checkedButtonId) {
                R.id.cash_button -> PaymentMethod.CASH_ON_DELIVERY
                R.id.card_button -> PaymentMethod.CARD_ON_ORDER
                else -> null // this case should not happen now, as we have a default selection
            }

            val address = binding.addressEditText.text.toString()

            // Validation for address field
            if (deliveryMethod == DeliveryMethod.DELIVERY && address.isBlank()) {
                binding.addressTextInputLayout.error = "Address cannot be blank"
                return@setOnClickListener
            } else if (deliveryMethod == DeliveryMethod.DELIVERY && address.length < 5) {
                binding.addressTextInputLayout.error = "Address is too short"
                return@setOnClickListener
            } else if (deliveryMethod == DeliveryMethod.DELIVERY && !address.any { it.isDigit() }) {
                binding.addressTextInputLayout.error = "Address must contain house number"
                return@setOnClickListener
            } else {
                binding.addressTextInputLayout.error = null // Clear the error
            }

            if (deliveryMethod != null && paymentMethod != null) {
                cartViewModel.placeOrder(deliveryMethod, paymentMethod, address)
            }
        }
    }
}