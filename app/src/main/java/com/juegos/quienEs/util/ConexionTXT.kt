package com.juegos.quienEs.util

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.ArrayList

// Clase creada para gestionar desde una única clase el guardado en archivo txt
class ConexionTXT(private val contextWrapper: Context) {

    // convertimos el contenido del archivo txt en el objeto jugadores
    fun obtenerJugadoresGuardados(): ArrayList<String> {
        val cadena = obtenerTextoArchivo()
        val array = arrayListOf<String>()
        if (cadena.isNotEmpty()) {
            // Separamos la cadena de texto, para dejar todos los jugadores separados
            val jugadores = cadena.split(';')
            for (jugador in jugadores)
                // Este control lo hago porque siempre deja un último elemento vacío con el split
                if (jugador.isNotEmpty())
                    array.add(jugador)
        }
        return array
    }

    // añadimos a los nombres que ya teníamos los nuevos (en minúsucula para poder comparar bien)
    fun guardarNombreJugadores(nombres: ArrayList<String>) {
        var cadena = obtenerTextoArchivo()
        for (nombre in nombres)
            cadena+= "${nombre.lowercase(Locale.getDefault())};"
        try {
            val archivo = OutputStreamWriter(contextWrapper.openFileOutput(Constantes.ARCHIVO,
                AppCompatActivity.MODE_PRIVATE))
            archivo.write(cadena)
            archivo.flush()
            archivo.close()
        } catch (e: IOException){
            Log.e("ARCHIVO", "Ha habido un problema al grabar en el archivo.")
        }
    }

    fun borrarJugadoresGuardados() {
        try {
            if (contextWrapper.fileList().contains(Constantes.ARCHIVO)){
                val archivo = OutputStreamWriter(contextWrapper.openFileOutput(Constantes.ARCHIVO,
                    AppCompatActivity.MODE_PRIVATE
                ))
                archivo.write("")
                archivo.flush()
                archivo.close()
            }
        } catch (e: IOException) {
            Log.e("ARCHIVO", "Errror al recuperar el archivo")
        }

    }

    // obtenemos el contenido del archivo txt y lo devolvemos como cadena string
    private fun obtenerTextoArchivo(): String {
        val cadena = StringBuilder()
        try {
            if (contextWrapper.fileList().contains(Constantes.ARCHIVO)){
                val archivo = InputStreamReader(contextWrapper.openFileInput((Constantes.ARCHIVO)))
                val br = BufferedReader(archivo)
                var linea = br.readLine()
                while(linea!=null){
                    cadena.append(linea)
                    linea = br.readLine()
                }
                br.close()
                archivo.close()
            }
        } catch (e: IOException) {
            Log.e("ARCHIVO", "Errror al recuperar el archivo.")
        }
        return cadena.toString()
    }

}