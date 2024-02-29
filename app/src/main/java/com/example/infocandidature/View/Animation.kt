package com.example.infocandidature.View

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.infocandidature.R

class Animation : AppCompatActivity() {

    private lateinit var handler3: Handler
    private lateinit var handler4: Handler
    private var screenHeight = 0
    private lateinit var colombefixe: ImageView
    private lateinit var colombeanimee: ImageView
    private lateinit var colombefixe2: ImageView
    private lateinit var colombeanimee2: ImageView
    private lateinit var nuage: ImageView
    private lateinit var piece1: ImageView
    private lateinit var mTest: Button

    private val runnable = object : Runnable {
        override fun run() {
            if (colombefixe.visibility == View.VISIBLE) {
                colombefixe.visibility = View.GONE
                colombeanimee.visibility = View.VISIBLE
            } else {
                colombefixe.visibility = View.VISIBLE
                colombeanimee.visibility = View.GONE
            }
            handler3.postDelayed(this, 75)
        }
    }

    private val runnable2 = object : Runnable {
        override fun run() {
            if (colombefixe2.visibility == View.VISIBLE) {
                colombefixe2.visibility = View.GONE
                colombeanimee2.visibility = View.VISIBLE
            } else {
                colombefixe2.visibility = View.VISIBLE
                colombeanimee2.visibility = View.GONE
            }
            handler4.postDelayed(this, 75)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_animation)

        mTest = findViewById(R.id.button)
        colombefixe = findViewById(R.id.ColombeFixe)
        colombeanimee = findViewById(R.id.ColombeAnimee)
        colombefixe2 = findViewById(R.id.ColombeFixe2)
        colombeanimee2 = findViewById(R.id.ColombeAnimee2)
        nuage = findViewById(R.id.nuage)
        piece1 = findViewById(R.id.Piece)

        colombeanimee.visibility = View.INVISIBLE
        colombeanimee2.visibility = View.INVISIBLE
        nuage.visibility = View.INVISIBLE

        mTest.setOnClickListener {
            mTest.visibility = View.INVISIBLE

            handler3 = Handler()
            handler3.postDelayed(runnable, 0)

            handler4 = Handler()
            handler4.postDelayed(runnable2, 0)

            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            screenHeight = displayMetrics.heightPixels

            startAnimation()
            startAnimation2()
            startAnimation3()
            startAnimation4()
        }
    }

    private fun startAnimation() {
        val set = AnimatorSet()

        val scaleDownX1 = ObjectAnimator.ofFloat(colombefixe, "scaleX", 1f, 0.5f).apply {
            duration = 1000
            startDelay = 2000
        }
        val scaleDownY1 = ObjectAnimator.ofFloat(colombefixe, "scaleY", 1f, 0.5f).apply {
            duration = 1000
            startDelay = 2000
        }
        // Ajoutez d'autres animations ici...

        set.addListener(object : Animator.AnimatorListener {
            private var isImage1 = true
            override fun onAnimationStart(animator: Animator) {}
            override fun onAnimationEnd(animator: Animator) {
                if (isImage1) {
                    colombeanimee.visibility = View.INVISIBLE
                    colombeanimee.visibility = View.VISIBLE
                    colombeanimee2.visibility = View.INVISIBLE
                    colombeanimee2.visibility = View.VISIBLE
                    isImage1 = false
                } else {
                    colombeanimee.visibility = View.INVISIBLE
                    colombefixe.visibility = View.VISIBLE
                    colombeanimee2.visibility = View.INVISIBLE
                    colombefixe2.visibility = View.VISIBLE
                    isImage1 = true
                }
            }

            override fun onAnimationCancel(animator: Animator) {}
            override fun onAnimationRepeat(animator: Animator) {}
        })

        set.start()
    }

    private fun startAnimation2() {
        val animation = ObjectAnimator.ofFloat(piece1, "translationY", 0f, screenHeight.toFloat()).apply {
            duration = 3000
            interpolator = AccelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    piece1.visibility = View.GONE
                }
            })
        }
        animation.start()
    }

    private fun startAnimation3() {
        Handler().postDelayed({
            val translationAnimator = ObjectAnimator.ofFloat(nuage, View.TRANSLATION_Y, -nuage.height.toFloat(), 0f).apply {
                duration = 7000
            }
            val animatorSet = AnimatorSet()
            animatorSet.playTogether(translationAnimator)
            animatorSet.start()
            nuage.visibility = View.VISIBLE
        }, 2000)
    }


        private fun startAnimation4() {
            // Créez votre animation personnalisée ici
            val fadeIn = ObjectAnimator.ofFloat(colombefixe, View.ALPHA, 0f, 1f)
            fadeIn.duration = 1000

            val fadeOut = ObjectAnimator.ofFloat(colombeanimee, View.ALPHA, 1f, 0f)
            fadeOut.duration = 1000

            val set = AnimatorSet()
            set.playSequentially(fadeIn, fadeOut)
            set.start()


    }
}
