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
import com.example.pizzeriaapp.model.Pizza

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