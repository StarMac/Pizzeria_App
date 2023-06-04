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
import com.example.pizzeriaapp.model.DeliveryMethod
import com.example.pizzeriaapp.model.Order
import java.text.DateFormat
import java.util.*

class EmployeeOrderAdapter (private var orderList : List<Order>) : RecyclerView.Adapter<EmployeeOrderAdapter.EmployeeOrderViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeOrderViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.employee_order_item,
            parent, false)
        return EmployeeOrderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EmployeeOrderViewHolder, position: Int) {
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

    class EmployeeOrderViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        private val context: Context = itemView.context
        private val orderDate: TextView = itemView.findViewById(R.id.txt_emp_order_date)
        private val orderUsername: TextView = itemView.findViewById(R.id.txt_emp_order_user_name)
        private val orderUserId: TextView = itemView.findViewById(R.id.txt_emp_order_user_id)
        private val orderPrice: TextView = itemView.findViewById(R.id.txt_emp_order_price)
        private val orderStatus: TextView = itemView.findViewById(R.id.txt_emp_order_status)
        private val orderDeliveryMethod: TextView =
            itemView.findViewById(R.id.txt_emp_order_delivery_method)
        private val orderPaymentMethod: TextView =
            itemView.findViewById(R.id.txt_emp_order_payment_method)
        private val orderDeliveryAddress: TextView =
            itemView.findViewById(R.id.txt_emp_order_delivery_address)
        private val orderProductsContainer: LinearLayout =
            itemView.findViewById(R.id.layout_emp_order_products_container)

        fun bind(order: Order) {
            val date = Date(order.creationTimestamp!!)
            orderDate.text = DateFormat.getDateTimeInstance().format(date)
            orderPrice.text = "Price: ${order.totalPrice.toString()}"

            // преобразование строки в перечисление
            val status = order.statusFromString(order.status!!)
            val deliveryMethod = order.deliveryMethodFromString(order.deliveryMethod!!)
            val paymentMethod = order.paymentMethodFromString(order.paymentMethod!!)

            // использование значений перечисления
            orderStatus.text = "Status: ${status?.status ?:"Unknown status"}"
            orderDeliveryMethod.text = "Delivery Method: ${deliveryMethod?.method ?: "Unknown method"}"
            orderPaymentMethod.text = "Payment Method: ${paymentMethod?.method ?: "Unknown method"}"

            orderUsername.text = "Username: ${order.clientName}"
            val shortId = order.clientUid?.takeLast(5) ?: "Unknown id"
            orderUserId.text = "User id: $shortId"

            // Check the delivery method
            if (order.deliveryMethod == DeliveryMethod.DELIVERY.name) {
                // If delivery, show the delivery address
                orderDeliveryAddress.visibility = View.VISIBLE
                orderDeliveryAddress.text = "Delivery Address: ${order.deliveryAddress}"
            } else {
                // If pick up, hide the delivery address
                orderDeliveryAddress.visibility = View.GONE
            }

            // Очистить предыдущие вьюшки продуктов, если таковые имеются
            orderProductsContainer.removeAllViews()

            // Добавить вьюшку для каждого продукта в заказе
            order.items?.forEach { orderItem ->
                // создание представления разделителя из xml-ресурса
                val dividerView = LayoutInflater.from(context)
                    .inflate(R.layout.divider_view, orderProductsContainer, false)
                // добавление представления разделителя в LinearLayout перед каждым элементом
                orderProductsContainer.addView(dividerView)

                val productView = LayoutInflater.from(context)
                    .inflate(R.layout.product_order_item, orderProductsContainer, false)

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