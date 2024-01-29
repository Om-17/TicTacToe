package com.example.tictactoe

import CustomConfirmDialog
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView

class HomeActivity : AppCompatActivity() {
    private lateinit var offlineBtn: Button
    private lateinit var onlineBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN
//        )
        setContentView(R.layout.activity_home)

        findViewById<ImageView>(R.id.exitBtn).setOnClickListener {
            showExitConfirmationDialog()
        }

        offlineBtn=findViewById(R.id.offline)
        onlineBtn=findViewById(R.id.online)

        offlineBtn.setOnClickListener{
            val mainIntent = Intent(this, AddPlayers::class.java)
            startActivity(mainIntent)
//            finish()
        }
        onlineBtn.setOnClickListener{
            val mainIntent = Intent(this, OnlineModeActivity::class.java)
            startActivity(mainIntent)
//            val onlineGameIntent = Intent(this@HomeActivity, GamePlayActivity::class.java)
//            onlineGameIntent.putExtra("playerOne", "om")
//            onlineGameIntent.putExtra("playerTwo", "coder")
//            onlineGameIntent.putExtra("roomCode","DE97E4")
//            startActivity(onlineGameIntent)
//            finish()
        }
    }
    private fun showExitConfirmationDialog() {
        val exitDialog = CustomConfirmDialog(
            this,
            {
                // User clicked Yes, exit the activity
                finishAffinity()
            },
            {
                // User clicked No, do nothing
            },
            "Exit",
            "Are you sure you want to exit?"
        )

        exitDialog.show()
    }
    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {

        showExitConfirmationDialog()
    }

}