package com.svs.ista

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import com.svs.ista.databinding.ActivityMainBinding
import com.svs.ista.databinding.ActivityReportesBinding

class Reportes : AppCompatActivity() {

    //esto va segun el activity
    lateinit var binding: ActivityReportesBinding
    lateinit var toggle: ActionBarDrawerToggle
    private lateinit var crite: Button
    private lateinit var acti: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityReportesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setTitle(R.string.reporte)
        actionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#004f9f")))
        menudes()
        crite = findViewById(R.id.btncrit)
        acti = findViewById(R.id.btnact)
        crite.setOnClickListener { criterios() }
        acti.setOnClickListener { actividades() }
    }

    private fun menudes() {
        binding.apply {
            toggle = ActionBarDrawerToggle(this@Reportes, report, R.string.open, R.string.close)
            report.addDrawerListener(toggle)
            toggle.syncState()
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            navView.setNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.notificaciones -> {
                        val intent = Intent(this@Reportes, MainActivity::class.java)
                        startActivity(intent)
                    }
                    R.id.reportes -> {
                        Toast.makeText(this@Reportes, "Reportes", Toast.LENGTH_SHORT).show()
                    }
                    R.id.principal -> {
                        val intent = Intent(this@Reportes, Inicio::class.java)
                        startActivity(intent)
                    }
                    R.id.salir -> {
                        val prefs = getSharedPreferences("sesiona", Context.MODE_PRIVATE)
                        prefs.edit().remove("token").apply()
                        val intent = Intent(this@Reportes, Login::class.java)
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

    //
    private fun criterios(){
        val intent = Intent(this@Reportes, Repcriterio::class.java)
        startActivity(intent)
    }

    private fun actividades(){
        val intent = Intent(this@Reportes, Repactividad::class.java)
        startActivity(intent)
    }
}