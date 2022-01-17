package com.juegos.quienEs.rondas

import com.juegos.quienEs.R

// es la tercera ronda, como el fin del turno es barajear las cartas, lo dejamos en Ronda
class TerceraRonda : Ronda() {
    override val titulo: Int = R.string.R3_TITULO
    override val descripcion: Int = R.string.R3_DESCRIPCION

    override fun isPrimeraRonda(): Boolean {
        return false
    }

    override fun isUltimaRonda(): Boolean {
        return true
    }



}