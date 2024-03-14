package com.svs.ista

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.svs.ista.Basesql.DatabaseHelper
import com.svs.ista.conexion.ApiCon
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException
import com.svs.ista.model.LoginUser
import com.svs.ista.model.Usuario
import com.svs.ista.service.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Login : AppCompatActivity() {
    var user:EditText?=null
    var pass:EditText?=null
    var eliminar:ImageButton?=null
    private lateinit var prefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializar SharedPreferences
        prefs = getSharedPreferences("sesiona", Context.MODE_PRIVATE)
        // Verificar si ya hay una sesión abierta
        val token = prefs.getString("token", null)
        if (token != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
        setContentView(R.layout.activity_login)
        val loginButton = findViewById<Button>(R.id.btnlogin)
        loginButton.setOnClickListener { login() }

        user=findViewById(R.id.txtusuario)
        pass=findViewById(R.id.txtpasword)
        //
        eliminar=findViewById(R.id.btnborrar)
        eliminar?.setOnClickListener{
            borrar()
        }
        cargarusuario()
        vercontra()
        //Abrir la conexion
        ApiCon.init(applicationContext)
    }

    private fun cargarusuario(){
        val usuario = prefs.getString("usuario", null)
        if (usuario != null) {
        user?.text = Editable.Factory.getInstance().newEditable(usuario)
        }
    }
    private fun borrar(){
        val prefs = getSharedPreferences("sesiona", Context.MODE_PRIVATE)
        prefs.edit().remove("usuario").apply()
        user?.text?.clear()
    }
    private fun vercontra(){
        val passwordEditText = findViewById<EditText>(R.id.txtpasword)
        val showPasswordButton = findViewById<ImageButton>(R.id.btn_show_password)

        showPasswordButton.setOnClickListener {
            if (passwordEditText.transformationMethod == PasswordTransformationMethod.getInstance()) {
                // Si la contraseña está oculta, mostrarla
                passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                showPasswordButton.setImageResource(R.mipmap.ic_visible_foreground)
            } else {
                // Si la contraseña está visible, ocultarla
                passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                showPasswordButton.setImageResource(R.mipmap.ic_invisible_foreground)
            }
        }



    }
    class Singleton {
        var token: String? = null
        var user: String? = null
        companion object {
            val instance = Singleton()
        }
    }

    private fun login() {
        //Llamo a la conexion
            val authService = ApiCon.authService
            val usuario = user?.text.toString()
            val contra = pass?.text.toString()
            val loginUser = LoginUser(usuario, contra)
        //llamo a los metodos que contiene la conexion
            val call = authService.login(loginUser)
            call.enqueue(object : Callback<Usuario> {
                override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                    if (response.isSuccessful) {
                        Singleton.instance.token = response.body()?.token
                        Singleton.instance.user=usuario
                        val token1 = response.body()?.token
                        prefs.edit().putString("token", token1).apply()
                        prefs.edit().putString("usuario", usuario).apply()
                        //Guardo el token para mantener iniciada la sesion
                        TokenManager.saveToken(applicationContext, token1)
                        // Guardo el usuario
                        val dbHelper = DatabaseHelper(applicationContext)
                        dbHelper.saveLoginUser(loginUser)
                        val intent = Intent(this@Login, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@Login, "Usuario o contraseña invalidos", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<Usuario>, t: Throwable) {
                    Toast.makeText(this@Login, "Esta iniciando sesion sin conexión", Toast.LENGTH_SHORT).show()
                    iniciarsesion()
                }
            })
        }


    private fun iniciarsesion() {
        val usuario = user?.text.toString()
    val contra = pass?.text.toString()
    val loginUser = LoginUser(usuario, contra)

    // Verificar si existen datos de usuario en la base de datos SQLite
    val dbHelper = DatabaseHelper(applicationContext)
    val userList = dbHelper.allData

    // Buscar el usuario que coincida con las credenciales ingresadas
    val matchingUser = userList.find { it.username == usuario && it.password == contra }

    if (matchingUser != null) {
        // Usuario encontrado, cargar los datos de usuario y establecer el estado de inicio de sesión
        //Singleton.instance.token = matchingUser.token
        Singleton.instance.user = matchingUser.username

        val intent = Intent(this@Login, MainActivity::class.java)
        startActivity(intent)
    } else {
        // No se encontró ningún usuario que coincida con las credenciales ingresadas, mostrar mensaje de error
        Toast.makeText(this@Login, "Credenciales de inicio de sesión inválidas", Toast.LENGTH_SHORT).show()
    }
    }
}

