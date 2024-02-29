package com.example.infocandidature.Controle;

import android.content.Context
import com.example.infocandidature.Model.AccesLocal
import com.example.infocandidature.Model.Sauvegarde

class Controle private constructor() {

    companion object {
        private var instance: Controle? = null
        private var chapitre: Sauvegarde? = null
        private var objet: Sauvegarde? = null
        private var actions: Sauvegarde? = null
        private lateinit var accesLocal: AccesLocal
        private lateinit var listObjets: List<Sauvegarde>
        private lateinit var listActions: List<Sauvegarde>

        fun getInstance(context: Context): Controle {
            if (instance == null) {
                instance = Controle()
                accesLocal = AccesLocal(context)
                chapitre = accesLocal.recupDernier()
                objet = accesLocal.recupDernierObjet()
                actions = accesLocal.recupDernierActions()
                listObjets = accesLocal.recupTousObjets()
                listActions = accesLocal.recupToutesActions()
            }
            return instance!!
        }
    }

    fun creerProfil(numero: Int, ouvert: Int, context: Context) {
        chapitre = Sauvegarde(numero, ouvert)
        accesLocal.ajout(chapitre!!)
    }

    fun creerObjet(numero: Int, ouvert: Int, context: Context) {
        objet = Sauvegarde(numero, ouvert)
        accesLocal.ajoutObjet(objet!!)
    }

    fun creerActions(numero: Int, ouvert: Int, context: Context) {
        actions = Sauvegarde(numero, ouvert)
        accesLocal.ajoutActions(actions!!)
    }

    fun getNumero(): Int? = chapitre?.numeroGetter()
    fun getOuvert(): Int? = chapitre?.ouvertGetter()
    fun getNumeroObjet(): Int? = objet?.numeroObjetGetter()
    fun getOuvertObjet(): Int? = objet?.OuvertObjetGetter()
    fun getNumeroActions(): Int? = actions?.NumeroActionsGetter()
    fun getOuvertActions(): Int? = actions?.OuvertActionsGetter()
    fun getListObjets(): List<Sauvegarde>? = listObjets
    fun getListActions(): List<Sauvegarde>? = listActions
}
