package com.example.tictactoe.api

import com.example.tictactoe.models.User
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class ProfileApi {
    companion object
    {
       fun getProfile(token:String): ApiResponse = Api.get("getuser.php",token )
        fun changePassword(user: User,token: String): ApiResponse = Api.post("changepassword.php", Gson().toJson(user),token)

        fun ApiResponse.asGetUser(): User? {
            return try {
                Gson().fromJson(this.json, User::class.java)
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
                null // Return null or a default User object in case of an exception
            }
        }

    }
}