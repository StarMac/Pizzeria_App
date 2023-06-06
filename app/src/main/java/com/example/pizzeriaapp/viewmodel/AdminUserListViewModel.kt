package com.example.pizzeriaapp.viewmodel

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pizzeriaapp.model.Order
import com.example.pizzeriaapp.model.Pizza
import com.example.pizzeriaapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdminUserListViewModel (application: Application) : AndroidViewModel(application){

    private val usersCollection = Firebase.firestore.collection("User")

    private val _usersLiveData: MutableLiveData<List<User>> = MutableLiveData()
    val usersLiveData: LiveData<List<User>> get() = _usersLiveData

    init {
        loadUsers()
    }

    private fun loadUsers() {
        val query = usersCollection
        query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val usersList = snapshot.toObjects(User::class.java)
                _usersLiveData.value = usersList
            } else {
                Log.d(ContentValues.TAG, "Current data: null")
            }
        }
    }
}