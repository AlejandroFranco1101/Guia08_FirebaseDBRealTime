package com.example.guia08_firebasedbrealtime

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.guia08_firebasedbrealtime.datos.Persona

class PersonaAdapter(private val context: Activity, var personas: List<Persona>) :
    ArrayAdapter<Persona>(context, R.layout.persona_layout, personas) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Método invocado tantas veces como elementos tenga la colección personas
        // para formar cada ítem que se visualizará en la lista personalizada.
        val layoutInflater = context.layoutInflater
        val rowView: View = convertView
            ?: layoutInflater.inflate(R.layout.persona_layout, parent, false)

        // Obtener las vistas del diseño.
        val tvNombre = rowView.findViewById<TextView>(R.id.tvNombre)
        val tvDUI = rowView.findViewById<TextView>(R.id.tvDUI)
        val tvFechaNacimiento = rowView.findViewById<TextView>(R.id.tvFechaNacimiento)
        val tvGenero = rowView.findViewById<TextView>(R.id.tvGenero)

        // Obtener el objeto Persona en la posición actual.
        val persona = personas[position]

        // Establecer los datos en las vistas.
        tvNombre.text = "Nombre: ${persona.nombre}"
        tvDUI.text = "DUI: ${persona.dui}"
        tvFechaNacimiento.text = "Fecha de nacimiento: ${persona.fechaNacimiento.orEmpty()}"
        tvGenero.text = "Género: ${persona.genero.orEmpty()}"

        return rowView
    }
}
