package com.example.pizzeriaapp.model

data class Order (
    val id: String? = null,
    val clientUid: String? = null, // Uid клиента, который сделал заказ
    var items: List<OrderItem>? = null, // Список заказанных пицц
    val totalPrice: Int? = null, // Общая стоимость заказа
    val status: String? = null, // Статус заказа, например, "В обработке", "Готовится", "Доставляется", "Доставлено"
    val creationTimestamp: Long? = null // Добавьте это поле для хранения времени создания заказа
)

data class OrderItem(
    val pizzaId: String? = null, // Id пиццы
    val pizzaName: String? = null, // Название пиццы
    val pizzaPhoto: String? = null, // Изображение пиццы
    val pizzaPrice: Int? = null, // Цена пиццы
    val quantity: Int? = null // Количество пиццы
)