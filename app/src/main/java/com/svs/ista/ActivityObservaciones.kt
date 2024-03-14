package com.svs.ista

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.svs.ista.conexion.ApiCon
import com.svs.ista.databinding.ActivityObservacionesBinding
import com.svs.ista.model.Actividad
import com.svs.ista.model.Actividades
import com.svs.ista.model.Notificacion
import com.svs.ista.model.Observacion
import com.svs.ista.model.ObservacionAdapter
import com.svs.ista.model.Usuarios
import com.svs.ista.service.ApiClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class ActivityObservaciones : AppCompatActivity() {
    lateinit var binding: ActivityObservacionesBinding
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var enviar: Button
    lateinit var reenviar: Button
    lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: ObservacionAdapter
    lateinit var idtext:TextView
    lateinit var des:TextView
    private lateinit var obser:EditText
    private lateinit var mApiClient: ApiClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityObservacionesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val acti = getSharedPreferences("acti", Context.MODE_PRIVATE)
        val nombre = acti.getString("nombreactividad", null)
        val actionBar: ActionBar? = supportActionBar

        actionBar?.title = getString(R.string.obse) + " " + nombre
        actionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#004f9f")))
        menu()
        des=findViewById(R.id.tvdescrip)
        obser=findViewById(R.id.txtobservacion)
        idtext=findViewById(R.id.tvobservacion)
        enviar=findViewById(R.id.btnenviar)
        reenviar=findViewById(R.id.btnreenviar)
        enviar.setOnClickListener{
            comentar()
        }
        enviarobs()
        reenviar.setOnClickListener{
            actualizar()
        }
        //
        descripcion()
        cargar()
    }
//
    private fun cargar(){
    val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    mRecyclerView = findViewById(R.id.rv)
    mRecyclerView.layoutManager = layoutManager
    mApiClient = ApiCon.authService
    mAdapter = ObservacionAdapter(this, ArrayList<Observacion>(), mApiClient)
    mRecyclerView.adapter = mAdapter
    mAdapter.cargarObservaciones()
    }

    //

@SuppressLint("SuspiciousIndentation")
private fun actualizar() {
    val prefs = getSharedPreferences("sesiona", Context.MODE_PRIVATE)
    val token = prefs.getString("token", null)
    val id = idtext.text.toString().toInt()
    val observa = obser.text.toString()
    val obs = Observacion(observa)
    val service = ApiCon.authService
        if(observa.isEmpty()){
            Toast.makeText(this@ActivityObservaciones, "Debe ingresar un texto", Toast.LENGTH_SHORT).show()
        }else {
            val call: Call<Observacion> = service.actualizarobservacion("Bearer $token", id, obs)
            call.enqueue(object : Callback<Observacion> {
                override fun onResponse(call: Call<Observacion>, response: Response<Observacion>) {
                    if (response.isSuccessful) {
                        // Actualización exitosa
                        val updatedObs = response.body()
                        toastEnviado("Observación enviada con éxito", this@ActivityObservaciones)
                        notificar()
                        notificaradmin()
                        notificarsuper()
                        enviar.visibility = View.VISIBLE
                        reenviar.visibility = View.GONE
                        obser.text.clear()
                        idtext.text = ""
                        cargar()
                    } else {
                        Toast.makeText(
                            this@ActivityObservaciones,
                            "No se pudo enviar la observacion",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<Observacion>, t: Throwable) {
                    Toast.makeText(this@ActivityObservaciones, "Error: $t", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }
}

    private fun enviarobs(){
    var observ=findViewById<EditText>(R.id.txtobservacion)
    if (intent.getBundleExtra("userdata") != null) {
        val bundle = intent.getBundleExtra("userdata")
        idtext.text= bundle!!.getInt("id_observacion").toString()
        observ.text = Editable.Factory.getInstance().newEditable(bundle.getString("observacion"))
        reenviar.visibility = View.VISIBLE
        enviar.visibility = View.GONE
    }
    }

    private fun descripcion(){
        val id = intent.getIntExtra("id_actividad", 0)
        val acti = getSharedPreferences("acti", Context.MODE_PRIVATE)
        val descrip = acti.getString("descripcion", null)
        val prefs = getSharedPreferences("sesiona", Context.MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val apiService = ApiCon.authService
        apiService.getactividad(token, id).enqueue(object : Callback<Actividades> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<Actividades>, response: Response<Actividades>) {
                if (response.isSuccessful) {
                    val actividad = response.body()
                    val descripcion = actividad?.descripcion
                    // hacer algo con la descripción obtenida
                    des.text="Descripcion: $descripcion"
                } else {
                    des.text="Descripcion: $descrip"
                }
            }

            override fun onFailure(call: Call<Actividades>, t: Throwable) {
                Toast.makeText(this@ActivityObservaciones, "Error al traer la descripcion: $t", Toast.LENGTH_SHORT).show()
            }
        })
    }
private fun comentar() {
    val id = intent.getIntExtra("id_actividad", 0)
    enviar.visibility=View.VISIBLE
    reenviar.visibility=View.GONE
    val descripcion = obser?.text.toString()
    if(descripcion.isEmpty()){
        Toast.makeText(this@ActivityObservaciones, "Debe ingresar un texto", Toast.LENGTH_SHORT).show()
    }else{
    GlobalScope.launch {
        val iduser = iduser()
        val prefs = getSharedPreferences("sesiona", Context.MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val authService = ApiCon.authService

        val usuario = Usuarios(iduser)
        val actividad = Actividad(id)
        val enviarobs = Observacion(descripcion, usuario, actividad)

        val call = authService.crearobservacion("Bearer $token", enviarobs)
        call.enqueue(object : Callback<Observacion> {
            override fun onResponse(call: Call<Observacion>, response: Response<Observacion>) {
                if (response.isSuccessful) {
                    val respuesta = response.body()
                    toastEnviado("Observación enviada con éxito", this@ActivityObservaciones)
                    notificar()
                    notificarsuper()
                    notificaradmin()
                    obser.text.clear()
                    cargar()
                } else {
                    Toast.makeText(
                        this@ActivityObservaciones,
                        "No se pudo enviar la observacion",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Observacion>, t: Throwable) {
                Toast.makeText(this@ActivityObservaciones, "Error: $t", Toast.LENGTH_SHORT).show()
            }
        })
    }
    }
}
    @SuppressLint("SimpleDateFormat")
    private fun notificarsuper(){
        val nom = getSharedPreferences("acti", Context.MODE_PRIVATE)
        val nombres = nom.getString("nombres", null)
        val actividad = nom.getString("nombreactividad", null)
        val prefs = getSharedPreferences("sesiona", Context.MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val fechaActual = Timestamp(System.currentTimeMillis())
        val formato = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        val fecha = formato.format(fechaActual)
        val authService = ApiCon.authService
        val superad =Notificacion(0,"SUPERADMIN","$nombres ha comentado la actividad $actividad",fecha, false)
        val call = authService.crearnoti("Bearer $token",superad)
        call.enqueue(object : Callback<Notificacion> {
            override fun onResponse(call: Call<Notificacion>, response: Response<Notificacion>) {
                if (response.isSuccessful) {
                    val respuesta = response.body()
                    Log.d("Notificación", "Notificación enviada")

                } else {
                    Log.d("Notificación", "No se pudo enviar la notificacion superadmin")
                }
            }

            override fun onFailure(call: Call<Notificacion>, t: Throwable) {
                Log.e("Notificación", "Error: $t")
            }
        })
    }

    @SuppressLint("SimpleDateFormat")
    private fun notificaradmin(){
        val nom = getSharedPreferences("acti", Context.MODE_PRIVATE)
        val nombres = nom.getString("nombres", null)
        val actividad = nom.getString("nombreactividad", null)
        val prefs = getSharedPreferences("sesiona", Context.MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val fechaActual = Timestamp(System.currentTimeMillis())
        val formato = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        val fecha = formato.format(fechaActual)
        val authService = ApiCon.authService
        val superad =Notificacion(0,"ADMIN","$nombres ha comentado la actividad $actividad",fecha, false)
        val call = authService.crearnoti("Bearer $token",superad)
        call.enqueue(object : Callback<Notificacion> {
            override fun onResponse(call: Call<Notificacion>, response: Response<Notificacion>) {
                if (response.isSuccessful) {
                    val respuesta = response.body()
                    Log.d("Notificación", "Notificación enviada")

                } else {
                    Log.d("Notificación", "No se pudo enviar la notificacion admin")
                }
            }

            override fun onFailure(call: Call<Notificacion>, t: Throwable) {
                Log.e("Notificación", "Error: $t")
            }
        })
    }

    @SuppressLint("SimpleDateFormat")
    private fun notificar(){
        val usuario=intent.getIntExtra("usuario",0)
        val nom = getSharedPreferences("acti", Context.MODE_PRIVATE)
        val nombres = nom.getString("nombres", null)
        val actividad = nom.getString("nombreactividad", null)
        val prefs = getSharedPreferences("sesiona", Context.MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val fechaActual = Timestamp(System.currentTimeMillis())
        val formato = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        val fecha = formato.format(fechaActual)
        val authService = ApiCon.authService
        val superad =Notificacion(usuario,"","$nombres ha comentado tu actividad $actividad",fecha,false)
        val call = authService.crearnoti("Bearer $token",superad)
        call.enqueue(object : Callback<Notificacion> {
            override fun onResponse(call: Call<Notificacion>, response: Response<Notificacion>) {
                if (response.isSuccessful) {
                    val respuesta = response.body()
                    Log.d("Notificación", "Notificación enviada")

                } else {
                    Log.d("Notificación", "No se pudo enviar la notificacion usuario")
                }
            }

            override fun onFailure(call: Call<Notificacion>, t: Throwable) {
                Log.e("Notificación", "Error: $t")
            }
        })
    }
    private suspend fun iduser(): Int {
        val authService = ApiCon.authService
        val prefs = getSharedPreferences("sesiona", Context.MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val nombre = prefs.getString("usuario", null)

        return suspendCoroutine { continuation ->
            val call = authService.getusuarios("Bearer $token", "$nombre")
            call.enqueue(object : Callback<Usuarios> {
                override fun onResponse(call: Call<Usuarios>, response: Response<Usuarios>) {
                    if (response.isSuccessful) {
                        val id = response.body()?.id ?: 0
                        continuation.resume(id)
                    } else {
                        continuation.resumeWithException(Exception("No se pudo extraer el id"))
                    }
                }

                override fun onFailure(call: Call<Usuarios>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    //
    private fun menu(){
        binding.apply {
            toggle= ActionBarDrawerToggle(this@ActivityObservaciones,actobservacion,R.string.open,R.string.close)
            actobservacion.addDrawerListener(toggle)
            toggle.syncState()
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            navView.setNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.notificaciones->{
                        val intent = Intent(this@ActivityObservaciones, MainActivity::class.java)
                        startActivity(intent)
                    }
                    R.id.reportes->{
                        val intent = Intent(this@ActivityObservaciones, Reportes::class.java)
                        startActivity(intent)
                    }
                    R.id.principal->{
                        val intent = Intent(this@ActivityObservaciones, Inicio::class.java)
                        startActivity(intent)
                    }
                    R.id.salir -> {
                        // Eliminar el token guardado en SharedPreferences
                        val prefs = getSharedPreferences("sesiona", Context.MODE_PRIVATE)
                        prefs.edit().remove("token").apply()
                        val intent = Intent(this@ActivityObservaciones, Login::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                true
            }
        }
    }

    private fun toastEnviado(msg: String, context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.toastenvio, findViewById<ViewGroup>(R.id.lltoastenvio))
        val txtMensaje = view.findViewById<TextView>(R.id.tvToasten)
        txtMensaje.text = msg
        val toast = Toast.makeText(context.applicationContext, null, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER_VERTICAL or Gravity.BOTTOM, 0, 200)
        toast.view = view
        toast.show()
    }
    ///metodos
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            true
        }
        return super.onOptionsItemSelected(item)
    }
}