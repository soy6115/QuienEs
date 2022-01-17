package com.juegos.quienEs

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.juegos.quienEs.activitys.ADescartarPersonajes
import com.juegos.quienEs.databinding.ItemPersonajeSeleccionableBinding
import com.juegos.quienEs.entidades.Jugador

// la clase de adapter la necesitampos para poder rellenar el recyclerView
class DescartarPersonajesAdapter (private val activity: ADescartarPersonajes,
                                  private val personajes : List<String>):
        RecyclerView.Adapter<DescartarPersonajesAdapter.PersonajeHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonajeHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PersonajeHolder((layoutInflater.inflate(R.layout.item_personaje_seleccionable, parent, false)))
    }

    // SE EJECUTA SIEMPRE,  RECIBIMOS EL ITEM Y LE DAMOS LA FUNCIONALIDAD
    override fun onBindViewHolder(holder: PersonajeHolder, position: Int) {
        val item = holder
        item.render(personajes[position])

        val cajaNombre = item.cajaNombre
        val nombrePersonajes = cajaNombre.text.toString()

        cajaNombre.setOnClickListener {
            // este metodo es publico en ADescartarPersonajes, hace la funcionalidad en los arrays
            activity.controlPersonajesABorrar(nombrePersonajes)

            item.colorActivado = activity.baseContext.resources.getColor(R.color.naranja, null)
            item.colorActivadoSombra = activity.baseContext.resources.getColor(R.color.marronOscuro, null)
            item.colorDesactivado = activity.baseContext.resources.getColor(R.color.azulHielo, null)
            item.colorDesactivadoSombra = activity.baseContext.resources.getColor(R.color.azulVivo, null)

            item.cambiarEstado()

        }
    }

    override fun getItemCount(): Int = personajes.size

    // ES LA CLASE CORRESPONDIENTE AL ITEM.XML, LAS MODIFICACIONES PARA EL ITEM (DISEÑO O CONTENIDO
    // TIENEN QUE SER AQUI)
    // TIENE ESTE NOMBRE POR SER EL HABITUAL
    class PersonajeHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = ItemPersonajeSeleccionableBinding.bind(view)
        private var swEstado = true

        // creamos este metodo y la variable en la clase para poder usarlo dentro del
        // onBindViewHolder que lo recibe como parámetro
        val cajaNombre = binding.twNombrePersonaje


        // al no tener contexto, no podemos obtener el color de la forma habitual (R.color.naranja)
        var colorActivado = 0
        var colorActivadoSombra = 0
        var colorDesactivado = 0
        var colorDesactivadoSombra = 0

        // USO ESTE NOMBRE POR SER EL HABITUAL, para mi sería inicializar
        fun render (personaje: String) {
            binding.twNombrePersonaje.text = personaje
        }

        fun cambiarEstado() {
            swEstado = !swEstado

            if (swEstado){
                cajaNombre.setTextColor(colorActivado)
                cajaNombre.setShadowLayer(1.0f,7.0f, 7.0f, colorActivadoSombra)
            }
            /*
            parece que así también se puede, pero he preferido traerlo desde el colors.xml por si
            hay algun cambio
            cajaNombre.setTextColor(Color.parseColor("#50e26010"))
             */
            else{
                cajaNombre.setTextColor(colorDesactivado)
                cajaNombre.setShadowLayer(1.0f,7.0f, 7.0f, colorActivadoSombra)
            }

        }

    }


}