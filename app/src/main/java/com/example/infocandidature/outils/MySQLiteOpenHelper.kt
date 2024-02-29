package com.example.infocandidature.outils;



import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class MySQLiteOpenHelper(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {

    private val creation = "create table Chapitre(" +
            "Numero INTEGER NOT NULL," +
            "Ouvert INTEGER NOT NULL);"

    private val creationObjet = "create table Objet(" +
            "Numero INTEGER NOT NULL," +
            "Ouvert INTEGER NOT NULL);"

    private val creationActions = "create table Actions(" +
            "Numero INTEGER NOT NULL," +
            "Ouvert INTEGER NOT NULL);"

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(creation)
        sqLiteDatabase.execSQL(creationObjet)
        sqLiteDatabase.execSQL(creationActions)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        // You can implement upgrade logic here if needed
    }

    fun getData(parameter: String): List<String> {
        val dataList = ArrayList<String>()
        val db = readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM table WHERE column = ?", arrayOf(parameter))
        if (cursor.moveToFirst()) {
            do {
                dataList.add(cursor.getString(cursor.getColumnIndex("column")))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return dataList
    }
}

