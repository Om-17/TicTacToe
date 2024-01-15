package com.example.tictactoe.api


import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.net.HttpURLConnection

data class ApiResponse(val statusCode: Int, val json: String)
{
    init {
        println("Response: $json")
    }
    private val isSuccess: Boolean get() = statusCode == HttpURLConnection.HTTP_OK
    val errorMessage: String? = if (!isSuccess) json else null
    fun getError(): ApiError? {
        return try {
            if (!isSuccess) {
                Gson().fromJson(json, ApiError::class.java)
            } else {
                null
            }
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            // Handle Gson exception, e.g., log the error
            null
        }
    }


}