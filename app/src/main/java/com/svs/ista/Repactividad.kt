package com.svs.ista

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.gson.Gson
import com.svs.ista.Basesql.DatabaseHelper
import com.svs.ista.conexion.ApiCon
import com.svs.ista.databinding.ActivityMainBinding
import com.svs.ista.databinding.ActivityRepactividadBinding
import com.svs.ista.model.Actividades
import com.svs.ista.model.ActividadesAdapter
import com.svs.ista.model.Persona
import com.svs.ista.model.Usuarios
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate

class Repactividad : AppCompatActivity() {
    lateinit var binding: ActivityRepactividadBinding
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var listView: ListView
    private lateinit var adapter: ActividadesAdapter
    private lateinit var acti: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        acti = getSharedPreferences("acti", Context.MODE_PRIVATE)
        binding= ActivityRepactividadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setTitle(R.string.repacti)
        actionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#004f9f")))

        menu()
        listaractividad()
        extraernombres()
    }
    //
    private fun listaractividad() {

        // Llamo al ListView y al adaptador personalizado
        listView = findViewById(R.id.listaactividades)
        adapter = ActividadesAdapter(this, emptyList())
        listView.adapter = adapter
        val authService = ApiCon.authService
        val prefs = getSharedPreferences("sesiona", Context.MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val call = authService.listactividad("Bearer $token")

        call.enqueue(object : Callback<List<Actividades>> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<List<Actividades>>,
                response: Response<List<Actividades>>
            ) {
                if (response.isSuccessful) {
                    val actividadesList = response.body() ?: emptyList()
                    // Filtro las actividades por fecha
                    val actividadesFiltradas = actividadesList.filter { actividad ->
                        val fechafin = LocalDate.parse(actividad.fecha_fin.substring(0, 10))
                        fechafin.isAfter(LocalDate.now())

                    }

                    val dbHelper = DatabaseHelper(applicationContext)
                    // Almacena las actividades en la base de datos local
                    dbHelper.insertDataActividad(actividadesList)

                    adapter = ActividadesAdapter(this@Repactividad, actividadesFiltradas)
                    listView.adapter = adapter
                    listView.setOnItemClickListener { _, _, position, _ ->
                        val intent = Intent(this@Repactividad, ActivityObservaciones::class.java)
                        intent.putExtra("id_actividad", actividadesFiltradas[position].id_actividad)
                        acti.edit().putInt("idactividad",actividadesFiltradas[position].id_actividad).apply()
                        intent.putExtra("descripcion", actividadesFiltradas[position].descripcion)
                        acti.edit().putString("descripcion",actividadesFiltradas[position].descripcion).apply()
                        intent.putExtra("nombre", actividadesFiltradas[position].nombre)
                        acti.edit().putString("nombreactividad",actividadesFiltradas[position].nombre).apply()
                        intent.putExtra("usuario", actividadesFiltradas[position].usuario.id)
                        acti.edit().putInt("idusuario",actividadesFiltradas[position].usuario.id).apply()
                        startActivity(intent)
                    }
                }
            }
            override fun onFailure(call: Call<List<Actividades>>, t: Throwable) {
                Toast.makeText(
                    this@Repactividad,
                    "Esta sin conexión",
                    Toast.LENGTH_SHORT
                ).show()
                val dbHelper = DatabaseHelper(applicationContext)
                val actividadesFromDB = dbHelper.getAllDataActividad()
                adapter = ActividadesAdapter(this@Repactividad, actividadesFromDB)
                listView.adapter = adapter
            }
        })
    }
//Nombre usuario
private fun extraernombres(){
    val authService = ApiCon.authService
    //Traigo el token generado al iniciar sesion
    val prefs = getSharedPreferences("sesiona", Context.MODE_PRIVATE)
    val token = prefs.getString("token", null)
    val user=prefs.getString("usuario", null)
    val call = authService.obtenerPersona("$user", "Bearer $token")
    call.enqueue(object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            if (response.isSuccessful) {
                val gson = Gson()
                val json = response.body()?.string()
                val persona = gson.fromJson(json, Persona::class.java)
                val nombre = persona.primerNombre
                val apellido = persona.primerApellido
                acti.edit().putString("nombres", "$nombre $apellido").apply()

            } else {
                Toast.makeText(this@Repactividad, "No se pudo carga los datos, inicie sesion nuevamente", Toast.LENGTH_SHORT).show()
            }
        }
        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            Toast.makeText(this@Repactividad, "Error: $t", Toast.LENGTH_SHORT).show()
        }
    })
}
    private fun menu(){
        binding.apply {
            toggle= ActionBarDrawerToggle(this@Repactividad, actividadrep,R.string.open,R.string.close)
            actividadrep.addDrawerListener(toggle)
            toggle.syncState()
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            navView.setNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.notificaciones->{
                        val intent = Intent(this@Repactividad, MainActivity::class.java)
                        startActivity(intent)
                    }
                    R.id.reportes->{
                        val intent = Intent(this@Repactividad, Reportes::class.java)
                        startActivity(intent)
                    }
                    R.id.principal->{
                        val intent = Intent(this@Repactividad, Inicio::class.java)
                        startActivity(intent)
                    }
                    R.id.salir -> {
                        // Eliminar el token guardado en SharedPreferences
                        val prefs = getSharedPreferences("sesiona", Context.MODE_PRIVATE)
                        prefs.edit().remove("token").apply()
                        val intent = Intent(this@Repactividad, Login::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                true
            }
        }
    }
    //metodos
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            true
        }
        return super.onOptionsItemSelected(item)
    }

}