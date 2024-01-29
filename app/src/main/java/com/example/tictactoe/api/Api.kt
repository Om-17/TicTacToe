package com.example.tictactoe.api

import java.io.IOException
import java.net.HttpURLConnection
import java.net.NoRouteToHostException
import java.net.URL

class Api
{
    companion object
    {
//        private const val BASE_URL = "http://192.168.69.159/TicTacToeServer/api"
private const val BASE_URL = "http://192.168.237.188:80/TicTacToeServer/api"

        fun get(path: String): ApiResponse
        {
            val url = "$BASE_URL/$path"
            val connection = URL(url).openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            connection.doInput = true
            connection.doOutput = true

            val reader = if (connection.responseCode == HttpURLConnection.HTTP_OK) connection.inputStream.bufferedReader() else connection.errorStream.bufferedReader()
            val response = reader.readText()

            reader.close()

            return ApiResponse(connection.responseCode, response)
        }

        fun post(path: String, data: String): ApiResponse
        {
            try {


                val url = "$BASE_URL/$path"
                val connection = URL(url).openConnection() as HttpURLConnection

                connection.requestMethod = "POST"
                connection.doInput = true
                connection.doOutput = true

                val writer = connection.outputStream.bufferedWriter()
                writer.write(data)
                writer.close()

                val reader =
                    if (connection.responseCode == HttpURLConnection.HTTP_OK) connection.inputStream.bufferedReader() else connection.errorStream.bufferedReader()
                val response = reader.readText()

                reader.close()
                return ApiResponse(connection.responseCode, response)
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle IOException here
                return ApiResponse(500, "{\"error\": \"${e.message?.toString()?.replace("\"", "\\\"")}\"}")

            } catch (e: NoRouteToHostException) {
                e.printStackTrace()
                // Handle NoRouteToHostException here
                return ApiResponse(500, "{\"error\": \"Host unreachable\"}")
            }

        }
    }
}