package com.example.pizzeriaapp.viewmodel

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pizzeriaapp.model.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class OrderListViewModel (application: Application) : AndroidViewModel(application){

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Order")
    private val auth: FirebaseAuth = Firebase.auth

    private val _ordersLiveData: MutableLiveData<List<Order>> = MutableLiveData()
    val ordersLiveData: LiveData<List<Order>> get() = _ordersLiveData

    fun loadOrders() {
        //TODO Отсортировать ещё по времени, а не только по uid
        val queryDatabase = databaseReference.orderByChild("uid").equalTo(auth.currentUser!!.uid)
        queryDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ordersList = mutableListOf<Order>()
                for (orderSnapshot in snapshot.children) {
                    val order = orderSnapshot.getValue(Order::class.java)
                    order?.let {
                        ordersList.add(it)
                    }
                }
                _ordersLiveData.value = ordersList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", error.toException())
            }
        })
    }
}