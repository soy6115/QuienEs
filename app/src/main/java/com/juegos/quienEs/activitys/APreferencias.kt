package com.juegos.quienEs.activitys

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.juegos.quienEs.R
import com.juegos.quienEs.databinding.ActivityPreferenciasBinding
import com.juegos.quienEs.util.ConexionTXT
import com.juegos.quienEs.util.Conexiones
import com.juegos.quienEs.util.Constantes

private lateinit var binding: ActivityPreferenciasBinding

private lateinit var spDificultad : Spinner
private lateinit var cajaNumJugadores: TextView
private lateinit var cajaNumCartas: TextView
private lateinit var radioColor : RadioGroup
private lateinit var conexiones : Conexiones


class APreferencias : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreferenciasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inicializarVariables()
        imprimirDatos()
        activarBotones()

    }

    private fun inicializarVariables() {
        val preferences = getSharedPreferences(Constantes.SP_PREFERENCES, MODE_PRIVATE)
        conexiones = Conexiones (preferences)

        cajaNumJugadores = binding.etNumJugadores
        cajaNumCartas = binding.etNumeroCartas
        radioColor = binding.rgColores

        spDificultad = binding.spDificultad
        val adaptador = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
            getArrayNivel())
        spDificultad.adapter = adaptador
    }

    private fun activarBotones() {
        val botonGuardar = binding.btnGuardar
        botonGuardar.setOnClickListener {
            grabarDatos()
        }
    }

    private fun getArrayNivel() : ArrayList<String>{
        val lista = arrayListOf<String>()
        lista.add(getString(R.string.NIVEL_FACIL))
        lista.add(getString(R.string.NIVEL_NORMAL))
        lista.add(getString(R.string.NIVEL_DIFICIL))
        return lista
    }

    private fun imprimirDatos() {
        cajaNumJugadores.text = conexiones.getNumeroJugadores().toString()


        if (conexiones.getBaraja() == Constantes.BARAJA_AMARILLA)
            radioColor.check(R.id.rbColorAmarillo)
        else if (conexiones.getBaraja() == Constantes.BARAJA_AZUL)
            radioColor.check(R.id.rbColorAzul)

        cajaNumCartas.text = conexiones.getNumeroCartasTotal().toString()

        when (conexiones.getNivelDificultad()) {
            Constantes.NIVEL_FACIL -> spDificultad.setSelection(0)
            Constantes.NIVEL_NORMAL -> spDificultad.setSelection(1)
            Constantes.NIVEL_DIFICIL -> spDificultad.setSelection(2)
            else -> spDificultad.setSelection(1)
        }
    }

    private fun grabarDatos() {
        val numJugadores = cajaNumJugadores.text.toString().toInt()
        if (numJugadores !in 3..12) {
            Toast.makeText(this, "Tiene que haber entre 3 y 12 jugadores",
                Toast.LENGTH_SHORT).show()
        } else {
            // decimos que es por defecto amarilla, pero si es azul, lo actualizamos
            var personajes = resources.getStringArray(R.array.personajesAmarillo)
            var colorBaraja = Constantes.BARAJA_AMARILLA
            if (binding.rbColorAzul.isChecked) {
                colorBaraja = Constantes.BARAJA_AZUL
                personajes = resources.getStringArray(R.array.personajesAzul)
            }
            //else if (binding.rbColorAmarillo.isChecked) {


            val numMaximoPersonajes = personajes.size
            val numCartas = cajaNumCartas.text.toString().toInt()
            if (numCartas > numMaximoPersonajes)
                Toast.makeText(this, "La baraja tiene $numMaximoPersonajes personajes, " +
                        "no puedes seleccionar tantas cartas", Toast.LENGTH_SHORT).show()
            else {
                val dificultad = spDificultad.selectedItemPosition
                conexiones.guardarDatos(numJugadores, colorBaraja, dificultad, numCartas)

                if (binding.cbBorrarJugadores.isChecked)
                    borrarListadoJugadores()

                volverAPrincipal()
            }
        }
    }


    private fun borrarListadoJugadores() {
        val conexionTXT = ConexionTXT(baseContext)
        conexionTXT.borrarJugadoresGuardados()
    }

    private fun volverAPrincipal() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}