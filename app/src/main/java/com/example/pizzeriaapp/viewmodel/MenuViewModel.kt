package com.example.pizzeriaapp.viewmodel

import android.app.Application
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pizzeriaapp.model.Pizza
import com.google.firebase.database.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MenuViewModel (application: Application) : AndroidViewModel(application) {
    private val databaseReference = Firebase.firestore.collection("Pizza")
    private val _menuLiveData: MutableLiveData<List<Pizza>> = MutableLiveData()

    val menuLiveData: LiveData<List<Pizza>> get() = _menuLiveData

    fun preloadMenuToFireStore(){
        val db = Firebase.firestore

        val hawaiianPizza = hashMapOf(
            "name" to "Hawaiian Pizza",
            "price" to "140",
            "photo" to "https://www.jessicagavin.com/wp-content/uploads/2020/07/hawaiian-pizza-16-1200.jpg",
            "description" to "mozzarella, pineapple, bacon, oregano, ounces, pizza sauce"
        )

        db.collection("Pizza")
            .add(hawaiianPizza)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

        val tomatoPizza = hashMapOf(
            "name" to "Pizza With Tomatoes",
            "price" to "130",
            "photo" to "https://eclecticrecipes.com/wp-content/uploads/2011/10/zucchini-pizza.jpg",
            "description" to "mozzarella, tomatoes, bacon, oregano, ounces, pizza sauce"
        )

        db.collection("Pizza")
            .add(tomatoPizza)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }

    fun loadMenu() {
        databaseReference.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(ContentValues.TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                val menuList = snapshot.toObjects(Pizza::class.java)
                _menuLiveData.value = menuList
            } else {
                Log.d(ContentValues.TAG, "Current data: null")
            }
        }
    }
}