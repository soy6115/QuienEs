package com.juegos.quienEs.activitys

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.juegos.quienEs.R
import com.juegos.quienEs.databinding.ActivityApartidoBinding
import com.juegos.quienEs.entidades.Equipo
import com.juegos.quienEs.entidades.Equipos
import com.juegos.quienEs.rondas.PrimeraRonda
import com.juegos.quienEs.rondas.Ronda
import com.juegos.quienEs.rondas.SegundaRonda
import com.juegos.quienEs.rondas.TerceraRonda
import com.juegos.quienEs.util.Conexiones
import com.juegos.quienEs.util.Constantes
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


// Todos los sonidos de Zapsplat.com

class APartido : AppCompatActivity() {

    private lateinit var binding: ActivityApartidoBinding
    private val scope = MainScope()

    private lateinit var personajes : ArrayList<String>
    private var auxPersonajes : ArrayList<String>? = null
    private var personajesPasados : ArrayList<String> = arrayListOf()
    private var swEquipos = false
    private lateinit var equipoBatalla : Equipo

    private lateinit var equipos: Equipos
    private lateinit var rondaActual : Ronda
    private var swParar = false
    private var swFinPartida = false

    private lateinit var cajaRonda : TextView

    private lateinit var alertResultados: AlertDialog

    private lateinit var divDatosJugador: ConstraintLayout
    private lateinit var cajaEquipo : TextView
    private lateinit var cajaJugador : TextView

    private lateinit var divDatosResolucion: ConstraintLayout
    private lateinit var cajaPersonaje : TextView
    private lateinit var cajaCuentaAtras : TextView
    private lateinit var barraProgreso : ProgressBar
    private lateinit var cajaNumCartas : TextView
    private var numCartas = 0
    private lateinit var btEmpezarResovler: ImageButton

    private lateinit var divBotonera: ConstraintLayout
    private lateinit var btPasar: ImageButton
    private lateinit var btError: ImageButton
    private lateinit var divBotoneraBatalla: ConstraintLayout
    private lateinit var btPasarBatalla: ImageButton
    private lateinit var btErrorBatalla: ImageButton

    private lateinit var divCentrar: View
    private lateinit var divNombreYLupa: LinearLayout

    private lateinit var btSonido : ImageButton
    private lateinit var btDudas: ImageButton

    private lateinit var mpReloj: MediaPlayer
    private lateinit var mpAcierto: MediaPlayer
    private lateinit var mpError: MediaPlayer
    private lateinit var mpFinTiempo: MediaPlayer

    private var swSonido = true

    override fun onDestroy() {
        super.onDestroy()
        // esto la hacemos para parar la rutina
        scope.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApartidoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inicializarVariables()
        actualizarDatosRonda()
        actualizarPantalla()
        activarBotones()
    }

    private fun inicializarVariables(){
        val bundle = intent.extras
        equipos = bundle!!.getSerializable(Constantes.EQUIPOS) as Equipos
        swEquipos = bundle.getBoolean(Constantes.TIPO)
        auxPersonajes = bundle.getStringArrayList(Constantes.PERSONAJES)
        personajes = auxPersonajes!!.clone() as ArrayList<String>

        mpAcierto = MediaPlayer.create(this, R.raw.acierto)
        mpReloj = MediaPlayer.create(this, R.raw.reloj)
        mpError = MediaPlayer.create(this, R.raw.error)
        mpFinTiempo = MediaPlayer.create(this, R.raw.fintiempo)

        rondaActual = PrimeraRonda()

        cajaRonda = binding.tvNumeroRonda

        divDatosJugador = binding.divDatosJugador
        cajaEquipo = binding.tvEquipoResuelve
        cajaJugador = binding.tvJugadorResuelve
        btEmpezarResovler = binding.btEmpezarResolver

        divDatosResolucion = binding.divDatosResolucion
        barraProgreso = binding.pbSegundero
        cajaCuentaAtras = binding.tvCuentaAtras
        cajaPersonaje = binding.tvNombrePersonaje
        cajaNumCartas = binding.tvNumCartas
        val conexion = Conexiones(getSharedPreferences(Constantes.SP_PREFERENCES, MODE_PRIVATE))
        numCartas = conexion.getNumeroCartasTotal()

        divBotonera = binding.divBotonera
        btPasar = binding.btPasar
        btError = binding.btError

        divBotoneraBatalla = binding.divBotoneraBatalla
        btPasarBatalla = binding.btPasarBatalla
        btErrorBatalla = binding.btErrorBatalla

        divCentrar = binding.divCentrarVertical
        divNombreYLupa = binding.divNombreYLupa

        btSonido = binding.btSonido
        btDudas = binding.btDudasRonda

        if (!swEquipos){
            equipoBatalla = equipos.getEquipoActual()
            controlModoBatalla()
        }



    }

    private fun controlModoBatalla() {
        cajaEquipo.visibility = View.GONE
        divBotonera.visibility = View.GONE
        val numJugadores = equipoBatalla.jugadores.size
        if (numJugadores>= 4 )
            binding.divTerceraFila.visibility = View.VISIBLE
        // si no hay tantos jugadores, podemos poner el botón más grande
        if (numJugadores <= 4) {
            btPasarBatalla.background = getDrawable(R.drawable.btnpasar)
            btErrorBatalla.background = getDrawable(R.drawable.btnerror)
        }

        val divsColumnas = binding.divLiners.children
        divsColumnas.forEach { divColumna ->
            divColumna as LinearLayout
            val cajas = divColumna.children
            cajas.forEach { caja ->
                caja as TextView
                caja.setOnClickListener{
                    acertar(caja.text.toString())
                }
            }
        }
    }

    private fun actualizarNombresBatalla() {
        val jugadoresBatalla = equipos.getEquipoActual().jugadores
        val numJugadores = jugadoresBatalla.size

        binding.tvResuelveJ2. text = jugadoresBatalla[1].nombre
        binding.tvResuelveJ3. text = jugadoresBatalla[2].nombre
        if (numJugadores >= 4) {
            binding.tvResuelveJ4. text = jugadoresBatalla[3].nombre
            binding.tvResuelveJ4.visibility = View.VISIBLE
        }
        if (numJugadores >= 5){
            binding.tvResuelveJ5. text = jugadoresBatalla[4].nombre
            binding.tvResuelveJ5.visibility = View.VISIBLE
        }
        if (numJugadores >= 6) {
            binding.tvResuelveJ6. text = jugadoresBatalla[5].nombre
            binding.tvResuelveJ6.visibility = View.VISIBLE
        }
        if (numJugadores >= 7){
            binding.tvResuelveJ7. text = jugadoresBatalla[6].nombre
            binding.tvResuelveJ7.visibility = View.VISIBLE
        }
        if (numJugadores >= 8){
            binding.tvResuelveJ8. text = jugadoresBatalla[7].nombre
            binding.tvResuelveJ8.visibility = View.VISIBLE
        }
        if (numJugadores >= 9){
            binding.tvResuelveJ9. text = jugadoresBatalla[8].nombre
            binding.tvResuelveJ9.visibility = View.VISIBLE
        }
        if (numJugadores >= 10){
            binding.tvResuelveJ10. text = jugadoresBatalla[9].nombre
            binding.tvResuelveJ10.visibility = View.VISIBLE
        }
        if (numJugadores >= 11){
            binding.tvResuelveJ11. text = jugadoresBatalla[10].nombre
            binding.tvResuelveJ11.visibility = View.VISIBLE
        }
        if (numJugadores == 12){
            binding.tvResuelveJ12. text = jugadoresBatalla[11].nombre
            binding.tvResuelveJ12.visibility = View.VISIBLE
        }


    }

    private fun activarBotones() {
        activarBotonNombre(true)
        btEmpezarResovler.setOnClickListener{
            iniciarTurno()
        }

        binding.btAcierto.setOnClickListener{
            acertar("")
        }

        btError.setOnClickListener{
            sonido(mpError)
            swParar = true
        }

        btErrorBatalla.setOnClickListener {
            sonido(mpError)
            swParar = true
        }

        btPasar.setOnClickListener {
            pasar()
        }

        btPasarBatalla.setOnClickListener {
            pasar()
        }

        btSonido.setOnClickListener {
            controlSonido()
        }

        btDudas.setOnClickListener {
            mostrarDescripcionRonda()

        }

        // AQUI IBA EL BOTON DE SIGUIENTE RONDA, PERO VA DENTRO DEL PROPIO ALERTDIALOG

    }

    private fun activarBotonNombre(swActivar : Boolean) {
        if (swActivar) {
            cajaJugador.setOnClickListener {
                iniciarTurno()
            }
        } else
            cajaJugador.setOnClickListener {
            }
    }

    private fun iniciarTurno() {
        activarBarraProgreso()
        mostrarCajas(true)
        activarBotonNombre(false)
        swParar = false
    }

    private fun acertar(nombreAcierto: String) {
        sonido(mpAcierto)
        if (swEquipos)
            equipos.getEquipoActual().acierto()
        else
            sumarPuntosBatalla(nombreAcierto)
        personajes.removeAt(0)
        comprobarPostAcierto()
    }

    private fun sumarPuntosBatalla(nombreAcierto: String) {
        when {
            rondaActual.isPrimeraRonda() -> {
                equipos.getJugadorActual().aciertoPrimeraRonda()
                equipoBatalla.getJugador(nombreAcierto).aciertoPrimeraRonda()
            }
            rondaActual.isUltimaRonda() -> {
                equipos.getJugadorActual().aciertoTerceraRonda()
                equipoBatalla.getJugador(nombreAcierto).aciertoTerceraRonda()
            }
            else -> {
                equipos.getJugadorActual().aciertoSegundaRonda()
                equipoBatalla.getJugador(nombreAcierto).aciertoSegundaRonda()
            }
        }
    }

    private fun comprobarPostAcierto() {
        // ya no quedan mas personajes por adivinar
        if (isFinRonda()){
            // cuando aun quedan personajes porque se han pasado, pasamos de turno
            if (isPersonajesPasadosPendientes()) {
                finalizarTurno()
            }
            // en el caso de que se haya acabado la ronda
            else {
                swParar = true
                // si la ronda no es la ultima, volvemos a obtener los personajes y los mezclamos
                if (!rondaActual.isUltimaRonda()) {
                    personajes = auxPersonajes!!.clone() as ArrayList<String>
                    personajes.shuffle()
                } else {
                    // esto es cuando se ha terminado la partida porque si era la ultima ronda
                    swFinPartida = true
                }
                activarResultados()
            }
        }
        else {
            // se ha acertado pero aun quedan personajes por adivinar, sigue jugando el mismo equipo
            cajaPersonaje.text = getNombrePersonajeActual()
            cajaNumCartas.text = getNumeroCartas()
        }

    }

    private fun pasar() {
        // si aun quedan personajes, dejamos el que teníamos el último y enseñamos el siguiente
        if (personajes.size > 1) {
            sonido(mpError)
            val personajeActual = personajes.first()
            personajesPasados.add(personajeActual)
            personajes.removeAt(0)
            cajaPersonaje.text = getNombrePersonajeActual()
        } else {
            // aqui entra cuando ha pasado pero los persoanjes que le quedan ya los ha visto
            sonido(mpFinTiempo)
            swParar = true
        }
        cajaNumCartas.text = getNumeroCartas()
    }

    private fun activarBarraProgreso() {
        cajaCuentaAtras.text = getString(R.string.tiempo)
        barraProgreso.progress = 0
        scope.launch {
            cuentaAtras()
        }
    }

    private suspend fun cuentaAtras(){
        while(barraProgreso.progress < barraProgreso.max && !swParar) {
            delay(1000)
            barraProgreso.incrementProgressBy(1)
            actualizarCuentaAtras()
            controlDeTiempoParaSonido()
        }
        if (!swFinPartida)
            finalizarTurno()
    }

    // se llama a finalizar turno cuando se ha acabado el tiempo o ya no quedan persoanjes que
    // mostrar. segun la ronda se hace una cosa diferente con las cartas que quedan pendientes y
    // cambiamos de equipo para que le toque al siguiente
    private fun finalizarTurno() {
        // comprueba el numero de personajes pendientes, si hay alguno lo que hace es recorrerlos
        // para volver a dejarlo en el array de personajes
        if (isPersonajesPasadosPendientes()){
            for (personajePasado in personajesPasados)
                if (!personajes.contains(personajePasado))
                    personajes.add(personajePasado)
        }
        // limpio los personajes pasados para que ne la siguiente ronda empiece vacío
        personajesPasados.clear()
        rondaActual.finTurno(personajes)
        cambiarDeEquipo()
    }

    private fun cambiarDeEquipo() {
        equipos.getEquipoActual().siguienteJugador()
        if (swEquipos)
            equipos.siguienteEquipo()
        mostrarCajas(false)
        activarBotonNombre(true)
        actualizarPantalla()
    }

    private fun isPersonajesPasadosPendientes(): Boolean{
        if (personajesPasados.size > 0)
            return true
        return false
    }

    // cuando adivinamos un personaje lo eliminamos del arraya, asi que determinamos cuando es el
    // final de la ronda
    private fun isFinRonda():Boolean{
        if (personajes.size > 0)
            return false
        return true
    }

    // visual, que enseñar segun el punto de la partida en el que nos encontremos
    private fun mostrarCajas(swMostrar: Boolean) {
        if (swMostrar) {
            divDatosResolucion.visibility = View.VISIBLE
            if (swEquipos)
                divBotonera.visibility = View.VISIBLE
            else
                divBotoneraBatalla.visibility = View.VISIBLE

        } else {
            divDatosResolucion.visibility = View.INVISIBLE
            if (swEquipos)
                divBotonera.visibility = View.INVISIBLE
            else
                divBotoneraBatalla.visibility = View.INVISIBLE
        }
        divDatosJugador.visibility = View.VISIBLE
        estilosCabecera(!swMostrar)
    }

    // metodo para controlar el aspecto visual de la cabecera en la resolucón
    private fun estilosCabecera(swPrincipal: Boolean) {
        if (swPrincipal) {
            btDudas.visibility = View.VISIBLE
            cajaEquipo.textAlignment = View.TEXT_ALIGNMENT_CENTER
            divNombreYLupa.setHorizontalGravity(Gravity.CENTER_HORIZONTAL)
            divCentrar.visibility = View.VISIBLE
            btEmpezarResovler.visibility = View.VISIBLE
            cajaRonda.textSize = resources.getDimension(R.dimen.textoPequeno)
        } else {
            btDudas.visibility = View.GONE
            cajaEquipo.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
            divNombreYLupa.setHorizontalGravity(Gravity.START)
            divCentrar.visibility = View.GONE
            btEmpezarResovler.visibility = View.INVISIBLE
            cajaRonda.textSize = resources.getDimension(R.dimen.textoMuyPequeno)
        }
    }

    private fun actualizarDatosRonda() {
        cajaRonda.text = getString(rondaActual.titulo)
        //cajaExplicacion.text = getString(rondaActual.descripcion)
    }

    private fun actualizarPantalla() {
        cajaEquipo.text = getNombreEquipoActual()
        cajaJugador.text = getNombreJugadorActual()
        cajaPersonaje.text = getNombrePersonajeActual()
        cajaNumCartas.text = getNumeroCartas()
        if (!swEquipos)
            actualizarNombresBatalla()
    }

    private fun actualizarCuentaAtras() {
        var tiempo = cajaCuentaAtras.text.toString().toInt()
        tiempo -= 1
        cajaCuentaAtras.text = tiempo.toString()
    }

    private fun activarResultados() {
        var numRonda = when {
            rondaActual.isPrimeraRonda() -> 1
            rondaActual.isUltimaRonda() -> 3
            else -> 2
        }
        if (swEquipos)
            equipos.actualizarMarcador(numRonda)

        pantallaParaResultado()
        alertResultados =  mostrarCajaResultado(numRonda).show()
    }

    private fun mostrarCajaResultado(numRonda: Int): AlertDialog.Builder {
        val builder = AlertDialog.Builder(this, R.style.CajaResultados)
        val inflater = this.layoutInflater
        val pantalla = if (swEquipos)
            inflater.inflate(R.layout.alertrequipos, null)
        else
            inflater.inflate(R.layout.alertbatalla, null)
        builder.setView(pantalla)

        if (swEquipos) {
            val numEquipos = equipos.equipos.size
            if (numEquipos == 2) {
                val filaE3 = pantalla.findViewById<TableRow>(R.id.trEq3)
                filaE3.visibility = View.GONE
            }
            if (numEquipos in 2..3) {
                val filaE4 = pantalla.findViewById<TableRow>(R.id.trEq4)
                filaE4.visibility = View.GONE
            }
            val equiposOrdenados = equipos.getEquiposOrdenados()

            val tabla = pantalla.findViewById<TableLayout>(R.id.tlTabla)
            val filasTabla = tabla.children
            for ((i, equipo) in equiposOrdenados.withIndex()) {
                val filaTabla = filasTabla.elementAt(i+1) as TableRow
                val cajaNombre = filaTabla.findViewWithTag<TextView>("nombreEquipo")
                cajaNombre.text = equipo.nombre
                val cajaPT = filaTabla.findViewWithTag<TextView>("puntosTotal")
                cajaPT.text = equipo.puntosTotal.toString()
                val cajaTR1 = filaTabla.findViewWithTag<TextView>("puntosR1")
                cajaTR1.text = equipo.puntosR1.toString()
                if (numRonda >= 2) {
                    val cajaPR2 = filaTabla.findViewWithTag<TextView>("puntosR2")
                    cajaPR2.text = equipo.puntosR2.toString()
                }
                if (numRonda == 3){
                    val cajaPR3 = filaTabla.findViewWithTag<TextView>("puntosR3")
                    cajaPR3.text = equipo.puntosR3.toString()
                }
            }

        } else {

            val tabla = pantalla.findViewById<TableLayout>(R.id.tlTablaBatalla)
            val filasTabla = tabla.children
            val auxJugadores = equipoBatalla.ordenJugadores(0)

            /*val auxJugadores = when {
                rondaActual.isPrimeraRonda() -> equipoBatalla.ordenJugadores(1)
                rondaActual.isUltimaRonda() -> equipoBatalla.ordenJugadores(3)
                else -> equipoBatalla.ordenJugadores(2)
            }*/

            for ((i, jugador) in auxJugadores.withIndex()) {
                val filaTabla = filasTabla.elementAt(i+1) as TableRow
                filaTabla.visibility = View.VISIBLE

                val cajaNombre = filaTabla.findViewWithTag<TextView>("nombreJugador")
                cajaNombre.text = jugador.nombre
                val cajaPT = filaTabla.findViewWithTag<TextView>("puntosTotal")
                cajaPT.text = jugador.puntos.toString()
                val cajaTR1 = filaTabla.findViewWithTag<TextView>("puntosR1")
                cajaTR1.text = jugador.primeraRonda.toString()
                if (numRonda >= 2) {
                    val cajaPR2 = filaTabla.findViewWithTag<TextView>("puntosR2")
                    cajaPR2.text = jugador.segundaRonda.toString()
                }
                if (numRonda == 3){
                    val cajaPR3 = filaTabla.findViewWithTag<TextView>("puntosR3")
                    cajaPR3.text = jugador.terceraRonda.toString()
                }
            }
        }

        val btnSiguienteRonda = pantalla.findViewById<ImageButton>(R.id.btSiguienteRonda)
        if (numRonda == 3)
            btnSiguienteRonda.background = getDrawable(R.drawable.btnvolverajugar)

        btnSiguienteRonda.setOnClickListener {
            if (numRonda == 3)
                irAVolverAJugar()
            else
                siguienteRonda()
        }

        builder.setCancelable(false)
        builder.create()
        return builder
    }

    private fun siguienteRonda() {
        if (rondaActual.isPrimeraRonda()){
            // quitamos el boton de error y mostramos el de pasar
            if (swEquipos){
                btError.visibility = View.INVISIBLE
                btPasar.visibility = View.VISIBLE
            } else{
                btErrorBatalla.visibility = View.INVISIBLE
                btPasarBatalla.visibility = View.VISIBLE
            }
            rondaActual = SegundaRonda()
        } else
            rondaActual = TerceraRonda()

        // hay que hacer el show sobre el resultado para poder cerrarlo con este dismiss
        alertResultados.dismiss()
        swParar = true
        actualizarDatosRonda()
    }

    private fun pantallaParaResultado() {
        divDatosResolucion.visibility = View.INVISIBLE
        divDatosJugador.visibility = View.INVISIBLE
        divBotonera.visibility = View.INVISIBLE
        divBotoneraBatalla.visibility = View.INVISIBLE
    }

    private fun controlDeTiempoParaSonido() {
        val tiempo = barraProgreso.progress
        if (tiempo == 15 ||tiempo == 20 || tiempo == 25 ||  (tiempo in 27..29))
            sonido(mpReloj)
        else if (tiempo == 30)
            sonido(mpFinTiempo)
    }

    private fun getNombreEquipoActual(): String {
        return equipos.equipos[0].nombre
    }

    private fun getNombreJugadorActual(): String {
        return equipos.equipos[0].jugadores[0].nombre
    }

    private fun getNombrePersonajeActual(): String {
        return personajes[0]
    }

    private fun getNumeroCartas() : String {
        val numTotal = numCartas
        val actual = personajes.size
        return  getString(R.string.tvNumeroCartas, actual, numTotal)
    }

    private fun controlSonido() {
        swSonido = !swSonido
        if (swSonido)
            btSonido.background = getDrawable(R.drawable.btnsonidoon)
        else
            btSonido.background = getDrawable(R.drawable.btnsonidooff)
    }

    private fun sonido(mp : MediaPlayer){
        if (swSonido)
            mp.start()
    }

    private fun mostrarDescripcionRonda() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(rondaActual.titulo))
        builder.setMessage(getString(rondaActual.descripcion))
        builder.create().show()
    }

    private fun irAVolverAJugar() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}
