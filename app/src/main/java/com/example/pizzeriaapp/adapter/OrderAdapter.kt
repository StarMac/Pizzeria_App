package com.example.pizzeriaapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pizzeriaapp.R
import com.example.pizzeriaapp.model.Order

class OrderAdapter (private var orderList : List<Order>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.order_item,
            parent, false)
        return OrderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val currentItem = orderList[position]

        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    fun updateOrders(newOrderList: List<Order>) {
        orderList = newOrderList
        notifyDataSetChanged()
    }

    class OrderViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        private val context: Context = itemView.context
        private val orderDate: TextView = itemView.findViewById(R.id.txt_order_date)
        private val orderPrice: TextView = itemView.findViewById(R.id.txt_order_price)
        private val orderStatus: TextView = itemView.findViewById(R.id.txt_order_status)
        private val orderProductName: TextView = itemView.findViewById(R.id.txt_order_product_name)
        private val orderProductImage: ImageView = itemView.findViewById(R.id.img_order_product_image)

        fun bind(order: Order) {
            orderDate.text = order.time
            orderPrice.text = order.price
            orderStatus.text = order.status
            orderProductName.text = order.pizzaName
            Glide.with(context)
                .load(order.pizzaPhoto)
                .error(R.drawable.ic_baseline_local_pizza_24)
                .into(orderProductImage)
        }
    }
}