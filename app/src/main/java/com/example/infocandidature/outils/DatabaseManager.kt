package com.example.infocandidature.outils;

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class DatabaseManager(context: Context) {
    private val context: Context = context
    val queue: RequestQueue = Volley.newRequestQueue(context)
}
