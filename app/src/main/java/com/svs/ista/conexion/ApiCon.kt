package com.svs.ista.conexion

import android.annotation.SuppressLint
import android.content.Context
import com.svs.ista.service.ApiClient
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@SuppressLint("StaticFieldLeak")
object ApiCon {
    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context
    }
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()

            // Agregar el token a la solicitud
            val token = TokenManager.getToken(context)
            if (token != null) {
                val request = original.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
                chain.proceed(request)
            } else {
                chain.proceed(original)
            }
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.103:8080")
        //.baseUrl("http://165.227.197.169:5000")
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()

    val authService: ApiClient = retrofit.create(ApiClient::class.java)
}