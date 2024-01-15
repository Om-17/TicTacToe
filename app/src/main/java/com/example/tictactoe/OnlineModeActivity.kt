package com.example.tictactoe

import CustomConfirmDialog
import WebSocketManager
import WebSocketResponseListener
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

import okhttp3.*
import okio.ByteString
import android.app.AlertDialog
import android.app.ProgressDialog
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.tictactoe.utils.CustomLoaderDialog
import org.json.JSONObject

class OnlineModeActivity : AppCompatActivity() {
    private lateinit var webSocketManager: WebSocketManager
    private lateinit var createRoomBtn:Button
    private lateinit var logoutBtn:ImageView
    private lateinit var createRoomDialog: CreateRoomDialog
    private var dialog: AlertDialog? = null
    private lateinit var customLoaderDialog: CustomLoaderDialog

    private lateinit var usernametxt: TextView

    private lateinit var joinRoomBtn:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_mode)
        createRoomBtn=findViewById(R.id.createroomBtn)
        joinRoomBtn=findViewById(R.id.joinroomBtn)
        logoutBtn=findViewById(R.id.logoutBtn)
        usernametxt=findViewById(R.id.usernameTxt)
        customLoaderDialog = CustomLoaderDialog(this)
//        getSharedPreferences("my_prefs", MODE_PRIVATE).edit().clear().apply()
        if(!ConnectivityUtil.isInternetAvailable(this)) {

            val mainIntent = Intent(this, ConnectionLostActivity::class.java)
            startActivity(mainIntent)
            finish()
        }
        val prefs = getSharedPreferences("my_prefs", MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val username = prefs.getString("username", null)
        if (token.isNullOrEmpty()) {
            // Token exists, start OnlineModeActivity
            val onlineModeIntent = Intent(this, LoginActivity::class.java)
            startActivity(onlineModeIntent)
            finish()
        }
       else{
            // WebSocket connection logic

            webSocketManager = WebSocketManager(token, object : WebSocketResponseListener {
                override fun onRoomCreated(roomCode: String) {
                    // Update dialog with the room code
                    println("roomcode : $roomCode")
                    hideLoader()
                    runOnUiThread{
                        createRoomDialog = CreateRoomDialog(this@OnlineModeActivity, webSocketManager,roomCode)
                        createRoomDialog.show()
                    }




                }
                override fun onResponse(message: String) {
                    // Handle the response message from the server
                    hideLoader()

                    if (message.startsWith("{\"error\":")) {
                        // Assuming the message is JSON formatted
                        val errormessage = JSONObject(message).getString("error")

                        // Running UI updates on the main thread
                        runOnUiThread {
                            Toast.makeText(this@OnlineModeActivity, errormessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                    if (message.startsWith("{\"userjoined\":")) {
                        // Assuming the message is JSON formatted
                        val startgamemsg = JSONObject(message).getString("userjoined")

                        // Running UI updates on the main thread
                        runOnUiThread {
                            Toast.makeText(this@OnlineModeActivity, startgamemsg, Toast.LENGTH_SHORT).show()
                        }
                    }
                    println("online Response message: $message")
                    // You can perform additional actions based on the response if needed
                }

                override fun onFailure(error: Throwable) {
                    hideLoader()
                    // Handle the failure
                    runOnUiThread {
                        showErrorDialog()
                        Toast.makeText(this@OnlineModeActivity, error.message, Toast.LENGTH_SHORT).show()

                    }
                    println("WebSocket failure: ${error.message}")


                }
            })
            showLoader()
            webSocketManager.connectWebSocket()

        }
        usernametxt.text=username
        logoutBtn.setOnClickListener{
            showLogoutConfirmationDialog()
        }
        createRoomBtn.setOnClickListener{

            val message = "{\"action\":\"createRoom\"}"
            webSocketManager.sendMessage(message)

        }
        joinRoomBtn.setOnClickListener(::showJoinRoomDialog)
    }
    private fun showJoinRoomDialog(view:View) {
        val joinRoomDialog = JoinRoomDialog(
            this,
            onJoinRoom = { roomCode ->
                println("roomcode===$roomCode")
                val message = "{\"action\":\"joinRoom\",\"roomCode\":\"$roomCode\",}"
                webSocketManager.sendMessage(message)
                // Handle room code
                // You can perform actions with the roomCode obtained from the dialog
                // For example, pass it to another function or start a new activity
                // e.g., startNewActivityWithRoomCode(roomCode)
            }
        )

        joinRoomDialog.show()
    }
    private fun showLogoutConfirmationDialog() {
        val confirmDialog = CustomConfirmDialog(
            this,
            {
                // User clicked Yes, perform logout
                logout()
            },
            {
                // User clicked No, do nothing
            },
            "Logout",
            "Are you sure you want to logout?"
        )

        confirmDialog.show()
    }
    private fun showErrorDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Connection Failed")
        builder.setCancelable(false)
        builder.setMessage("Unable to connect to the WebSocket. Please try again.")
        builder.setPositiveButton("Try Again") { dialog, _ ->
            dialog.dismiss()
            showLoader()
            webSocketManager.connectWebSocket() // Attempt to reconnect
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->

            startActivity(Intent(this, HomeActivity::class.java))
            dialog.dismiss()
        }
        builder.show()
    }
    private fun showLoader() {
        customLoaderDialog.show()
    }

    private fun hideLoader() {
        if (customLoaderDialog.isShowing) {
            customLoaderDialog.dismiss()
        }
    }


    private fun logout() {
        // Perform logout actions
        getSharedPreferences("my_prefs", MODE_PRIVATE).edit().clear().apply()
        val onlineModeIntent = Intent(this, LoginActivity::class.java)
        startActivity(onlineModeIntent)
        finish()
    }
}