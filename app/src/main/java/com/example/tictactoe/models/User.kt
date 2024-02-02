package com.example.tictactoe.models

import android.os.Message

data class User(
    val id: Int = 0,
    val username: String = "",
    val email:String="",
    val name:String="",
    val token:String="",
    val password: String = "",
    val confirmPassword: String = "",
    val oldPassword:String="",
    val message: String=""
)
