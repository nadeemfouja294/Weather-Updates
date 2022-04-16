package com.logical.weatherupdate.city_updates

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.logical.weatherupdate.R
import java.util.*
import kotlin.concurrent.timerTask

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val intent=Intent(this,CityUpdatesActivity::class.java)

        Timer().schedule(timerTask {
            startActivity(intent)
            finish()
        }, 3000)


    }
}