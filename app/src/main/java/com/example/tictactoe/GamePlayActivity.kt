    package com.example.tictactoe
    
    import CustomConfirmDialog
    import WebSocketManager
    import WebSocketResponseListener
    import android.annotation.SuppressLint
    import android.app.AlertDialog
    import android.content.Intent
    import androidx.appcompat.app.AppCompatActivity
    import android.os.Bundle
    import android.os.Handler
    import android.os.Looper
    import android.view.View
    import android.view.animation.Animation
    import android.view.animation.AnimationUtils
    import android.widget.ImageView
    import android.widget.LinearLayout
    import android.widget.TextView
    import android.widget.Toast
    import com.example.tictactoe.models.GameMove
    import com.example.tictactoe.models.User
    import com.example.tictactoe.utils.CustomLoaderDialog
    import com.example.tictactoe.utils.OnlineWinDialog
    import com.example.tictactoe.utils.SnackbarUtils
    import com.google.gson.Gson
    import com.google.gson.JsonSyntaxException
    import okhttp3.Response
    import org.json.JSONException
    import org.json.JSONObject


    class GamePlayActivity : AppCompatActivity() {
        private lateinit var webSocketManager: WebSocketManager
        private lateinit var  playerOneName : TextView
        private lateinit var customLoaderDialog: CustomLoaderDialog
        private lateinit var leavegame:ImageView
        private lateinit var  playerTwoName : TextView
        private lateinit var  playerOnelayout: LinearLayout
        private lateinit var  playerTwolayout: LinearLayout
        private lateinit var turnTxt:TextView
        private lateinit var image1: ImageView
        private lateinit var image2: ImageView
        private lateinit var image3: ImageView
        private lateinit var image4: ImageView
        private lateinit var image5: ImageView
        private lateinit var image6: ImageView
        private lateinit var fadeInAnimation: Animation
        private var playerOneWin=0
        private var playerTwoWin=0
        private lateinit var image7: ImageView
        private lateinit var image8: ImageView
        private lateinit var image9: ImageView
        private var gameEnded = false
        private lateinit var playerOneWinTxt:TextView
        private lateinit var playerTwoWinTxt:TextView
        private lateinit var roomCode:String
        private var playerTurn: Int = 1
        private val combinationList = mutableListOf<List<Int>>()
        private var boxPositions = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
        private var totalSelectionBoxes=1
        private lateinit var localPlayerName: String
        private lateinit var remotePlayerName: String
        private lateinit var getPlayerOneName: String
        private lateinit var getPlayerTwoName: String
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_game_play)
            val prefs = getSharedPreferences("my_prefs", MODE_PRIVATE)
            localPlayerName = prefs.getString("username", null) ?: ""
            customLoaderDialog = CustomLoaderDialog(this)
            leavegame=findViewById(R.id.leavegame)

            val token = prefs.getString("token", null)
            playerOneName= findViewById(R.id.playerOneName)
            playerTwoName= findViewById(R.id.playerTwoName)
            playerOnelayout=findViewById(R.id.linearLayout4)
            playerTwolayout=findViewById(R.id.linearLayout2)
            playerOneWinTxt=findViewById(R.id.playerOneWin)
            playerTwoWinTxt=findViewById(R.id.playerTwoWin)
            turnTxt=findViewById(R.id.turnTxt)
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

            leavegame.setOnClickListener{
                leaveGameDialog()
            }
            val roomcode=intent.getStringExtra("roomCode")
            roomCode=roomcode.toString()
            playerOneName.text= getPlayerOneName
//            playerTurn = if (getPlayerOneName == localPlayerName) 1 else 2
            playerTwoName.text= getPlayerTwoName
            println("player turn $getPlayerOneName $localPlayerName $playerTurn")
            if (playerTurn == 1) {
                playerOnelayout.setBackgroundResource(R.drawable.round_back_blue_border)
                playerTwolayout.setBackgroundResource(R.drawable.round_back_dark_blue)
            }
            turnTxt.text = if (localPlayerName == getPlayerOneName) {
                getString(R.string.your_turn)
            } else {
                getString(R.string.turn, getPlayerOneName)
            }

            remotePlayerName = if (playerTurn == 1) getPlayerTwoName ?: "" else getPlayerOneName ?: ""
            setBoxesInteractivity()


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
                        runOnUiThread {
                        hideLoader()
                        println("online $roomCode resp : $message")

                        try {

                            val jsonObject = JSONObject(message)
                            println("is action there " +jsonObject.has("action").toString())

                            if(jsonObject.has("action")){
                                if(jsonObject.getString("action")=="move"){
                                    val gson = Gson()
                                    val gameMove = gson.fromJson(message, GameMove::class.java)
                                    println("${gameMove.playerTurn} ${gameMove.selectedBoxPosition} ${gameMove.totalSelectionBoxes} ${gameMove.nextTurnPlayer} ")
                                    if  (gameMove.playerTurn!=null &&
                                        gameMove.selectedBoxPosition!=null &&
                                        gameMove.totalSelectionBoxes!=null &&
                                        gameMove.roomCode!=null &&
                                        gameMove.nextTurnPlayer!=null){

                                        handleMoveAction(gameMove)
                                    }
                                }


                            }
                            if(jsonObject.has("message")){
                                if (jsonObject.getString("message") == "Other player left the game.") {
                                    val snackbarUtils = SnackbarUtils(this@GamePlayActivity)
                                    val rootView = findViewById<View>(android.R.id.content)
                                    snackbarUtils.showSnackbar(rootView, jsonObject.getString("message"))

                                    // Handler to post a delayed task
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        val onlineModeIntent = Intent(this@GamePlayActivity, HomeActivity::class.java)
                                        startActivity(onlineModeIntent)
                                        finish()
                                    }, 4000)
                                }


                            }



                        }
                        catch (e:JSONException){
                            e.printStackTrace()
                            println(e.message)
//                                 Handle the error appropriately
                            Toast.makeText(this@GamePlayActivity, "Error in JSON parsing ${e.message}", Toast.LENGTH_SHORT).show()

                        }catch (e: JsonSyntaxException) {
                            // Handle the exception here
                            e.printStackTrace()
                            Toast.makeText(this@GamePlayActivity, "Error in JSON parsing ${e.message}", Toast.LENGTH_SHORT).show()

                        }



                        }
                    }
                    override fun onclose(message: String) {
                        runOnUiThread {
                            showLoader()
                        }
                    }
                    override fun onFailure(error: Throwable) {

                        runOnUiThread {

                            hideLoader()
                            showErrorDialog()
                            Toast.makeText(this@GamePlayActivity, error.message, Toast.LENGTH_SHORT).show()
                            println("WebSocket failure: ${error.message}")

                        }

                    }
                })
                webSocketManager.connectWebSocket()
                showLoader()


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

        private fun playerTurnName(playerTurn: Int) {
            println("playerTurn===> $playerTurn")
            if (playerTurn == 1) {
                // If it's player one's turn
                turnTxt.text = if (localPlayerName == getPlayerOneName) {
                    getString(R.string.your_turn)
                } else {
                    getString(R.string.turn, getPlayerOneName)
                }
            } else {
                // If it's player two's turn
                turnTxt.text = if (localPlayerName == getPlayerTwoName) {
                    getString(R.string.your_turn)
                } else {
                    getString(R.string.turn, getPlayerTwoName)
                }
            }
        }

//        private fun handleMoveAction(jsonObject: JSONObject) {
//
//            if(jsonObject.has("selectedBoxPosition")&&jsonObject.has("playerTurn")&&jsonObject.has("totalSelectionBoxes")){
//                var player=jsonObject.getInt("playerTurn")
//                var selectedBoxPosition= jsonObject.getInt("selectedBoxPosition")
//                var totalseleted=jsonObject.getInt("totalSelectionBoxes")
//                boxPositions[selectedBoxPosition] = player
//
//                var imageBtn = getImageViewForPosition(selectedBoxPosition)
//                if(imageBtn!=null){
//                    if (player == 1) {
//                        imageBtn.startAnimation(fadeInAnimation)
//                        imageBtn.setImageResource(R.drawable.neon_x)
//                        changePlayerTurn(2)
//                        totalSelectionBoxes++
//                    }
//                    else {
//                        imageBtn.startAnimation(fadeInAnimation)
//                        imageBtn.setImageResource(R.drawable.neon_o)
//                        changePlayerTurn(1)
//                        totalSelectionBoxes++
//
//                    }
//                }
//
//
////
//            }
//        }
        private fun handleMoveAction(gameMove:GameMove) {

            if(gameMove.selectedBoxPosition!=null && gameMove.playerTurn!=null&& gameMove.totalSelectionBoxes!=null){
                val player=gameMove.playerTurn
                val selectedBoxPosition=gameMove.selectedBoxPosition
                val totalSeleted=gameMove.totalSelectionBoxes
                boxPositions[selectedBoxPosition] = player
                totalSelectionBoxes=totalSeleted
                println("res total selected =>> $totalSeleted $totalSelectionBoxes ")
                val imageBtn = getImageViewForPosition(selectedBoxPosition)
                if(imageBtn!=null){
                    if (player == 1) {
                        imageBtn.startAnimation(fadeInAnimation)
                        imageBtn.setImageResource(R.drawable.neon_x)
                        if (checkPlayerWin()) {
                            showWinnerDialog(getPlayerOneName)
//
//                            playerOneWin++
//                            playerOneWinTxt.text = playerOneWin.toString()
                        } else if (totalSelectionBoxes == 9) {
                            showDrawDialog()
                        } else {
                            changePlayerTurn(2)
                            totalSelectionBoxes++
                        }





                    }
                    else {
                        imageBtn.startAnimation(fadeInAnimation)
                        imageBtn.setImageResource(R.drawable.neon_o)





                        if (checkPlayerWin()) {
                            showWinnerDialog(getPlayerTwoName)
//                            playerTwoWin++
//                            playerTwoWinTxt.text = playerTwoWin.toString()
                        } else if (totalSelectionBoxes == 9) {
                            showDrawDialog()
                        } else {
                            changePlayerTurn(1)
                            totalSelectionBoxes++
                        }


                    }
                }


//
            }
        }

        private fun performAction(imageBtn: ImageView, selectedBoxPosition: Int) {
            if (isBoxSelectable(selectedBoxPosition)) {
                boxPositions[selectedBoxPosition] = playerTurn

//            Toast.makeText(this@GamePlayActivity, selectedBoxPosition.toString(), Toast.LENGTH_SHORT).show()
            try {
                val nextTurnPlayer=if (playerTurn==1) 2 else 1
                val gameMove = GameMove(
                    action = "move",
                    selectedBoxPosition = selectedBoxPosition,  // replace with actual value
                    playerTurn = playerTurn,                   // replace with actual value
                    nextTurnPlayer = nextTurnPlayer,           // replace with actual value
                    totalSelectionBoxes = totalSelectionBoxes, // replace with actual value
                    roomCode = roomCode                   // assuming 'room' is an instance of Room class
                )

                val jsonString = Gson().toJson(gameMove)

                println("Sending JSON: $jsonString") // For debugging
                webSocketManager.sendMessage(jsonString)

                if (playerTurn == 1) {
                    imageBtn.startAnimation(fadeInAnimation)
                    imageBtn.setImageResource(R.drawable.neon_x)


                    if (checkPlayerWin()) {
                        showWinnerDialog(getPlayerOneName)

                    } else if (totalSelectionBoxes == 9) {
                        showDrawDialog()
                    } else {
                        changePlayerTurn(2)

                    }
                }
                else {
                    imageBtn.startAnimation(fadeInAnimation)
                    imageBtn.setImageResource(R.drawable.neon_o)
                 // Disable the box after being selected
                    if (checkPlayerWin()) {
                        showWinnerDialog(getPlayerTwoName)
//                        playerTwoWin++

//                        playerTwoWinTxt.text = playerTwoWin.toString()

                    } else if (totalSelectionBoxes == 9) {
                        showDrawDialog()
                    } else {
                        changePlayerTurn(1)

                    }



                }

            }
            catch (e: Exception) {
                // Handle errors in sending message
                e.printStackTrace()
            }}
        }
    
        private fun isBoxSelectable(boxPosition: Int): Boolean {
            return boxPositions[boxPosition] == 0
        }
        private fun setBoxesInteractivity() {
            val isLocalPlayerTurn = (playerTurn == 1 && localPlayerName == getPlayerOneName) ||
                    (playerTurn == 2 && localPlayerName == getPlayerTwoName)
            println("button enabled $isLocalPlayerTurn")
            listOf(image1, image2, image3, image4, image5, image6, image7, image8, image9).forEach { imageView ->
                imageView.isEnabled = isLocalPlayerTurn
            }
        }
        private  fun changePlayerTurn(currentPlayerTurn:Int){
            playerTurn=currentPlayerTurn
            playerTurnName(playerTurn)
            setBoxesInteractivity()
            if (playerTurn == 1) {
                playerOnelayout.setBackgroundResource(R.drawable.round_back_blue_border)
                playerTwolayout.setBackgroundResource(R.drawable.round_back_dark_blue)
            } else {


                playerTwolayout.setBackgroundResource(R.drawable.round_back_blue_border)
                playerOnelayout.setBackgroundResource(R.drawable.round_back_dark_blue)
            }

        }



        fun checkPlayerWin(): Boolean {
            var response = false

            for (i in 0 until combinationList.size) {
                val combination = combinationList[i]
                if (boxPositions[combination[0]] == playerTurn && boxPositions[combination[1]] == playerTurn && boxPositions[combination[2]] == playerTurn) {
                    response = true
                }
            }

            return response
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
        private fun restartMatch(){
            boxPositions = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
            gameEnded = false
            playerTurn=1
            totalSelectionBoxes=1
            playerOnelayout.setBackgroundResource(R.drawable.round_back_blue_border)
            playerTwolayout.setBackgroundResource(R.drawable.round_back_dark_blue)
            image1.setImageResource(R.drawable.bg_transparent)
            image2.setImageResource(R.drawable.bg_transparent)
            image3.setImageResource(R.drawable.bg_transparent)
            image4.setImageResource(R.drawable.bg_transparent)
            image5.setImageResource(R.drawable.bg_transparent)
            image6.setImageResource(R.drawable.bg_transparent)
            image7.setImageResource(R.drawable.bg_transparent)
            image8.setImageResource(R.drawable.bg_transparent)
            image9.setImageResource(R.drawable.bg_transparent)
            turnTxt.text = if (localPlayerName == getPlayerOneName) {
                getString(R.string.your_turn)
            } else {
                getString(R.string.turn, getPlayerOneName)
            }
        }
        private fun showDialog(title: String, message: String) {
            val customDialog =OnlineWinDialog(
                this@GamePlayActivity,
                message,
                {
                    restartMatch()
                },
                title
            )

            customDialog.show()
        }
        private fun showWinnerDialog(winnerName: String) {
            if (!gameEnded) {
                gameEnded = true // Add this flag to indicate that the game has ended
                showDialog("Wins", "$winnerName wins")

                if (winnerName == getPlayerOneName) {
                    playerOneWin++
                    playerOneWinTxt.text = playerOneWin.toString()
                } else if (winnerName == getPlayerTwoName) {
                    playerTwoWin++
                    playerTwoWinTxt.text = playerTwoWin.toString()
                }
            }
        }




        private fun showDrawDialog() {
            showDialog("Draw", "It is a draw!")
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
                "Are you sure you want to leave the game?"
            )
            confirmDialog.show()
        }

        override fun onPause() {
            println("online onPause")

            if(!webSocketManager.isWebSocketConnected()) {
                webSocketManager.connectWebSocket()
            }

            super.onPause()
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
        override fun onRestart() {
            if(!webSocketManager.isWebSocketConnected()) {
                webSocketManager.connectWebSocket()
            }
            println("online onRestart")
            super.onRestart()
        }
        private fun showErrorDialog() {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Connection Failed")
            builder.setCancelable(false)
            builder.setMessage("Unable to connect to the game server. Please try again.")
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
        override fun onResume() {
            println("online onResume")
            if(!webSocketManager.isWebSocketConnected()) {
                webSocketManager.connectWebSocket()
            }
            super.onResume()
        }
        override fun onDestroy() {
            println("online onDestory")
            if (this::webSocketManager.isInitialized) {
                val message = "{\"action\":\"leaveRoom\"}"

                webSocketManager.sendMessage(message)

                webSocketManager.closeWebSocket()
            }

            super.onDestroy()

        }
    }