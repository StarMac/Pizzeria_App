package com.example.pizzeriaapp.viewmodel

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pizzeriaapp.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val databasePreOrderReference = Firebase.firestore.collection("User")
        .document(FirebaseAuth.getInstance().currentUser?.uid!!)
        .collection("PreOrder")
    private val databaseUserReference = Firebase.firestore.collection("User")
        .document(FirebaseAuth.getInstance().currentUser?.uid!!)
    private val databaseBanListCollection = Firebase.firestore.collection("BanList")
    private var newOrderRef = Firebase.firestore.collection("Order").document()

    private val _cartLiveData: MutableLiveData<List<OrderItem>> = MutableLiveData()
    private val _totalPriceLiveData: MutableLiveData<Int> = MutableLiveData()
    private val _userNameLiveData: MutableLiveData<String> = MutableLiveData()
    private val _isBannedLiveData: MutableLiveData<Boolean> = MutableLiveData()
    private val _userSignedOutLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val orderPlacedLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val cartLiveData: LiveData<List<OrderItem>> get() = _cartLiveData
    val totalPriceLiveData: LiveData<Int> get() = _totalPriceLiveData
    private val userNameLiveData: LiveData<String> get() = _userNameLiveData
    val isBannedLiveData: LiveData<Boolean> get() = _isBannedLiveData
    val userSignedOutLiveData: LiveData<Boolean> get() = _userSignedOutLiveData

    init {
        fetchUserName()
    }

    private fun fetchUserName() {
        databaseUserReference.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val user = document.toObject(User::class.java)
                    _userNameLiveData.value = user?.name
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    private fun checkUserBanStatus(callback: (Boolean) -> Unit) {
        databaseBanListCollection.document(FirebaseAuth.getInstance().currentUser?.uid!!)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                callback.invoke(documentSnapshot.exists())
            }
            .addOnFailureListener { e ->
                Log.d(TAG, "get failed with ", e)
                callback.invoke(false)
            }
    }


    fun loadCart() {
        databasePreOrderReference.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                if (!snapshot.isEmpty) {
                    val cartList = snapshot.toObjects(OrderItem::class.java)
                    _cartLiveData.value = cartList
                    calculateTotal(cartList)
                } else {
                    // Update _cartLiveData and _totalPriceLiveData if snapshot is empty
                    _cartLiveData.value = emptyList()
                    _totalPriceLiveData.value = 0
                    Log.d(TAG, "Current data: null")
                }
            }
        }
    }

    fun increaseQuantity(item: OrderItem) {
        val currentQuantity = item.quantity ?: 0
        updateItemQuantity(item, currentQuantity + 1)
    }

    fun decreaseQuantity(item: OrderItem) {
        val currentQuantity = item.quantity ?: 0
        if (currentQuantity > 1) {
            updateItemQuantity(item, currentQuantity - 1)
        }
    }

    fun removeItemFromCart(item: OrderItem) {
        val itemRef = databasePreOrderReference.document(item.pizzaId!!)
        itemRef.delete()
            .addOnSuccessListener {
                Log.d(TAG, "Item successfully deleted.")
                // reload the cart
                loadCart()
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error deleting item", e)
            }
    }

    private fun updateItemQuantity(item: OrderItem, newQuantity: Int) {
        val itemRef = databasePreOrderReference.document(item.pizzaId!!)
        itemRef.update("quantity", newQuantity)
            .addOnSuccessListener {
                Log.d(TAG, "Item quantity updated.")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating item quantity", e)
            }
    }

    private fun calculateTotal(orderItems: List<OrderItem>) {
        var total = 0
        orderItems.forEach {
            if (it.pizzaPrice != null && it.quantity != null) {
                total += it.pizzaPrice * it.quantity
            }
        }
        _totalPriceLiveData.value = total
    }

    fun placeOrder(deliveryMethod: DeliveryMethod, paymentMethod: PaymentMethod, deliveryAddress: String) {
        val currentOrderItems = cartLiveData.value
        val userName = userNameLiveData.value ?: "Name Error"

        checkUserBanStatus { isBanned ->
            if (isBanned) {
                Log.d(TAG, "User is banned. Can't place order.")
                Toast.makeText(getApplication(), "Your account is banned", Toast.LENGTH_LONG).show()
                Firebase.auth.signOut()
                _userSignedOutLiveData.value = true
                return@checkUserBanStatus
            }

            // Check if the order list is empty
            if (!currentOrderItems.isNullOrEmpty()) {
                // We use the already existing method to calculate the total cost
                val totalPrice = totalPriceLiveData.value ?: 0

                // Create a new Order object
                val order = Order(
                    id = newOrderRef.id,
                    clientUid = FirebaseAuth.getInstance().currentUser?.uid,
                    clientName = userName,
                    items = currentOrderItems,
                    totalPrice = totalPrice,
                    status = OrderStatus.PENDING.name,
                    deliveryMethod = deliveryMethod.name,
                    paymentMethod = paymentMethod.name,
                    deliveryAddress = deliveryAddress,
                    creationTimestamp = System.currentTimeMillis()
                )

                // Adding an order to Firebase
                Firebase.firestore.collection("Order")
                    .document(order.id!!)
                    .set(order)
                    .addOnSuccessListener {
                        Log.d(TAG, "Order successfully placed!")

                        // After successfully adding an order, clear the PreOrder for the current user
                        currentOrderItems.forEach { orderItem ->
                            databasePreOrderReference.document(orderItem.pizzaId!!).delete()
                        }

                        // Updating cart data
                        loadCart()

                        // Set the flag that the order was successfully created
                        orderPlacedLiveData.value = true
                        newOrderRef = Firebase.firestore.collection("Order").document()
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error placing order", e)
                    }
            } else {
                Log.w(TAG, "Cart is empty. Can't place order.")
            }
        }
    }
}