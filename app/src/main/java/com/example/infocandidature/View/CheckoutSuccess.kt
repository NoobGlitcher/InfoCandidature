package com.example.infocandidature.View


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.infocandidature.databinding.ActivityCheckoutSuccessBinding

class CheckoutSuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layoutBinding = ActivityCheckoutSuccessBinding.inflate(layoutInflater)
        setContentView(layoutBinding.root)
    }
}
