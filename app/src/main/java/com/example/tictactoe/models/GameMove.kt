package com.example.tictactoe.models

data class GameMove(
    val action: String ="",
    val selectedBoxPosition: Int? =null,
    val playerTurn: Int? =null,
    val nextTurnPlayer: Int?=null,
    val totalSelectionBoxes: Int?=null,
    val roomCode: String? =null,
    val win:Boolean=false,
    val draw:Boolean=false,
    val restart:Boolean=false

)
