package com.example.infocandidature.View

import android.widget.*


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.infocandidature.R
import com.example.infocandidature.outils.DatabaseManager
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class Createuser : AppCompatActivity() {

    private lateinit var mIDtext: EditText
    private lateinit var mPassword2: EditText
    private lateinit var mPassword3: EditText
    private lateinit var mClef: EditText
    private lateinit var mCity: EditText
    private lateinit var mConnexion: Button
    private lateinit var mRetour: Button
    private lateinit var mFrance: ImageView
    private lateinit var mSuisse: ImageView
    private lateinit var mErrorConnexion: TextView
    private lateinit var mFranceBoutton: RadioButton
    private lateinit var mSuisseBoutton: RadioButton
    private lateinit var progressBar: ProgressBar
    private lateinit var username: String
    private lateinit var password: String
    private lateinit var mail: String
    private lateinit var pays: String
    private lateinit var databaseManager: DatabaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_createuser)

        mIDtext = findViewById(R.id.ID2)
        mPassword2 = findViewById(R.id.Password2)
        mPassword3 = findViewById(R.id.Password3)
        mClef = findViewById(R.id.Clef)
        mCity = findViewById(R.id.City)
        mFranceBoutton = findViewById(R.id.franceBoutton)
        mSuisseBoutton = findViewById(R.id.suisseBoutton)
        mFrance = findViewById(R.id.DrapeauFrance)
        mSuisse = findViewById(R.id.DrapeauSuisse)
        mRetour = findViewById(R.id.retourButton)
        mErrorConnexion = findViewById(R.id.ErrorMessage)

        databaseManager = DatabaseManager(applicationContext)

        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.GONE

        mConnexion = findViewById(R.id.Connexion)

        mConnexion.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            Handler().postDelayed({ progressBar.visibility = View.GONE }, 4000)

            username = mIDtext.text.toString()
            password = mPassword3.text.toString()
            mail = mClef.text.toString()
            val paysInt = if (mFranceBoutton.isChecked) 1 else 0
            pays = paysInt.toString()

            createAccount()
        }

        mRetour.setOnClickListener {
            val createAccount = Intent(this@Createuser, loggin::class.java)
            startActivity(createAccount)
            finish()
        }
    }

    private fun onApiResponse(response: JSONObject) {
        var success: Boolean
        var error: String

        try {
            success = response.getBoolean("success")

            if (success) {
                val decorationActivity = Intent(applicationContext, MainActivity::class.java)
                decorationActivity.putExtra("username", username)
                decorationActivity.putExtra("pays", pays)
                startActivity(decorationActivity)
                finish()
            } else {
                error = response.getString("error")
                mErrorConnexion.visibility = View.VISIBLE
                mErrorConnexion.text = error
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "BRAVO !!", Toast.LENGTH_LONG).show()
        }
    }

    private fun createAccount() {
        val url = "https://oribabil.myhostpoint.ch/createusers/action/createaccount.php"
        val params = HashMap<String, String>()
        params["username"] = username
        params["password"] = password
        params["mail"] = mail
        params["pays"] = pays
        val parameters = JSONObject(params as Map<*, *>?)

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, parameters,
            { response ->
                onApiResponse(response)
                Toast.makeText(applicationContext, "Successs", Toast.LENGTH_LONG).show()
            },
            { error -> Toast.makeText(applicationContext, "erreur de connection au serveur !!", Toast.LENGTH_LONG).show() }
        )

        databaseManager.queue.add(jsonObjectRequest)
    }
}
