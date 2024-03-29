package com.example.tictactoe.api

import com.example.tictactoe.models.Token
import com.example.tictactoe.models.User


import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class VerifyTokenApi {

    companion object
    {
        fun verifyToken(token: Token): ApiResponse = Api.post("tokenVerify.php", Gson().toJson(token))

        fun ApiResponse.asToken(): Token? {
            return try {
                Gson().fromJson(this.json, Token::class.java)
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
                null // Return null or a default User object in case of an exception
            }
        }
    }
}