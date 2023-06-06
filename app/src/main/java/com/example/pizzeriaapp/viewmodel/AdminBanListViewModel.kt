package com.example.pizzeriaapp.viewmodel

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pizzeriaapp.model.BanListUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AdminBanListViewModel (application: Application) : AndroidViewModel(application){

    private val banListCollection = Firebase.firestore.collection("BanList")

    private val _bannedUsersLiveData: MutableLiveData<List<BanListUser>> = MutableLiveData()
    val bannedUsersLiveData: LiveData<List<BanListUser>> get() = _bannedUsersLiveData

    init {
        loadBanList()
    }

    private fun loadBanList() {
        val query = banListCollection
        query.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val banList = snapshot.toObjects(BanListUser::class.java)
                _bannedUsersLiveData.value = banList
            } else {
                Log.d(ContentValues.TAG, "Current data: null")
            }
        }
    }
}