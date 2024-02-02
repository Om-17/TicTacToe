package com.example.tictactoe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import com.example.tictactoe.api.ForgotPasswordApi
import com.example.tictactoe.api.ForgotPasswordApi.Companion.asGetForgot
import com.example.tictactoe.models.OTP
import com.example.tictactoe.utils.SnackbarUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection

class ForgetpasswordActivity : AppCompatActivity() {
    private  lateinit var  emailTxt:EditText
    private lateinit var otpBtm:Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgetpassword)
        emailTxt=findViewById(R.id.email)
        otpBtm=findViewById(R.id.otpbtn)
        progressBar=findViewById(R.id.progressBar2)
        val backBtn: ImageView = findViewById(R.id.backBtn)
        backBtn.setOnClickListener {

            onBackPressedDispatcher.onBackPressed()
        }
        otpBtm.setOnClickListener(::otpBtmClicked)
    }
    private fun otpBtmClicked(view:View){
        val email:String=emailTxt.text.trim().toString()
        if (!ConnectivityUtil.isInternetAvailable(this)) {
            // Show a message or perform some action to inform the user that there is no internet connection
            val snackbarUtils = SnackbarUtils(this)
            val rootView = findViewById<View>(android.R.id.content)
            snackbarUtils.showSnackbar(rootView, "No internet connection")
            return
        }
        if (email.isEmpty()) {
            val snackbarUtils = SnackbarUtils(this)
            val rootView = findViewById<View>(android.R.id.content)
            snackbarUtils.showSnackbar(rootView, "Email are required")
            return
        }

        val data=OTP(email = email )
        CoroutineScope((Dispatchers.IO)).launch {
            withContext(Dispatchers.Main) {
                showLoader()

            }
            val response =ForgotPasswordApi.forgotPassword(data)
            withContext(Dispatchers.Main) {
                hideLoader()
            }
            if (response.statusCode != HttpURLConnection.HTTP_OK) {
                val error = response.getError()

                withContext(Dispatchers.Main) {
                    println(error)
                    if (error != null) {
                        val message = error.error
                        val snackbarUtils = SnackbarUtils(this@ForgetpasswordActivity)
                        val rootView = findViewById<View>(android.R.id.content)
                        snackbarUtils.showSnackbar(rootView, message)

                    }
                    else{

                        val snackbarUtils = SnackbarUtils(this@ForgetpasswordActivity)
                        val rootView = findViewById<View>(android.R.id.content)
                        snackbarUtils.showSnackbar(rootView, "Internal Server Error.")

                    }
                }
                return@launch

            }
            println("status=>${response.statusCode}")
            val data=response.asGetForgot()
            withContext(Dispatchers.Main) {

                if (data != null) {
                    println(" data.message==> ${data.message}")
                    val snackbarUtils = SnackbarUtils(this@ForgetpasswordActivity)
                    val rootView = findViewById<View>(android.R.id.content)
                    snackbarUtils.showSnackbar(rootView, data.message)
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent =
                            Intent(this@ForgetpasswordActivity, OtpActivity::class.java)
                        intent.putExtra("email", email)
                        startActivity(intent)

                    }, 800)
                } else {
                    val snackbarUtils = SnackbarUtils(this@ForgetpasswordActivity)
                    val rootView = findViewById<View>(android.R.id.content)
                    snackbarUtils.showSnackbar(
                        rootView,
                        "This email is invalid and trying again with other email ID."
                    )
                }
            }

        }
    }
    private fun showLoader() {
        progressBar.visibility = View.VISIBLE
        otpBtm.visibility=View.GONE
    }


    private fun hideLoader() {

        progressBar.visibility = View.GONE
        otpBtm.visibility=View.VISIBLE
    }
}