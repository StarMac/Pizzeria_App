package com.example.pizzeriaapp.adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pizzeriaapp.R
import com.example.pizzeriaapp.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
        val diffCallback = OrderDiffCallback(orderList, newOrderList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        orderList = newOrderList
        diffResult.dispatchUpdatesTo(this)
    }

    class EmployeeOrderViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        private val context: Context = itemView.context
        private val orderDate: TextView = itemView.findViewById(R.id.txt_emp_order_date)
        private val orderUsername: TextView = itemView.findViewById(R.id.txt_emp_order_user_name)
        private val orderUserId: TextView = itemView.findViewById(R.id.txt_emp_order_user_id)
        private val orderPrice: TextView = itemView.findViewById(R.id.txt_emp_order_price)
        private val orderStatus: TextView = itemView.findViewById(R.id.txt_emp_order_status)
        private val orderDeliveryMethod: TextView = itemView.findViewById(R.id.txt_emp_order_delivery_method)
        private val orderPaymentMethod: TextView = itemView.findViewById(R.id.txt_emp_order_payment_method)
        private val orderDeliveryAddress: TextView = itemView.findViewById(R.id.txt_emp_order_delivery_address)
        private val orderProductsContainer: LinearLayout = itemView.findViewById(R.id.layout_emp_order_products_container)
        private val btnBanUser: ImageButton = itemView.findViewById(R.id.btn_emp_order_ban_user)
        private val btnDeclineOrder: ImageButton = itemView.findViewById(R.id.btn_emp_order_decline)
        private val btnRedoOrder: ImageButton = itemView.findViewById(R.id.btn_emp_order_redo)
        private val btnChangeOrderStatus: ImageButton = itemView.findViewById(R.id.btn_emp_order_change_status)
        fun bind(order: Order) {
            btnBanUser.setOnClickListener {
                showDialogBlockUser(order.clientUid!!, order.clientName!!) // Pass the User who made the order
            }
            btnDeclineOrder.setOnClickListener{
                showDialogCancelOrder(order)
            }
            btnRedoOrder.setOnClickListener{
                showDialogRedoOrder(order)
            }
            btnChangeOrderStatus.setOnClickListener{
                showDialogChangeOrderStatus(order)
            }

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

            if (order.status == OrderStatus.PENDING.name){
                btnRedoOrder.visibility = View.GONE
            } else {
                btnRedoOrder.visibility = View.VISIBLE
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
                val productQuantity: TextView = productView.findViewById(R.id.txt_order_product_quantity)
                val productImage: ImageView = productView.findViewById(R.id.img_order_product_image)

                productName.text = orderItem.pizzaName
                productQuantity.text = orderItem.quantity.toString()
                Glide.with(context)
                    .load(orderItem.pizzaPhoto)
                    .error(R.drawable.ic_baseline_local_pizza_24)
                    .into(productImage)

                orderProductsContainer.addView(productView)
            }
        }

        private fun showDialogBlockUser(userId: String, userName: String){
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_block_user, null)

            val txtUserName: TextView = dialogView.findViewById(R.id.txt_dialog_user_name)
            val txtUserId: TextView = dialogView.findViewById(R.id.txt_dialog_user_id)
            val banReasonEditText: EditText = dialogView.findViewById(R.id.ban_reason_editText)
            val btnCancel: Button = dialogView.findViewById(R.id.btn_dialog_cancel)
            val btnConfirm: Button = dialogView.findViewById(R.id.btn_dialog_confirm)

            // Set user information to TextViews
            txtUserName.text = "User name: $userName"
            txtUserId.text = "User id: $userId"

            // Build the AlertDialog
            val builder = AlertDialog.Builder(context).setView(dialogView)
            val alertDialog = builder.create()

            // Set listeners
            btnCancel.setOnClickListener { alertDialog.dismiss() }
            btnConfirm.setOnClickListener {
                val banReason = banReasonEditText.text.toString()
                if (banReason.isNotBlank()) {
                    banUser(userId, userName, banReason)
                    alertDialog.dismiss()
                } else {
                    banReasonEditText.error = "Ban reason cannot be empty"
                    // Show error: Ban reason can't be blank
                }
            }

            val window = alertDialog.window
            if (window != null) {
                val params = WindowManager.LayoutParams().apply {
                    copyFrom(window.attributes)
                    width = WindowManager.LayoutParams.MATCH_PARENT
                    height = WindowManager.LayoutParams.WRAP_CONTENT
                    gravity = Gravity.CENTER
                }
                window.attributes = params
                window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }

            alertDialog.show()
        }

        private fun banUser(userId: String, userName: String, banReason: String) {
            val db = FirebaseFirestore.getInstance()

            val banListUser = BanListUser(userId, userName, banReason)

            db.collection("BanList")
                .add(banListUser)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }

        private fun showDialogCancelOrder(order: Order) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_cancel_order, null)

            val txtCancelOrder: TextView = dialogView.findViewById(R.id.txt_cancel_order)
            val btnCancel: Button = dialogView.findViewById(R.id.btn_dialog_cancel)
            val btnConfirm: Button = dialogView.findViewById(R.id.btn_dialog_confirm)

            // Set order information to TextView
            txtCancelOrder.text = "Cancel the order with ID: ${order.id}?"

            // Build the AlertDialog
            val builder = AlertDialog.Builder(context).setView(dialogView)
            val alertDialog = builder.create()

            // Set listeners
            btnCancel.setOnClickListener { alertDialog.dismiss() }
            btnConfirm.setOnClickListener {
                cancelOrder(order)
                alertDialog.dismiss()
            }

            val window = alertDialog.window
            if (window != null) {
                val params = WindowManager.LayoutParams().apply {
                    copyFrom(window.attributes)
                    width = WindowManager.LayoutParams.MATCH_PARENT
                    height = WindowManager.LayoutParams.WRAP_CONTENT
                    gravity = Gravity.CENTER
                }
                window.attributes = params
                window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }

            alertDialog.show()
        }

        private fun cancelOrder(order: Order) {
            val db = FirebaseFirestore.getInstance()

            val orderRef = db.collection("Order").document(order.id!!)

            orderRef.update("status", OrderStatus.CANCELED.name)
                .addOnSuccessListener {
                    Log.d(TAG, "Order with ID ${order.id} successfully updated.")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error updating order", e)
                }
        }

        private fun showDialogRedoOrder(order: Order) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_redo_order, null)

            val txtRedoOrder: TextView = dialogView.findViewById(R.id.txt_redo_order)
            val btnCancel: Button = dialogView.findViewById(R.id.btn_dialog_cancel)
            val btnConfirm: Button = dialogView.findViewById(R.id.btn_dialog_confirm)

            // Set order information to TextView
            txtRedoOrder.text = "Redo the order with ID: ${order.id}?"

            // Build the AlertDialog
            val builder = AlertDialog.Builder(context).setView(dialogView)
            val alertDialog = builder.create()

            // Set listeners
            btnCancel.setOnClickListener { alertDialog.dismiss() }
            btnConfirm.setOnClickListener {
                redoOrder(order)
                alertDialog.dismiss()
            }

            val window = alertDialog.window
            if (window != null) {
                val params = WindowManager.LayoutParams().apply {
                    copyFrom(window.attributes)
                    width = WindowManager.LayoutParams.MATCH_PARENT
                    height = WindowManager.LayoutParams.WRAP_CONTENT
                    gravity = Gravity.CENTER
                }
                window.attributes = params
                window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }

            alertDialog.show()
        }

        private fun redoOrder(order: Order) {
            val db = FirebaseFirestore.getInstance()

            val orderRef = db.collection("Order").document(order.id!!)

            orderRef.update("status", OrderStatus.PENDING.name)
                .addOnSuccessListener {
                    Log.d(TAG, "Order with ID ${order.id} successfully redone.")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error redoing order", e)
                }
        }

        private fun showDialogChangeOrderStatus(order: Order) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_change_order_status, null)

            val txtChangeStatus: TextView = dialogView.findViewById(R.id.txt_change_status)
            val btnCancel: Button = dialogView.findViewById(R.id.btn_dialog_cancel)
            val btnConfirm: Button = dialogView.findViewById(R.id.btn_dialog_confirm)

            // Set order information to TextView
            txtChangeStatus.text = "Change order status to the next stage for Order ID: ${order.id}?"

            // Build the AlertDialog
            val builder = AlertDialog.Builder(context).setView(dialogView)
            val alertDialog = builder.create()

            // Set listeners
            btnCancel.setOnClickListener { alertDialog.dismiss() }
            btnConfirm.setOnClickListener {
                changeOrderStatus(order)
                alertDialog.dismiss()
            }

            val window = alertDialog.window
            if (window != null) {
                val params = WindowManager.LayoutParams().apply {
                    copyFrom(window.attributes)
                    width = WindowManager.LayoutParams.MATCH_PARENT
                    height = WindowManager.LayoutParams.WRAP_CONTENT
                    gravity = Gravity.CENTER
                }
                window.attributes = params
                window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }

            alertDialog.show()
        }

        private fun changeOrderStatus(order: Order) {
            val db = FirebaseFirestore.getInstance()

            val orderRef = db.collection("Order").document(order.id!!)

            val currentStatus = order.statusFromString(order.status!!)
            val nextStatus = when (currentStatus) {
                OrderStatus.PENDING -> OrderStatus.PREPARING
                OrderStatus.PREPARING -> if (order.deliveryMethodFromString(order.deliveryMethod!!) == DeliveryMethod.DELIVERY)
                    OrderStatus.DELIVERING
                else OrderStatus.READY_FOR_PICKUP
                OrderStatus.DELIVERING, OrderStatus.READY_FOR_PICKUP -> OrderStatus.DELIVERED
                else -> null
            }

            if (nextStatus != null) {
                orderRef.update("status", nextStatus.name)
                    .addOnSuccessListener {
                        Log.d(TAG, "Order with ID ${order.id} successfully updated.")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error updating order", e)
                    }
            }
        }
    }
}