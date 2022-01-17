package com.juegos.quienEs.entidades

import java.io.Serializable


// en esta clase solo vamos a guardar el nombre del jugador y de forma provisional un listado de
// personajes provisionales para que pueda descartarlos. Heredamos de serializable porque vamos
// a pasarlo a través del bundle
data class Jugador(val nombre: String, var personajes: List<String>) : Serializable {

    var puntos = 0
    var primeraRonda = 0
    var segundaRonda = 0
    var terceraRonda = 0

    fun aciertoPrimeraRonda () {
        puntos++
        primeraRonda++
    }

    fun aciertoSegundaRonda () {
        puntos++
        segundaRonda++
    }

    fun aciertoTerceraRonda () {
        puntos++
        terceraRonda++
    }


}
