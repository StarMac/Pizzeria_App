package com.example.pizzeriaapp.adapter

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pizzeriaapp.R
import com.example.pizzeriaapp.model.OrderItem
import com.example.pizzeriaapp.model.Pizza
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AdminMenuAdapter (private var productList : List<Pizza>) : RecyclerView.Adapter<AdminMenuAdapter.AdminMenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminMenuViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.admin_menu_item,
            parent, false
        )
        return AdminMenuViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AdminMenuViewHolder, position: Int) {
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

    class AdminMenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val context = itemView.context
        private val productName: TextView = itemView.findViewById(R.id.txt_admin_product_name)
        private val productDesc: TextView = itemView.findViewById(R.id.txt_admin_product_description)
        private val productPrice: TextView = itemView.findViewById(R.id.txt_admin_product_price)
        private val productPhoto: ImageView = itemView.findViewById(R.id.img_admin_product_image)
        private val productEditButton: Button = itemView.findViewById(R.id.btn_admin_product_edit)
        private val productDeleteButton: Button = itemView.findViewById(R.id.btn_admin_product_delete)

        fun bind(item: Pizza) {
            productName.text = item.name
            productDesc.text = item.description
            productPrice.text = item.price.toString()
            Glide.with(context)
                .load(item.photo)
                .error(R.drawable.ic_baseline_local_pizza_24)
                .into(productPhoto)

            productEditButton.setOnClickListener {
                showDialogEdit(item)
            }

            productDeleteButton.setOnClickListener {
                showDialogDelete(item)
            }
        }

        private fun showDialogEdit(item: Pizza) {
            TODO("Not yet implemented")
        }

        private fun showDialogDelete(item: Pizza) {
            TODO("Not yet implemented")
        }

    }
}