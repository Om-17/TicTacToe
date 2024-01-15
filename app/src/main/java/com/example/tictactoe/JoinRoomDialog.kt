package com.example.tictactoe

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.example.tictactoe.utils.SnackbarUtils

class JoinRoomDialog(
    context: Context,
    private val onJoinRoom: (roomCode: String) -> Unit
) : Dialog(context) {
    private lateinit var closeBtn: ImageView
    private lateinit var editTextRoomCode: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_room_dialog)

        closeBtn = findViewById(R.id.closebtn)
        editTextRoomCode = findViewById(R.id.editTextRoomCode)
        val btnJoinRoom: Button = findViewById(R.id.btnJoinRoom)

        btnJoinRoom.setOnClickListener(::onjoinroom)

        closeBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun onjoinroom(view:View){
        val roomCode = editTextRoomCode.text.toString()
        if(roomCode.isEmpty()){
            val snackbarUtils = SnackbarUtils(context)
            val rootView = findViewById<View>(android.R.id.content)
            snackbarUtils.showSnackbar(rootView, "RoomCode is a required.")
            return
        }
        onJoinRoom.invoke(roomCode)
    }
}
