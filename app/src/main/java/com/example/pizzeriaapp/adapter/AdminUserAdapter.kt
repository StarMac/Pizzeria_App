package com.example.pizzeriaapp.adapter

import android.app.AlertDialog
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
import com.example.pizzeriaapp.model.DeliveryMethod
import com.example.pizzeriaapp.model.Order
import com.example.pizzeriaapp.model.User
import com.google.firebase.firestore.FirebaseFirestore
import java.text.DateFormat
import java.util.*

class AdminUserAdapter (private var userList: List<User>) : RecyclerView.Adapter<AdminUserAdapter.AdminUserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminUserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.admin_user_item,
            parent, false
        )
        return AdminUserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AdminUserViewHolder, position: Int) {
        val currentItem = userList[position]

        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun updateUsers(newUserList: List<User>) {
        val diffCallback = UserDiffCallback(userList, newUserList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        userList = newUserList
        diffResult.dispatchUpdatesTo(this)
    }

    class AdminUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val context: Context = itemView.context
        private val txtEmail: TextView = itemView.findViewById(R.id.txt_admin_user_email)
        private val txtName: TextView = itemView.findViewById(R.id.txt_admin_user_name)
        private val txtId: TextView = itemView.findViewById(R.id.txt_admin_user_id)
        private val txtRole: TextView = itemView.findViewById(R.id.txt_admin_user_role)
        private val btnBanUser: ImageButton = itemView.findViewById(R.id.btn_admin_ban_user)
        private val btnChangeUserRole: ImageButton = itemView.findViewById(R.id.btn_admin_change_user_role)

        fun bind(user: User) {
            txtEmail.text = user.email
            txtName.text = "User name: ${user.name}"
            txtId.text = "User id: ${user.uid}"
            txtRole.text = "User role: ${user.role}"
            btnChangeUserRole.setOnClickListener{
                showDialogChangeUserRole(user)
            }
        }

        private fun showDialogChangeUserRole(user: User){
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_change_user_role, null)

            val radioGroup: RadioGroup = dialogView.findViewById(R.id.role_radio_group)
            val btnCancel: Button = dialogView.findViewById(R.id.btn_dialog_cancel)
            val btnConfirm: Button = dialogView.findViewById(R.id.btn_dialog_confirm)

            // Set current role
            when (user.role) {
                "Client" -> radioGroup.check(R.id.radio_client)
                "Employee" -> radioGroup.check(R.id.radio_employee)
                "Admin" -> radioGroup.check(R.id.radio_admin)
            }

            // Build the AlertDialog
            val builder = AlertDialog.Builder(context).setView(dialogView)
            val alertDialog = builder.create()

            // Set listeners
            btnCancel.setOnClickListener { alertDialog.dismiss() }
            btnConfirm.setOnClickListener {
                val checkedId = radioGroup.checkedRadioButtonId
                val newRole = when (checkedId) {
                    R.id.radio_client -> "Client"
                    R.id.radio_employee -> "Employee"
                    R.id.radio_admin -> "Admin"
                    else -> user.role
                }
                changeUserRole(user, newRole!!)
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

        private fun changeUserRole(user: User, newRole: String) {
            val db = FirebaseFirestore.getInstance()

            db.collection("User")
                .document(user.uid!!)
                .update("role", newRole)
                .addOnSuccessListener { Log.d(TAG, "User role updated with ID: ${user.uid}") }
                .addOnFailureListener { e -> Log.w(TAG, "Error updating user role", e) }
        }
    }
}