package com.juegos.quienEs.entidades

import java.io.Serializable

// guarda para cada equipo un identificador, el nombre y los jugadores que pertenecen al equipo
// hereda de serializable porque lo pasamos en el bundle
class Equipo (val id: Int, val nombre: String, var jugadores: ArrayList<Jugador> )  : Serializable {

    // se crea evidentemente con el puntaje a cero
    var puntos = 0
    var puntosR1 = 0
    var puntosR2 = 0
    var puntosR3 = 0
    var puntosTotal = 0


    // va actualizando el orden de los jugadores
    fun siguienteJugador(){
        val primerJugador = jugadores.first()
        jugadores.removeAt(0)
        jugadores.add(primerJugador)
    }

    fun acierto () {
        puntos++
    }

    fun getJugador(nombre: String) : Jugador{
        for (jugador in jugadores)
            if (jugador.nombre == nombre)
                return jugador
        return jugadores[0]
    }


    fun ordenJugadores(numRonda : Int) : ArrayList<Jugador>{
        val aux = arrayListOf<Jugador>()
        when (numRonda) {
            1 -> aux.addAll(jugadores.sortedByDescending { it.primeraRonda })
            2 -> aux.addAll(jugadores.sortedByDescending { it.segundaRonda })
            3 -> aux.addAll(jugadores.sortedByDescending { it.terceraRonda })
            else -> aux.addAll(jugadores.sortedByDescending { it.puntos })
        }
        return aux
    }

}
