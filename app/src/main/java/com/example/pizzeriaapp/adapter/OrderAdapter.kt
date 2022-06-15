package com.example.pizzeriaapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pizzeriaapp.R
import com.example.pizzeriaapp.model.Order

class OrderAdapter (private val orderList : ArrayList<Order>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.order_item,
            parent, false)
        return OrderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val currentItem = orderList[position]

        holder.orderDate.text = currentItem.time
        holder.orderPrice.text = currentItem.price
        holder.orderStatus.text = currentItem.status
        holder.orderProductName.text = currentItem.pizzaName
        Glide.with(holder.context)
            .load(currentItem.PizzaPhoto)
            .error(R.drawable.ic_baseline_local_pizza_24)
            .into(holder.orderProductImage)

    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    class OrderViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        val context = itemView.context
        val orderDate : TextView = itemView.findViewById(R.id.txt_order_date)
        val orderPrice : TextView = itemView.findViewById(R.id.txt_order_price)
        val orderStatus : TextView = itemView.findViewById(R.id.txt_order_status)
        val orderProductName : TextView = itemView.findViewById(R.id.txt_order_product_name)
        val orderProductImage : ImageView = itemView.findViewById(R.id.img_order_product_image)
    }
}