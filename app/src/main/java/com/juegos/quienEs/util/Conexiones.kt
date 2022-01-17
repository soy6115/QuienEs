package com.juegos.quienEs.util

import android.content.SharedPreferences

// en esta clase devolvemos todos los datos que guardamos en el shared preferences
class Conexiones (private val pref: SharedPreferences) {

    // devuelve el número de jugadores por partida
    fun getNumeroJugadores(): Int = pref.getInt(Constantes.SP_NUM_JUGADORES, 4)

    // devuelve la baraja con la que vamos a jugar la partida
    fun getBaraja(): Int = pref.getInt(Constantes.SP_COLOR, Constantes.BARAJA_AZUL)

    // devuelve el nivel de dificultad
    fun getNivelDificultad():Int = pref.getInt(Constantes.SP_DIFICULTAD, Constantes.NIVEL_NORMAL)

    // devuelve el número de cartas con las que se va a jugar la partida
    fun getNumeroCartasTotal(): Int = pref.getInt(Constantes.SP_NUM_CARTAS, 20)

    // nos devuelve el número de cartas que se descartan basandonos en el nivel de dificultad
    fun getNumeroCartasADescartar(): Int{
        return when (getNivelDificultad()) {
            Constantes.NIVEL_FACIL -> 4
            Constantes.NIVEL_NORMAL -> 2
            Constantes.NIVEL_DIFICIL -> 0
            else -> 2
        }
    }

    fun guardarDatos(numJugadores: Int, colorBaraja: Int, dificultad: Int, numCartas: Int) {
        val editor = pref.edit()

        editor.putInt(Constantes.SP_NUM_JUGADORES, numJugadores)
        editor.putInt(Constantes.SP_COLOR, colorBaraja)
        editor.putInt(Constantes.SP_DIFICULTAD, dificultad)
        editor.putInt(Constantes.SP_NUM_CARTAS, numCartas)

        editor.commit()
    }

}