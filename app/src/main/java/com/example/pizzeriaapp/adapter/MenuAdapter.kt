package com.example.pizzeriaapp.adapter

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pizzeriaapp.R
import com.example.pizzeriaapp.model.OrderItem
import com.example.pizzeriaapp.model.Pizza
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


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
            productPrice.text = item.price.toString()
            Glide.with(context)
                .load(item.photo)
                .error(R.drawable.ic_baseline_local_pizza_24)
                .into(productPhoto)
            productAddToCartButton.setOnClickListener {
                showDialogAddToCart(item)
            }
        }

        private fun showDialogAddToCart(item: Pizza){

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
            pizzaTotalPrice.text = item.price.toString()
            var totalPrice = item.price!!

            decreaseButton.setOnClickListener {
                val count = quantity.text.toString().toInt()
                if (count > 1) {
                    quantity.text = (count - 1).toString()
                    totalPrice -= item.price
                    pizzaTotalPrice.text = totalPrice.toString()
                }
            }

            increaseButton.setOnClickListener {
                val count = quantity.text.toString().toInt()
                quantity.text = (count + 1).toString()
                totalPrice += item.price
                pizzaTotalPrice.text = totalPrice.toString()
            }

            val builder = AlertDialog.Builder(context).setView(dialogView)
            val alertDialog = builder.create()

            cancelButton.setOnClickListener {
                alertDialog.dismiss()
            }

            addToCartButton.setOnClickListener {
                addToCart(item, quantity.text.toString().toInt())
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

        private fun addToCart(item: Pizza, quantity : Int){
            val db = FirebaseFirestore.getInstance()
            val pizzaId = item.id // The id of the pizza that the user wants to add to cart

            val preOrderRef = db.collection("User")
                .document(FirebaseAuth.getInstance().currentUser?.uid!!)
                .collection("PreOrder")
                .document(pizzaId!!) // Use the pizza id as the document id

            preOrderRef.get().addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // If the document already exists, then increase the amount of pizza
                    val currentQuantity = documentSnapshot.getLong("quantity")?.toInt() ?: 0
                    preOrderRef.update("quantity", currentQuantity + quantity)
                    Toast.makeText(context, "Quantity updated!", Toast.LENGTH_SHORT).show()
                } else {
                    // If the document does not exist, then create a new pre-order item with this pizza
                    val newPreOrderItem =
                        OrderItem(pizzaId, item.name, item.photo, item.price, quantity)
                    preOrderRef.set(newPreOrderItem)
                    Toast.makeText(context, "Item added to cart!", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                // Error handling
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}