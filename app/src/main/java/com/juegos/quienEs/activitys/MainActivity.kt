package com.juegos.quienEs.activitys

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.juegos.quienEs.R
import com.juegos.quienEs.util.Conexiones
import com.juegos.quienEs.util.Constantes
import com.juegos.quienEs.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var btnEmpezarEquipos : ImageButton
    private lateinit var cajaNumJugadores : TextView
    private lateinit var conexion : Conexiones
    private var numJugadores = 0

    // esto es por si volvemos de los ajustes, actualice el número de jugadores introducido
    override fun onResume() {
        super.onResume()
        numJugadores = conexion.getNumeroJugadores()
        actualizarNumJugadores()
        actualizarNumJugadores()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        // pongo esto para que se quede el titulo de la app
        Thread.sleep(1500)
        setTheme(R.style.SplashTheme)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inicializarVariables()
        activarBotones()
    }

    // instanciamos en esta funcion todos los lateinit y llamamos al control de botones
    private fun inicializarVariables() {
        conexion = Conexiones(getSharedPreferences(Constantes.SP_PREFERENCES, MODE_PRIVATE))
        btnEmpezarEquipos = binding.btnEmpezarEquipos
        cajaNumJugadores = binding.tvNumJugadores

        // reccgemos el número de jugadores guardado y actualizamos
        numJugadores = conexion.getNumeroJugadores()
        actualizarNumJugadores()
    }

    // función para recoger todas las acciones al pulsar cualquier botón
    private fun activarBotones() {
        val btnRestarJugador = binding.btnQuitarJugador
        btnRestarJugador.setOnClickListener {
            cambiarNumJugadores(false)
        }

        val btnSumarJugadores = binding.btnAnadirJugador
        btnSumarJugadores.setOnClickListener {
            cambiarNumJugadores(true)
        }

        cajaNumJugadores.setOnClickListener {
            mostrarDialogNumJugadores()
        }

        btnEmpezarEquipos.setOnClickListener {
            irANombreJugadores(true)
        }

        val btnEmpezarBatalla = binding.btnEmpezarBatalla
        btnEmpezarBatalla.setOnClickListener {
            irANombreJugadores(false)
        }

        val btnDudasBatalla = binding.btnDudasBatalla
        btnDudasBatalla.setOnClickListener {
            mostrarDudasBatalla()
        }

        val btnIrAPreferencias = binding.btnPreferencias
        btnIrAPreferencias.setOnClickListener {
            irAPreferencias()
        }
    }

    // sumamos o restamos un número al número actual de jugadores
    private fun cambiarNumJugadores(swSuma : Boolean) {
        var aux = numJugadores
        aux = if (swSuma)
            aux + 1
        else
            aux - 1

        // control para que solo acepte jugadores entre 3 y 12 jugadores incluidos
        if (aux in 3..12){
            numJugadores = aux
            actualizarNumJugadores()
        } else
            Toast.makeText(this, "Entre 3 y 12 jugadores", Toast.LENGTH_SHORT).show()

    }

    // creamos un alert dialog con todas las opciones del número de jugadores. Tiene dentro su
    // propia acción en el {}
    private fun mostrarDialogNumJugadores(){
        val opciones = arrayOf("3","4","5","6","7","8","9","10","11","12")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Número de jugadores")
        builder.setItems(opciones) { _, i ->
            val suEleccion = when (i) {
                0 ->  3
                1 ->  4
                2 ->  5
                3 ->  6
                4 ->  7
                5 ->  8
                6 ->  9
                7 ->  10
                8 ->  11
                9 ->  12
                else -> 0
            }
            numJugadores = suEleccion
            actualizarNumJugadores()
        }
        builder.create().show()
    }

    // cuando hay 3 jugadores ocultamos el botón de equipos porque no se puede jugar
    private fun actualizarNumJugadores() {
        cajaNumJugadores.text = numJugadores.toString()
        if (numJugadores == 3)
            btnEmpezarEquipos.visibility = View.GONE
        else {
            if (btnEmpezarEquipos.visibility == View.GONE)
                btnEmpezarEquipos.visibility = View.VISIBLE
        }
    }


    private fun mostrarDudasBatalla() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.BATALLA_TITULO))
        builder.setMessage(getString(R.string.BATALLA_DESCRIPCIÓN))
        builder.create().show()
    }

    // vamos a la pantalla de rellenar nombres
    private fun irANombreJugadores(swEquipos: Boolean) {
        val intentNombres = Intent(this, ANombreJugadores::class.java)
        intentNombres.putExtra(Constantes.TIPO, swEquipos)
        intentNombres.putExtra(Constantes.NUM_JUGADORES, numJugadores)
        startActivity(intentNombres)
    }

    private fun irAPreferencias() {
        val intent = Intent(this, APreferencias::class.java)
        startActivity(intent)
    }

}