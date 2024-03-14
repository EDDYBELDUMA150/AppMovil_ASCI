package com.svs.ista

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import com.svs.ista.conexion.ApiCon
import com.svs.ista.databinding.ActivityRepcriterioBinding
import com.svs.ista.model.Criterios
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repcriterio : AppCompatActivity() {
    lateinit var binding: ActivityRepcriterioBinding
    lateinit var toggle: ActionBarDrawerToggle

    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<Criterios>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding= ActivityRepcriterioBinding.inflate(layoutInflater)
         setContentView(binding.root)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setTitle(R.string.repcri)
        actionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#004f9f")))
        menu()

        //Llamar metodos

        listView=findViewById(R.id.listacriterios)
        listarcriterios()

    }

    //metodos
    private fun listarcriterios() {
        val authService = ApiCon.authService
        val token = Login.Singleton.instance.token
        val call = authService.obtenerCriterios("Bearer $token")
        call.enqueue(object : Callback<List<Criterios>> {
            override fun onResponse(
                call: Call<List<Criterios>>,
                response: Response<List<Criterios>>
            ) {
                if (response.isSuccessful) {
                    val listaCriterios = response.body()?.map {
                        Criterios(it.id_criterio, it.nombre)
                    } ?: emptyList()

                    adapter = CriteriosAdapter(this@Repcriterio, listaCriterios)
                    listView.adapter = adapter
                    listView.setOnItemClickListener { _, _, position, _ ->
                        val criterio = adapter.getItem(position) as Criterios
                        val intent = Intent(this@Repcriterio, Repespecifico::class.java)
                        intent.putExtra("id_criterio", criterio.id_criterio)
                        intent.putExtra("nombre", criterio.nombre)
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(
                        this@Repcriterio,
                        "Error: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Criterios>>, t: Throwable) {
                Toast.makeText(
                    this@Repcriterio,
                    "Debes conectarte a internet",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
//

    class CriteriosAdapter(context: Context, criterios: List<Criterios>) :
        ArrayAdapter<Criterios>(context, android.R.layout.simple_list_item_1, criterios) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var view = convertView
            if (view == null) {
                view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false)
            }
            val criterio = getItem(position)
            val text = "${criterio?.id_criterio}. ${criterio?.nombre}"
            (view as TextView).text = text
            return view
        }
    }

    //
    private fun menu(){
        binding.apply {
            toggle= ActionBarDrawerToggle(this@Repcriterio, criteriorep,R.string.open,R.string.close)
            criteriorep.addDrawerListener(toggle)
            toggle.syncState()
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            navView.setNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.notificaciones->{
                        val intent = Intent(this@Repcriterio, MainActivity::class.java)
                        startActivity(intent)
                    }
                    R.id.reportes->{
                        val intent = Intent(this@Repcriterio, Reportes::class.java)
                        startActivity(intent)
                    }
                    R.id.principal->{
                        val intent = Intent(this@Repcriterio, Inicio::class.java)
                        startActivity(intent)
                    }
                    R.id.salir -> {
                        // Eliminar el token guardado en SharedPreferences
                        val prefs = getSharedPreferences("sesiona", Context.MODE_PRIVATE)
                        prefs.edit().remove("token").apply()
                        val intent = Intent(this@Repcriterio, Login::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
                true
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)){
            true
        }
        return super.onOptionsItemSelected(item)
    }
}