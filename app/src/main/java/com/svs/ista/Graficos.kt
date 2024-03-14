package com.svs.ista

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import com.echo.holographlibrary.Bar
import com.echo.holographlibrary.BarGraph
import com.svs.ista.databinding.ActivityGraficosBinding
import com.svs.ista.databinding.ActivityReportesBinding
import kotlin.math.roundToInt

class Graficos : AppCompatActivity() {
    lateinit var binding: ActivityGraficosBinding
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var nom:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityGraficosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setTitle(R.string.grafico)
        actionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#004f9f")))
        menudes()
        barras()

       /* val puntos=ArrayList<Bar>()

        binding.btnadd.setOnClickListener{
            graficarbarras(puntos)
        }*/
    }

    /*private fun graficarbarras(puntos:ArrayList<Bar>) {
        val barra=Bar()
        var color=generarColor()
        barra.color=Color.parseColor(color)
        //llamo a los botones o edittext mediante el binding
        barra.name=binding.txtconcepto.text.toString()
        barra.value=binding.txtcantidad.text.toString().toFloat()
        puntos.add(barra)

        val grafi=findViewById<View>(R.id.barragraph) as BarGraph
        grafi.bars=puntos
    }*/

    private fun barras() {
        nom=findViewById(R.id.tvtit)
        val sumaPeso = intent.getDoubleExtra("peso", 0.0)
        val sumaEstandar = intent.getDoubleExtra("estandar", 0.0)
        val obtener=intent.getDoubleExtra("obtener",0.0)
        val nombre=intent.getStringExtra("nombre")
        val peso = "Ponderacion"
        val estandar = "V/Obtenido"
        val obt="V/ por obtener"
        nom.text=nombre

        // Crear los objetos Bar para los valores de la suma de las columnas
        val barraPeso = Bar()
        barraPeso.name = peso
        barraPeso.value = sumaPeso.toFloat()

        val barraEstandar = Bar()
        barraEstandar.name = estandar
        barraEstandar.value = sumaEstandar.toFloat()

        val barraObtener = Bar()
        barraObtener.name = obt
        barraObtener.value = obtener.toFloat()
        // Agregar los objetos Bar a la lista de puntos
        val puntos = ArrayList<Bar>()
        puntos.add(barraPeso)
        puntos.add(barraEstandar)
        puntos.add(barraObtener)
        // Configurar las barras de la gráfica
        for (barra in puntos) {
            val color = when (barra.name) {
                "Ponderacion" -> "#D45202" // Tomate
                "V/Obtenido" -> "#087601" // Verde
                "V/ por obtener"->"#B23E3E"//lacre
                else -> generarColor() // Otro color aleatorio
            }
            barra.color = Color.parseColor(color)
        }

        // Obtener el objeto BarGraph y configurarlo con los valores de las barras
        val grafi = findViewById<View>(R.id.barragraph) as BarGraph
        grafi.bars = puntos
    }


    private fun generarColor(): String {
        val letras= arrayOf("0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F")
        var color="#"
        for (i in 0..5){
            color+=letras[(Math.random()*15).roundToInt()]
        }
        return color
    }

    private fun menudes() {
        binding.apply {
            toggle = ActionBarDrawerToggle(this@Graficos, grafico, R.string.open, R.string.close)
            grafico.addDrawerListener(toggle)
            toggle.syncState()
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.notificaciones -> {
                        val intent = Intent(this@Graficos, MainActivity::class.java)
                        startActivity(intent)

                    }
                    R.id.reportes -> {
                        val intent = Intent(this@Graficos, Reportes::class.java)
                        startActivity(intent)

                    }
                    R.id.principal -> {
                        val intent = Intent(this@Graficos, Inicio::class.java)
                        startActivity(intent)

                    }
                    R.id.salir -> {
                        // Eliminar el token guardado en SharedPreferences
                        val prefs = getSharedPreferences("sesiona", Context.MODE_PRIVATE)
                        prefs.edit().remove("token").apply()
                        val intent = Intent(this@Graficos, Login::class.java)
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
}