package com.juegos.quienEs.rondas

import com.juegos.quienEs.R

// es la segunda ronda, como el fin del turno es barajear las cartas, lo dejamos en Ronda
class SegundaRonda: Ronda() {

    override val titulo: Int = R.string.R2_TITULO
    override val descripcion: Int = R.string.R2_DESCRIPCION


    override fun isPrimeraRonda(): Boolean {
        return false
    }

    override fun isUltimaRonda(): Boolean {
        return false
    }




}