package com.example.guia08_firebasedbrealtime

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.guia08_firebasedbrealtime.datos.Persona
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddPersonaActivity : AppCompatActivity() {

    private var edtDUI: EditText? = null
    private var edtNombre: EditText? = null
    private var edtFechaNacimiento: EditText? = null
    private var key: String = ""
    private var accion: String = ""
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_persona)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        inicializar()
    }

    private fun inicializar() {
        edtNombre = findViewById(R.id.edtNombre)
        edtDUI = findViewById(R.id.edtDUI)
        edtFechaNacimiento = findViewById(R.id.edtFechaNacimiento)

        // Obtener los datos enviados desde la actividad principal.
        val datos: Bundle? = intent.extras
        datos?.let {
            key = it.getString("key", "")
            edtDUI?.setText(it.getString("dui", ""))
            edtNombre?.setText(it.getString("nombre", ""))
            edtFechaNacimiento?.setText(it.getString("fechaNacimiento", ""))
            accion = it.getString("accion", "")
        }
    }

    fun guardar(view: View?) {
        val nombre: String = edtNombre?.text.toString()
        val dui: String = edtDUI?.text.toString()
        val fechaNacimiento: String = edtFechaNacimiento?.text.toString()

        database = FirebaseDatabase.getInstance().getReference("personas")

        // Formar el objeto Persona que se enviará a Firebase.
        val persona = Persona(
            dui = dui,
            nombre = nombre,
            fechaNacimiento = fechaNacimiento
        )

        if (accion == "a") {
            // Agregar un nuevo registro con una clave generada por Firebase.
            val newKey = database.push().key
            if (newKey != null) {
                database.child(newKey).setValue(persona)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Se guardó con éxito", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(
                    this,
                    "No se pudo generar una clave",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else if (accion == "e") {
            // Actualizar el registro asociado con la clave recibida.
            if (key.isNotEmpty()) {
                val personaValues = persona.toMap()
                val childUpdates = hashMapOf<String, Any>(
                    key to personaValues
                )
                database.updateChildren(childUpdates)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Se actualizó con éxito", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(
                    this,
                    "No se encontró la clave del registro",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        finish()
    }

    fun cancelar(view: View?) {
        finish()
    }
}
