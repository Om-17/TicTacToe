package com.example.tictactoe

import CustomConfirmDialog
import WebSocketManager
import WebSocketResponseListener
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.tictactoe.utils.CustomLoaderDialog
import okhttp3.Response
import org.json.JSONObject

class OnlineGameActivity : AppCompatActivity() {
    private lateinit var webSocketManager: WebSocketManager
    private lateinit var  playerOneName : TextView
    private lateinit var  playerTwoName : TextView
    private lateinit var customLoaderDialog: CustomLoaderDialog


    private lateinit var  playerOnelayout: LinearLayout
    private lateinit var  playerTwolayout: LinearLayout
    private lateinit var image1: ImageView
    private lateinit var image2: ImageView
    private lateinit var image3: ImageView
    private lateinit var image4: ImageView
    private lateinit var image5: ImageView
    private lateinit var image6: ImageView
    private lateinit var fadeInAnimation: Animation
    private lateinit var image7: ImageView
    private lateinit var image8: ImageView
    private lateinit var image9: ImageView
    private lateinit var roomCode:String
    private var playerTurn: Int = 0
    private lateinit var localPlayerName: String
    private lateinit var remotePlayerName: String
    private lateinit var getPlayerOneName: String
    private lateinit var getPlayerTwoName: String
    private val combinationList = mutableListOf<List<Int>>()
    private var boxPositions = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var totalSelectionBoxes=1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_game)
        val prefs = getSharedPreferences("my_prefs", MODE_PRIVATE)
        localPlayerName = prefs.getString("username", null) ?: ""
        customLoaderDialog = CustomLoaderDialog(this)

        val token = prefs.getString("token", null)
        playerOneName= findViewById(R.id.playerOneName)
        playerTwoName= findViewById(R.id.playerTwoName)
        playerOnelayout=findViewById(R.id.linearLayout4)
        playerTwolayout=findViewById(R.id.linearLayout2)
        image1=findViewById(R.id.imgbtn1)
        image2=findViewById(R.id.imgbtn2)
        image3=findViewById(R.id.imgbtn3)
        image4=findViewById(R.id.imgbtn4)
        image5=findViewById(R.id.imgbtn5)
        image6=findViewById(R.id.imgbtn6)
        image7=findViewById(R.id.imgbtn7)
        image8=findViewById(R.id.imgbtn8)
        image9=findViewById(R.id.imgbtn9)
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        combinationList.add(listOf(0, 1, 2))
        combinationList.add(listOf(3, 4, 5))
        combinationList.add(listOf(6, 7, 8))
        combinationList.add(listOf(0, 3, 6))
        combinationList.add(listOf(1, 4, 7))
        combinationList.add(listOf(2, 5, 8))
        combinationList.add(listOf(2, 4, 6))
        combinationList.add(listOf(0, 4, 8))
        getPlayerOneName = intent.getStringExtra("playerOne").toString()
        getPlayerTwoName = intent.getStringExtra("playerTwo").toString()

        val roomcode=intent.getStringExtra("roomCode")
        roomCode=roomcode.toString()
        playerOneName.text= getPlayerOneName

        playerTwoName.text= getPlayerTwoName

        playerTurn = if (getPlayerOneName == localPlayerName) 1 else 2
        if (playerTurn == 1) {
            playerOnelayout.setBackgroundResource(R.drawable.round_back_blue_border)
            playerTwolayout.setBackgroundResource(R.drawable.round_back_dark_blue)

        }

        remotePlayerName = if (playerTurn == 1) getPlayerTwoName ?: "" else getPlayerOneName ?: ""

        if (token.isNullOrEmpty()) {
            // Token exists, start OnlineModeActivity
            val onlineModeIntent = Intent(this, LoginActivity::class.java)
            startActivity(onlineModeIntent)
            finish()
        }
        else{

                webSocketManager = WebSocketManager(token, object : WebSocketResponseListener {
                    override fun onRoomCreated(roomCode: String) {}
                    override fun onOpen(message: Response) {

                    }
                    override fun onResponse(message: String) {
//                        runOnUiThread {
//                        hideLoader()
//                        }
                        println("online game activity Response message: $message")
                        handleServerResponse(message)


                    }
                    override fun onclose(message: String) {
//                        runOnUiThread {
//                            showLoader()
//                        }

                    }
                    override fun onFailure(error: Throwable) {

                        runOnUiThread {
//                            hideLoader()

                            Toast.makeText(this@OnlineGameActivity, error.message.toString(), Toast.LENGTH_SHORT).show()
                        }
                        println("WebSocket failure: ${error.message}")

                    }
                })
            webSocketManager.connectWebSocket()

            image1.setOnClickListener { view ->
                if (isBoxSelectable(0)) {
                    performAction(view as ImageView, 0)

                }

            }
            image2.setOnClickListener { view ->
                if (isBoxSelectable(1)) {
                    performAction(view as ImageView, 1)

                }

            }
            image3.setOnClickListener { view ->

                if (isBoxSelectable(2)) {
                    performAction(view as ImageView, 2)

                }
            }
            image4.setOnClickListener { view ->

                if (isBoxSelectable(3)) {
                    performAction(view as ImageView, 3)

                }

            }

            image5.setOnClickListener { view ->

                if (isBoxSelectable(4)) {
                    performAction(view as ImageView, 4)

                }

            }
            image6.setOnClickListener { view ->

                if (isBoxSelectable(5)) {
                    performAction(view as ImageView, 5)

                }

            }
            image7.setOnClickListener { view ->

                if (isBoxSelectable(6)) {
                    performAction(view as ImageView, 6)

                }

            }
            image8.setOnClickListener { view ->

                if (isBoxSelectable(7)) {
                    performAction(view as ImageView, 7)

                }

            }
            image9.setOnClickListener { view ->

                if (isBoxSelectable(8)) {
                    performAction(view as ImageView, 8)
                }
            }
        }


    }
    private fun performAction(imageBtn: ImageView, selectedBoxPosition: Int) {
        if (isBoxSelectable(selectedBoxPosition)) {
            boxPositions[selectedBoxPosition] = playerTurn
            updateUIForSelectedBox(imageBtn, playerTurn)
            totalSelectionBoxes++
            updateGameOnServer(selectedBoxPosition,)
            if (checkPlayerWin()) {
                showWinnerDialog(if (playerTurn == 1) playerOneName.text.toString() else playerTwoName.text.toString())
            } else if (totalSelectionBoxes == 9) {
                showDrawDialog()
            } else {
                togglePlayerTurn()
            }
        }
    }
    private fun togglePlayerTurn() {


        playerTurn = if (playerTurn == 1) 2 else 1
        if (playerTurn == 1) {
            playerOnelayout.setBackgroundResource(R.drawable.round_back_blue_border)
            playerTwolayout.setBackgroundResource(R.drawable.round_back_dark_blue)
            // Optionally show toast
            // Toast.makeText(this, playerOneName.text.toString() + " Turn", Toast.LENGTH_SHORT).show()
        } else {
            playerTwolayout.setBackgroundResource(R.drawable.round_back_blue_border)
            playerOnelayout.setBackgroundResource(R.drawable.round_back_dark_blue)
            // Optionally show toast
            // Toast.makeText(this, playerTwoName.text.toString() + " Turn", Toast.LENGTH_SHORT).show()
        }
        setBoxesInteractivity()
    }

    private fun updateUIForSelectedBox(imageView: ImageView, player: Int) {
        imageView.startAnimation(fadeInAnimation)
        val drawableRes = if (player == 1) R.drawable.neon_x else R.drawable.neon_o
        imageView.setImageResource(drawableRes)
        imageView.isEnabled = false // Disable the box after being selected
    }

    private fun getImageViewForPosition(position: Int): ImageView? {
        // Map the position to the corresponding ImageView in your layout
        return when (position) {
            0 -> image1
            1 -> image2
            2 -> image3
            3 -> image4
            4 -> image5
            5 -> image6
            6 -> image7
            7 -> image8
            8 -> image9
            else -> null // Return null if the position is out of range
        }
    }
    private fun showDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Start Again") { dialog, _ -> dialog.dismiss() }
        builder.setCancelable(false)
        builder.show()
    }
    private fun showWinnerDialog(winnerName: String) {
        showDialog("Message", "$winnerName wins")
    }

    private fun showDrawDialog() {
        showDialog("Message", "It is a draw!")
    }

    private fun handleServerResponse(message: String) {
        try{
            val response = JSONObject(message)
            if (response.has("action")) {
//                val player1Response = response.getJSONObject("player1")
                when (response.getString("action")) {
                    "move" -> handleMoveAction(response)
                    "win" ->{
                        val player = response.getInt("player")
                        val winner = if (player == 1) getPlayerOneName else getPlayerTwoName
                        showWinnerDialog(winner)
                    }
                    // Handle other actions for player1...
                }
            }
//            if (response.has("player2")) {
//                val player1Response = response.getJSONObject("player2")
//                when (player1Response.getString("action")) {
//                    "move" -> handleMoveAction(player1Response)
//                    "win" ->{
//                        val player = player1Response.getInt("player")
//                        val winner = if (player == 1) getPlayerOneName else getPlayerTwoName
//                        showWinnerDialog(winner)
//                    }
//                    // Handle other actions for player1...
//                }
//
//            }




        } catch (e: Exception) {
            // Handle other exceptions
            e.printStackTrace()
        }
    }

    private fun handleMoveAction(playerResponse: JSONObject) {
        val position = playerResponse.getInt("position")
        val player = playerResponse.getInt("player")
        val totalSelectionBox = playerResponse.getInt("totalSelectionBoxes")

        runOnUiThread {
            getImageViewForPosition(position)?.let { imageView ->
                updateUIForSelectedBox(imageView, player)
                boxPositions[position] = player
                totalSelectionBoxes = totalSelectionBox
            }
        }

        // Check for any winning condition or draw
        if (checkPlayerWin()) {
            val winner = if (playerTurn == 1) getPlayerTwoName else getPlayerOneName
//            val gameUpdate = JSONObject().apply {
//                put("action", "win")
//                put("player", playerTurn)
//                put("roomCode", roomCode)
//            }

            webSocketManager.sendMessage("{\n  \"action\":\"win\",\n  \"player\":${playerTurn},\n  \"roomCode\":\"${roomCode}\"\n}")
            showWinnerDialog(winner)
        } else if (totalSelectionBoxes == 9) {
            showDrawDialog()
        } else {
            togglePlayerTurn()
        }
    }

    private fun setBoxesInteractivity() {
        val isLocalPlayerTurn = (playerTurn == 1 && localPlayerName == getPlayerOneName) ||
                (playerTurn == 2 && localPlayerName == getPlayerTwoName)
        listOf(image1, image2, image3, image4, image5, image6, image7, image8, image9).forEach { imageView ->
            imageView.isEnabled = isLocalPlayerTurn
        }
    }
    fun checkPlayerWin(): Boolean {
        combinationList.forEach { combination ->
            if (boxPositions[combination[0]] == playerTurn &&
                boxPositions[combination[1]] == playerTurn &&
                boxPositions[combination[2]] == playerTurn) {
                return true
            }
        }
        return false
    }
    private fun updateGameOnServer(selectedBoxPosition: Int) {
        try {
//            val gameUpdate = JSONObject().apply {
//                put("action", "move")
//                put("position", selectedBoxPosition)
//                put("player", playerTurn)
//                put("roomCode", roomCode)
//                put("totalSelectionBoxes", totalSelectionBoxes)
//            }
            webSocketManager.sendMessage("{\n  \"action\":\"move\",\n  \"position\":${selectedBoxPosition},\n  \"player\":${playerTurn},\n  \"totalSelectionBoxes\":${totalSelectionBoxes},\n  \"roomCode\":\"${roomCode}\"\n}")
        }
            catch (e: Exception) {
                // Handle errors in sending message
                e.printStackTrace()
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
                val message = "{\"action\":\"leaveRoom\"}"
                webSocketManager.sendMessage(message)
               // User clicked Yes, perform action for leaving the game
                val onlineModeIntent = Intent(this, OnlineModeActivity::class.java)
                startActivity(onlineModeIntent)

                finish()

            },
            {
                // User clicked No, do nothing

            },
            "Leave Game",
            "Are you sure you want to leave the game?"
        )
        confirmDialog.show()
    }

    private fun isBoxSelectable(boxPosition: Int): Boolean {
        return boxPositions[boxPosition] == 0
    }


    fun showLoader() {
        if (!isFinishing && !isDestroyed) { // Check if activity is running
            customLoaderDialog.show()
        }
    }


    private fun hideLoader() {
        if (customLoaderDialog.isShowing) {
            customLoaderDialog.dismiss()
        }
    }
    override fun onDestroy() {
        println("online onDestory")

        webSocketManager.closeWebSocket()
        super.onDestroy()
    }



    override fun onPause() {
        webSocketManager.closeWebSocket()
        println("online onPause")
        webSocketManager.connectWebSocket()

        super.onPause()
    }

    override fun onResume() {
        println("online onResume")
        webSocketManager.closeWebSocket()
        webSocketManager.connectWebSocket()
        super.onResume()
    }
}