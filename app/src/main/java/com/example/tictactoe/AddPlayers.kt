package com.example.tictactoe


import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import android.view.View
import android.widget.Button
import android.widget.EditText

import android.animation.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.graphics.Color
import android.media.MediaPlayer
import android.view.Window
import android.view.WindowManager
import androidx.core.graphics.ColorUtils
class AddPlayers : AppCompatActivity() {
    private lateinit var playerone: EditText
    private lateinit var playerTwo: EditText
    private lateinit var startbtn: Button
    private var isMuted = false
    private var mediaPlayerBg: MediaPlayer? = null
    private var isBot=false
    private lateinit var vol_img: ImageView
    private lateinit var audiolayoutbtn: LinearLayout
    private lateinit var botbtn:Button
    private var neonColor: Int = Color.rgb(0, 0, 255)
    private lateinit var vibrator: Vibrator
    private lateinit var textView: TextView

//    @SuppressLint("ObjectAnimatorBinding")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//    requestWindowFeature(Window.FEATURE_NO_TITLE) // Hide the title bar, if it exists
//    window.setFlags(
//        WindowManager.LayoutParams.FLAG_FULLSCREEN,
//        WindowManager.LayoutParams.FLAG_FULLSCREEN
//    )
//    supportActionBar?.hide() // To hide the action bar (app bar)

    setContentView(R.layout.activity_add_players)
        textView = findViewById(R.id.titletextView)
        mediaPlayerBg = MediaPlayer.create(this, R.raw.bg_audio)
        mediaPlayerBg?.isLooping = true
        mediaPlayerBg?.setVolume(0.3f, 0.3f)
        mediaPlayerBg?.start()

    val colors = listOf(
        Color.rgb(0, 0, 255),    // Blue
        Color.rgb(255, 0, 0),    // Red
        Color.rgb(0, 255, 0),    // Green
        Color.rgb(255, 255, 0),  // Yellow
        Color.rgb(255, 0, 255)   // Pink
    )
    fun setNeonColor(neonColor: Int) {
        this.neonColor = neonColor
        val alpha = 255 // Maximum alpha value
        val finalNeonColor = ColorUtils.setAlphaComponent(neonColor, alpha)
        textView.setShadowLayer(30f, 0f, 0f, finalNeonColor)
    }

    val animator = ObjectAnimator.ofArgb(this, getString(R.string.neoncolor), *colors.toIntArray())

    animator.duration = 6000 // Animation duration in milliseconds
    animator.repeatCount = ObjectAnimator.INFINITE
    animator.addListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {}

        override fun onAnimationEnd(animation: Animator) {
            // Handle the end of the animation if needed
        }

        override fun onAnimationCancel(animation: Animator) {}

        override fun onAnimationRepeat(animation: Animator) {}
    })

    animator.addUpdateListener { animation ->
        val neonColor = animation.animatedValue as Int
        setNeonColor(neonColor)
    }

    animator.start()


        playerone= findViewById(R.id.playerOneName)
        playerTwo =findViewById(R.id.playerTwoName)
        startbtn=findViewById(R.id.startGameBtn)
        botbtn=findViewById(R.id.botplayBtn2)
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        vol_img=findViewById(R.id.vol_img1)
        audiolayoutbtn = findViewById(R.id.audiolayoutbtn1)
        audiolayoutbtn.setOnClickListener(View.OnClickListener {
            if (isMuted) {
                vibrator.vibrate(100)

                // Unmute the audio
                isMuted = false
                mediaPlayerBg?.setVolume(0.5f, 0.5f)
                vol_img.setImageResource(R.drawable.neon_audio)


            } else {
                vibrator.vibrate(180)

                // Mute the audio
                mediaPlayerBg?.setVolume(0.0f, 0.0f)
                vol_img.setImageResource(R.drawable.neon_mute)

                isMuted = true

            }
        })
        botbtn.setOnClickListener(View.OnClickListener {
            isBot=true

            vibrator.vibrate(100)

            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("playerOne","Player")
            intent.putExtra("playerTwo","Bot")
            intent.putExtra("isBot",isBot)
            intent.putExtra("isMuted",isMuted)
            startActivity(intent)
        })
        startbtn.setOnClickListener(View.OnClickListener {
            // Do some work here
            var getplayerone=playerone.text.toString()
            var getplayertwo=playerTwo.text.toString()
            if(getplayerone.isEmpty()||getplayertwo.isEmpty()){

                vibrator.vibrate(150)

                Toast.makeText(this,"Please enter player name", Toast.LENGTH_SHORT).show()
            }
            else{
                vibrator.vibrate(100)

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("playerOne",getplayerone)
                intent.putExtra("playerTwo",getplayertwo)
                intent.putExtra("isMuted",isMuted)
                startActivity(intent)
//                finish()
            }

        })
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