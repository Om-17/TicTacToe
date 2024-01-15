package com.example.tictactoe.models

data class User(
    val id: Int = 0,
    val username: String = "",
    val email:String="",
    val token:String="",
    val password: String = "",
    val confirmPassword: String = ""
)
