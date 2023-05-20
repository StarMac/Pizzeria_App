package com.example.pizzeriaapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
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
        private val orderProductsContainer: LinearLayout = itemView.findViewById(R.id.layout_order_products_container)

        fun bind(order: Order) {
            orderDate.text = order.creationTimestamp.toString()
            orderPrice.text = order.totalPrice.toString()
            orderStatus.text = order.status


            // Очистить предыдущие вьюшки продуктов, если таковые имеются
            orderProductsContainer.removeAllViews()

            // Добавить вьюшку для каждого продукта в заказе
            order.items?.forEach { orderItem ->
                // создание представления разделителя из xml-ресурса
                val dividerView = LayoutInflater.from(context).inflate(R.layout.divider_view, orderProductsContainer, false)
                // добавление представления разделителя в LinearLayout перед каждым элементом
                orderProductsContainer.addView(dividerView)

                val productView = LayoutInflater.from(context).inflate(R.layout.product_order_item, orderProductsContainer, false)

                val productName: TextView = productView.findViewById(R.id.txt_order_product_name)
                val productImage: ImageView = productView.findViewById(R.id.img_order_product_image)

                productName.text = orderItem.pizzaName
                Glide.with(context)
                    .load(orderItem.pizzaPhoto)
                    .error(R.drawable.ic_baseline_local_pizza_24)
                    .into(productImage)

                orderProductsContainer.addView(productView)
            }
        }
    }
}