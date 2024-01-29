package com.example.tictactoe.utils

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler

import android.os.Looper
import android.view.ViewGroup
import android.view.Window
import android.widget.ProgressBar
import com.example.tictactoe.R
import kotlinx.coroutines.*

class CustomLoaderDialog(context: Context) : Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
    private lateinit var progressBar: ProgressBar

    init {

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setCancelable(false)
        setContentView(R.layout.loader)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        progressBar = findViewById(R.id.progressBar4)

        progressBar.max = 100 // Set maximum value if tracking progress
        progressBar.progress = 0

    }

    override fun show() {
        super.show()
        startLoadingProcess()

    }

//    private fun startLoadingProcess() {
//        // Example of a loading process
//        // Update the progress bar as the process progresses
//        progressBar.progress = 0
//        val handler = Handler(Looper.getMainLooper())
//        handler.postDelayed({
//            // Update progress here
//            progressBar.progress = 50 // Example progress update
//        }, 2000) // Delayed for demonstration
//    }
private fun startLoadingProcess() {
    progressBar.progress = 0 // Initialize progress

    // Launch a coroutine in the Main scope for UI updates
    CoroutineScope(Dispatchers.Main).launch {
        // Use 'withContext' to switch to a background thread
        withContext(Dispatchers.IO) {
            for (i in 1..100) {
                delay(100) // Simulating a time-consuming task
                val progress = i // Calculate progress

                // Update the UI on the main thread
                launch(Dispatchers.Main) {
                    progressBar.progress = progress
                }
            }
        }
    }
}
}
