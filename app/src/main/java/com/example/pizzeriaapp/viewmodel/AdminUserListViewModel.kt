package com.example.pizzeriaapp.viewmodel

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pizzeriaapp.model.BanListUser
import com.example.pizzeriaapp.model.Order
import com.example.pizzeriaapp.model.Pizza
import com.example.pizzeriaapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdminUserListViewModel (application: Application) : AndroidViewModel(application) {


    private val usersCollection = Firebase.firestore.collection("User")
    private val banListCollection = Firebase.firestore.collection("BanList")

    private val _usersLiveData: MutableLiveData<List<User>> = MutableLiveData()
    val usersLiveData: LiveData<List<User>> get() = _usersLiveData

    private val _bannedUsersLiveData: MutableLiveData<List<BanListUser>> = MutableLiveData()
    val bannedUsersLiveData: LiveData<List<BanListUser>> get() = _bannedUsersLiveData

    private val _activeUsersLiveData: MutableLiveData<List<User>> = MutableLiveData()
    val activeUsersLiveData: LiveData<List<User>> get() = _activeUsersLiveData

    init {
        loadUsers()
        loadBanList()
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
                syncUserLists()
            } else {
                Log.d(ContentValues.TAG, "Current data: null")
            }
        }
    }

    fun loadBanList() {
        val query = banListCollection
        query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val banList = snapshot.toObjects(BanListUser::class.java)
                _bannedUsersLiveData.value = banList
                syncUserLists()
            } else {
                Log.d(ContentValues.TAG, "Current data: null")
            }
        }
    }

    private fun syncUserLists() {
        val bannedUserIds = _bannedUsersLiveData.value?.map { it.uid } ?: emptyList()
        val activeUsers = _usersLiveData.value?.filter { it.uid !in bannedUserIds }
        _activeUsersLiveData.value = activeUsers
    }
}