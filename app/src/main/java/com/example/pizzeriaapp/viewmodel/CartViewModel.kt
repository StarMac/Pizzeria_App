package com.example.pizzeriaapp.viewmodel

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.pizzeriaapp.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val databasePreOrderReference = Firebase.firestore.collection("User")
        .document(FirebaseAuth.getInstance().currentUser?.uid!!)
        .collection("PreOrder")
    private val databaseUserReference = Firebase.firestore.collection("User")
        .document(FirebaseAuth.getInstance().currentUser?.uid!!)

    private var newOrderRef = Firebase.firestore.collection("Order").document()

    private val _cartLiveData: MutableLiveData<List<OrderItem>> = MutableLiveData()
    private val _totalPriceLiveData: MutableLiveData<Int> = MutableLiveData()
    val orderPlacedLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val cartLiveData: LiveData<List<OrderItem>> get() = _cartLiveData
    val totalPriceLiveData: LiveData<Int> get() = _totalPriceLiveData

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
                    // Обновляем _cartLiveData и _totalPriceLiveData, если snapshot пуст
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
        // Получаем текущее значение OrderItem
        val currentOrderItems = cartLiveData.value

        // Проверяем, не пуст ли список заказов
        if (currentOrderItems != null && currentOrderItems.isNotEmpty()) {
            // Используем уже существующий метод для вычисления общей стоимости
            val totalPrice = totalPriceLiveData.value ?: 0

            // Создаем новый объект Order
            val order = Order(
                id = newOrderRef.id,
                clientUid = FirebaseAuth.getInstance().currentUser?.uid,
                clientName = FirebaseAuth.getInstance().currentUser?.displayName, //TODO отображать имя не через FirebaseAuth
                items = currentOrderItems,
                totalPrice = totalPrice,
                status = OrderStatus.PENDING.name,
                deliveryMethod = deliveryMethod.name,
                paymentMethod = paymentMethod.name,
                deliveryAddress = deliveryAddress,
                creationTimestamp = System.currentTimeMillis()
            )

            // Добавляем заказ в Firebase
            Firebase.firestore.collection("Order")
                .document(order.id!!)
                .set(order)
                .addOnSuccessListener {
                    Log.d(TAG, "Order successfully placed!")

                    // После успешного добавления заказа очистите PreOrder для текущего пользователя
                    currentOrderItems.forEach { orderItem ->
                        databasePreOrderReference.document(orderItem.pizzaId!!).delete()
                    }

                    // Обновляем данные корзины
                    loadCart()

                    // Устанавливаем флаг что заказ успешно создан
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