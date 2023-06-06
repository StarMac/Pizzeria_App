package com.example.pizzeriaapp.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.pizzeriaapp.model.Order

class OrderDiffCallback(private val oldList: List<Order>, private val newList: List<Order>) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition].id == newList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        oldList[oldItemPosition] == newList[newItemPosition]
}