package com.example.pizzeriaapp.adapter

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pizzeriaapp.R
import com.example.pizzeriaapp.model.Pizza


class MenuAdapter (private var productList : List<Pizza>) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.menu_item,
            parent, false
        )
        return MenuViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val currentItem = productList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    fun updateProducts(newList: List<Pizza>) {
        productList = newList
        notifyDataSetChanged()
    }

    class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val context = itemView.context
        private val productName: TextView = itemView.findViewById(R.id.txt_product_name)
        private val productDesc: TextView = itemView.findViewById(R.id.txt_product_description)
        private val productPrice: TextView = itemView.findViewById(R.id.txt_product_price)
        private val productPhoto: ImageView = itemView.findViewById(R.id.img_product_image)
        private val productAddToCartButton: Button = itemView.findViewById(R.id.btn_product_addToCart)

        fun bind(item: Pizza) {
            productName.text = item.name
            productDesc.text = item.description
            productPrice.text = item.price
            Glide.with(context)
                .load(item.photo)
                .error(R.drawable.ic_baseline_local_pizza_24)
                .into(productPhoto)
            productAddToCartButton.setOnClickListener {
                // TODO: Handle add to cart button click
                // Create a new document reference

                val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_to_cart, null)

                val pizzaName: TextView = dialogView.findViewById(R.id.txt_dialog_pizza_name)
                val pizzaDesc: TextView = dialogView.findViewById(R.id.txt_dialog_pizza_description)
                val pizzaTotalPrice: TextView = dialogView.findViewById(R.id.txt_dialog_pizza_total_price)
                val quantity: TextView = dialogView.findViewById(R.id.txt_dialog_quantity)
                val decreaseButton: ImageButton = dialogView.findViewById(R.id.btn_dialog_decrease)
                val increaseButton: ImageButton = dialogView.findViewById(R.id.btn_dialog_increase)
                val addToCartButton: Button = dialogView.findViewById(R.id.btn_dialog_add_to_cart)
                val cancelButton: Button = dialogView.findViewById(R.id.btn_dialog_cancel)

                pizzaName.text = item.name
                pizzaDesc.text = item.description
                pizzaTotalPrice.text = item.price

                decreaseButton.setOnClickListener {
                    val count = quantity.text.toString().toInt()
                    if (count > 1) {
                        quantity.text = (count - 1).toString()
                    }
                }

                increaseButton.setOnClickListener {
                    val count = quantity.text.toString().toInt()
                    quantity.text = (count + 1).toString()
                }

                val builder = AlertDialog.Builder(context).setView(dialogView)
                val alertDialog = builder.create()

                cancelButton.setOnClickListener {
                    alertDialog.dismiss()
                }

                addToCartButton.setOnClickListener {
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

//                val newOrderRef = Firebase.firestore.collection("Order").document()
//                val auth: FirebaseAuth = Firebase.auth
//                val orderItem = OrderItem(pizzaId = item.id, pizzaName = item.name, quantity = 1, pizzaPhoto = item.photo) // Replace "pizzaId" with actual pizza id
//                val orderItem2 = OrderItem(pizzaId = item.id, pizzaName = item.name, quantity = 2, pizzaPhoto = item.photo) // Replace "pizzaId" with actual pizza id
//                val order = Order(
//                    id = newOrderRef.id,  // Set id from the new document reference
//                    clientUid = auth.currentUser!!.uid,
//                    items = listOf(orderItem, orderItem2),
//                    totalPrice = 10, // replace this with actual total price
//                    status = "В обработке",
//                    creationTimestamp = System.currentTimeMillis()
//                )
//
//                // Set the order to the new document reference
//                newOrderRef.set(order)
//                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
//                    .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
            }
        }
    }
}