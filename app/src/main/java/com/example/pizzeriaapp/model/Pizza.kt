package com.example.pizzeriaapp.model

data class Pizza (
    val name: String,
    val price: Int,
    val size: String,
    val photo: String,
    val ingredientsList : List<Ingredient>)