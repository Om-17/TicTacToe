package com.example.tictactoe.api


import com.example.tictactoe.models.User
import com.google.gson.Gson

class UserApi
{
    companion object
    {
        fun login(user: User): ApiResponse = Api.post("login.php", Gson().toJson(user))
        fun register(user: User): ApiResponse = Api.post("signup.php", Gson().toJson(user))

        fun ApiResponse.asUser(): User = Gson().fromJson(this.json, User::class.java)
    }
}