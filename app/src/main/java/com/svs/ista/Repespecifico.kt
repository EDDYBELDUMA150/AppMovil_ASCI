package com.svs.ista

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import com.svs.ista.conexion.ApiCon
import com.svs.ista.databinding.ActivityMainBinding
import com.svs.ista.databinding.ActivityRepespecificoBinding
import com.svs.ista.model.Indicadores
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repespecifico : AppCompatActivity() {
    lateinit var binding:ActivityRepespecificoBinding
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var idelegido: TextView
    lateinit var tabla: TableLayout
    lateinit var grafi: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRepespecificoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val actionBar: ActionBar? = supportActionBar
        val nombre = intent?.getStringExtra("nombre")
        actionBar?.title = getString(R.string.repac) + " " + nombre
        actionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#004f9f")))
        idelegido=findViewById(R.id.tvid)
        tabla=findViewById(R.id.tblcriterios)
        tabla?.removeAllViews()
        menu()
        eleccion()
        grafi=findViewById(R.id.btnrepgraf)
        grafi.setOnClickListener{
            graficas()
        }
    }

    //
    private fun eleccion(){
        val idCriterio = intent.getIntExtra("id_criterio", -1)
        idelegido?.text=idCriterio.toString()
        val authService = ApiCon.authService
        val token = Login.Singleton.instance.token
        val listai = mutableListOf<Indicadores>()
        val call = authService.obtenerIndicadores(idCriterio,"Bearer $token")
        call.enqueue(object : Callback<List<Indicadores>> {
            override fun onResponse(call: Call<List<Indicadores>>, response: Response<List<Indicadores>>) {
                if (response.isSuccessful) {
                    response.body()?.let { registros ->
                        // Crear encabezado
                        val vistaEncabezado = layoutInflater.inflate(R.layout.tabla_row, null)
                        val colnombreEncabezado = vistaEncabezado.findViewById<View>(R.id.colnombre) as TextView
                        val colpesoEncabezado = vistaEncabezado.findViewById<View>(R.id.colpeso) as TextView
                        val colestandarEncabezado = vistaEncabezado.findViewById<View>(R.id.colestandar) as TextView

                        //Color negrita en el encabezado
                        colnombreEncabezado.setTypeface(null, Typeface.BOLD)
                        colpesoEncabezado.setTypeface(null, Typeface.BOLD)
                        colestandarEncabezado.setTypeface(null, Typeface.BOLD)
                        //Colores fondo
                        colnombreEncabezado.setBackgroundColor(Color.parseColor("#004f9f"))
                        colpesoEncabezado.setBackgroundColor(Color.parseColor("#004f9f"))
                        colestandarEncabezado.setBackgroundColor(Color.parseColor("#004f9f"))
                        //colores letra
                        colnombreEncabezado.setTextColor(Color.WHITE)
                        colpesoEncabezado.setTextColor(Color.WHITE)
                        colestandarEncabezado.setTextColor(Color.WHITE)
                        colnombreEncabezado.gravity = Gravity.CENTER
                        //nombres
                        colnombreEncabezado.text = "DESCRIPCIÓN"
                        colpesoEncabezado.text = "% Indicador"
                        colestandarEncabezado.text = "% Obtenido"
                        // Agregar encabezado a la tabla
                        tabla.addView(vistaEncabezado)
                        for (registro in registros) {
                            listai.add(registro)

                            val vistaRegistro = layoutInflater.inflate(R.layout.tabla_row, null)
                            val colid = vistaRegistro.findViewById<View>(R.id.colid) as TextView
                            val colnombre = vistaRegistro.findViewById<View>(R.id.colnombre) as TextView
                            val colpeso = vistaRegistro.findViewById<View>(R.id.colpeso) as TextView
                            val colestandar = vistaRegistro.findViewById<View>(R.id.colestandar) as TextView

                            //texto
                            colid.text = registro.id_indicador.toString()
                            colnombre.text = registro.nombre
                            colpeso.text = registro.peso.toString()
                            colestandar.text=registro.porc_utilida_obtenida.toString()

                            // Agrego la vista del registro a la tabla
                            tabla.addView(vistaRegistro)
                     }
                   }
                } else {
                    Toast.makeText(this@Repespecifico, "No se pudo cargar los indicadores", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Indicadores>>, t: Throwable) {
                Toast.makeText(this@Repespecifico, "Error: $t", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun graficas() {
        var sumaPeso = 0.0
        var sumaEstandar = 0.0

        for (i in 0 until tabla.childCount) {
            val vistaRegistro = tabla.getChildAt(i)
            val colpeso = vistaRegistro.findViewById<View>(R.id.colpeso) as TextView
            val colestandar = vistaRegistro.findViewById<View>(R.id.colestandar) as TextView
            // Comprobar si el valor de la columna es numérico antes de sumarlo
            if (colpeso.text.toString().matches("-?\\d+(\\.\\d+)?".toRegex())) {
            sumaPeso += colpeso.text.toString().toDouble()}

            if (colestandar.text.toString().matches("-?\\d+(\\.\\d+)?".toRegex())) {
            sumaEstandar += colestandar.text.toString().toDouble()}
        }
        val obtener=sumaPeso-sumaEstandar
        val nom = intent?.getStringExtra("nombre")
        val intent = Intent(this@Repespecifico, Graficos::class.java)
        intent.putExtra("peso", sumaPeso)
        intent.putExtra("estandar", sumaEstandar)
        intent.putExtra("obtener", obtener)
        intent.putExtra("nombre", nom)
        startActivity(intent)

    }
    private fun menu(){
        binding.apply {
            toggle= ActionBarDrawerToggle(this@Repespecifico,repespe,R.string.open,R.string.close)
            repespe.addDrawerListener(toggle)
            toggle.syncState()
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            navView.setNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.notificaciones->{
                        val intent = Intent(this@Repespecifico, MainActivity::class.java)
                        startActivity(intent)
                    }
                    R.id.reportes->{
                        val intent = Intent(this@Repespecifico, Reportes::class.java)
                        startActivity(intent)
                    }
                    R.id.principal->{
                        val intent = Intent(this@Repespecifico, Inicio::class.java)
                        startActivity(intent)
                    }
                    R.id.salir -> {
                        // Eliminar el token guardado en SharedPreferences
                        val prefs = getSharedPreferences("sesiona", Context.MODE_PRIVATE)
                        prefs.edit().remove("token").apply()
                        val intent = Intent(this@Repespecifico, Login::class.java)
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
}