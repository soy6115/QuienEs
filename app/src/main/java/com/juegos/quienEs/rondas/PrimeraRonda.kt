package com.juegos.quienEs.rondas

import com.juegos.quienEs.R

// Primera ronda
class PrimeraRonda : Ronda() {

    override val titulo: Int = R.string.R1_TITULO
    override val descripcion: Int = R.string.R1_DESCRIPCION

    // en esta ronda el personaje que estaba arriba se pone el último, pisa al del padre
    override fun finTurno(personajes: ArrayList<String>) {
        val personajeActual = personajes.first()
        personajes.removeAt(0)
        personajes.add(personajeActual)
    }

    override fun isPrimeraRonda(): Boolean {
        return true
    }

    override fun isUltimaRonda(): Boolean {
        return false
    }

}