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

    fun preloadMenuToFireStore(){
        val db = Firebase.firestore

        val newPizzaRef1 = db.collection("Pizza").document()
        val hawaiianPizza = hashMapOf(
            "id" to newPizzaRef1.id,
            "name" to "Hawaiian Pizza",
            "price" to 140,
            "photo" to "https://www.jessicagavin.com/wp-content/uploads/2020/07/hawaiian-pizza-16-1200.jpg",
            "description" to "mozzarella, pineapple, bacon, oregano, ounces, pizza sauce"
        )
        newPizzaRef1.set(hawaiianPizza)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }


        val newPizzaRef2 = db.collection("Pizza").document()
        val tomatoPizza = hashMapOf(
            "id" to newPizzaRef2.id,
            "name" to "Pizza With Tomatoes",
            "price" to 130,
            "photo" to "https://eclecticrecipes.com/wp-content/uploads/2011/10/zucchini-pizza.jpg",
            "description" to "mozzarella, tomatoes, bacon, oregano, ounces, pizza sauce"
        )
        newPizzaRef2.set(tomatoPizza)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

    fun loadMenu() {
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