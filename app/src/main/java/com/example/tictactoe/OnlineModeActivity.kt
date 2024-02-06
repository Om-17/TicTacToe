package com.example.tictactoe

import CustomConfirmDialog
import WebSocketManager
import WebSocketResponseListener
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

import okhttp3.*
import okio.ByteString
import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import com.example.tictactoe.api.UserApi.Companion.asUser
import com.example.tictactoe.models.Token
import com.example.tictactoe.utils.CustomLoaderDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

import com.example.tictactoe.api.VerifyTokenApi
import com.example.tictactoe.api.VerifyTokenApi.Companion.asToken
import com.example.tictactoe.utils.SnackbarUtils
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection

class OnlineModeActivity : AppCompatActivity() {
    private lateinit var webSocketManager: WebSocketManager
    private lateinit var createRoomBtn:Button
    private lateinit var logoutBtn:ImageView
    private lateinit var createRoomDialog: CreateRoomDialog
    private lateinit var joinRoomDialog:JoinRoomDialog
    private var dialog: AlertDialog? = null
    private lateinit var customLoaderDialog: CustomLoaderDialog

    private lateinit var usernametxt: TextView
    private lateinit var userImg:ImageView

    private lateinit var joinRoomBtn:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_mode)
        createRoomBtn=findViewById(R.id.createroomBtn)
        joinRoomBtn=findViewById(R.id.joinroomBtn)
        userImg=findViewById(R.id.user_img)
        logoutBtn=findViewById(R.id.logoutBtn)
        usernametxt=findViewById(R.id.usernameTxt)
        customLoaderDialog = CustomLoaderDialog(this)
//        getSharedPreferences("my_prefs", MODE_PRIVATE).edit().clear().apply()

        if(!ConnectivityUtil.isInternetAvailable(this)) {

            val mainIntent = Intent(this, ConnectionLostActivity::class.java)
            startActivity(mainIntent)
            finish()
        }
        else{
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
                tokenVerifyFun(token)
                userImg.setOnClickListener{
                    val mainIntent = Intent(this, ProfileActivity::class.java)
                    mainIntent.putExtra("token",token)
                    startActivity(mainIntent)
                }
                // WebSocket connection logic
                webSocketManager = WebSocketManager(token, object : WebSocketResponseListener {
                    override fun onOpen(message: Response) {

                    }

                    override fun onRoomCreated(roomCode: String) {
                        // Update dialog with the room code
                        println("roomcode : $roomCode")
                        runOnUiThread {
                            hideLoader()

                            createRoomDialog = CreateRoomDialog(
                                this@OnlineModeActivity,
                                webSocketManager,
                                roomCode
                            )
                            createRoomDialog.show()
                        }


                    }

                    override fun onclose(message: String) {
                        println(message)

                    }

                    override fun onResponse(message: String) {
                        runOnUiThread {

                            // Handle the response message from the server
                            println(" Response message: $message")

                            val jsonResponse = JSONObject(message)

                            // Check for error
                            if (jsonResponse.has("error")) {
                                val errorMessage = jsonResponse.getString("error")

                                Toast.makeText(
                                    this@OnlineModeActivity,
                                    errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()

                                val builder = AlertDialog.Builder(this@OnlineModeActivity)
                                builder.setTitle("Error")
                                builder.setCancelable(false)
                                builder.setMessage(errorMessage)
                                builder.setPositiveButton("ok") { dialog, _ ->

                                    dialog.dismiss()

                                }

                                builder.show()

                            }
                            if (jsonResponse.has("status")) {
                                val errorMessage = jsonResponse.getString("status")

                                val builder = AlertDialog.Builder(this@OnlineModeActivity)
                                builder.setTitle("Message")
                                builder.setCancelable(false)
                                builder.setMessage(errorMessage)
                                builder.setPositiveButton("ok") { dialog, _ ->

                                    dialog.dismiss()

                                }

                                builder.show()

                            }
                            if (jsonResponse.has("userjoined")) {
                                val startGameMsg = jsonResponse.getString("userjoined")

                                // Check for player names inside the userjoined condition
                                if (jsonResponse.has("player1name") && jsonResponse.has("player2name")) {
                                    val player1Name = jsonResponse.getString("player1name")
                                    val player2Name = jsonResponse.getString("player2name")
                                    val roomCode = jsonResponse.getString("roomCode")

                                    Toast.makeText(
                                        this@OnlineModeActivity,
                                        "$startGameMsg $player1Name, $player2Name",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    val onlineGameIntent = Intent(
                                        this@OnlineModeActivity,
                                        GamePlayActivity::class.java
                                    )
                                    onlineGameIntent.putExtra("playerOne", player1Name)
                                    onlineGameIntent.putExtra("playerTwo", player2Name)
                                    onlineGameIntent.putExtra("roomCode", roomCode)
                                    startActivity(onlineGameIntent)
                                    finish()

                                } else {

                                    Toast.makeText(
                                        this@OnlineModeActivity,
                                        startGameMsg,
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }

                            }

                        }
                        hideLoader()

                        // You can perform additional actions based on the response if needed
                    }

                    override fun onFailure(error: Throwable) {
                        // Handle the failure
                        runOnUiThread {
                            hideLoader()

                            showErrorDialog()
                            Toast.makeText(
                                this@OnlineModeActivity,
                                error.message,
                                Toast.LENGTH_SHORT
                            ).show()

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



    }
    private fun showJoinRoomDialog(view:View) {
         joinRoomDialog = JoinRoomDialog(
            this,
            onJoinRoom = { roomCode ->
                println("roomcode===$roomCode")
                val message = "{\"action\":\"joinRoom\",\"roomCode\":\"$roomCode\"}"
                webSocketManager.sendMessage(message)

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
        if (!isFinishing) { // Add this check to prevent BadTokenException
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
    @Deprecated("Deprecated in Java")
    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {

        leaveGameDialog()
    }

    private fun leaveGameDialog() {
        val confirmDialog = CustomConfirmDialog(
            this,
            {

                // User clicked Yes, perform action for leaving the game
                val onlineModeIntent = Intent(this, HomeActivity::class.java)
                startActivity(onlineModeIntent)

                finish()

            },
            {
                // User clicked No, do nothing

            },
            "Leave Game",
            "Are you sure you want to leave the online game mode?"
        )
        confirmDialog.show()
    }

    private fun logout() {
        // Perform logout actions
        getSharedPreferences("my_prefs", MODE_PRIVATE).edit().clear().apply()
        val onlineModeIntent = Intent(this, LoginActivity::class.java)
        startActivity(onlineModeIntent)
        finish()
    }



    override fun onDestroy() {
//        println("modeactivity onDestory")
//
        if (this::webSocketManager.isInitialized) {
            webSocketManager.closeWebSocket()
        }
        super.onDestroy()
    }

    private  fun tokenVerifyFun(token:String) {
        val tokenObj = Token(
            token = token
        )
        CoroutineScope(Dispatchers.IO).launch {

            val response = VerifyTokenApi.verifyToken(tokenObj)
            if (response.statusCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                val error = response.getError()

                withContext(Dispatchers.Main) {
                    println(error)
                    if (error != null) {

                        val message = error.error
                        val snackbarUtils = SnackbarUtils(this@OnlineModeActivity)
                        val rootView = findViewById<View>(android.R.id.content)
                        snackbarUtils.showSnackbar(rootView, message)
                        getSharedPreferences("my_prefs", MODE_PRIVATE).edit().clear().apply()

                        Handler(Looper.getMainLooper()).postDelayed({
                            val intent = Intent(this@OnlineModeActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }, 800)

                    }

                }

                return@launch

            }
            if (response.statusCode == HttpURLConnection.HTTP_OK) {

            }
            withContext(Dispatchers.IO){


                    val tokenmesage = response.asToken()
                if (tokenmesage != null) {
                    println(tokenmesage.message)
                }
            }
        }
    }
//    override fun onPause() {
//        webSocketManager.closeWebSocket()
//        println("onPause")
//        webSocketManager.connectWebSocket()
//
//        super.onPause()
//    }
//
//    override fun onResume() {
//        println("onResume")
//        webSocketManager.closeWebSocket()
//        webSocketManager.connectWebSocket()
//        super.onResume()
//    }


}