package com.example.pizzeriaapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pizzeriaapp.R
import com.example.pizzeriaapp.model.Order
import com.example.pizzeriaapp.model.OrderItem
import com.example.pizzeriaapp.model.Pizza
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase


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
                val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Order")
                val auth: FirebaseAuth = Firebase.auth
                val orderItem = OrderItem(pizzaId = itemId.toString(), pizzaName = item.name, quantity = 1, pizzaPhoto = item.photo) // Replace "pizzaId" with actual pizza id
                val orderItem2 = OrderItem(pizzaId = "testId", pizzaName = item.name, quantity = 2, pizzaPhoto = item.photo) // Replace "pizzaId" with actual pizza id
                val order = Order(
                    id = databaseReference.push().key,
                    clientUid = auth.currentUser!!.uid,
                    items = listOf(orderItem, orderItem2),
                    totalPrice = 10, // replace this with actual total price
                    status = "В обработке",
                    creationTimestamp = System.currentTimeMillis()
                )
                val orderId = databaseReference.push().key
                if (orderId != null) {
                    databaseReference.child(orderId).setValue(order)
                }
//                val orderDatabaseRef: DatabaseReference = Firebase.database.getReference("Order")
//                val auth = Firebase.auth
//                val sdf = SimpleDateFormat("dd MM yyyy HH:mm:ss")
//                val calendar = Calendar.getInstance()
//                val orderDate = sdf.format(calendar.time)
//                val order = Order(
//                    orderDate,
//                    item.name,
//                    item.photo,
//                    "Waiting",
//                    item.price + " uah",
//                    auth.currentUser!!.uid
//                )
//                orderDatabaseRef.child(order.time + " " + order.clientUid).setValue(order)
            }
        }
    }
}