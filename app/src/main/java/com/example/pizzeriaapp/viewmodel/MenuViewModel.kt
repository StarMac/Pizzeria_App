package com.example.pizzeriaapp.viewmodel

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pizzeriaapp.model.Pizza
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MenuViewModel (application: Application) : AndroidViewModel(application) {
    private val databaseReference = Firebase.firestore.collection("Pizza")
    private val _menuLiveData: MutableLiveData<List<Pizza>> = MutableLiveData()

    val menuLiveData: LiveData<List<Pizza>> get() = _menuLiveData

    init {
        loadMenu()
    }

    private fun loadMenu() {
        databaseReference.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val menuList = snapshot.toObjects(Pizza::class.java)
                _menuLiveData.value = menuList
            } else {
                Log.d(TAG, "Current data: null")
            }
        }
    }
}