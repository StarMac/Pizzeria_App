package com.example.pizzeriaapp.adapter

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzeriaapp.R
import com.example.pizzeriaapp.model.BanListUser
import com.google.firebase.firestore.FirebaseFirestore

class AdminBanListAdapter (private var banList: List<BanListUser>) : RecyclerView.Adapter<AdminBanListAdapter.AdminBanListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminBanListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.admin_banned_user_item,
            parent, false
        )
        return AdminBanListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AdminBanListViewHolder, position: Int) {
        val currentItem = banList[position]

        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return banList.size
    }

    fun updateBanList(newBanList: List<BanListUser>) {
        val diffCallback = BannedUserDiffCallback(banList, newBanList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        banList = newBanList
        diffResult.dispatchUpdatesTo(this)
    }

    class AdminBanListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val context: Context = itemView.context
        private val txtName: TextView = itemView.findViewById(R.id.txt_admin_banned_user_name)
        private val txtId: TextView = itemView.findViewById(R.id.txt_admin_banned_user_id)
        private val txtBanReason: TextView = itemView.findViewById(R.id.txt_admin_ban_reason)
        private val btnUnblockUser: Button = itemView.findViewById(R.id.btn_admin_unblock_user)

        fun bind(bannedUser: BanListUser) {
            txtName.text = "User name: ${bannedUser.userName}"
            txtId.text = "User id: ${bannedUser.uid}"
            txtBanReason.text = "Ban reason: ${bannedUser.banReason}"
            btnUnblockUser.setOnClickListener {
                showDialogUnblockUser(bannedUser)
            }
        }

        private fun showDialogUnblockUser(bannedUser: BanListUser) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_unblock_user, null)

            val txtUserName: TextView = dialogView.findViewById(R.id.txt_dialog_user_name)
            val txtUserId: TextView = dialogView.findViewById(R.id.txt_dialog_user_id)
            val txtBanReason: TextView = dialogView.findViewById(R.id.txt_dialog_ban_reason)
            val btnCancel: Button = dialogView.findViewById(R.id.btn_dialog_cancel)
            val btnConfirm: Button = dialogView.findViewById(R.id.btn_dialog_confirm)

            // Set user information to TextViews
            txtUserName.text = "User name: ${bannedUser.userName}"
            txtUserId.text = "User id: ${bannedUser.uid}"
            txtBanReason.text = "Ban reason: ${bannedUser.banReason}"

            // Build the AlertDialog
            val builder = AlertDialog.Builder(context).setView(dialogView)
            val alertDialog = builder.create()

            // Set listeners
            btnCancel.setOnClickListener { alertDialog.dismiss() }
            btnConfirm.setOnClickListener {
                unblockUser(bannedUser)
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

        private fun unblockUser(banListUser: BanListUser) {
            val db = FirebaseFirestore.getInstance()

            db.collection("BanList")
                .document(banListUser.uid!!)
                .delete()
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot with ID: ${banListUser.uid} successfully deleted!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        }
    }
}