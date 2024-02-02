package com.example.tictactoe.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class DiagonalWinView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var drawDiagonal: Int = 0 // 0 for none, 1 for top-left to bottom-right, 2 for top-right to bottom-left
    private val paint = Paint()
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val paint = paint.apply {
            color = Color.YELLOW
            strokeWidth = 5f
            style = Paint.Style.STROKE
        }

        if (drawDiagonal == 1) {
            canvas.drawLine(0f, 0f, width.toFloat(), height.toFloat(), paint)
        } else if (drawDiagonal == 2) {
            canvas.drawLine(width.toFloat(), 0f, 0f, height.toFloat(), paint)
        }
    }
}
