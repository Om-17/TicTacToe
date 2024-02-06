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
import com.example.tictactoe.api.ProfileApi
import com.example.tictactoe.api.ProfileApi.Companion.asGetUser
import com.example.tictactoe.models.User
import com.example.tictactoe.utils.SnackbarUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var newPassTxt: EditText
    private lateinit var cPassTxt: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var resetBtn: Button
    private var userId:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        newPassTxt=findViewById(R.id.passwordtxt)
        cPassTxt=findViewById(R.id.cpasswordtxt)
        progressBar=findViewById(R.id.progressBar2)
        resetBtn=findViewById(R.id.resetPassBtn)
        userId=intent.getIntExtra("userId",0)
        resetBtn.setOnClickListener(::resetPasswordClick)
        val backBtn: ImageView = findViewById(R.id.backBtn)
        backBtn.setOnClickListener {

            onBackPressedDispatcher.onBackPressed()
        }
    }
    private fun resetPasswordClick(view:View) {

        val newPassword = newPassTxt.text.trim().toString()
        val cPassword = cPassTxt.text.trim().toString()

        if (!ConnectivityUtil.isInternetAvailable(this)) {
            // Show a message or perform some action to inform the user that there is no internet connection
            val snackbarUtils = SnackbarUtils(this)
            val rootView = findViewById<View>(android.R.id.content)
            snackbarUtils.showSnackbar(rootView, "No internet connection")
            return
        }
        if ( newPassword.isEmpty() || cPassword.isEmpty()) {
            val snackbarUtils = SnackbarUtils(this)
            val rootView = findViewById<View>(android.R.id.content)
            snackbarUtils.showSnackbar(rootView, "new and confirm password are required")
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
            id = userId,
            password = newPassword

        )

        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                showLoader()

            }
            val response = ForgotPasswordApi.resetPassword(user)
            withContext(Dispatchers.Main) {
                hideLoader()
            }
            if (response != null) {
                if (response.statusCode != HttpURLConnection.HTTP_OK) {
                    val error = response.getError()

                    withContext(Dispatchers.Main) {
                        println(error)
                        if (error != null) {
                            val message = error.error
                            val snackbarUtils = SnackbarUtils(this@ResetPasswordActivity)
                            val rootView = findViewById<View>(android.R.id.content)
                            snackbarUtils.showSnackbar(rootView, message)

                        }
                    }
                    return@launch

                }
            }

            val user = response?.asGetUser()
            withContext(Dispatchers.Main) {

            val snackbarUtils = SnackbarUtils(this@ResetPasswordActivity)
            val rootView = findViewById<View>(android.R.id.content)
            if (user != null) {
                snackbarUtils.showSnackbar(rootView, user.message)
                Handler(Looper.getMainLooper()).postDelayed({
                    val intent =
                        Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 800)
            }}


        }




    }
    private fun showLoader() {
//        if (!isFinishing && !isDestroyed) { // Check if activity is running
//            customLoaderDialog.show()
//        }
        progressBar.visibility = View.VISIBLE
        resetBtn.visibility= View.GONE
    }


    private fun hideLoader() {
//        if (customLoaderDialog.isShowing) {
//            customLoaderDialog.dismiss()
//        }
        progressBar.visibility = View.GONE
        resetBtn.visibility= View.VISIBLE
    }

}