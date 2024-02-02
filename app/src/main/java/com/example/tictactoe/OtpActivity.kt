package com.example.tictactoe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import com.example.tictactoe.api.ForgotPasswordApi
import com.example.tictactoe.api.ForgotPasswordApi.Companion.asGetForgot
import com.example.tictactoe.api.ProfileApi.Companion.asGetUser
import com.example.tictactoe.models.OTP
import com.example.tictactoe.utils.SnackbarUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection

class OtpActivity : AppCompatActivity() {

    private lateinit var otpVerifyBtm: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var otpDigit1:EditText
    private lateinit var otpDigit2:EditText
    private lateinit var otpDigit3:EditText
    private lateinit var otpDigit4:EditText
    private lateinit var otpDigit5:EditText
    private lateinit var otpDigit6:EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)
        otpVerifyBtm=findViewById(R.id.otpVerfiyBtn)
        progressBar=findViewById(R.id.progressBar2)
        otpDigit1=  findViewById<EditText>(R.id.otpDigit1)
        otpDigit2=  findViewById<EditText>(R.id.otpDigit2)
        otpDigit3=  findViewById<EditText>(R.id.otpDigit3)
        otpDigit4=  findViewById<EditText>(R.id.otpDigit4)
        otpDigit5=  findViewById<EditText>(R.id.otpDigit5)
        otpDigit6=  findViewById<EditText>(R.id.otpDigit6)

        val backBtn: ImageView = findViewById(R.id.backBtn)
        backBtn.setOnClickListener {

            onBackPressedDispatcher.onBackPressed()
        }

        otpVerifyBtm.setOnClickListener(::onOTPVerfiyClicked)
        setupOtpInputs()
    }
    private  fun onOTPVerfiyClicked(view: View){

        if (!ConnectivityUtil.isInternetAvailable(this)) {
            // Show a message or perform some action to inform the user that there is no internet connection
            val snackbarUtils = SnackbarUtils(this)
            val rootView = findViewById<View>(android.R.id.content)
            snackbarUtils.showSnackbar(rootView, "No internet connection")
            return
        }
        val otpCode = otpDigit1.text.toString() +
                otpDigit2.text.toString() +
                otpDigit3.text.toString() +
                otpDigit4.text.toString() +
                otpDigit5.text.toString() +
                otpDigit6.text.toString()

        if (otpCode.length != 6) {
            // Show a message that OTP code must be 6 digits
            val snackbarUtils = SnackbarUtils(this)
            val rootView = findViewById<View>(android.R.id.content)
            snackbarUtils.showSnackbar(rootView, "OTP code must be 6 digits")
            return
        }
        val email=intent.getStringExtra("email").toString()

        val data =OTP(otp_code = otpCode,email=email)
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                showLoader()

            }
            val response = ForgotPasswordApi.otpVerify(data)
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
                            val snackbarUtils = SnackbarUtils(this@OtpActivity)
                            val rootView = findViewById<View>(android.R.id.content)
                            snackbarUtils.showSnackbar(rootView, message)

                        }
                    }
                    return@launch

                }
                val otpres = response.asGetForgot()
                withContext(Dispatchers.Main) {

                    if (otpres != null) {

                        if (otpres.isvalid) {
                            val snackbarUtils = SnackbarUtils(this@OtpActivity)
                            val rootView = findViewById<View>(android.R.id.content)
                            snackbarUtils.showSnackbar(rootView, otpres.message)

                            if (otpres.userId != 0) {
                                Handler(Looper.getMainLooper()).postDelayed({
                                    val intent =
                                        Intent(this@OtpActivity, ResetPasswordActivity::class.java)
                                    intent.putExtra("userId", otpres.userId)
                                    startActivity(intent)
                                    finish()
                                }, 800)
                            }
                        }
                        val snackbarUtils = SnackbarUtils(this@OtpActivity)
                        val rootView = findViewById<View>(android.R.id.content)
                        snackbarUtils.showSnackbar(rootView, otpres.message)


                    }
                }


            }


        }
    }
    private fun setupOtpInputs() {
        val editTexts = listOf(
            findViewById<EditText>(R.id.otpDigit1),
            findViewById<EditText>(R.id.otpDigit2),
            findViewById<EditText>(R.id.otpDigit3),
            findViewById<EditText>(R.id.otpDigit4),
            findViewById<EditText>(R.id.otpDigit5),
            findViewById<EditText>(R.id.otpDigit6)
        )

        editTexts.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.toString().length == 1 && index < editTexts.size - 1) {
                        editTexts[index + 1].requestFocus()
                    }
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            // Optional: Handle backspace key
            editText.setOnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && editText.text.isEmpty() && index > 0) {
                    editTexts[index - 1].requestFocus()
                }
                false
            }
        }}
    private fun showLoader() {

        progressBar.visibility = View.VISIBLE
        otpVerifyBtm.visibility= View.GONE
    }


    private fun hideLoader() {

        progressBar.visibility = View.GONE
        otpVerifyBtm.visibility= View.VISIBLE
    }
}