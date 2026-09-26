package com.example.guia08_firebasedbrealtime

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.guia08_firebasedbrealtime.datos.Persona
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {

    // Ordenamiento para hacer las consultas a los datos.
    private val consultaOrdenada: Query = refPersonas.orderByChild("nombre")
    private var personas: MutableList<Persona>? = null
    private var listaPersonas: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        inicializar()
    }

    private fun inicializar() {
        val fabAgregar: FloatingActionButton = findViewById(R.id.fab_agregar)
        listaPersonas = findViewById(R.id.ListaPersonas)

        // Cuando el usuario haga clic en la lista, se abrirá el registro para editarlo.
        listaPersonas!!.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent(this, AddPersonaActivity::class.java)
            intent.putExtra("accion", "e")
            val persona = personas!![position]
            intent.putExtra("key", persona.key)
            intent.putExtra("nombre", persona.nombre)
            intent.putExtra("dui", persona.dui)
            intent.putExtra("fechaNacimiento", persona.fechaNacimiento)
            intent.putExtra("genero", persona.genero)
            startActivity(intent)
        }

        // Una pulsación prolongada permite eliminar el registro seleccionado.
        listaPersonas!!.onItemLongClickListener =
            AdapterView.OnItemLongClickListener { _, _, position, _ ->
                val dialogo = AlertDialog.Builder(this@MainActivity)
                dialogo.setMessage("¿Está seguro de eliminar el registro?")
                    .setTitle("Confirmación")
                dialogo.setPositiveButton("Sí") { _, _ ->
                    personas!![position].key?.let {
                        refPersonas.child(it).removeValue()
                    }
                    Toast.makeText(
                        this@MainActivity,
                        "Registro borrado!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                dialogo.setNegativeButton("No") { _, _ ->
                    Toast.makeText(
                        this@MainActivity,
                        "Operación de borrado cancelada!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                dialogo.show()
                true
            }

        fabAgregar.setOnClickListener {
            // Abrir el formulario para agregar un nuevo registro.
            val intent = Intent(this, AddPersonaActivity::class.java)
            intent.putExtra("accion", "a")
            intent.putExtra("key", "")
            intent.putExtra("nombre", "")
            intent.putExtra("dui", "")
            intent.putExtra("fechaNacimiento", "")
            intent.putExtra("genero", "")
            intent.putExtra("apellido", "")
            intent.putExtra("telefono", "")
            intent.putExtra("edad", "")
            intent.putExtra("direccion", "")
            startActivity(intent)
        }

        personas = ArrayList()

        consultaOrdenada.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Actualizar la colección cuando cambien los datos de Firebase.
                personas!!.clear()
                for (dato in dataSnapshot.children) {
                    val persona: Persona? = dato.getValue(Persona::class.java)
                    persona?.key = dato.key
                    if (persona != null) {
                        personas!!.add(persona)
                    }
                }

                val adapter = PersonaAdapter(
                    this@MainActivity,
                    personas as ArrayList<Persona>
                )
                listaPersonas!!.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    this@MainActivity,
                    "Error al consultar los registros: ${databaseError.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    companion object {
        private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        private val refPersonas: DatabaseReference = database.getReference("personas")
    }
}
