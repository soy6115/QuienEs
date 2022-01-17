package com.juegos.quienEs.entidades

import android.util.Log
import java.io.Serializable

// Usamos esta clase para guardar a los equipos y poder trabajar juntos sobre ellos
// hereda de seriarizable para poder pasarlo en el bundle
//TODO PLANTEAR CAMBIO DE NOMBRE A PARTIDO
class Equipos (var equipos: ArrayList<Equipo>) : Serializable {

    // devuelve todos los jugadores
    fun getJugadores() : ArrayList<Jugador> {
        val jugadores = arrayListOf<Jugador>()
        for (equipo in equipos)
            for (jugador in equipo.jugadores)
                jugadores.add(jugador)
        return jugadores
    }

    // devuelve el nombre de  los jugadores
    fun getNombreJugadores() : ArrayList<String> {
        val nombres = arrayListOf<String>()
        for (equipo in equipos)
            for (jugador in equipo.jugadores)
                nombres.add(jugador.nombre)
        return nombres
    }

    fun getJugadorActual(): Jugador {
        val equipo = getEquipoActual()
        return  equipo.jugadores.first()
    }


    // devuelve todos los personajes
    fun getPersonajes() : ArrayList<String>{
        val personajes = arrayListOf<String>()
        for (jugador in getJugadores())
            for (personaje in jugador.personajes)
                personajes.add(personaje)
        return personajes
    }

    // pasar saber el orden de los equipos
    fun getEquipoActual(): Equipo {
        return equipos.first()
    }

    // para cambiar el orden de los equipos (el primero pasa a ser el último)
    fun siguienteEquipo() {
        val primerEquipo = equipos.first()
        equipos.removeAt(0)
        equipos.add(primerEquipo)
    }

    fun getEquiposOrdenados() : List<Equipo>{
        val arrayAux = arrayListOf<Equipo>()
        val auxEquipos = equipos
        arrayAux.addAll(auxEquipos.sortedByDescending { it.puntosTotal })
        return arrayAux
    }

    fun actualizarMarcador (numRonda : Int) {
        for (equipo in equipos){
            when (numRonda) {
                1 -> equipo.puntosR1 = equipo.puntos
                2 -> equipo.puntosR2 = equipo.puntos
                3 -> equipo.puntosR3 = equipo.puntos
                else -> equipo.puntos = 0
            }
            equipo.puntosTotal += equipo.puntos
            equipo.puntos = 0
        }
    }
}