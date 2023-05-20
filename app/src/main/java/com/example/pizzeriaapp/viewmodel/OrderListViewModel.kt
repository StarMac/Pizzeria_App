package com.example.pizzeriaapp.viewmodel

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pizzeriaapp.model.Order
import com.example.pizzeriaapp.model.Pizza
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class OrderListViewModel (application: Application) : AndroidViewModel(application){

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Order")
    private val pizzaDatabaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Pizza")
    private val auth: FirebaseAuth = Firebase.auth

    private val _pizzasLiveData: MutableLiveData<List<Pizza>> = MutableLiveData()
    val pizzasLiveData: LiveData<List<Pizza>> get() = _pizzasLiveData

    private val _ordersLiveData: MutableLiveData<List<Order>> = MutableLiveData()
    val ordersLiveData: LiveData<List<Order>> get() = _ordersLiveData

    init {
        loadPizzas()
        loadOrders()
    }

    private fun loadPizzas() {
        pizzaDatabaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val pizzasList = mutableListOf<Pizza>()
                for (pizzaSnapshot in snapshot.children) {
                    val pizza = pizzaSnapshot.getValue(Pizza::class.java)
                    pizza?.let {
                        pizzasList.add(it)
                    }
                }
                _pizzasLiveData.value = pizzasList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPizza:onCancelled", error.toException())
            }
        })
    }

    private fun loadOrders() {
        val queryDatabase = databaseReference.orderByChild("clientUid").equalTo(auth.currentUser!!.uid)
        queryDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ordersList = mutableListOf<Order>()
                for (orderSnapshot in snapshot.children) {
                    var newOrder = orderSnapshot.getValue(Order::class.java)
                    newOrder?.let {
                        for (item in it.items!!) {
                            val pizzaQuery = pizzaDatabaseReference.orderByKey().equalTo(item.pizzaId)
                            val listener = object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    for (pizzaSnapshot in dataSnapshot.children) {
                                        val pizza = pizzaSnapshot.getValue(Pizza::class.java)
                                        pizza?.let {
                                            val newOrderItem = item.copy(pizzaName = pizza.name, pizzaPhoto = pizza.photo)
                                            val newList = newOrder?.items?.toMutableList()
                                            val index = newList?.indexOf(item)
                                            if (index != null && index != -1) {
                                                newList[index] = newOrderItem
                                            }
                                            newOrder = newOrder?.copy(items = newList)
                                        }
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    Log.w(ContentValues.TAG, "loadPizza:onCancelled", databaseError.toException())
                                }
                            }
                            pizzaQuery.addListenerForSingleValueEvent(listener)
                        }
                        ordersList.add(newOrder!!)
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