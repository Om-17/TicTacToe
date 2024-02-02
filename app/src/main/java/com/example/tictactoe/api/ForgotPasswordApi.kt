package com.example.tictactoe.api

import com.example.tictactoe.models.OTP
import com.example.tictactoe.models.User
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class ForgotPasswordApi {
    companion object
    {
        fun forgotPassword(email: OTP): ApiResponse = Api.post("forgotpassword.php", Gson().toJson(email))

        fun resetPassword(data: User):ApiResponse?{
            return try {
                Api.post("resetpassword.php", Gson().toJson(data))
            }catch (e: JsonSyntaxException) {
                e.printStackTrace()
                null // Return null or a default User object in case of an exception
            }
        }
        fun otpVerify(data: OTP):ApiResponse?{
        return try {
            Api.post("OTPverify.php", Gson().toJson(data))
        }catch (e: JsonSyntaxException) {
            e.printStackTrace()
            null // Return null or a default User object in case of an exception
        }
    }
        fun ApiResponse.getGetResetPassword():User? {
            return try {
                Gson().fromJson(this.json, User::class.java)
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
                null // Return null or a default User object in case of an exception
            }
        }
        fun ApiResponse.asGetForgot(): OTP? {
            return try {
                Gson().fromJson(this.json, OTP::class.java)
            } catch (e: JsonSyntaxException) {
                e.printStackTrace()
                null // Return null or a default User object in case of an exception
            }
        }

    }
}