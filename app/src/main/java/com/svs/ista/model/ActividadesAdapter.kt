package com.svs.ista.model

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.svs.ista.R
import java.time.LocalDate

class ActividadesAdapter(private val context: Context, private val actividades: List<Actividades>) : BaseAdapter() {

    override fun getCount(): Int {
        return actividades.size
    }

    override fun getItem(position: Int): Any {
        return actividades[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.listactividad, parent, false)
        }

        val actividad = getItem(position) as Actividades

        val nombreTextView = view?.findViewById<TextView>(R.id.tvnomact)
        nombreTextView?.text = actividad.nombre

        val fechaInicioTextView = view?.findViewById<TextView>(R.id.tvfeini)
        val fechain = LocalDate.parse(actividad.fecha_inicio.substring(0, 10))
        fechaInicioTextView?.text = fechain.toString()

        val fechaFinTextView = view?.findViewById<TextView>(R.id.tvfefin)
        val fechafin = LocalDate.parse(actividad.fecha_fin.substring(0, 10))
        fechaFinTextView?.text = fechafin.toString()

        val idTextView = view?.findViewById<TextView>(R.id.tvidact)
        idTextView?.text = actividad.id_actividad.toString()

        return view
    }
}
