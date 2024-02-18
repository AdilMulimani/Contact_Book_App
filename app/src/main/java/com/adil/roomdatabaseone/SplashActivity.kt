package com.adil.roomdatabaseone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import es.dmoral.toasty.Toasty

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        Toasty.normal(this,"Welcome!", Toast.LENGTH_SHORT).show()

        Looper.myLooper()?.let {
            Handler(it).postDelayed({
                val i = Intent(this,MainActivity::class.java)
                startActivity(i)
                finish()
            },3000)
        }

    }
}