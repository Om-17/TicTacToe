package com.example.tictactoe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.tictactoe.api.ProfileApi
import com.example.tictactoe.api.ProfileApi.Companion.asGetUser
import com.example.tictactoe.utils.CustomLoaderDialog
import com.example.tictactoe.utils.SnackbarUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection

class ProfileActivity : AppCompatActivity() {
    private var token:String=""
    private lateinit var userTxt:TextView
    private lateinit var emailTxt:TextView
    private lateinit var customLoaderDialog: CustomLoaderDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        token= intent.getStringExtra("token").toString()
        userTxt=findViewById(R.id.textuser)
        emailTxt=findViewById(R.id.textemail)
        customLoaderDialog = CustomLoaderDialog(this)
        println(token)
        showLoader()
        val backBtn: ImageView = findViewById(R.id.backBtn)
        val changePasswordBtn:Button=findViewById(R.id.changepassbtn)
        changePasswordBtn.setOnClickListener{
            val mainIntent = Intent(this, ChangePasswordActivity::class.java)
            mainIntent.putExtra("token",token)
            startActivity(mainIntent)
        }

        backBtn.setOnClickListener {

            onBackPressedDispatcher.onBackPressed()
        }

        CoroutineScope(Dispatchers.IO).launch {

            val response = ProfileApi.getProfile(token)
            withContext(Dispatchers.Main) {
                hideLoader()
            }
            if (response.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                val error = response.getError()

                withContext(Dispatchers.Main) {
                    println(error)
                    if (error != null) {

                        val message = error.error
                        val snackbarUtils = SnackbarUtils(this@ProfileActivity)
                        val rootView = findViewById<View>(android.R.id.content)
                        snackbarUtils.showSnackbar(rootView, message)
                        getSharedPreferences("my_prefs", MODE_PRIVATE).edit().clear().apply()

                        Handler(Looper.getMainLooper()).postDelayed({
                            val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }, 800)

                    }

                }


                return@launch

            }
            if (response.statusCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
                val error = response.getError()

                withContext(Dispatchers.Main) {
                    println(error)
                    if (error != null) {
                        val message = error.error
                        val snackbarUtils = SnackbarUtils(this@ProfileActivity)
                        val rootView = findViewById<View>(android.R.id.content)
                        snackbarUtils.showSnackbar(rootView, message)
                        Handler(Looper.getMainLooper()).postDelayed({
                            val intent =
                                Intent(this@ProfileActivity, OnlineModeActivity::class.java)
                            startActivity(intent)
                            finish()
                        }, 800)
                    }
                }
                return@launch

            }
            if (response.statusCode == HttpURLConnection.HTTP_OK) {
                val user = response.asGetUser()
                withContext(Dispatchers.Main) {


                    if (user != null) {
                        userTxt.text = user.username
                        emailTxt.text = user.email
                    } else {
                        val snackbarUtils = SnackbarUtils(this@ProfileActivity)
                        val rootView = findViewById<View>(android.R.id.content)
                        snackbarUtils.showSnackbar(rootView, "Internal server error")
                    }
                }
                return@launch

            }
        }
    }

    private fun showLoader() {
        if (!isFinishing && !isDestroyed) { // Check if activity is running
            customLoaderDialog.show()
        }
    }


    private fun hideLoader() {
        if (customLoaderDialog.isShowing) {
            customLoaderDialog.dismiss()
        }
    }
}
