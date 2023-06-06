package com.example.pizzeriaapp.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.pizzeriaapp.model.User

class UserDiffCallback (private val oldList: List<User>, private val newList: List<User>) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].uid == newList[newItemPosition].uid

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]
}