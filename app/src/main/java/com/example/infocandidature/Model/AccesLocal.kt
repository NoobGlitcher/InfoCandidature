package com.example.infocandidature.Model;


import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase;
import com.example.infocandidature.outils.MySQLiteOpenHelper;

class AccesLocal(context: Context) {
    private val nomBase = "dbProfil.sqlite"
    private val versionBase = 3
    private val accesBD: MySQLiteOpenHelper = MySQLiteOpenHelper(context, nomBase, null, versionBase)
    private lateinit var bd: SQLiteDatabase

    fun ajout(chapitre: Sauvegarde) {
        bd = accesBD.writableDatabase
        var req = buildString {
        append("insert into Chapitre (Numero,Ouvert) values")
    }
        req += "(${chapitre.numero},${chapitre.ouvert})"
        bd.execSQL(req)
    }

    fun recupDernier(): Sauvegarde? {
        bd = accesBD.readableDatabase
        var chapitre: Sauvegarde? = null
        val req = "select * from Chapitre"
        val curseur: Cursor = bd.rawQuery(req, null)
        curseur.moveToLast()
        if (!curseur.isAfterLast) {
            val numero: Int = curseur.getInt(0)
            val ouvert: Int = curseur.getInt(1)
            chapitre = Sauvegarde(numero, ouvert)
        }
        curseur.close()
        return chapitre
    }

    fun ajoutObjet(objet: Sauvegarde) {
        bd = accesBD.writableDatabase
        var req2 = "insert into Objet (Numero,Ouvert) values"
        req2 += "(${objet.numero},${objet.ouvert})"
        bd.execSQL(req2)
    }

    fun recupDernierObjet(): Sauvegarde? {
        bd = accesBD.readableDatabase
        var objet: Sauvegarde? = null
        val req2 = "select * from Objet"
        val curseur: Cursor = bd.rawQuery(req2, null)
        curseur.moveToLast()
        if (!curseur.isAfterLast) {
            val numero: Int = curseur.getInt(0)
            val ouvert: Int = curseur.getInt(1)
            objet = Sauvegarde(numero, ouvert)
        }
        curseur.close()
        return objet
    }

    fun ajoutActions(actions: Sauvegarde) {
        bd = accesBD.writableDatabase
        var req2 = "insert into Actions (Numero,Ouvert) values"
        req2 += "(${actions.numero},${actions.ouvert})"
        bd.execSQL(req2)
    }

    fun recupDernierActions(): Sauvegarde? {
        bd = accesBD.readableDatabase
        var actions: Sauvegarde? = null
        val req2 = "select * from Actions"
        val curseur: Cursor = bd.rawQuery(req2, null)
        curseur.moveToLast()
        if (!curseur.isAfterLast) {
            val numero: Int = curseur.getInt(0)
            val ouvert: Int = curseur.getInt(1)
            actions = Sauvegarde(numero, ouvert)
        }
        curseur.close()
        return actions
    }

    fun recupTousObjets(): List<Sauvegarde> {
        bd = accesBD.readableDatabase
        val listObjets: MutableList<Sauvegarde> = ArrayList()
        val req2 = "SELECT * FROM Objet"
        val curseur: Cursor = bd.rawQuery(req2, null)
        try {
            while (curseur.moveToNext()) {
                val numero: Int = curseur.getInt(0)
                val ouvert: Int = curseur.getInt(1)
                val objets = Sauvegarde(numero, ouvert)
                listObjets.add(objets)
            }
        } finally {
            curseur.close()
        }
        return listObjets
    }

    fun recupToutesActions(): List<Sauvegarde> {
        bd = accesBD.readableDatabase
        val listActions: MutableList<Sauvegarde> = ArrayList()
        val req2 = "SELECT * FROM Actions"
        val curseur: Cursor = bd.rawQuery(req2, null)
        try {
            while (curseur.moveToNext()) {
                val numero: Int = curseur.getInt(0)
                val ouvert: Int = curseur.getInt(1)
                val action = Sauvegarde(numero, ouvert)
                listActions.add(action)
            }
        } finally {
            curseur.close()
        }
        return listActions
    }
}
