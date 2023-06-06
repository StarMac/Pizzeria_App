package com.example.pizzeriaapp.adapter

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
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

        private fun showDialogEdit(pizza : Pizza) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_new_product, null)

            val txtProductName: EditText = dialogView.findViewById(R.id.product_name_editText)
            val txtProductDescription: EditText = dialogView.findViewById(R.id.product_description_editText)
            val txtProductPhotoUrl: EditText = dialogView.findViewById(R.id.product_photo_editText)
            val txtProductPrice: EditText = dialogView.findViewById(R.id.product_price_editText)

            val btnCancel: Button = dialogView.findViewById(R.id.btn_dialog_cancel)
            val btnConfirm: Button = dialogView.findViewById(R.id.btn_dialog_confirm)

            // Set pizza information to EditTexts
            txtProductName.setText(pizza.name)
            txtProductDescription.setText(pizza.description)
            txtProductPhotoUrl.setText(pizza.photo)
            txtProductPrice.setText(pizza.price?.toString())

            // Build the AlertDialog
            val builder = AlertDialog.Builder(context).setView(dialogView)
            val alertDialog = builder.create()

            // Set listeners
            btnCancel.setOnClickListener { alertDialog.dismiss() }
            btnConfirm.setOnClickListener {
                val newPizzaName = txtProductName.text.toString()
                val newPizzaDescription = txtProductDescription.text.toString()
                val newPizzaPhotoUrl = txtProductPhotoUrl.text.toString()
                val newPizzaPrice = txtProductPrice.text.toString().toIntOrNull()

                if (newPizzaName.isNotBlank() && newPizzaDescription.isNotBlank() &&
                    newPizzaPhotoUrl.isNotBlank() && newPizzaPrice != null) {
                    updatePizza(pizza.id!!, newPizzaName, newPizzaPrice, newPizzaPhotoUrl, newPizzaDescription)
                    alertDialog.dismiss()
                } else {
                    if (newPizzaName.isBlank()) txtProductName.error = "Name cannot be empty"
                    if (newPizzaPrice == null) txtProductPrice.error = "Price cannot be empty"
                    if (newPizzaPhotoUrl.isBlank()) txtProductPhotoUrl.error = "Photo URL cannot be empty"
                    if (newPizzaDescription.isBlank()) txtProductDescription.error = "Description cannot be empty"
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

        private fun updatePizza(pizzaId: String, name: String, price: Int, photoUrl: String, description: String) {
            val db = FirebaseFirestore.getInstance()

            val updatedPizza = Pizza(pizzaId, name, price, photoUrl, description)

            db.collection("Pizza")
                .document(pizzaId)
                .set(updatedPizza)
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully updated!")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error updating document", e)
                }
        }

        private fun showDialogDelete(item: Pizza) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_delete_product, null)

            val btnCancel: Button = dialogView.findViewById(R.id.btn_dialog_cancel)
            val btnConfirm: Button = dialogView.findViewById(R.id.btn_dialog_confirm)

            // Build the AlertDialog
            val builder = AlertDialog.Builder(context).setView(dialogView)
            val alertDialog = builder.create()

            // Set listeners
            btnCancel.setOnClickListener { alertDialog.dismiss() }
            btnConfirm.setOnClickListener {
                deletePizza(item.id!!)
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

        private fun deletePizza(pizzaId: String) {
            val db = FirebaseFirestore.getInstance()

            db.collection("Pizza")
                .document(pizzaId)
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully deleted!")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error deleting document", e)
                }
        }

    }
}