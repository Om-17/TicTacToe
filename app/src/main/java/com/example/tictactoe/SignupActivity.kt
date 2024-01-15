package com.example.tictactoe

import com.example.tictactoe.utils.SnackbarUtils
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.example.tictactoe.R.*
import com.example.tictactoe.api.UserApi
import com.example.tictactoe.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection

class SignupActivity : AppCompatActivity() {
    private lateinit var login: TextView
    private  lateinit var txtUsername :EditText
    private  lateinit var txtPassword :EditText
    private  lateinit var txtCPassword :EditText
    private  lateinit var txtEmail :EditText
    private  lateinit var signBtn:Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_signup)
        login = findViewById(id.login)
        txtPassword=findViewById(id.password)
        txtUsername=findViewById(id.username)
        txtCPassword=findViewById(id.confirmPassword)
        progressBar = findViewById(id.progressBar2)

        txtEmail=findViewById(id.email)
        signBtn=findViewById(id.signup_btn)
        login.setOnClickListener{
            val mainIntent = Intent(this, LoginActivity::class.java)
            startActivity(mainIntent)
            finish()
        }
        val prefs = getSharedPreferences("my_prefs", MODE_PRIVATE)
        val token = prefs.getString("token", null)
        if (!token.isNullOrEmpty()) {
            // Token exists, start OnlineModeActivity
            val onlineModeIntent = Intent(this, OnlineModeActivity::class.java)
            startActivity(onlineModeIntent)
            finish()
        }
        signBtn.setOnClickListener(::signupClicked)

    }

    private  fun signupClicked(view: View) {
        if (!ConnectivityUtil.isInternetAvailable(this)) {
            // Show a message or perform some action to inform the user that there is no internet connection
            val snackbarUtils = SnackbarUtils(this)
            val rootView = findViewById<View>(android.R.id.content)
            snackbarUtils.showSnackbar(rootView, "No internet connection")
            return
        }
        val username = txtUsername.text.toString()
        val password = txtPassword.text.toString()
        val email = txtEmail.text.toString()
        val cpassword = txtCPassword.text.toString()
        if (username.isEmpty() || password.isEmpty()||email.isEmpty()) {
//            Toast.makeText(
//                this,
//                "username and email and password are required",// Assuming error.data contains the message
//                Toast.LENGTH_LONG
//            ).show()
            val snackbarUtils = SnackbarUtils(this@SignupActivity)
            val rootView = findViewById<View>(android.R.id.content)
            snackbarUtils.showSnackbar(rootView, "username and email and password are required")

            // Show a message or perform some action to inform the user that both username and password are required
            return
        }
        if (password != cpassword)
        {

            val snackbarUtils = SnackbarUtils(this@SignupActivity)
            val rootView = findViewById<View>(android.R.id.content)
            snackbarUtils.showSnackbar(rootView, "Passwords do not match!")

//            Toast.makeText(this, "Error: Passwords do not match!", Toast.LENGTH_LONG).show()
            return
        }
        val user = User(username = username, password = password, email = email)
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                progressBar.visibility = View.VISIBLE
                signBtn.visibility=View.GONE
                login.visibility=View.GONE
                findViewById<TextView>(R.id.alreadyAc).visibility=View.GONE

            }
            val response = UserApi.register(user)
            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE
                signBtn.visibility=View.VISIBLE
                login.visibility=View.VISIBLE
                findViewById<TextView>(R.id.alreadyAc).visibility=View.VISIBLE
            }
            if (response.statusCode != HttpURLConnection.HTTP_OK)
            {
                withContext(Dispatchers.Main) {
                    val error = response.getError()

                    if (error != null) {
//                        Toast.makeText(
//                            this@SignupActivity,
//                            error.error,
//                            Toast.LENGTH_LONG
//                        ).show()
//                        val rootView = findViewById<View>(android.R.id.content)
                        val message = error.error

                        val snackbarUtils = SnackbarUtils(this@SignupActivity)
                        val rootView = findViewById<View>(android.R.id.content)
                        snackbarUtils.showSnackbar(rootView, message)

                    }
                }

                return@launch
            }

            withContext(Dispatchers.Main) {
//                Toast.makeText(
//                    this@SignupActivity,
//                    "New account creation successful",
//                    Toast.LENGTH_LONG
//                ).show()

                val message =  "New account creation successful"
                val snackbarUtils = SnackbarUtils(this@SignupActivity)
                val rootView = findViewById<View>(android.R.id.content)
                snackbarUtils.showSnackbar(rootView, message)
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 1000)

            }
        }
    }
}