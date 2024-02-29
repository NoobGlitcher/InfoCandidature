package com.example.infocandidature.View


import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.infocandidature.R
import com.example.infocandidature.outils.DatabaseManager
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class loggin : AppCompatActivity() {

    private lateinit var mID: EditText
    private lateinit var mPassword: EditText
    private lateinit var mConnexion: Button
    private lateinit var mInscription: Button
    private lateinit var countdownTextView: TextView
    private lateinit var errorConnectTextView: TextView
    private lateinit var username: String
    private lateinit var password: String
    private lateinit var mDatabaseManager: DatabaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loggin)

        mDatabaseManager = DatabaseManager(applicationContext)
        mID = findViewById(R.id.ID)
        mPassword = findViewById(R.id.Password)
        errorConnectTextView = findViewById(R.id.errorConnectTextView)
        mConnexion = findViewById(R.id.Connexion)
        countdownTextView = findViewById(R.id.countdownTextView)
        mInscription = findViewById(R.id.Inscription)

        val targetDate = Calendar.getInstance()
        targetDate.set(2023, Calendar.APRIL, 27, 21, 0, 0)

        object : CountDownTimer(targetDate.timeInMillis - System.currentTimeMillis(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val minutes = seconds / 60
                val hours = minutes / 60
                val days = hours / 24
                val timeLeft = "$days jours ${hours % 24} heures ${minutes % 60} minutes ${seconds % 60} secondes"
                countdownTextView.text = timeLeft
            }

            override fun onFinish() {
                countdownTextView.text = "Compte à rebours terminé !"
            }
        }.start()

        countdownTextView.visibility = View.INVISIBLE

        mConnexion.setOnClickListener {
            username = mID.text.toString()
            password = mPassword.text.toString()
            connectUser()
        }

        mInscription.setOnClickListener {
            val createAccount = Intent(applicationContext, Createuser::class.java)
            startActivity(createAccount)
            finish()
        }
    }

    private fun onApiResponse(response: JSONObject) {
        try {
            if (response.has("username") && response.has("mail") && response.has("pays")) {
                val username = response.getString("username")
                val mail = response.getString("mail")
                val pays = response.getString("pays")

                val decorationActivity = Intent(applicationContext, MainActivity::class.java)
                decorationActivity.putExtra("username", username)
                decorationActivity.putExtra("mail", mail)
                decorationActivity.putExtra("pays", pays)

                Toast.makeText(applicationContext, "Bravo ! Vous êtes connecté.", Toast.LENGTH_LONG).show()

                startActivity(decorationActivity)
                finish()
            } else if (response.has("error")) {
                val error = response.getString("error")
                errorConnectTextView.visibility = View.VISIBLE
                errorConnectTextView.text = error

                val atelierActivity = Intent(applicationContext, Createuser::class.java)
                startActivity(atelierActivity)
                finish()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun connectUser() {
        val url = "https://oribabil.myhostpoint.ch/createusers/action/connectuser.php"
        val params = HashMap<String, String>()
        params["username"] = username
        params["password"] = password
        val parameters = JSONObject(params as Map<*, *>?)

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, parameters, this::onApiResponse,
            { error -> Toast.makeText(applicationContext, "erreur de connection au serveur !!", Toast.LENGTH_LONG).show() })

        mDatabaseManager.queue.add(jsonObjectRequest)
    }
}
