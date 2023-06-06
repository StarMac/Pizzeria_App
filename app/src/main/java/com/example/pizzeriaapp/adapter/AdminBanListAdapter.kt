package com.example.pizzeriaapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.pizzeriaapp.R
import com.example.pizzeriaapp.model.BanListUser

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
        private val txtName: TextView = itemView.findViewById(R.id.txt_admin_banned_user_name)
        private val txtId: TextView = itemView.findViewById(R.id.txt_admin_banned_user_id)
        private val txtBanReason: TextView = itemView.findViewById(R.id.txt_admin_ban_reason)
        private val btnUnblockUser: Button = itemView.findViewById(R.id.btn_admin_unblock_user)

        fun bind(bannedUser: BanListUser) {
            txtName.text = "User name: ${bannedUser.userName}"
            txtId.text = "User id: ${bannedUser.uid}"
            txtBanReason.text = "User role: ${bannedUser.banReason}"
        }
    }
}