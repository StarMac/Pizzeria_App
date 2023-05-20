package com.example.pizzeriaapp.model

data class User(
    val uid: String? = null,
    val name: String? = null,
    val email: String? = null,
    val role: String? = null // Client, Employee, Admin
)