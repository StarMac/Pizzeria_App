package com.example.pizzeriaapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pizzeriaapp.R
import com.example.pizzeriaapp.model.Order
import com.example.pizzeriaapp.model.Pizza
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ProductAdapter (private val productList : ArrayList<Pizza>) : RecyclerView.Adapter<ProductAdapter.ProductsViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.product_item,
        parent, false)
        return ProductsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val currentItem = productList[position]
        holder.productName.text = currentItem.name
        holder.productDesc.text = currentItem.description
        holder.productPrice.text = currentItem.price
        Glide.with(holder.context)
            .load(currentItem.photo)
            .error(R.drawable.ic_baseline_local_pizza_24)
            .into(holder.productPhoto)
        holder.productAddToCartButton.setOnClickListener {
            //TODO Test
            val orderDatabaseRef: DatabaseReference = Firebase.database.getReference("Order")
            val auth = Firebase.auth
            val sdf = SimpleDateFormat("dd MM yyyy HH:mm:ss")
            val calendar = Calendar.getInstance()
            val orderDate = sdf.format(calendar.time)
            val order = Order(orderDate,currentItem.name,currentItem.photo,"Status: Waiting",currentItem.price + " uah",auth.currentUser!!.uid)
            orderDatabaseRef.child(order.time + " " + order.uid).setValue(order)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    class ProductsViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView){
        val context = itemView.context
        val productName : TextView = itemView.findViewById(R.id.txt_product_name)
        val productDesc : TextView = itemView.findViewById(R.id.txt_product_description)
        val productPrice : TextView = itemView.findViewById(R.id.txt_product_price)
        val productPhoto : ImageView = itemView.findViewById(R.id.img_product_image)
        val productAddToCartButton : Button = itemView.findViewById(R.id.btn_product_addToCart)
    }
}