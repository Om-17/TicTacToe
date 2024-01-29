package com.example.tictactoe.utils



import android.app.Dialog
import android.content.Context
import android.os.Bundle

import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.tictactoe.R

class OnlineWinDialog(
    context: Context,
    private val message: String,
    private val onStartAgainClick: () -> Unit,
    private val mode: String
) : Dialog(context) {
    private lateinit var messagetxt: TextView
    private lateinit var startAgainBtn: Button
    private lateinit var imgView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.win_dialog_layout)
        startAgainBtn = findViewById(R.id.startAgainBtn)
        messagetxt = findViewById(R.id.messageTxt)
        imgView = findViewById(R.id.img_view)
        messagetxt.text = message
        setCancelable(false)

        val fadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fade_in)

        window?.decorView?.rootView?.startAnimation(fadeInAnimation)

        if (mode == "Win") {
            // Load the image using Glide
            Glide.with(context)
                .load(R.drawable.winner)
                .into(imgView)
        } else {
            Glide.with(context)
                .load(R.drawable.draw)
                .into(imgView)
        }

        startAgainBtn.setOnClickListener { view ->
            onStartAgainClick.invoke()
            dismiss()
        }
    }
}
