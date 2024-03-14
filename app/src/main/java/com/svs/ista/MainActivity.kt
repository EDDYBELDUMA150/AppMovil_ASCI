package com.svs.ista

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import com.svs.ista.conexion.ApiCon
import com.svs.ista.databinding.ActivityMainBinding
import com.svs.ista.model.Actividad
import com.svs.ista.model.Notificacion
import com.svs.ista.model.Usuarios
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalTime
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var nomuser: SharedPreferences
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nomuser= getSharedPreferences("nombreusuario", Context.MODE_PRIVATE)
        // Obtener una referencia al ActionBar
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setTitle(R.string.noti)
        actionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#004f9f")))
        //Mantener conexion abierta
        ApiCon.init(applicationContext)

        // Llamo los métodos
        menudes()
        val messageContainer = findViewById<LinearLayout>(R.id.message_container)
        this@MainActivity.notificacion(messageContainer)
        val notificacionContainer=findViewById<LinearLayout>(R.id.notificacion_container)
        this@MainActivity.notificacionuser(notificacionContainer)
    }

    private fun menudes() {
        binding.apply {
            toggle = ActionBarDrawerToggle(this@MainActivity, principal, R.string.open, R.string.close)
            principal.addDrawerListener(toggle)
            toggle.syncState()
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.notificaciones -> {
                        val intent = Intent(this@MainActivity, MainActivity::class.java)
                        startActivity(intent)
                    }
                    R.id.reportes -> {
                        val intent = Intent(this@MainActivity, Reportes::class.java)
                        startActivity(intent)

                    }
                    R.id.principal -> {
                        val intent = Intent(this@MainActivity, Inicio::class.java)
                        startActivity(intent)
                    }
                    R.id.salir -> {
                        // Eliminar el token guardado en SharedPreferences
                        val prefs = getSharedPreferences("sesiona", Context.MODE_PRIVATE)
                        prefs.edit().remove("token").apply()
                        val intent = Intent(this@MainActivity, Login::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                true
            }
        }
    }
    ///metodos
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            true
        }
        return super.onOptionsItemSelected(item)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun notificacionuser(notificacionContainer: LinearLayout) {
        if (isNetworkAvailable()) {
        GlobalScope.launch {
        val id = iduser()

        val authService = ApiCon.authService
        val prefs = getSharedPreferences("sesiona", Context.MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val call = authService.listarnoti("Bearer $token", id)

        call.enqueue(object : Callback<List<Notificacion>> {
            override fun onResponse(call: Call<List<Notificacion>>, response: Response<List<Notificacion>>) {
                if (response.isSuccessful) {
                    val notificaciones = response.body()
                    Log.d("Notificaciones",notificaciones.toString())
                    // Limpiar el contenedor de mensajes
                    notificacionContainer.removeAllViews()
                    // Iterar sobre las notificaciones y crear vistas para cada una
                    notificaciones?.forEach { notificacion ->
                        val textView = TextView(this@MainActivity)
                        textView.text = notificacion.mensaje
                        notificacionContainer.addView(textView)
                    }
                } else {
                    val textView = TextView(this@MainActivity)
                    textView.text = "Sin notificaciones"
                    notificacionContainer.addView(textView)
                }
            }

            override fun onFailure(call: Call<List<Notificacion>>, t: Throwable) {
                toastIncorrecto("El inicio de sesion expiro", this@MainActivity)
                val prefs = getSharedPreferences("sesiona", Context.MODE_PRIVATE)
                prefs.edit().remove("token").apply()
                // Redirigir al usuario a la pantalla de inicio de sesión
                val intent = Intent(this@MainActivity, Login::class.java)
                startActivity(intent)
                finish()
            }
        })
    }
    } else {
        // No hay conexión a Internet, mostrar mensaje de error
        val textView = TextView(this@MainActivity)
        textView.text = "...."
        notificacionContainer.addView(textView)
    }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun notificacion(messageContainer: LinearLayout) {
        if (isNetworkAvailable()) {
        val authService = ApiCon.authService
        //Traigo el token generado al iniciar sesion
        val prefs = getSharedPreferences("sesiona", Context.MODE_PRIVATE)
        val token = prefs.getString("token", null)
        val fechaInicio = LocalDate.now().toString()
        val fechaFin = LocalDate.now().plusDays(7).toString()
        val call = authService.getactividad("Bearer $token", fechaInicio, fechaFin)
        call.enqueue(object : Callback<List<Actividad>> {
            override fun onResponse(call: Call<List<Actividad>>, response: Response<List<Actividad>>) {
                if (response.isSuccessful) {
                    val actividades = response.body()
                    if (actividades.isNullOrEmpty()) {
                        val mensaje = getMsjPersonalizado()
                        runOnUiThread {
                            val textView = TextView(this@MainActivity)
                            textView.text =mensaje
                            textView.setCompoundDrawablesWithIntrinsicBounds(androidx.core.R.drawable.notification_icon_background, 0, 0, 0)
                            textView.compoundDrawablePadding = 10
                            messageContainer.addView(textView)
                        }
                    } else {
                        actividades?.forEach { actividad ->
                            val fechaFin = LocalDate.parse(actividad.fecha_fin.substring(0, 10))
                            val fechaFinMenosUnDia = fechaFin.minusDays(1)
                            val fechaHoy = LocalDate.now()
                            if (fechaHoy == fechaFinMenosUnDia) {
                                runOnUiThread {
                                    val textView = TextView(this@MainActivity)
                                    textView.text = "La actividad ${actividad.nombre} esta proxima a finalizar"
                                    textView.setCompoundDrawablesWithIntrinsicBounds(androidx.core.R.drawable.notification_icon_background, 0, 0, 0)
                                    textView.compoundDrawablePadding = 10
                                    messageContainer.addView(textView)
                                }
                            }
                        }
                    } } else {
                    // Toast.makeText(this@MainActivity, "El inicio de sesion expiro", Toast.LENGTH_SHORT).show()
                    toastIncorrecto("El inicio de sesion expiro", this@MainActivity)
                   val prefs = getSharedPreferences("sesiona", Context.MODE_PRIVATE)
                    prefs.edit().remove("token").apply()
                    // Redirigir al usuario a la pantalla de inicio de sesión
                    val intent = Intent(this@MainActivity, Login::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            override fun onFailure(call: Call<List<Actividad>>, t: Throwable) {

                toastIncorrecto("Error: $t", this@MainActivity)
            }
        })
        } else {
            // No hay conexión a Internet, mostrar mensaje de error
            val textView = TextView(this@MainActivity)
            textView.text = "No se pueden mostrar las actividades sin conexión a Internet"
            messageContainer.addView(textView)
        }
    }
    // Función para verificar la disponibilidad de conexión a Internet
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
//extraer id
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

    fun toastIncorrecto(msg: String, context: Context) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.toasterror, findViewById<ViewGroup>(R.id.lltoasterror))
        val txtMensaje = view.findViewById<TextView>(R.id.tvToast)
        txtMensaje.text = msg
        val toast = Toast.makeText(context.applicationContext, null, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER_VERTICAL or Gravity.BOTTOM, 0, 200)
        toast.view = view
        toast.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getMsjPersonalizado(): String {
        val horaActual = LocalTime.now()
        return when {
            horaActual < LocalTime.NOON -> "Buenos días, no hay ninguna notificación"
            horaActual < LocalTime.of(18, 0) -> "Buenas tardes, no tienes ninguna notificación disponible"
            else -> "Buenas noches, no tienes ninguna notificación disponible"
        }
    }
}