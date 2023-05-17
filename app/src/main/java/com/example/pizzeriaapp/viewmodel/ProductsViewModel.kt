package com.example.pizzeriaapp.viewmodel

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pizzeriaapp.model.Pizza
import com.google.firebase.database.*

class ProductsViewModel (application: Application) : AndroidViewModel(application) {
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Pizza")
    private val _productsLiveData: MutableLiveData<List<Pizza>> = MutableLiveData()

    val productsLiveData: LiveData<List<Pizza>> get() = _productsLiveData

    fun loadProducts() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productsList = mutableListOf<Pizza>()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Pizza::class.java)
                    product?.let {
                        productsList.add(it)
                    }
                }
                _productsLiveData.value = productsList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", error.toException())
            }
        })
    }
}