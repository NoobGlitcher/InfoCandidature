package com.example.infocandidature.Model;


import java.io.Serializable

class Sauvegarde(val numero: Int,  val ouvert: Int) : Serializable {

    fun numeroGetter(): Int {
        return numero
    }


    fun ouvertGetter(): Int {
        return ouvert
    }

    fun numeroObjetGetter(): Int {
        return numero
    }

    fun OuvertObjetGetter(): Int {
        return ouvert
    }

    fun NumeroActionsGetter(): Int {
        return numero
    }

    fun OuvertActionsGetter(): Int {
        return ouvert
    }
}
