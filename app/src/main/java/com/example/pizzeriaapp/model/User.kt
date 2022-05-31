package com.example.pizzeriaapp.model

data class User (
    val name: String,
    val email: String,
    val password: String,
    val photo: String,
    val isBlacklisted: Boolean)