package com.example.infocandidature.View


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.infocandidature.R
import com.example.infocandidature.outils.DatabaseManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class Bidules : AppCompatActivity() {

    private lateinit var mDatabaseManager: DatabaseManager
    private var textileType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bidules)
        mDatabaseManager = DatabaseManager(applicationContext)

        textileType = intent.getStringExtra("type")

        connectUser()
    }

    class ImageTextListAdapter(private val mContext: Context, private val mData: ArrayList<HashMap<String, Any>>) :
        BaseAdapter() {

        override fun getCount(): Int {
            return mData.size
        }

        override fun getItem(position: Int): Any {
            return mData[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var convertView = convertView
            val holder: ViewHolder

            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.listtextile_view, parent, false)
                holder = ViewHolder()
                holder.imageView = convertView.findViewById(R.id.testimage)
                holder.textView = convertView.findViewById(R.id.testtext2)
                holder.textView2 = convertView.findViewById(R.id.testtext1)
                holder.button = convertView.findViewById(R.id.buybutton)

                convertView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
            }

            val item = mData[position]

            // Vérifier si l'objet Bitmap est null avant de le définir dans l'ImageView
            val bitmap = item["img_desc"] as? Bitmap

// Vérifiez si l'objet est null ou s'il ne s'agit pas d'un Bitmap
            if (bitmap != null) {
                // Si c'est un Bitmap valide, définissez-le dans l'ImageView
                holder.imageView.setImageBitmap(bitmap)
            } else {
                holder.imageView.setImageDrawable(null) // Pour vider l'ImageView

                Log.e("ImageViewError", "L'objet Bitmap est null ou invalide pour la position $position")
            }
            holder.textView.text = item["img_type"].toString()
            holder.textView2.text = item["img_descri"].toString()

            // Vérifiez le type de données pour décider d'afficher ou de masquer le bouton "buy"
            val itemType = item["img_type"]
            if (itemType is String && itemType.isNotEmpty()) {
                holder.button.visibility = View.VISIBLE // Afficher le bouton
            } else {
                holder.button.visibility = View.GONE // Masquer le bouton
            }

            holder.button.setOnClickListener {
                // Faire quelque chose lorsque le bouton est cliqué pour cet élément de la liste
                val intent = Intent(mContext, Eshop::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Ajoutez le drapeau FLAG_ACTIVITY_NEW_TASK
                mContext.startActivity(intent)
            }

            return convertView!!
        }

        internal class ViewHolder {
            lateinit var imageView: ImageView
            lateinit var textView: TextView
            lateinit var textView2: TextView
            lateinit var button: Button
        }
    }

    @SuppressLint("ResourceType")
    private fun connectUser() {
        val params: MutableMap<Any?, Any?> = HashMap() // Utilisation de Any? pour correspondre à la signature Java
        params["img_type"] = textileType.toString()

        val parameters = JSONObject(params as Map<*, *>?) // Conversion explicite

        val url = "https://oribabil.myhostpoint.ch/createusers/action/testReception.php"
        val request = JsonObjectRequest(Request.Method.POST, url, parameters,
            { response ->
                try {
                    val rows = response.getJSONArray("rows")
                    val table = rows.toString()
                    val table2 = table.replace("[{}\\s]".toRegex(), "")
                    val table3 = table2.replace("^\\[|\"\\]$".toRegex(), "")
                    val tableauString = table3.split(",".toRegex()).toTypedArray()
                    val data: ArrayList<HashMap<String, Any>> = ArrayList()
                    for (element in tableauString) {
                        val keyValue = element.split(":".toRegex()).toTypedArray()
                        if (keyValue.size == 2) {
                            val key = keyValue[0].replace("^\"|\"$".toRegex(), "")
                            val value = keyValue[1].replace("^\"|\"$".toRegex(), "")
                            val map: HashMap<String, Any> = HashMap()
                            if (key == "img_desc") {
                                val decodedString = Base64.decode(value, Base64.DEFAULT)
                                if (decodedString != null && decodedString.isNotEmpty()) {
                                    val bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                                    if (bitmap != null) {
                                        map["img_desc"] = bitmap
                                    } else {
                                        Log.e("BitmapDecode", "Le bitmap est null après le décodage du tableau de bytes")
                                    }
                                } else {
                                    Log.e("BitmapDecode", "La chaîne décodée est null ou vide")
                                }

                            } else {
                                map[key] = value
                            }
                            data.add(map)
                        } else {
                            Log.e("KeyValueError", "Key-value pair size is not 2: $element")
                        }
                    }

                    val adapter = ImageTextListAdapter(applicationContext, data)
                    val listView = findViewById<ListView>(R.id.list_view2)
                    listView?.apply {
                        divider = resources.getDrawable(R.xml.list_separator)
                        dividerHeight = 1
                        setAdapter(adapter)
                    } ?: run {
                        Log.e("ListViewError", "ListView is null")
                        Toast.makeText(applicationContext, "listview null", Toast.LENGTH_LONG).show()
                    }

                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(applicationContext, "Erreur de traitement de la réponse JSON", Toast.LENGTH_LONG).show()
                }
            },
            { error ->
                error.printStackTrace()
                Toast.makeText(applicationContext, "Erreur de connexion au serveur !!", Toast.LENGTH_LONG).show()
            })

        mDatabaseManager.queue.add(request)
    }


}
