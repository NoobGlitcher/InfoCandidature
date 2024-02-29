package com.example.infocandidature.View


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.infocandidature.R

class game : AppCompatActivity() {

    private lateinit var bChicken: Button
    private lateinit var bData: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        bChicken = findViewById(R.id.Chicken)
        bData = findViewById(R.id.dataGame)

        bChicken.setOnClickListener {
            startActivity(Intent(applicationContext, chickenGame::class.java))
            finish()
        }

        bData.setOnClickListener {
            startActivity(Intent(applicationContext, loggin::class.java))
            finish()
        }
        }
    }

