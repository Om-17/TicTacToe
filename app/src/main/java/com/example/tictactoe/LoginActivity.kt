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
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView

import com.example.tictactoe.api.UserApi
import com.example.tictactoe.api.UserApi.Companion.asUser
import com.example.tictactoe.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection

class LoginActivity : AppCompatActivity() {
    private lateinit var signup:TextView
    private  lateinit var txtUsername:EditText
    private  lateinit var txtPassword:EditText
    private lateinit var btnLogin: Button
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        txtUsername = findViewById(R.id.txtusername)
        txtPassword = findViewById(R.id.txtpassword)
        progressBar = findViewById(R.id.progressBar)


        signup = findViewById(R.id.signup)
        btnLogin=findViewById(R.id.login_btn)
        signup.setOnClickListener{
            val mainIntent = Intent(this, SignupActivity::class.java)
            startActivity(mainIntent)
//            finish()
        }
        findViewById<ImageView>(R.id.backBtn).setOnClickListener {
            super.onBackPressedDispatcher.onBackPressed()
        }
        btnLogin.setOnClickListener(::loginClicked)
        val prefs = getSharedPreferences("my_prefs", MODE_PRIVATE)
        val token = prefs.getString("token", null)
        if (!token.isNullOrEmpty()) {
            // Token exists, start OnlineModeActivity
            val onlineModeIntent = Intent(this, OnlineModeActivity::class.java)
            startActivity(onlineModeIntent)
            finish()
        }
    }

    private  fun loginClicked(view:View){
        val username = txtUsername.text.toString()
        val password = txtPassword.text.toString()
        if (!ConnectivityUtil.isInternetAvailable(this)) {
            // Show a message or perform some action to inform the user that there is no internet connection
            val snackbarUtils = SnackbarUtils(this)
            val rootView = findViewById<View>(android.R.id.content)
            snackbarUtils.showSnackbar(rootView, "No internet connection")
            return
        }
        if (username.isEmpty() || password.isEmpty()) {

//            Toast.makeText(
//                this@LoginActivity,
//               "username and password are required",// Assuming error.data contains the message
//                Toast.LENGTH_LONG
//            ).show()
            val snackbarUtils = SnackbarUtils(this)
            val rootView = findViewById<View>(android.R.id.content)
            snackbarUtils.showSnackbar(rootView, "Username and password are required")
            // Show a message or perform some action to inform the user that both username and password are required
            return
        }

        val user = User(
            username = txtUsername.text.toString(),
            password = txtPassword.text.toString()

        )
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                progressBar.visibility = View.VISIBLE
                btnLogin.visibility=View.GONE
                signup.visibility=View.GONE
                findViewById<TextView>(R.id.haveaccount).visibility=View.GONE

            }
            val response = UserApi.login(user)
            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE
                btnLogin.visibility=View.VISIBLE
                signup.visibility=View.VISIBLE
                findViewById<TextView>(R.id.haveaccount).visibility=View.VISIBLE
            }
            if (response.statusCode != HttpURLConnection.HTTP_OK) {
                val error = response.getError()

                withContext(Dispatchers.Main) {
                    println(error)
                    if (error != null) {
//                        Toast.makeText(
//                            this@LoginActivity,
//                            error.error,
//                            Toast.LENGTH_LONG
//                        ).show()

                        val message = error.error
                        val snackbarUtils = SnackbarUtils(this@LoginActivity)
                        val rootView = findViewById<View>(android.R.id.content)
                        snackbarUtils.showSnackbar(rootView, message)

                    }
                }

                return@launch
            }


            withContext(Dispatchers.Main) {
                getSharedPreferences("my_prefs", MODE_PRIVATE).edit().clear().apply()
                val user = response.asUser()
                println(user)
                val prefs = getSharedPreferences("my_prefs", MODE_PRIVATE)
                val editor = prefs.edit()

                editor.putInt("user_id", user.id)
                editor.putString("token", user.token)
                editor.putString("username", user.username)

                editor.apply()
//                Toast.makeText(
//                    this@LoginActivity,
//                    "Successfully logged in",
//                    Toast.LENGTH_LONG
//                ).show()

                val message = "Successfully logged in"

                val snackbarUtils = SnackbarUtils(this@LoginActivity)
                val rootView = findViewById<View>(android.R.id.content)
                snackbarUtils.showSnackbar(rootView, message)
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    btnLogin.visibility=View.VISIBLE
                    signup.visibility=View.VISIBLE
                    findViewById<TextView>(R.id.haveaccount).visibility=View.VISIBLE
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this@LoginActivity, OnlineModeActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 800)
            }
        }
    }

}
