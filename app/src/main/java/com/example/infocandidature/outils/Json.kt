package com.example.infocandidature.outils


import android.content.Context
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.stream.Collectors

object Json {

    fun readFromFile(context: Context, fileName: String): JSONArray {
        return try {
            val inputStream: InputStream = context.assets.open(fileName)
            readFromInputStream(inputStream)
        } catch (e: Exception) {
            JSONArray()
        }
    }

    fun readFromResources(context: Context, resource: Int): JSONArray {
        return try {
            val inputStream: InputStream = context.resources.openRawResource(resource)
            readFromInputStream(inputStream)
        } catch (e: Exception) {
            JSONArray()
        }
    }

    @Throws(JSONException::class)
    private fun readFromInputStream(inputStream: InputStream): JSONArray {
        val reader = BufferedReader(InputStreamReader(inputStream))
        val inputString = reader.lines().collect(Collectors.joining())
        return JSONArray(inputString)
    }
}
