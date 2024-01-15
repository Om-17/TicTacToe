package com.example.tictactoe
//random moves

//class TicTacToeBot {
//    fun makeMove(board: IntArray): Int {
//        val availableMoves = mutableListOf<Int>()
//
//        // Find available (empty) positions
//        for (i in board.indices) {
//            if (board[i] == 0) {
//                println(i)
//                availableMoves.add(i)
//            }
//        }
//
//        if(availableMoves.isNotEmpty()) {
//            // Randomly select a move from the available options
//            val randomIndex = (0 until availableMoves.size).random()
//            return availableMoves[randomIndex]
//        } else {
//            // If no moves are available, return -1 to indicate a draw
//            return -1
//        }
//    }
//}
class TicTacToeBot {
    fun makeMove(board: IntArray): Int {
        // Check if there's an immediate winning move available
        for (i in board.indices) {
            if (board[i] == 0) {
                // Try making the move and check if it results in a win
                board[i] = 2 // Assuming 2 represents the bot's move
                if (checkWin(board, 2)) {
                    return i // Return the winning move
                }
                // Undo the move for further checks
                board[i] = 0
            }
        }

        // If no immediate winning move, prioritize a blocking move for the opponent
        for (i in board.indices) {
            if (board[i] == 0) {
                // Try making the move and check if it blocks the opponent from winning
                board[i] = 1 // Assuming 1 represents the opponent's move
                if (checkWin(board, 1)) {
                    return i // Return the blocking move
                }
                // Undo the move for further checks
                board[i] = 0
            }
        }

        // If no winning or blocking move, select a random move
        val availableMoves = mutableListOf<Int>()
        for (i in board.indices) {
            if (board[i] == 0) {
                availableMoves.add(i)
            }
        }

        if (availableMoves.isNotEmpty()) {
            val randomIndex = (0 until availableMoves.size).random()
            return availableMoves[randomIndex]
        } else {
            // If no moves are available, return -1 to indicate a draw
            return -1
        }
    }
    private fun checkWin(board: IntArray, player: Int): Boolean {
        // Check rows
        for (row in 0 until 3) {
            if (board[row * 3] == player && board[row * 3 + 1] == player && board[row * 3 + 2] == player) {
                return true
            }
        }

        // Check columns
        for (col in 0 until 3) {
            if (board[col] == player && board[col + 3] == player && board[col + 6] == player) {
                return true
            }
        }

        // Check diagonals
        if (board[0] == player && board[4] == player && board[8] == player) {
            return true
        }
        if (board[2] == player && board[4] == player && board[6] == player) {
            return true
        }

        return false
    }

}


//class TicTacToeBot {
//    private lateinit var mainActivity :MainActivity
//    private  var playerTurn:Int =1
//    fun makeMove(board: IntArray, playerWinCount: Int,playerTurnNo:Int,activity: MainActivity): Int {
//        val difficulty = getBotDifficulty(playerWinCount)
//        mainActivity =activity
//        playerTurn=playerTurnNo
//        // Implement bot logic based on difficulty level
//        when (difficulty) {
//            BotDifficulty.EASY -> {
//                // Implement easy bot logic (e.g., random move)
//                return makeEasyMove(board)
//            }
//            BotDifficulty.NORMAL -> {
//                // Implement normal bot logic (e.g., simple strategy)
//                return makeNormalMove(board)
//            }
//            BotDifficulty.HARD -> {
//                // Implement hard bot logic (e.g., advanced strategy)
//                return makeHardMove(board)
//            }
//            BotDifficulty.EXTRA_HARD -> {
//                // Implement extra hard bot logic (e.g., very advanced strategy)
//                return makeExtraHardMove(board)
//            }
//        }
//
//        // Default: Return -1 if no move is possible (e.g., in case of a draw).
//        return -1
//    }
//
//    private fun getBotDifficulty(playerWinCount: Int): BotDifficulty {
//        // Adjust bot difficulty based on player's win count
//        return when {
//            playerWinCount >= 18 -> BotDifficulty.EXTRA_HARD
//            playerWinCount >= 10 -> BotDifficulty.HARD
//            playerWinCount >= 5 -> BotDifficulty.NORMAL
//            playerWinCount >= 1 -> BotDifficulty.EASY
//            else -> BotDifficulty.EASY
//        }
//    }
//
//    private fun makeEasyMove(board: IntArray): Int {
//        val emptyPositions = mutableListOf<Int>()
//
//        // Find all the empty positions on the board
//        for (i in 0 until 9) {
//            if (board[i] == 0) {
//                emptyPositions.add(i)
//            }
//        }
//
//        if (emptyPositions.isNotEmpty()) {
//            // Choose a random empty position
//            val randomIndex = (0 until emptyPositions.size).random()
//            return emptyPositions[randomIndex]
//        }
//
//        // Return -1 if no move is possible (e.g., in case of a draw).
//        return -1
//    }
//
//
//    private fun makeNormalMove(board: IntArray): Int {
//        // Check for winning move
//        val winningMove = findWinningMove(board, 1)
//        if (winningMove >= 0) {
//            return winningMove
//        }
//
//        // Block the player from winning
//        val opponent = if (playerTurn == 1) 2 else 1
//        val blockingMove = findWinningMove(board, opponent)
//        if (blockingMove >= 0) {
//            return blockingMove
//        }
//
//        // If no winning or blocking moves, make a random move
//        val emptyPositions = mutableListOf<Int>()
//        for (i in 0 until 9) {
//            if (board[i] == 0) {
//                emptyPositions.add(i)
//            }
//        }
//
//        if (emptyPositions.isNotEmpty()) {
//            // Choose a random empty position
//            val randomIndex = (0 until emptyPositions.size).random()
//            return emptyPositions[randomIndex]
//        }
//
//        // Return -1 if no move is possible (e.g., in case of a draw).
//        return -1
//    }
//
//    private fun findWinningMove(board: IntArray, player: Int): Int {
//        for (i in 0 until 9) {
//            if (board[i] == 0) {
//                board[i] = player
//                if (mainActivity.checkPlayerWin()) {
//                    // If the current move leads to a win, return the move
//                    board[i] = 0 // Restore the board
//                    return i
//                }
//                board[i] = 0 // Restore the board
//            }
//        }
//        return -1
//    }
//
//    private fun makeHardMove(board: IntArray): Int {
//        // First, check if the bot can win with the next move
//        for (i in 0 until 9) {
//            if (board[i] == 0) {
//                // Simulate making the move
//                board[i] = 2 // Assume the bot is player 2
//                if (mainActivity.checkPlayerWin()) {
//                    // Bot can win, make this move
//                    return i
//                }
//                // Undo the move
//                board[i] = 0
//            }
//        }
//
//        // Then, check if the opponent can win with the next move and block it
//        for (i in 0 until 9) {
//            if (board[i] == 0) {
//                // Simulate making the move for the opponent
//                board[i] = 1 // Assume the opponent is player 1
//                if (mainActivity.checkPlayerWin()) {
//                    // Opponent can win, block this move
//                    return i
//                }
//                // Undo the move
//                board[i] = 0
//            }
//        }
//
//        // If neither the bot nor the opponent can win immediately, implement an advanced strategy:
//        // Example: Prioritize taking the center square if it's available (increases chances of winning).
//        if (board[4] == 0) {
//            return 4
//        }
//
//        // If the center square is not available, prioritize corners.
//        val cornerMoves = listOf(0, 2, 6, 8)
//        for (corner in cornerMoves) {
//            if (board[corner] == 0) {
//                return corner
//            }
//        }
//
//        // If no winning, blocking, or strategic moves are available, make a random move.
//        val emptyPositions = mutableListOf<Int>()
//        for (i in 0 until 9) {
//            if (board[i] == 0) {
//                emptyPositions.add(i)
//            }
//        }
//
//        if (emptyPositions.isNotEmpty()) {
//            // Choose a random empty position
//            val randomIndex = (0 until emptyPositions.size).random()
//            return emptyPositions[randomIndex]
//        }
//
//        // Return -1 if no move is possible (e.g., in case of a draw).
//        return -1
//    }
//    private fun makeExtraHardMove(board: IntArray): Int {
//        // First, check if the bot can win with the next move
//        for (i in 0 until 9) {
//            if (board[i] == 0) {
//                // Simulate making the move
//                board[i] = 2 // Assume the bot is player 2
//                if (mainActivity.checkPlayerWin()) {
//                    // Bot can win, make this move
//                    return i
//                }
//                // Undo the move
//                board[i] = 0
//            }
//        }
//
//        // Then, check if the opponent can win with the next move and block it
//        for (i in 0 until 9) {
//            if (board[i] == 0) {
//                // Simulate making the move for the opponent
//                board[i] = 1 // Assume the opponent is player 1
//                if (mainActivity.checkPlayerWin()) {
//                    // Opponent can win, block this move
//                    return i
//                }
//                // Undo the move
//                board[i] = 0
//            }
//        }
//
//        // If neither the bot nor the opponent can win immediately, implement a very advanced strategy.
//        // Example: Prioritize taking the center square if it's available (increases chances of winning).
//        if (board[4] == 0) {
//            return 4
//        }
//
//        // If the center square is not available, prioritize corners.
//        val cornerMoves = listOf(0, 2, 6, 8)
//        for (corner in cornerMoves) {
//            if (board[corner] == 0) {
//                return corner
//            }
//        }
//
//        // If no winning, blocking, or strategic moves are available, implement additional advanced strategies:
//        // Example: Check if the bot can create a two-in-a-row situation that leads to a win, and prioritize that move.
//
//        // ...
//
//        // If no specific strategy can be applied, make a random move.
//        val emptyPositions = mutableListOf<Int>()
//        for (i in 0 until 9) {
//            if (board[i] == 0) {
//                emptyPositions.add(i)
//            }
//        }
//
//        if (emptyPositions.isNotEmpty()) {
//            // Choose a random empty position
//            val randomIndex = (0 until emptyPositions.size).random()
//            return emptyPositions[randomIndex]
//        }
//
//        // Return -1 if no move is possible (e.g., in case of a draw).
//        return -1
//    }
//
//
//
//}
//
//
//enum class BotDifficulty {
//    EASY,
//    NORMAL,
//    HARD,
//    EXTRA_HARD
//}
