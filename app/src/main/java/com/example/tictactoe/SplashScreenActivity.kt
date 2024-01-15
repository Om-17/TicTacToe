package com.example.tictactoe

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.content.Intent

import android.os.Handler
import android.os.Looper
import android.widget.Button

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT: Long = 3000 // 3 seconds
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )

        setContentView(R.layout.activity_splash_screen)
        Handler(Looper.getMainLooper()).postDelayed({
            // Create an Intent to start the main activity
            val mainIntent = Intent(this@SplashScreenActivity, HomeActivity::class.java)
            startActivity(mainIntent)

            // Close the splash activity to prevent going back to it
            finish()
        }, SPLASH_TIME_OUT)


    }
}
