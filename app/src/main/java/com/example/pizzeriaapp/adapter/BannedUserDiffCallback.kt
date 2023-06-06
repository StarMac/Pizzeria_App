package com.example.pizzeriaapp.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.pizzeriaapp.model.BanListUser

class BannedUserDiffCallback (private val oldList: List<BanListUser>, private val newList: List<BanListUser>) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].uid == newList[newItemPosition].uid

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]
}