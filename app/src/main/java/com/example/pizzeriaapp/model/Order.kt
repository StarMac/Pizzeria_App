package com.example.pizzeriaapp.model

data class Order (
    val id: String? = null,
    val clientUid: String? = null, // The Uid of the customer who placed the order
    val clientName: String? = null, // Name of the customer who placed the order
    var items: List<OrderItem>? = null, // List of pizzas ordered
    val totalPrice: Int? = null, // Total order price
    var status: String? = null, // Order status
    val deliveryMethod: String? = null, // How to receive order
    val paymentMethod: String? = null, // Payment method
    val deliveryAddress: String? = null, // Shipping address
    val creationTimestamp: Long? = null // Order creation time
) {
    fun statusFromString(statusString: String): OrderStatus? = OrderStatus.values().find { it.name == statusString }
    fun deliveryMethodFromString(methodString: String): DeliveryMethod? = DeliveryMethod.values().find { it.name == methodString }
    fun paymentMethodFromString(methodString: String): PaymentMethod? = PaymentMethod.values().find { it.name == methodString }
}

data class OrderItem(
    val pizzaId: String? = null, // Pizza Id
    val pizzaName: String? = null, // Pizza name
    val pizzaPhoto: String? = null, // Изображение пиццы
    val pizzaPrice: Int? = null, // Pizza image
    val quantity: Int? = null // Quantity of pizza
)

enum class OrderStatus(val status: String) {
    PENDING("In processing"),
    PREPARING("Cooking"),
    DELIVERING("Delivering"),
    READY_FOR_PICKUP("Ready for pickup"),
    DELIVERED("Delivered"),
    CANCELED("Canceled")
}

enum class DeliveryMethod(val method: String) {
    PICK_UP("Self-delivery"),
    DELIVERY("Delivery")
}

enum class PaymentMethod(val method: String) {
    CASH_ON_DELIVERY("Cash"),
    CARD_ON_ORDER("Card")
}