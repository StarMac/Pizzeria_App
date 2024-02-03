package com.example.pizzeriaapp.viewmodel

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pizzeriaapp.model.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OrderListViewModel (application: Application) : AndroidViewModel(application){

    private val ordersCollection = Firebase.firestore.collection("Order")
    private val auth: FirebaseAuth = Firebase.auth

    private val _ordersLiveData: MutableLiveData<List<Order>> = MutableLiveData()
    val ordersLiveData: LiveData<List<Order>> get() = _ordersLiveData

    init {
        loadOrders()
    }

    private fun loadOrders() {
        val query = ordersCollection.whereEqualTo("clientUid", auth.currentUser!!.uid)
        query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val ordersList = snapshot.toObjects(Order::class.java)
                _ordersLiveData.value = ordersList.sortedByDescending { it.creationTimestamp }
            } else {
                Log.d(ContentValues.TAG, "Current data: null")
            }
        }
    }
}