package com.example.infocandidature.View

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.infocandidature.R

class MainActivity : AppCompatActivity() {

    private lateinit var bAnimation: Button
    private lateinit var bEshop: Button
    private lateinit var bGame: Button
    private lateinit var bLoggin: Button
    private lateinit var bSiteModificator: Button
    private lateinit var bWallet: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bAnimation = findViewById(R.id.Animation)
        bEshop = findViewById(R.id.eShop)
        bGame = findViewById(R.id.EscapeGame)
        bLoggin = findViewById(R.id.Loggin)
        bSiteModificator = findViewById(R.id.SiteModificator)
        bWallet = findViewById(R.id.Wallet)

        bAnimation.setOnClickListener {
            startActivity(Intent(applicationContext, Animation::class.java))
            finish()
        }

        bGame.setOnClickListener {
            startActivity(Intent(applicationContext, game::class.java))
            finish()
        }

        bEshop.setOnClickListener {
            startActivity(Intent(applicationContext, Eshop::class.java))
            finish()
        }

        bLoggin.setOnClickListener {
            startActivity(Intent(applicationContext, loggin::class.java))
            finish()
        }

        bSiteModificator.setOnClickListener {
            startActivity(Intent(applicationContext, siteModificator::class.java))
            finish()
        }

        bWallet.setOnClickListener {
            startActivity(Intent(applicationContext, CheckoutActivity::class.java))
            finish()
        }
    }
}
