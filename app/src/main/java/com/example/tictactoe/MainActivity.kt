package com.example.tictactoe

import android.annotation.SuppressLint
import android.content.Context

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.media.MediaPlayer

import android.os.Vibrator
import android.view.WindowManager
import android.view.animation.Animation

import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.view.animation.AnimationUtils
import java.util.Locale

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private var mediaPlayerWin: MediaPlayer? = null
    private var mediaPlayerBoxTap: MediaPlayer? = null
    private var mediaPlayerStartGame: MediaPlayer? = null
    private var mediaPlayerDraw:MediaPlayer?=null
    private var mediaPlayerBg: MediaPlayer? = null

    private var isMuted = false
    private lateinit var vibrator: Vibrator
    private lateinit var fadeInAnimation: Animation
    private var isBot=false
    private lateinit var  playerOneName :TextView
    private lateinit var  playerTwoName :TextView
    private lateinit var  playerOnelayout:LinearLayout
    private lateinit var  playerTwolayout:LinearLayout

    private lateinit var image1:ImageView
    private lateinit var image2:ImageView
    private lateinit var image3:ImageView
    private lateinit var image4:ImageView
    private lateinit var image5:ImageView
    private lateinit var image6:ImageView
    private lateinit var image7:ImageView
    private lateinit var image8:ImageView
    private lateinit var image9:ImageView

    private var playerOneWin=0
    private var playerTwoWin=0
    private lateinit var volImg:ImageView
    private lateinit var restartlayout:LinearLayout
    private lateinit var audiolayoutbtn:LinearLayout
    private lateinit var playerOneWinTxt:TextView
    private lateinit var playerTwoWinTxt:TextView
    private val ticTacToeBot = TicTacToeBot()

    private val combinationList = mutableListOf<List<Int>>()
    private var boxPositions = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
    private var playerTurn=1
    private var totalSelectionBoxes=1
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_main)
        mediaPlayerWin = MediaPlayer.create(this, R.raw.winner_sound)
        mediaPlayerBoxTap = MediaPlayer.create(this, R.raw.box_tap)
        mediaPlayerDraw=MediaPlayer.create(this,R.raw.draw)
        mediaPlayerBg = MediaPlayer.create(this, R.raw.bg_audio)

        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        volImg=findViewById(R.id.vol_img)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        audiolayoutbtn = findViewById(R.id.audiolayoutbtn)
        playerOneWinTxt=findViewById(R.id.playerOneWin)
        playerTwoWinTxt=findViewById(R.id.playerTwoWin)

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
        restartlayout=findViewById(R.id.restartlayout)
        combinationList.add(listOf(0, 1, 2))
        combinationList.add(listOf(3, 4, 5))
        combinationList.add(listOf(6, 7, 8))
        combinationList.add(listOf(0, 3, 6))
        combinationList.add(listOf(1, 4, 7))
        combinationList.add(listOf(2, 5, 8))
        combinationList.add(listOf(2, 4, 6))
        combinationList.add(listOf(0, 4, 8))
        isBot=intent.getBooleanExtra("isBot",false)

        audiolayoutbtn.setOnClickListener {
            toggleMute()
        }
        val getPlayerOneName = intent.getStringExtra("playerOne")
        val getPlayerTwoName = intent.getStringExtra("playerTwo")
        isMuted = intent.getBooleanExtra("isMuted", false)
        if(isMuted){

            volImg.setImageResource(R.drawable.neon_mute)
            mediaPlayerWin?.setVolume(0.0f, 0.0f)
            mediaPlayerBoxTap?.setVolume(0.0f, 0.0f)
            mediaPlayerBg?.isLooping = true
            mediaPlayerBg?.setVolume(0.0f, 0.0f)

            mediaPlayerStartGame?.setVolume(0.0f, 0.0f)
            mediaPlayerDraw?.setVolume(0.0f, 0.0f)
        }else{
            volImg.setImageResource(R.drawable.neon_audio)
            mediaPlayerBg?.isLooping = true
            mediaPlayerBg?.setVolume(0.2f, 0.2f)
            mediaPlayerBg?.start()
            mediaPlayerWin?.setVolume(1.0f, 1.0f)
            mediaPlayerBoxTap?.setVolume(1.0f, 1.0f)
            mediaPlayerStartGame?.setVolume(1.0f, 1.0f)
            mediaPlayerDraw?.setVolume(1.0f, 1.0f)

        }

        playerOneName.text= getPlayerOneName.toString()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
        playerTwoName.text= getPlayerTwoName.toString()
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }

        restartlayout.setOnClickListener {
            fullrestartMatch()
        }
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


//    private fun performAction(imageBtn: ImageView, selectedBoxPosition: Int) {
//    if (isBoxSelectable(selectedBoxPosition)) {
//        // Player's move
//        boxPositions[selectedBoxPosition] = playerTurn
//        vibrator.vibrate(100)
//        mediaPlayerBoxTap?.start()
//
//        if (playerTurn == 1) {
//            imageBtn.startAnimation(fadeInAnimation)
//            imageBtn.setImageResource(R.drawable.neon_x)
//
//            if (checkPlayerWin()) {
//                val winDialog = WinDialog(this@MainActivity, playerOneName.text.toString() + " wins", this, "Win")
//                winDialog.setCancelable(false)
//                winDialog.show()
//                mediaPlayerWin?.start()
//                playerOneWin++
//                playerOneWinTxt.text = playerOneWin.toString()
//            } else if (totalSelectionBoxes == 9) {
//                val winDialog = WinDialog(this@MainActivity, "It is a draw!", this, "Draw")
//                winDialog.setCancelable(false)
//                winDialog.show()
//                mediaPlayerDraw?.start()
//            } else {
//                changePlayerTurn(2)
//                totalSelectionBoxes++
//            }
//        } else {
//            imageBtn.startAnimation(fadeInAnimation)
//            imageBtn.setImageResource(R.drawable.neon_o)
//
//            if (checkPlayerWin()) {
//                val winDialog = WinDialog(this@MainActivity, playerTwoName.text.toString().capitalize() + " wins", this, "Win")
//                winDialog.setCancelable(false)
//                winDialog.show()
//                mediaPlayerWin?.start()
//                playerTwoWin++
//                playerTwoWinTxt.text = playerTwoWin.toString()
//            } else if (totalSelectionBoxes == 9) {
//                val winDialog = WinDialog(this@MainActivity, "It is a draw!", this, "Draw")
//                winDialog.setCancelable(false)
//                winDialog.show()
//                mediaPlayerDraw?.start()
//            } else {
//                changePlayerTurn(1)
//                totalSelectionBoxes++
//            }
//        }
//
//        // Check for bot's turn
//        if (isBot && playerTurn == 2) {
//            val botMove = ticTacToeBot.makeMove(boxPositions)
//            Toast.makeText(this,botMove.toString(), Toast.LENGTH_SHORT).show()
//            if (botMove >= 0) {
//                // Make the bot's move
//                val botImageView = getImageViewForPosition(botMove)
//                if (botImageView != null) {
//                    performAction(botImageView, botMove)
//                }
//            }
//        }
//    }
//}
//
private fun performAction(imageBtn: ImageView, selectedBoxPosition: Int) {
    if (isBoxSelectable(selectedBoxPosition)) {
        // Player's move
        boxPositions[selectedBoxPosition] = playerTurn
        vibrator.vibrate(100)
        mediaPlayerBoxTap?.start()

        if (playerTurn == 1) {
            imageBtn.startAnimation(fadeInAnimation)
            imageBtn.setImageResource(R.drawable.neon_x)

            if (checkPlayerWin()) {
                playerOneWins()
            } else if (totalSelectionBoxes == 9) {
                showDrawDialog()
            } else {
                changePlayerTurn(2)
                totalSelectionBoxes++
            }
        } else {
            imageBtn.startAnimation(fadeInAnimation)
            imageBtn.setImageResource(R.drawable.neon_o)

            if (checkPlayerWin()) {
                playerTwoWins()
            } else if (totalSelectionBoxes == 9) {
                showDrawDialog()
            } else {
                changePlayerTurn(1)
                totalSelectionBoxes++
            }
        }

        // Check for bot's turn
        if (isBot && playerTurn == 2) {
            val botMove = ticTacToeBot.makeMove(boxPositions)

            if (botMove >= 0) {
                // Make the bot's move
                val botImageView = getImageViewForPosition(botMove)
                if (botImageView != null) {
                    botMakesMove(botImageView, botMove)
                }
            }
        }
    }
}

    private fun botMakesMove(imageView: ImageView, botMove: Int) {
        // Bot's move
        boxPositions[botMove] = playerTurn
        imageView.startAnimation(fadeInAnimation)
        imageView.setImageResource(R.drawable.neon_o)

        if (checkPlayerWin()) {
            playerTwoWins()
        } else if (totalSelectionBoxes == 9) {
            showDrawDialog()
        } else {
            changePlayerTurn(1)
            totalSelectionBoxes++
        }
    }

    private fun playerOneWins() {
        val winDialog = WinDialog(this@MainActivity, playerOneName.text.toString() + " wins", this, "Win")
        winDialog.setCancelable(false)
        winDialog.show()
        mediaPlayerWin?.start()
        playerOneWin++
        playerOneWinTxt.text = playerOneWin.toString()
    }

    private fun playerTwoWins() {
        val winDialog = WinDialog(this@MainActivity, playerTwoName.text.toString() + " wins", this, "Win")
        winDialog.setCancelable(false)
        winDialog.show()
        mediaPlayerWin?.start()
        playerTwoWin++
        playerTwoWinTxt.text = playerTwoWin.toString()
    }

    private fun showDrawDialog() {
        val winDialog = WinDialog(this@MainActivity, "It is a draw!", this, "Draw")
        winDialog.setCancelable(false)
        winDialog.show()
        mediaPlayerDraw?.start()
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

    private  fun changePlayerTurn(currentPlayerTurn:Int){
         playerTurn=currentPlayerTurn

        if (playerTurn == 1) {
//            if(!isBot){
//
//            Toast.makeText(this, playerOneName.text.toString()+" Turn", Toast.LENGTH_SHORT).show()
//            }
            playerOnelayout.setBackgroundResource(R.drawable.round_back_blue_border)
            playerTwolayout.setBackgroundResource(R.drawable.round_back_dark_blue)
        } else {
//            if(!isBot){
//                Toast.makeText(this, playerTwoName.text.toString()+" Turn", Toast.LENGTH_SHORT).show()
//
//                 }

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

    private fun isBoxSelectable(boxPosition:Int):Boolean{
        var response =false
        if(boxPositions[boxPosition]==0){
            response=true
        }
        return response
    }
    fun  fullrestartMatch(){
        restartMatch()
        playerOneWin=0
        playerTwoWin=0
        playerTwoWinTxt.text="0"
        playerOneWinTxt.text="0"

    }
   fun  restartMatch(){
       vibrator.vibrate(100)


        boxPositions = intArrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0)
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


   }
    private fun toggleMute() {
        if (isMuted) {
            // Unmute the audio
            vibrator.vibrate(100)

            isMuted = false
            volImg.setImageResource(R.drawable.neon_audio)
            mediaPlayerWin?.setVolume(1.0f, 1.0f)
            mediaPlayerBg?.setVolume(0.2f,0.2f)
            mediaPlayerBoxTap?.setVolume(1.0f, 1.0f)
            mediaPlayerStartGame?.setVolume(1.0f, 1.0f)
            mediaPlayerDraw?.setVolume(1.0f, 1.0f)

        } else {
            vibrator.vibrate(180)

            // Mute the audio
            isMuted = true
            volImg.setImageResource(R.drawable.neon_mute)
            mediaPlayerWin?.setVolume(0.0f, 0.0f)
            mediaPlayerBoxTap?.setVolume(0.0f, 0.0f)
            mediaPlayerStartGame?.setVolume(0.0f, 0.0f)
            mediaPlayerBg?.setVolume(0.0f,0.0f)

            mediaPlayerDraw?.setVolume(0.0f, 0.0f)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayerWin?.release()
        mediaPlayerBoxTap?.release()
        mediaPlayerStartGame?.release()
        mediaPlayerDraw?.release()
    }
    override fun onPause() {
        super.onPause()
        mediaPlayerBg?.pause()
    }

    override fun onResume() {
        super.onResume()
        mediaPlayerBg?.start()
    }
}



