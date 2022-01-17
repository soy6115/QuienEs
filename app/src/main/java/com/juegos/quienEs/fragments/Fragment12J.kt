package com.juegos.quienEs.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.view.allViews
import com.juegos.quienEs.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Fragment12J.newInstance] factory method to
 * create an instance of this fragment.
 */
class Fragment12J(val nombres: List<String>) : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        // Antes devovolvía esto directamente, pero tenemos que rellenar los autocomplete antes para
        // que tenga el autocompletado con los nombres guardados del txt
        val vista = inflater.inflate(R.layout.fragment12j, container, false)
        val adaptador: ArrayAdapter<String> = ArrayAdapter(requireContext(),
            R.layout.support_simple_spinner_dropdown_item, nombres)
        // OJO A ESTO COMO ALTERNATIVA AL CHILDREN
        val vistas = vista.allViews
        for (caja in vistas)
            if (caja is AutoCompleteTextView)
                caja.setAdapter(adaptador)
        return vista
    }

/* LO TENGO QUE COMENTAR PORQUE NO SE COMO PASAR AL CONSTRUCTOR EL NOMBRE
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Fragment4J.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Fragment4J().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    } */
}