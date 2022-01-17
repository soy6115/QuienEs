package com.juegos.quienEs.activitys

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.juegos.quienEs.DescartarPersonajesAdapter
import com.juegos.quienEs.R
import com.juegos.quienEs.databinding.ActivityDescartarPersonajesBinding
import com.juegos.quienEs.entidades.Equipos
import com.juegos.quienEs.entidades.Jugador
import com.juegos.quienEs.util.Conexiones
import com.juegos.quienEs.util.Constantes

class ADescartarPersonajes : AppCompatActivity() {

    private lateinit var binding: ActivityDescartarPersonajesBinding
    // tiene de forma provisional los personajes que cada jugador esta descartando
    private lateinit var personajesABorrar: ArrayList<String>
    // será el listado definitivo de personajes con los que vamos a jugar
    private lateinit var personajesDefinitivos: ArrayList<String>
    private lateinit var jugadores : ArrayList<Jugador>
    private lateinit var equipos : Equipos
    private var descarteSegunDificultad = 0
    private var swEquipos = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescartarPersonajesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inicializarVariables()
        activarBotones()
        cargarRecicler()
    }

    private fun inicializarVariables() {
        val bundle = intent.extras
        swEquipos = bundle!!.getBoolean(Constantes.TIPO)
        equipos = bundle.getSerializable(Constantes.EQUIPOS) as Equipos
        personajesDefinitivos = equipos.getPersonajes()
        jugadores = equipos.getJugadores()

        val conexion = Conexiones(getSharedPreferences(Constantes.SP_PREFERENCES, MODE_PRIVATE))
        descarteSegunDificultad = conexion.getNumeroCartasADescartar()
        personajesABorrar = arrayListOf()

        // dejar en pantalla cuantos personajes deben borrar
        val cajaNumeroDescartes = binding.tvNumeroDescartes
        cajaNumeroDescartes.text = getString(R.string.tvNumeroDescartes, descarteSegunDificultad)
    }

    private fun activarBotones() {
        binding.tvNombreJugador.setOnClickListener {
            actualizarPantalla(true)
        }
        binding.btMostrar.setOnClickListener{
            actualizarPantalla(true)
        }

        binding.btDescartarPersonaje.setOnClickListener {
            descartar()
        }
    }

    // cargamos los personajes de los que tendrá que descartarse el jugador
    private fun cargarRecicler() {
        // vamos cogiendo siempre al primer jugador que no haya descartado
        val jugador = jugadores[0]
        val nombreJugador: TextView = binding.tvNombreJugador
        nombreJugador.text = jugador.nombre

        // se crea el recicler view y se asigna, pasamos jugador.personajes que es lo que da el
        // elemento de iteración
        binding.rvPersonajes.layoutManager = LinearLayoutManager(this)
        val adapter = DescartarPersonajesAdapter(this, jugador.personajes)
        binding.rvPersonajes.adapter = adapter

        // como este jugador ya ha descartado, lo eliminamos de la pila de eliminación
        // limpiamos el array para que lo pueda usar el siguiente
        jugadores.removeAt(0)
        personajesABorrar.clear()
    }

    // el método para eliminar los personajes con los que ese jugador no quiere jugar
    private fun descartar() {
        // comprobamos que lo que queremos eliminar sea el número correcto
        if (personajesABorrar.size != descarteSegunDificultad) {
            Toast.makeText(this,
                "Debes seleccionar $descarteSegunDificultad personajes para descartar",
                Toast.LENGTH_SHORT).show()
        } else {
            eliminarParaDefinitivos()
            if (jugadores.size==0)
                irAJugar(equipos)
            else {
                // cuando aún quedan jugadores seguimos cargando los personajes para descartar
                cargarRecicler()
                actualizarPantalla(false)
            }
        }
    }

    // de todos los personajes que quedan, quitamos estos que ya sabemos seguro que son descartes
    // personajesABorrar tiene el lsitado de descartes del jugador por lo que lo eliminamos del
    // listado definitivo
    private fun eliminarParaDefinitivos() {
        for (personajeABorrar in personajesABorrar)
            personajesDefinitivos.remove(personajeABorrar)
    }

    // SE LLAMA DESDE EL ADAPTER
    // si el personaje ya existía en el array (remove devuelve true), lo quitamos del array (ya es
    // el remove el que lo hace). Si el personaje no se puede borrar, porque no existe aun en el
    // array, lo añadimos
    fun controlPersonajesABorrar(personaje : String) {
        //Toast.makeText(this, "estas intentando quitar a $personaje", Toast.LENGTH_SHORT).show()
        if (!personajesABorrar.remove(personaje))
            personajesABorrar.add(personaje)

        actualizarBoton(personajesABorrar.size)
    }

    // control para que se muestre los botones correctos según el momento de la pantalla (swListado)
    private fun actualizarPantalla(swListado: Boolean) {
        if (swListado) {
            binding.vPaddingTop.visibility = View.GONE
            binding.llRecicle.visibility = View.VISIBLE
            binding.btMostrar.visibility = View.GONE
            binding.tvNumeroDescartes.visibility = View.VISIBLE
        } else {
            binding.vPaddingTop.visibility = View.VISIBLE
            binding.llRecicle.visibility = View.INVISIBLE
            binding.btMostrar.visibility = View.VISIBLE
            binding.tvNumeroDescartes.visibility = View.INVISIBLE
            binding.btDescartarPersonaje.background = resources.getDrawable(R.drawable.btnpersonajesinicial, null)
        }
    }

    private fun actualizarBoton(numDescartes : Int) {
        if (numDescartes == descarteSegunDificultad)
            binding.btDescartarPersonaje.background = resources.getDrawable(R.drawable.btnpersonajesok, null)
        else
            binding.btDescartarPersonaje.background = resources.getDrawable(R.drawable.btnpersonajesinicial, null)

    }

    private fun irAJugar(equipos: Equipos) {
        personajesDefinitivos.shuffle()
        val intent : Intent
        intent = Intent(this, APartido::class.java)
        intent.putExtra(Constantes.EQUIPOS, equipos)
        intent.putExtra(Constantes.TIPO, swEquipos)
        intent.putStringArrayListExtra(Constantes.PERSONAJES, personajesDefinitivos)
        startActivity(intent)
        finish()
    }

}

