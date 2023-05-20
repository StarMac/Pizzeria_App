package com.example.pizzeriaapp.viewmodel

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pizzeriaapp.model.Pizza
import com.google.firebase.database.*

class MenuViewModel (application: Application) : AndroidViewModel(application) {
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Pizza")
    private val _menuLiveData: MutableLiveData<List<Pizza>> = MutableLiveData()

    val menuLiveData: LiveData<List<Pizza>> get() = _menuLiveData

    fun loadMenu() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val menuList = mutableListOf<Pizza>()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Pizza::class.java)
                    product?.let {
                        menuList.add(it)
                    }
                }
                _menuLiveData.value = menuList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", error.toException())
            }
        })
    }
}