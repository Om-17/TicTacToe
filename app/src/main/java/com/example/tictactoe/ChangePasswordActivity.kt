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
import android.widget.TextView
import com.example.tictactoe.api.ProfileApi
import com.example.tictactoe.api.ProfileApi.Companion.asGetUser
import com.example.tictactoe.models.User
import com.example.tictactoe.utils.CustomLoaderDialog
import com.example.tictactoe.utils.SnackbarUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection

class ChangePasswordActivity : AppCompatActivity() {
    private var token:String=""
    private lateinit var oldPassTxt: EditText
    private lateinit var newPassTxt: EditText
    private lateinit var cPassTxt:EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var changeBtn:Button
    private lateinit var customLoaderDialog: CustomLoaderDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        oldPassTxt=findViewById(R.id.oldPasswordtxt)
        newPassTxt=findViewById(R.id.newpasswordtxt)
        cPassTxt=findViewById(R.id.cpasswordtxt)
        progressBar=findViewById(R.id.progressBar2)
        customLoaderDialog = CustomLoaderDialog(this)
        token= intent.getStringExtra("token").toString()

        val backBtn: ImageView = findViewById(R.id.backBtn)
        backBtn.setOnClickListener {

            onBackPressedDispatcher.onBackPressed()
        }
        changeBtn=findViewById(R.id.changepassbtn)
        changeBtn.setOnClickListener(::changePasswordClick)
    }
    private fun changePasswordClick(view:View) {
        val oldPassword = oldPassTxt.text.trim().toString()
        val newPassword = newPassTxt.text.trim().toString()
        val cPassword = cPassTxt.text.trim().toString()

        if (!ConnectivityUtil.isInternetAvailable(this)) {
            // Show a message or perform some action to inform the user that there is no internet connection
            val snackbarUtils = SnackbarUtils(this)
            val rootView = findViewById<View>(android.R.id.content)
            snackbarUtils.showSnackbar(rootView, "No internet connection")
            return
        }
        if (oldPassword.isEmpty() || newPassword.isEmpty() || cPassword.isEmpty()) {
            val snackbarUtils = SnackbarUtils(this)
            val rootView = findViewById<View>(android.R.id.content)
            snackbarUtils.showSnackbar(rootView, "old and new and confirm password are required")
            return
        }
        if (cPassword != newPassword) {

            val snackbarUtils = SnackbarUtils(this)
            val rootView = findViewById<View>(android.R.id.content)
            snackbarUtils.showSnackbar(rootView, "Confirm Passwords do not match!")

//            Toast.makeText(this, "Error: Passwords do not match!", Toast.LENGTH_LONG).show()
            return

        }

        val user = User(
            oldPassword = oldPassword,
            password = newPassword

        )

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                showLoader()

            }
            val response = ProfileApi.changePassword(user, token)
            withContext(Dispatchers.Main) {
                hideLoader()
            }
            if (response.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                val error = response.getError()

                withContext(Dispatchers.Main) {
                    println(error)
                    if (error != null) {

                        val message = error.error
                        val snackbarUtils = SnackbarUtils(this@ChangePasswordActivity)
                        val rootView = findViewById<View>(android.R.id.content)
                        snackbarUtils.showSnackbar(rootView, message)
                        getSharedPreferences("my_prefs", MODE_PRIVATE).edit().clear().apply()

                        Handler(Looper.getMainLooper()).postDelayed({
                            val intent =
                                Intent(this@ChangePasswordActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }, 800)

                    }

                }


                return@launch

            } else if (response.statusCode != HttpURLConnection.HTTP_OK) {
                val error = response.getError()

                withContext(Dispatchers.Main) {
                    println(error)
                    if (error != null) {
                        val message = error.error
                        val snackbarUtils = SnackbarUtils(this@ChangePasswordActivity)
                        val rootView = findViewById<View>(android.R.id.content)
                        snackbarUtils.showSnackbar(rootView, message)

                    }
                }
                return@launch

            }

            val user = response.asGetUser()
            withContext(Dispatchers.Main) {

                val snackbarUtils = SnackbarUtils(this@ChangePasswordActivity)
                val rootView = findViewById<View>(android.R.id.content)
                snackbarUtils.showSnackbar(rootView, "Password Change Successfully.")
                getSharedPreferences("my_prefs", MODE_PRIVATE).edit().clear().apply()

                Handler(Looper.getMainLooper()).postDelayed({
                    val intent =
                        Intent(this@ChangePasswordActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 800)

            }
        }




    }
    private fun showLoader() {
//        if (!isFinishing && !isDestroyed) { // Check if activity is running
//            customLoaderDialog.show()
//        }
        progressBar.visibility = View.VISIBLE
        changeBtn.visibility=View.GONE
    }


    private fun hideLoader() {
//        if (customLoaderDialog.isShowing) {
//            customLoaderDialog.dismiss()
//        }
        progressBar.visibility = View.GONE
        changeBtn.visibility=View.VISIBLE
    }
}