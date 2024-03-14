package com.svs.ista

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.gson.Gson
import com.svs.ista.Basesql.DatabaseHelper

import com.svs.ista.conexion.ApiCon
import com.svs.ista.databinding.ActivityInicioBinding
import com.svs.ista.model.Persona
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Inicio : AppCompatActivity() {
    lateinit var binding: ActivityInicioBinding
    lateinit var toggle: ActionBarDrawerToggle
    //iniciamos los componetes luego de haber iniciado
    private lateinit var txtfecha:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityInicioBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setTitle(R.string.perfil)
        actionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#004f9f")))
        //debe ir despues de que la vista se infle los botones o textview
        txtfecha = findViewById(R.id.tvfecha1)
        menudes()
        // Obtener la fecha actual
        val fechaActual = fecha()
        txtfecha.text = fechaActual
        usuario()
    }
    private fun usuario(){
        val authService = ApiCon.authService
        //Traigo el token generado al iniciar sesion
        val prefs = getSharedPreferences("sesiona", Context.MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val user=prefs.getString("usuario", null)
        //Llamo los textviews
        val cedula = findViewById<TextView>(R.id.tvcedula)
        val nombres=findViewById<TextView>(R.id.tvnombres)
        val apellidos=findViewById<TextView>(R.id.tvapellidos)
        val correo=findViewById<TextView>(R.id.tvcorreo)
        val dir=findViewById<TextView>(R.id.tvdireccion)
        val cel=findViewById<TextView>(R.id.tvcelular)
        val dbHelper = DatabaseHelper(applicationContext)
        val call = authService.obtenerPersona("$user", "Bearer $token")
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val gson = Gson()
                    val json = response.body()?.string()
                    val persona = gson.fromJson(json, Persona::class.java)
                    dbHelper.insertDataPersona(persona)
                    cedula.text = persona.cedula
                    val nombre = getString(R.string.unirstring, persona.primerNombre, persona.segundoNombre)
                    nombres.text = nombre
                    val apellido = getString(R.string.unirstring, persona.primerApellido, persona.segundoApellido)
                    apellidos.text=apellido
                    correo.text=persona.correo
                    dir.text=persona.direccion
                    cel.text=persona.celular

                } else {
                    Toast.makeText(this@Inicio, "No se pudo cargar los datos, inicie sesion nuevamente", Toast.LENGTH_SHORT).show()
                    cedula.text =""
                    nombres.text =""
                    apellidos.text=""
                    correo.text=""
                    dir.text=""
                    cel.text=""
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@Inicio, "Estas sin conexión", Toast.LENGTH_SHORT).show()
                val dbHelper = DatabaseHelper(applicationContext)
                val personas = dbHelper.allDataPersonas
                val matchingPersona=personas.find {
                    it.cedula==user
                }
                // Verificar si hay personas almacenadas en la base de datos
                if (matchingPersona != null) {
                    // Actualizar los TextViews con los datos de la persona
                    cedula.text = matchingPersona.cedula
                    nombres.text = "${matchingPersona.primerNombre} ${matchingPersona.segundoNombre}"
                    apellidos.text = "${matchingPersona.primerApellido} ${matchingPersona.segundoApellido}"
                    correo.text = matchingPersona.correo
                    dir.text = matchingPersona.direccion
                    cel.text = matchingPersona.celular
                } else {
                    // Limpiar los TextViews
                    cedula.text = ""
                    nombres.text = ""
                    apellidos.text = ""
                    correo.text = ""
                    dir.text = ""
                    cel.text = ""
                }
            }
        })
    }
    private fun menudes() {
        binding.apply {
            toggle = ActionBarDrawerToggle(this@Inicio, inicio, R.string.open, R.string.close)
            inicio.addDrawerListener(toggle)
            toggle.syncState()
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.notificaciones -> {
                        val intent = Intent(this@Inicio, MainActivity::class.java)
                        startActivity(intent)
                    }
                    R.id.reportes -> {
                        val intent = Intent(this@Inicio, Reportes::class.java)
                        startActivity(intent)

                    }
                    R.id.principal -> {
                        Toast.makeText(this@Inicio, "Inicio", Toast.LENGTH_SHORT).show()
                    }
                    R.id.salir -> {
                        // Eliminar el token guardado en SharedPreferences
                        val prefs = getSharedPreferences("sesiona", Context.MODE_PRIVATE)
                        prefs.edit().remove("token").apply()
                        val intent = Intent(this@Inicio, Login::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                true
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            true
        }
        return super.onOptionsItemSelected(item)
    }

    //Metodos
    private fun fecha(): String {
        // Obtengo la fecha actual
        val calendar = Calendar.getInstance()
        // Creo un objeto SimpleDateFormat con el formato deseado
        val dateFormat = SimpleDateFormat("EEEE dd 'de' MMMM 'del' yyyy", Locale.getDefault())
        // Formateo la fecha como un string y la primera letra en mayuscula
        return dateFormat.format(calendar.time).replaceFirstChar { it.uppercase() }
    }
}