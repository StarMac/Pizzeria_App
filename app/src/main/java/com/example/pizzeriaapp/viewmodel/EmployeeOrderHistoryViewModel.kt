package com.example.pizzeriaapp.viewmodel

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pizzeriaapp.model.Order
import com.example.pizzeriaapp.model.OrderStatus
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EmployeeOrderHistoryViewModel (application: Application) : AndroidViewModel(application){

    private val ordersCollection = Firebase.firestore.collection("Order")


    private val _ordersLiveData: MutableLiveData<List<Order>> = MutableLiveData()
    val ordersLiveData: LiveData<List<Order>> get() = _ordersLiveData

    init {
        loadOrders()
    }

    private fun loadOrders() {
        ordersCollection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val ordersList = snapshot.toObjects(Order::class.java)
                // Here we filter the order list to include only orders with the status "DELIVERED" or "CANCELED
                val filteredOrders = ordersList.filter { it.status == OrderStatus.DELIVERED.name || it.status == OrderStatus.CANCELED.name }
                _ordersLiveData.value = filteredOrders.sortedByDescending { it.creationTimestamp }
            } else {
                Log.d(ContentValues.TAG, "Current data: null")
            }
        }
    }
}