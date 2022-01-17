package com.juegos.quienEs.rondas

// clase padre de la que heredarán las tres rondas diferentes
abstract class Ronda {

    // siempre va a tener un título y descipción, lo guardamos como Int para obtener el valor del
    // strings.xml pero no puedo usar aqui el getString()
    abstract val titulo : Int
    abstract val descripcion : Int

    // el método que determina que hacer al terminar el turno
    open fun finTurno(personajes: ArrayList<String>) {
        personajes.shuffle()
    }

    // para saber en que turno nos encontramos (al tener estos dos, no necesitamos preguntar la
    // ronda del medio
    abstract fun isPrimeraRonda() : Boolean
    abstract fun isUltimaRonda() : Boolean

}