package com.svs.ista

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView

class Splash : AppCompatActivity() {
    lateinit var txtprogress: TextView
    lateinit var imag:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        splash()

    }

    //
    private fun splash() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        object: CountDownTimer(3000,1000){
            override fun onTick(millisUntilFinished: Long) {
                val progress = ((3000 - millisUntilFinished) * 100 / 3000).toInt()
                progressBar.progress = progress
            }

            override fun onFinish() {
                var abrir= Intent(applicationContext, Login::class.java).apply {  }
                startActivity(abrir)
            }
        }.start() // comienza el contador
    }
}