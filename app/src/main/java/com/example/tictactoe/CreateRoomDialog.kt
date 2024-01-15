package com.example.tictactoe

import WebSocketManager
import WebSocketResponseListener
import android.app.AlertDialog
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class CreateRoomDialog(context: Context, private val webSocketManager: WebSocketManager,private val roomCode: String) : Dialog(context)  {
    private lateinit var closeBtn: ImageView
    private lateinit var timerRoomCode: TextView
    private lateinit var roomCodeTxt: TextView
    private lateinit var copyRoomCode: ImageView
    private var countDownTimer: CountDownTimer? = null
    private val countdownDuration: Long = 600000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_room_dialog)
        closeBtn = findViewById(R.id.closeBtn)
        timerRoomCode = findViewById(R.id.timerRoomCode)
        roomCodeTxt = findViewById(R.id.roomCodeTxt)
        copyRoomCode = findViewById(R.id.copyRoomCode)
        roomCodeTxt.text = roomCode

        startCountdownTimer()

        closeBtn.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("leave the room")
            builder.setCancelable(false)
            builder.setMessage("are you sure ?,you want to leave the room.")
            builder.setPositiveButton("Yes") { dialog, _ ->
                callDeleteRoom()
                dismiss()
            }
            builder.setNegativeButton("No") { dialog, _ ->

            }
            builder.show()

        }

        setCancelable(false)

        // Copy room code to clipboard when the copyRoomCode ImageView is clicked
        copyRoomCode.setOnClickListener {
            copyRoomCodeToClipboard()
        }

        // Add any other customization or initialization code here
    }


    private fun startCountdownTimer() {
        countDownTimer?.cancel() // Cancel previous timer if exists

        countDownTimer = object : CountDownTimer(countdownDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 60000
                val seconds = (millisUntilFinished % 60000) / 1000

                val formattedTime = String.format("%02d:%02d", minutes, seconds)
                timerRoomCode.text = formattedTime
            }

            override fun onFinish() {
                // Timer finished, call your API here
                callDeleteRoom()
                dismiss()

                // Restart the countdown timer
                startCountdownTimer()
            }
        }.start()
    }

    private fun callDeleteRoom() {
        val message = "{\"action\":\"leaveRoom\"}"
        webSocketManager.sendMessage(message)
        // Implement your API call logic here
        // This method will be called when the countdown timer finishes
    }

    private fun copyRoomCodeToClipboard() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Room Code", roomCodeTxt.text)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(context, "Room Code copied to clipboard", Toast.LENGTH_SHORT).show()
    }
}
