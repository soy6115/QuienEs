package com.juegos.quienEs.activitys

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.juegos.quienEs.R
import com.juegos.quienEs.databinding.ActivityNombreJugadoresBinding
import com.juegos.quienEs.entidades.Equipo
import com.juegos.quienEs.entidades.Equipos
import com.juegos.quienEs.entidades.Jugador
import com.juegos.quienEs.fragments.*
import com.juegos.quienEs.util.ConexionTXT
import com.juegos.quienEs.util.Conexiones
import com.juegos.quienEs.util.Constantes
import java.util.*
import kotlin.collections.ArrayList

class ANombreJugadores : AppCompatActivity() {

    private lateinit var personajesPartida : ArrayList<String>
    private lateinit var binding: ActivityNombreJugadoresBinding
    private lateinit var nombresGuardados : ArrayList<String>
    private lateinit var conexionTXT : ConexionTXT
    private var numJugadores = 0
    private var auxNumJugadores = 0
    private var swEquipos = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNombreJugadoresBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inicializarVariables()
        loadFragment()
        cargarPersonajes()

        // como solo hay un botón, lo dejo aquí. Le ponemos su listener simplemente
        val btnEmpezar = binding.btnEmpezar
        btnEmpezar.setOnClickListener {
            hacerEquipos()
        }
    }

    private fun inicializarVariables() {
        val bundle = intent.extras
        numJugadores = bundle!!.getInt(Constantes.NUM_JUGADORES)
        swEquipos = bundle.getBoolean(Constantes.TIPO)

        personajesPartida = arrayListOf()

        // necesitamos crear la aux para ir restando el número de jugadores sin perder el original
        auxNumJugadores = numJugadores

        // sacamos del archivo txt los nombres de las personas que han jugado con anterioridad
        // porque necesitamos añadirlo posteriormente en el fragment
        conexionTXT = ConexionTXT(baseContext)
        nombresGuardados = conexionTXT.obtenerJugadoresGuardados()

    }

    // dependiendo del tipo de juego y el número de jugadores, cargaremos el fragemt correcto
    private fun loadFragment() {
        val fragment: Fragment?
        if (swEquipos){
            fragment = when (numJugadores) {
                4-> Fragment4J(nombresGuardados)
                5 -> Fragment5J(nombresGuardados)
                6 -> Fragment6J(nombresGuardados)
                7 -> Fragment7J(nombresGuardados)
                8 -> Fragment8J(nombresGuardados)
                9 -> Fragment9J(nombresGuardados)
                10 -> Fragment10J(nombresGuardados)
                11 -> Fragment11J(nombresGuardados)
                12 -> Fragment12J(nombresGuardados)
                else -> null
            }
        } else {
            fragment = Fragment3J(nombresGuardados, numJugadores)
        }

        if (fragment != null) {
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.frContenedor, fragment)
            fragmentTransaction.commit()
        }
    }

    // cogemos los personajes y hacemos los equipos
    private fun hacerEquipos() {
        val equipos = if (swEquipos) {
            crearEquipos()
        } else {
            crearEquiposBatalla()
        }

        // obtenemos el nivel de dificutal del shared preferences, si está en nivel difícil
        // podemos pasar directamente a la partida, en otro modo, debemos descartar personajes
        val conexion = Conexiones(getSharedPreferences(Constantes.SP_PREFERENCES, MODE_PRIVATE))
        val nivel = conexion.getNivelDificultad()
        if (nivel == Constantes.NIVEL_DIFICIL){
            irAJugar(equipos)
        } else {
            irASeleccionarPersonajes(equipos)
        }
    }

    // vamos a guardar los personajes que hay que adivinar en la partida
    private fun cargarPersonajes() {
        val conexion = Conexiones(getSharedPreferences(Constantes.SP_PREFERENCES, MODE_PRIVATE))

        // sabiendo la baraja de la partida, obtenemos todos los personajes de esa baraja por
        // último los mezclamos para que cada vez se juege con unos personajes diferentes
        val baraja = conexion.getBaraja()
        val personajesTotal = arrayListOf<String>()
        if (baraja == Constantes.BARAJA_AMARILLA)
            personajesTotal.addAll(resources.getStringArray(R.array.personajesAzul))
        else if (baraja == Constantes.BARAJA_AZUL)
            personajesTotal.addAll(resources.getStringArray(R.array.personajesAmarillo))
        personajesTotal.shuffle()

        // obtenemos el número de cartas para la partida y el número de descartes por jugador
        // para saber el número total de personajes con los que quedarnos
        val numeroDCartasPartida = conexion.getNumeroCartasTotal()
        val numeroDDescartes = conexion.getNumeroCartasADescartar()
        val numPersonajesPartida = numeroDCartasPartida + (numJugadores * numeroDDescartes)
        personajesPartida.addAll(personajesTotal.subList(0, numPersonajesPartida))
    }

    // vamos a crear a los jugadores, añadirle sus cartas de personaje y juntarlos por equipos
    private fun crearEquipos() : Equipos {
        // creamos los array que vamos a necesitar
        val equipos = arrayListOf<Equipo>()
        val jugadoresE1 = arrayListOf<Jugador>()
        val jugadoresE2 = arrayListOf<Jugador>()
        val jugadoresE3 = arrayListOf<Jugador>()
        val jugadoresE4 = arrayListOf<Jugador>()

        // nombre para todos los posibles equipos y jugadores
        var nombreEquipo1: String
        var nombreEquipo2: String
        var nombreEquipo3 = ""
        var nombreEquipo4 = ""
        var nombreJugador1E1: String
        var nombreJugador2E1: String
        var nombreJugador3E1: String
        var nombreJugador1E2: String
        var nombreJugador2E2: String
        var nombreJugador3E2: String
        var nombreJugador1E3: String
        var nombreJugador2E3: String
        var nombreJugador3E3: String
        var nombreJugador1E4: String
        var nombreJugador2E4: String
        var nombreJugador3E4: String

        // A partir de aquí se hace la ASIGNACIÓN de jugadores a cada equipo según el número de
        // equipos y jugadores. Lo dejamos con ifs simples ya que el mismo número de jugadores nos
        // va a dar lógicas de añadir jugadores diferentes.
        // Vamos a hacer siempre la comprobación de que si está el nombre vacío le asignemos un
        // nombre para poder identificarlo posteriormente

        // ASGINACIONES: sabemos que siempre, va a haber al menos dos equipos y dos jugadores por
        // equipo
        nombreEquipo1 = findViewById<EditText>(R.id.etNombreEquipo1).text.toString()
        if (nombreEquipo1.isEmpty())
            nombreEquipo1 = getString(R.string.EQ1)
        nombreEquipo2 = findViewById<EditText>(R.id.etNombreEquipo2).text.toString()
        if (nombreEquipo2.isEmpty())
            nombreEquipo2 = getString(R.string.EQ2)

        nombreJugador1E1 = findViewById<AutoCompleteTextView>(R.id.acE1J1).text.toString()
        if (nombreJugador1E1.isEmpty())
            nombreJugador1E1 = getString(R.string.J1)
        nombreJugador2E1 = findViewById<AutoCompleteTextView>(R.id.acE1J2).text.toString()
        if (nombreJugador2E1.isEmpty())
            nombreJugador2E1 = getString(R.string.J2)
        nombreJugador1E2 = findViewById<AutoCompleteTextView>(R.id.acE2J1).text.toString()
        if (nombreJugador1E2.isEmpty())
            nombreJugador1E2 = getString(R.string.J1)
        nombreJugador2E2 = findViewById<AutoCompleteTextView>(R.id.acE2J2).text.toString()
        if (nombreJugador2E2.isEmpty())
            nombreJugador2E2 = getString(R.string.J2)

        // añadimos al equipo el jugador, ya con los personajes que le corresponden
        jugadoresE1.add(Jugador(nombreJugador1E1, getPersonajesParaJugador()))
        jugadoresE1.add(Jugador(nombreJugador2E1, getPersonajesParaJugador()))
        jugadoresE2.add(Jugador(nombreJugador1E2, getPersonajesParaJugador()))
        jugadoresE2.add(Jugador(nombreJugador2E2, getPersonajesParaJugador()))


        // ASIGNACIONES: sabemos que cuando hay 5, 7 o 9..12 jugadores siempre el primer equipo va
        // a tener tres jugadores. Lo creamos, lo añadimos a su equipo y guardamos su nombre
        if (numJugadores == 5 || numJugadores == 7 || numJugadores in 9..12) {
            nombreJugador3E1 = findViewById<AutoCompleteTextView>(R.id.acE1J3).text.toString()
            if (nombreJugador3E1.isEmpty())
                nombreJugador3E1 = getString(R.string.J3)

            jugadoresE1.add(Jugador(nombreJugador3E1, getPersonajesParaJugador()))
        }

        // ASIGNACIONES: siempre que haya entre 6 y 12 jugadores va a haber un tercer equipo con al
        // menos dos jugadores
        if (numJugadores in 6..12){
            nombreEquipo3 = findViewById<EditText>(R.id.etNombreEquipo3).text.toString()
            if (nombreEquipo3.isEmpty())
                nombreEquipo3 = getString(R.string.EQ3)

            nombreJugador1E3 = findViewById<AutoCompleteTextView>(R.id.acE3J1).text.toString()
            if (nombreJugador1E3.isEmpty())
                nombreJugador1E3 = getString(R.string.J1)
            nombreJugador2E3 = findViewById<AutoCompleteTextView>(R.id.acE3J2).text.toString()
            if (nombreJugador2E3.isEmpty())
                nombreJugador2E3 = getString(R.string.J2)

            jugadoresE3.add(Jugador(nombreJugador1E3, getPersonajesParaJugador()))
            jugadoresE3.add(Jugador(nombreJugador2E3, getPersonajesParaJugador()))
        }

        // ASGINACIONES: cuando hay 8 jugadores o entre 10 y 12 (incluidos) vamos a tener un cuarto
        // equipo con al menos dos jugadores
        if (numJugadores == 8 || numJugadores in 10..12) {
            nombreEquipo4 = findViewById<EditText>(R.id.etNombreEquipo4).text.toString()
            if (nombreEquipo4.isEmpty())
                nombreEquipo4 = getString(R.string.EQ4)

            nombreJugador1E4 = findViewById<AutoCompleteTextView>(R.id.acE4J1).text.toString()
            if (nombreJugador1E4.isEmpty())
                nombreJugador1E4 = getString(R.string.J1)
            nombreJugador2E4 = findViewById<AutoCompleteTextView>(R.id.acE4J2).text.toString()
            if (nombreJugador2E4.isEmpty())
                nombreJugador2E4 = getString(R.string.J2)

            jugadoresE4.add(Jugador(nombreJugador1E4, getPersonajesParaJugador()))
            jugadoresE4.add(Jugador(nombreJugador2E4, getPersonajesParaJugador()))
        }

        // ASIGNACIONES: si hay entre 9 y 12 jugadores, el equipo 2 va a tener un tercer jugador
        if (numJugadores in 9..12) {
            nombreJugador3E2 = findViewById<AutoCompleteTextView>(R.id.acE2J3).text.toString()
            if (nombreJugador3E2.isEmpty())
                nombreJugador3E2 = getString(R.string.J3)

            jugadoresE2.add(Jugador(nombreJugador3E2, getPersonajesParaJugador()))
        }

        // ASIGNACIONES: para los casos de partidas con 9, 11 o 12 jugadores, el tercer equipo
        // también va a tener un tercer jugador
        if (numJugadores== 9 || numJugadores == 11 || numJugadores == 12) {
            nombreJugador3E3 = findViewById<AutoCompleteTextView>(R.id.acE3J3).text.toString()
            if (nombreJugador3E3.isEmpty())
                nombreJugador3E3 = getString(R.string.J3)

            jugadoresE3.add(Jugador(nombreJugador3E3, getPersonajesParaJugador()))
        }

        // ASIGNACIONES: si están los 12 jugador, el cuarto equipo va a tener un tercer jugador
        if (numJugadores == 12) {
            nombreJugador3E4 = findViewById<AutoCompleteTextView>(R.id.acE4J3).text.toString()
            if (nombreJugador3E4.isEmpty())
                nombreJugador3E4 = getString(R.string.J3)

            jugadoresE4.add(Jugador(nombreJugador3E4, getPersonajesParaJugador()))
        }

        // mezclamos el orden de los jugadores de cada equipo y el de los equipos
        jugadoresE1.shuffle()
        jugadoresE2.shuffle()
        equipos.add(Equipo(1, nombreEquipo1, jugadoresE1))
        equipos.add(Equipo(2, nombreEquipo2, jugadoresE2))
        if (jugadoresE3.size > 0) {
            jugadoresE3.shuffle()
            equipos.add(Equipo(3, nombreEquipo3, jugadoresE3))
        }
        if (jugadoresE4.size > 0) {
            jugadoresE4.shuffle()
            equipos.add(Equipo(4, nombreEquipo4, jugadoresE4))
        }
        equipos.shuffle()

        val partido = Equipos(equipos)
        guardarNombresJugadores(partido.getNombreJugadores())
        return partido
    }

    private fun crearEquiposBatalla() : Equipos {
        val jugadores = arrayListOf<Jugador>()
        val nombresJugador = arrayListOf<String>()
        // no lo puedo coger con el binding, porque se refiera a activity_nombre
        val divBatalla = findViewById<LinearLayout>(R.id.divModoBatalla)
        var contador = 1
        for (autocomplete in divBatalla.children) {
            if (autocomplete.visibility == View.VISIBLE){
                val cajaNombre = autocomplete as AutoCompleteTextView
                var nombre = cajaNombre.text.toString()
                if (cajaNombre.text.isEmpty())
                    nombre = getString(R.string.acNumJugadores, contador)
                nombresJugador.add(nombre)
                jugadores.add(Jugador(nombre, getPersonajesParaJugador()))
                contador++
            } else
                break
        }
        jugadores.shuffle()
        guardarNombresJugadores(nombresJugador)
        val equipo = Equipo(1,"BATALLA",jugadores)
        return Equipos(arrayListOf(equipo))
    }


    // obtenemos el listado de personajes que le corresponde al juador
    private fun getPersonajesParaJugador(): List<String>{
        val numPersonajes  = getNumCartasParaJugador()
        val listaAux = arrayListOf<String>()
        for (i in 0 until numPersonajes){
            listaAux.add(personajesPartida[0])
            /* YO LO TENIA COMO EL COMENTARIO,  pero me recomienda esto
            //personajesPartida -= personajesPartida[0]
            // esto lo que me recomendaba, pero creo que el removeAt es mejor
            //personajesPartida = personajesPartida - personajesPartida[0]
            */
            personajesPartida.removeAt(0)
        }
        return listaAux
    }

    // calcula el número exacto de cartas que hay que dar a cada jugador
    private fun getNumCartasParaJugador(): Int {
        // dividimos el número de personajes (se va actualizando en el método padre para saber las
        // que ya se han repartido) entre el número de jugadores (se actualiza a continuación)
        var resultado = personajesPartida.size / auxNumJugadores

        // si el resultado no es exacto tenemos que añadir una carta más (este método esta sacado
        // del analisis del juego en el que sabemos que al dividir entre el último es exacto)
        if (personajesPartida.size % auxNumJugadores != 0)
            resultado += 1

        // vamos actualizando el número de jugadores (por eso está como aux) para saber en cualquier
        // momento las cartas que hay que repartir sabiendo el número de jugadores actualizado
        auxNumJugadores--
        return resultado
    }

    // lo que queremos hacer es guardar en un archivo de la aplicación el nombre de todos
    // los jugadores por si vuelven a jugar aparezcan en el listado de autocompletado
    private fun guardarNombresJugadores(nombres: ArrayList<String>) {
        val nombresNuevos = arrayListOf<String>()
        for (nombre in nombres)
            if (!isNombrePredeterminado(nombre))
                if (!nombresGuardados.contains(nombre.lowercase(Locale.getDefault())))
                    nombresNuevos.add(nombre)
        conexionTXT.guardarNombreJugadores(nombresNuevos)
    }

    // comprobamos que el nombre no sea uno de los que nosotros damos por defecto
    private fun isNombrePredeterminado(nombre: String) : Boolean{
        if (nombre==getString(R.string.J1) || nombre == getString(R.string.J2) ||
            nombre == getString(R.string.J3) )
            return true
        return false
    }

    // DIRECTAMENTE PASAMOS A LA PANTALLA DE JUEGO
    private fun irAJugar(equipos: Equipos) {
        // como no hay que descartar personajes, los juntamos todos y los mezclamos
        personajesPartida.addAll(equipos.getPersonajes())
        personajesPartida.shuffle()
        Log.i("IR A JUGAR",  personajesPartida.size.toString())
        val intent = Intent(this, APartido::class.java)
        intent.putExtra(Constantes.EQUIPOS, equipos)
        intent.putExtra(Constantes.TIPO, swEquipos)
        intent.putStringArrayListExtra(Constantes.PERSONAJES, personajesPartida)
        startActivity(intent)
        finish()
    }

    private fun irASeleccionarPersonajes(equipos: Equipos) {
        val intent = Intent(this, ADescartarPersonajes::class.java)
        intent.putExtra(Constantes.TIPO, swEquipos)
        intent.putExtra(Constantes.EQUIPOS, equipos)
        startActivity(intent)
        finish()
    }

}