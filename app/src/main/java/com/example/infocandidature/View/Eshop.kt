package com.example.infocandidature.View


import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.infocandidature.R
import com.example.infocandidature.outils.DatabaseManager

class Eshop : AppCompatActivity() {

    private lateinit var mGenerateurText: TextView
    private lateinit var PaysRecup: TextView
    private lateinit var mDatabaseManager: DatabaseManager
    private lateinit var mButtonSynthe: RadioButton
    private lateinit var mButtonLaine: RadioButton
    private lateinit var mButtonSoie: RadioButton
    private lateinit var mButtonCreation: Button

    private lateinit var pays: String
    private lateinit var Textiletype: String
    private lateinit var mvioletTissus: ImageView
    private lateinit var mvertTissus: ImageView
    private lateinit var mimageLaine: ImageView
    private lateinit var mimageSoie: ImageView
    private lateinit var mimageSynthe: ImageView
    private lateinit var mbleuTissus: ImageView
    private lateinit var mrougeTissus: ImageView
    private lateinit var mjauneTissus: ImageView
    private lateinit var mMachineAnimee: ImageView
    private lateinit var mMachineFixe: ImageView

    private lateinit var mediaPlayer: MediaPlayer

    private lateinit var handler2: Handler
    private lateinit var handler3: Handler
    private lateinit var stopPlayerRunnable: Runnable

    private val runnable = object : Runnable {
        override fun run() {
            if (mMachineFixe.visibility == View.VISIBLE) {
                mMachineFixe.visibility = View.GONE
                mMachineAnimee.visibility = View.VISIBLE
            } else {
                mMachineFixe.visibility = View.VISIBLE
                mMachineAnimee.visibility = View.GONE
            }
            handler3.postDelayed(this, 50)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eshop)

        mimageLaine = findViewById(R.id.imageLaine)
        mimageSynthe = findViewById(R.id.imageSynthe)
        mimageSoie = findViewById(R.id.imageSoie)
        mbleuTissus = findViewById(R.id.bleuTissus)
        mrougeTissus = findViewById(R.id.rougeTissus)
        mjauneTissus = findViewById(R.id.jauneTissus)
        mvertTissus = findViewById(R.id.vertTissus)
        mvioletTissus = findViewById(R.id.violetTissus)
        mrougeTissus.visibility = View.INVISIBLE
        mjauneTissus.visibility = View.INVISIBLE
        mbleuTissus.visibility = View.INVISIBLE
        mvioletTissus.visibility = View.INVISIBLE
        mvertTissus.visibility = View.INVISIBLE

        mediaPlayer = MediaPlayer.create(this, R.raw.machinebruit)
        mMachineFixe = findViewById(R.id.machinefixe)
        mMachineAnimee = findViewById(R.id.machineanimee)
        mMachineAnimee.visibility = View.INVISIBLE
        mDatabaseManager = DatabaseManager(applicationContext)
        pays = intent.getStringExtra("pays") ?: ""
        PaysRecup = findViewById(R.id.ChooseMessage)

        PaysRecup.visibility = View.VISIBLE
        if (pays == "1") {
            PaysRecup.text = "Bienvenue dans ta future création Parisienne."
        } else {
            PaysRecup.text = "Bienvenue dans ta future création Suisse."
        }

        val progressBar = findViewById<ProgressBar>(R.id.progressBar2)
        progressBar.visibility = View.GONE
        mButtonCreation = findViewById(R.id.buttonCreation)

        mButtonSoie = findViewById(R.id.radioButtonSoie)
        mButtonSoie.setPadding(200, 0, 0, 0)

        mButtonSoie.setOnClickListener {
            if (!mButtonSoie.isSelected) {
                mButtonSoie.isChecked = true
                mButtonSoie.isSelected = true
                mimageSoie.setImageResource(R.drawable.check)
                mrougeTissus.visibility = View.VISIBLE
            } else {
                mButtonSoie.isChecked = false
                mButtonSoie.isSelected = false
                mimageSoie.setImageResource(R.drawable.nocheck)
                mrougeTissus.visibility = View.INVISIBLE
            }
        }

        mButtonSynthe = findViewById(R.id.radioButtonSynthe)
        mButtonSynthe.setPadding(200, 0, 0, 0)

        mButtonSynthe.setOnClickListener {
            if (!mButtonSynthe.isSelected) {
                mButtonSynthe.isChecked = true
                mButtonSynthe.isSelected = true
                mimageSynthe.setImageResource(R.drawable.check)
                mvertTissus.visibility = View.VISIBLE
            } else {
                mButtonSynthe.isChecked = false
                mButtonSynthe.isSelected = false
                mimageSynthe.setImageResource(R.drawable.nocheck)
                mvertTissus.visibility = View.INVISIBLE
            }
        }

        mButtonLaine = findViewById(R.id.radioButtonLaine)
        mButtonLaine.setPadding(200, 0, 0, 0)

        mButtonLaine.setOnClickListener {
            if (!mButtonLaine.isSelected) {
                mButtonLaine.isChecked = true
                mButtonLaine.isSelected = true
                mimageLaine.setImageResource(R.drawable.check)
                mvioletTissus.visibility = View.VISIBLE
            } else {
                mButtonLaine.isChecked = false
                mButtonLaine.isSelected = false
                mimageLaine.setImageResource(R.drawable.nocheck)
                mvioletTissus.visibility = View.INVISIBLE
            }
        }

        mGenerateurText = findViewById(R.id.GenerateurText)
        mGenerateurText.visibility = View.INVISIBLE

        mButtonCreation.setOnClickListener {
            val Laine = if (mButtonLaine.isChecked) 1 else 0
            val Soie = if (mButtonSoie.isChecked) 10 else 0
            val Synthe = if (mButtonSynthe.isChecked) 100 else 0
            val TextileType = Soie + Synthe + Laine

            if (TextileType < 1) {
                Toast.makeText(applicationContext, "Veuillez sélectionner au moins 2 choses.", Toast.LENGTH_LONG).show()
            } else if (TextileType == 111) {
                Toast.makeText(applicationContext, "Veuillez enlever une chose.", Toast.LENGTH_LONG).show()
            } else {
                progressBar.visibility = View.VISIBLE
                Handler().postDelayed({ progressBar.visibility = View.GONE }, 6000)

                val handler = Handler()
                val duration = 8000
                val frequency = 700
                val iterations = duration / frequency

                val r = object : Runnable {
                    var count = 0
                    override fun run() {
                        if (mGenerateurText.visibility == View.INVISIBLE) {
                            mGenerateurText.visibility = View.VISIBLE
                        } else {
                            mGenerateurText.visibility = View.INVISIBLE
                        }
                        count++
                        if (count < iterations) {
                            handler.postDelayed(this, frequency.toLong())
                        }
                    }
                }

                handler.postDelayed(r, frequency.toLong())

                Handler().postDelayed({
                    Textiletype = TextileType.toString()
                    val intent = Intent(this@Eshop, Bidules::class.java)
                    intent.putExtra("type", Textiletype)
                    startActivity(intent)
                }, 8000)

                mediaPlayer.start()
                handler2 = Handler()
                stopPlayerRunnable = Runnable {
                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.stop()
                    }
                }
                handler2.postDelayed(stopPlayerRunnable, 8000)
                handler3 = Handler()
                handler3.postDelayed(runnable, 1000)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler3.removeCallbacks(runnable)
        mediaPlayer.release()
        handler2.removeCallbacks(stopPlayerRunnable)
    }
}
