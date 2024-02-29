package com.example.infocandidature.View


import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.infocandidature.R
import com.example.infocandidature.outils.DatabaseManager
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*
import android.util.Base64


class siteModificator : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var bModificator: Button
    private lateinit var mLogo: ImageButton
    private lateinit var mLeft: ImageButton
    private lateinit var mRight: ImageButton
    private lateinit var tLogo: TextView
    private lateinit var tLeft: TextView
    private lateinit var tRight: TextView
    private lateinit var tContact: TextView
    private lateinit var tValidation: TextView
    private lateinit var iLogo: ImageView
    private lateinit var iLeft: ImageView
    private lateinit var iRight: ImageView
    private lateinit var eTextContact: EditText
    private lateinit var mrb1: RadioButton
    private lateinit var mrb2: RadioButton
    private lateinit var mrb3: RadioButton
    private lateinit var mdatabaseManager: DatabaseManager
    private lateinit var contactDescri: String
    private val REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site_modificator)

        webView = findViewById(R.id.webview)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
        webView.loadUrl("https://oribabil.myhostpoint.ch")

        mdatabaseManager = DatabaseManager(applicationContext)

        iLogo = findViewById(R.id.imgLogo)
        iLeft = findViewById(R.id.imgLeft)
        iRight = findViewById(R.id.imgRight)

        mLogo = findViewById(R.id.LogoSearch)
        mLeft = findViewById(R.id.LeftSearch)
        mRight = findViewById(R.id.RightSearch)
        bModificator = findViewById(R.id.buttonModificator)

        bModificator.isEnabled = false
        bModificator.isClickable = false

        tLogo = findViewById(R.id.logoText)
        tLeft = findViewById(R.id.LeftText)
        tRight = findViewById(R.id.RightText)
        tContact = findViewById(R.id.textContact)
        tValidation = findViewById(R.id.textValidation)

        tValidation.visibility = View.INVISIBLE

        eTextContact = findViewById(R.id.editTextContact)

        eTextContact.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    boutonOK()
                } else {
                    boutonPasOK()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        mrb1 = findViewById(R.id.radioButtonLogo)
        mrb2 = findViewById(R.id.radioButtonLeft)
        mrb3 = findViewById(R.id.radioButtonRight)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE
            )
        }

        mLogo.setOnClickListener {
            mrb1.isSelected = true
            mrb2.isSelected = false
            mrb3.isSelected = false
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1)
        }

        mLeft.setOnClickListener {
            mrb1.isSelected = false
            mrb2.isSelected = true
            mrb3.isSelected = false
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1)
        }

        mRight.setOnClickListener {
            mrb1.isSelected = false
            mrb2.isSelected = false
            mrb3.isSelected = true
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1)
        }

        bModificator.setOnClickListener {
            contactDescri = eTextContact.text.toString()
            val iLogoEnvoie: Drawable = iLogo.drawable
            val iLeftEnvoie: Drawable = iLeft.drawable
            val iRightEnvoie: Drawable = iRight.drawable

            if (iLogoEnvoie is BitmapDrawable) {
                if (iLeftEnvoie is BitmapDrawable) {
                    if (iRightEnvoie is BitmapDrawable) {
                        logoEnvoie(iLogoEnvoie, contactDescri)
                        leftEnvoie(iLeftEnvoie, contactDescri)
                        rightEnvoie(iRightEnvoie, contactDescri)
                        iLogo.setImageBitmap(null)
                        iRight.setImageBitmap(null)
                        iLeft.setImageBitmap(null)
                    } else {
                        logoEnvoie(iLogoEnvoie, contactDescri)
                        leftEnvoie(iLeftEnvoie, contactDescri)
                        iLogo.setImageBitmap(null)
                        iLeft.setImageBitmap(null)
                    }
                } else if (iRightEnvoie is BitmapDrawable) {
                    logoEnvoie(iLogoEnvoie, contactDescri)
                    rightEnvoie(iRightEnvoie, contactDescri)
                    iRight.setImageBitmap(null)
                    iLogo.setImageBitmap(null)
                } else {
                    logoEnvoie(iLogoEnvoie, contactDescri)
                    iLogo.setImageBitmap(null)
                }
            } else {
                if (iLeftEnvoie is BitmapDrawable) {
                    if (iRightEnvoie is BitmapDrawable) {
                        leftEnvoie(iLeftEnvoie, contactDescri)
                        rightEnvoie(iRightEnvoie, contactDescri)
                        iRight.setImageBitmap(null)
                        iLeft.setImageBitmap(null)
                    }
                } else {
                    if (iRightEnvoie is BitmapDrawable) {
                        rightEnvoie(iRightEnvoie, contactDescri)
                        iRight.setImageBitmap(null)
                    } else {
                        if (eTextContact.text.toString().trim { it <= ' ' }.isEmpty()) {
                            tValidation.visibility = View.VISIBLE
                            tValidation.text = "Veuillez choisir un élément à modifier"
                        } else {
                            val url = "https://oribabil.myhostpoint.ch/createusers/action/siteModificator.php"
                            val params: MutableMap<String, String> = HashMap()
                            params["texteContact"] = contactDescri
                            val parameters = JSONObject(params as Map<*, *>?)
                            val jsonObjectRequest = JsonObjectRequest(
                                Request.Method.POST, url, parameters,
                                { response -> onApiResponse(response) },
                                { error -> Toast.makeText(applicationContext, "erreur de connection au serveur !!", Toast.LENGTH_LONG).show() })
                            mdatabaseManager.queue.add(jsonObjectRequest)
                        }
                    }
                }
            }
            val handler = Handler()
            handler.postDelayed({
                webView.reload()
                boutonPasOK()
                eTextContact.setText("")
            }, 5000)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val selectedImage: Uri? = data?.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? = contentResolver.query(selectedImage!!, filePathColumn, null, null, null)
            cursor?.moveToFirst()
            val columnIndex: Int = cursor!!.getColumnIndex(filePathColumn[0])
            val imgPath: String = cursor.getString(columnIndex)
            cursor.close()
            val originalImage: Bitmap = BitmapFactory.decodeFile(imgPath)
            val maxSize = 3024
            val originalSize: Int = originalImage.byteCount / 3024
            if (originalSize > maxSize) {
                val newWidth = (originalImage.width * maxSize / originalSize.toFloat()).toInt()
                val newHeight = (originalImage.height * maxSize / originalSize.toFloat()).toInt()
                val resizedImage: Bitmap = Bitmap.createScaledBitmap(originalImage, newWidth, newHeight, true)
                when {
                    mrb1.isSelected -> {
                        iLogo.setImageBitmap(resizedImage)
                        boutonOK()
                    }
                    mrb2.isSelected -> {
                        iLeft.setImageBitmap(resizedImage)
                        boutonOK()
                    }
                    else -> {
                        iRight.setImageBitmap(resizedImage)
                        boutonOK()
                    }
                }
            } else {
                when {
                    mrb1.isSelected -> {
                        iLogo.setImageBitmap(originalImage)
                        boutonOK()
                    }
                    mrb2.isSelected -> {
                        iLeft.setImageBitmap(originalImage)
                        boutonOK()
                    }
                    else -> {
                        iRight.setImageBitmap(originalImage)
                        boutonOK()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission accordée pour accéder au stockage externe", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(
                    this,
                    "Permission refusée pour accéder au stockage externe",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun boutonOK() {
        bModificator.isEnabled = true
        bModificator.isClickable = true
    }

    private fun boutonPasOK() {
        bModificator.isEnabled = false
        bModificator.isClickable = false
    }

    @SuppressLint("SetTextI18n")
    private fun onApiResponse(response: JSONObject) {
        var success: Boolean
        var error = ""
        try {
            success = response.getBoolean("success")
            if (success) {
                tValidation.visibility = View.VISIBLE
                tValidation.text = "Site modifié!"
            } else {
                tValidation.visibility = View.VISIBLE
                tValidation.text = error
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "BRAVO !!", Toast.LENGTH_LONG).show()
        }
    }

    private fun logoEnvoie(iLogoEnvoie: Drawable, contactDescri: String) {
        val bitmapDrawableLogo = iLogoEnvoie as BitmapDrawable
        val bitmapLogo = bitmapDrawableLogo.bitmap
        val ByteStreamLogo = ByteArrayOutputStream()
        bitmapLogo.compress(Bitmap.CompressFormat.PNG, 100, ByteStreamLogo)
        val bLogo = ByteStreamLogo.toByteArray()
        val donneeLogo = Base64.encodeToString(bLogo, Base64.DEFAULT)
        val url = "https://oribabil.myhostpoint.ch/createusers/action/siteModificator.php"
        val params: MutableMap<String, String> = HashMap()
        params["headerlogo"] = donneeLogo
        params["texteContact"] = contactDescri
        val parameters = JSONObject(params as Map<*, *>?)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, parameters,
            { response -> onApiResponse(response) },
            { error -> Toast.makeText(applicationContext, "erreur de connection au serveur !!", Toast.LENGTH_LONG).show() })
        mdatabaseManager.queue.add(jsonObjectRequest)
    }

    private fun leftEnvoie(iLeftEnvoie: Drawable, contactDescri: String) {
        val bitmapDrawableLeft = iLeftEnvoie as BitmapDrawable
        val bitmapLeft = bitmapDrawableLeft.bitmap
        val ByteStreamLeft = ByteArrayOutputStream()
        bitmapLeft.compress(Bitmap.CompressFormat.PNG, 100, ByteStreamLeft)
        val bLeft = ByteStreamLeft.toByteArray()
        val donneeLeft = Base64.encodeToString(bLeft, Base64.DEFAULT)
        val url = "https://oribabil.myhostpoint.ch/createusers/action/siteModificator.php"
        val params: MutableMap<String, String> = HashMap()
        params["headerleft"] = donneeLeft
        params["texteContact"] = contactDescri
        val parameters = JSONObject(params as Map<*, *>?)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, parameters,
            { response -> onApiResponse(response) },
            { error -> Toast.makeText(applicationContext, "erreur de connection au serveur !!", Toast.LENGTH_LONG).show() })
        mdatabaseManager.queue.add(jsonObjectRequest)
    }

    private fun rightEnvoie(iRightEnvoie: Drawable, contactDescri: String) {
        val bitmapDrawableRight = iRightEnvoie as BitmapDrawable
        val bitmapRight = bitmapDrawableRight.bitmap
        val ByteStreamRight = ByteArrayOutputStream()
        bitmapRight.compress(Bitmap.CompressFormat.PNG, 100, ByteStreamRight)
        val bRight = ByteStreamRight.toByteArray()
        val donneeRight = Base64.encodeToString(bRight, Base64.DEFAULT)
        val url = "https://oribabil.myhostpoint.ch/createusers/action/siteModificator.php"
        val params: MutableMap<String, String> = HashMap()
        params["headerright"] = donneeRight
        params["texteContact"] = contactDescri
        val parameters = JSONObject(params as Map<*, *>?)
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST, url, parameters,
            { response -> onApiResponse(response) },
            { error -> Toast.makeText(applicationContext, "erreur de connection au serveur !!", Toast.LENGTH_LONG).show() })
        mdatabaseManager.queue.add(jsonObjectRequest)
    }
}
