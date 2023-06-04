package com.example.pizzeriaapp.model

data class Order (
    val id: String? = null,
    val clientUid: String? = null, // Uid клиента, который сделал заказ
    val clientName: String? = null, // Имя клиента, который сделал заказ
    var items: List<OrderItem>? = null, // Список заказанных пицц
    val totalPrice: Int? = null, // Общая стоимость заказа
    var status: String? = null, // Статус заказа
    val deliveryMethod: String? = null, // Способ получения заказа
    val paymentMethod: String? = null, // Способ оплаты
    val deliveryAddress: String? = null, // Адрес доставки
    val creationTimestamp: Long? = null // Время создания заказа
) {
    fun statusFromString(statusString: String): OrderStatus? = OrderStatus.values().find { it.name == statusString }
    fun deliveryMethodFromString(methodString: String): DeliveryMethod? = DeliveryMethod.values().find { it.name == methodString }
    fun paymentMethodFromString(methodString: String): PaymentMethod? = PaymentMethod.values().find { it.name == methodString }
}

data class OrderItem(
    val pizzaId: String? = null, // Id пиццы
    val pizzaName: String? = null, // Название пиццы
    val pizzaPhoto: String? = null, // Изображение пиццы
    val pizzaPrice: Int? = null, // Цена пиццы
    val quantity: Int? = null // Количество пиццы
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