package com.example.tictactoe.api

import com.example.tictactoe.models.Token


import com.google.gson.Gson

class VerifyTokenApi {

    companion object
    {
        fun verifyToken(token: Token): ApiResponse = Api.post("tokenVerify.php", Gson().toJson(token))
        fun ApiResponse.asToken(): Token = Gson().fromJson(this.json, Token::class.java)
    }
}