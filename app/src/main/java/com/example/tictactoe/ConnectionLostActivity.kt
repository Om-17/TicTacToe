package com.example.tictactoe

import ConnectivityUtil.isInternetAvailable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.tictactoe.utils.SnackbarUtils

class ConnectionLostActivity : AppCompatActivity() {
    private lateinit var tryAgain: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connection_lost)
        tryAgain = findViewById(R.id.tryAgain) // Assuming you have defined a Button with id tryAgain in your XML layout

        tryAgain.setOnClickListener {
            if (isInternetAvailable(this)) {
                // Internet is available, finish this activity and resume previous activity
                finish()
            } else {
                val snackbarUtils = SnackbarUtils(this)
                val rootView = findViewById<View>(android.R.id.content)
                snackbarUtils.showSnackbar(rootView, "No internet connection")
                // Show a message or perform some action to inform the user that there is still no internet connection
            }
        }
    }
}
