package com.example.pizzeriaapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pizzeriaapp.R
import com.example.pizzeriaapp.model.OrderItem
import com.example.pizzeriaapp.viewmodel.CartViewModel

class CartAdapter(private var productList: List<OrderItem>, private val cartViewModel: CartViewModel) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.cart_item,
            parent, false
        )
        return CartViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val currentItem = productList[position]
        holder.bind(currentItem, cartViewModel)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    fun updateList(newList: List<OrderItem>?) {
        productList = newList ?: emptyList()
        notifyDataSetChanged()
    }

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val context = itemView.context
        private val productName: TextView = itemView.findViewById(R.id.txt_cart_product_name)
        private val productQuantity: TextView = itemView.findViewById(R.id.txt_cart_quantity)
        private val productPrice: TextView = itemView.findViewById(R.id.txt_cart_product_price)
        private val productPhoto: ImageView = itemView.findViewById(R.id.img_cart_product_image)
        private val increaseButton: ImageButton = itemView.findViewById(R.id.btn_cart_increase)
        private val decreaseButton: ImageButton = itemView.findViewById(R.id.btn_cart_decrease)
        private val deleteButton: ImageView = itemView.findViewById(R.id.btn_cart_delete)

        fun bind(item: OrderItem, cartViewModel: CartViewModel) {
            productName.text = item.pizzaName
            productQuantity.text = item.quantity.toString()
            if(item.pizzaPrice != null && item.quantity != null){
                productPrice.text = (item.pizzaPrice * item.quantity).toString() // Update accordingly based on your data model
            }
            Glide.with(context)
                .load(item.pizzaPhoto)
                .error(R.drawable.ic_baseline_local_pizza_24)
                .into(productPhoto)

            increaseButton.setOnClickListener {
                // Calling a function in CartViewModel to increase the number of product
                cartViewModel.increaseQuantity(item)
            }

            decreaseButton.setOnClickListener {
                // Calling a function in CartViewModel to decrease the number of product
                cartViewModel.decreaseQuantity(item)
            }

            deleteButton.setOnClickListener {
                // Calling a function in CartViewModel to remove an item from the cart
                cartViewModel.removeItemFromCart(item)
            }
        }
    }
}