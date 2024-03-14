package com.svs.ista.Basesql;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.svs.ista.model.Actividades;
import com.svs.ista.model.LoginUser;
import com.svs.ista.model.Persona;
import com.svs.ista.model.Usuar;
import com.svs.ista.model.Usuario;
import com.svs.ista.model.Usuarios;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "calidad.db";
    private static final int DATABASE_VERSION = 1;
    //usuario
    private static final String TABLE_NAME = "usuarios";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_PASSWORD = "password";
    // Actividades
    private static final String TABLE_ACTIVIDADES = "actividad";
    private static final String COLUMN_ID = "id_actividad";
    private static final String COLUMN_DESCRIPCION = "descripcion";
    private static final String COLUMN_NOMBRE = "nombre";
    private static final String COLUMN_FECHAI="fecha_inicio";
    private static final String COLUMN_FECHAF="fecha_fin";
    private static final String COLUMN_VISIBLE = "visible";
    private static final String COLUMN_USER = "usuario";
    private static final String TABLE_PERSONAS = "personas";
    //Persona
    private static final String COLUMN_ID_PERSONA = "id_persona";
    private static final String COLUMN_CEDULA = "cedula";
    private static final String COLUMN_PRIMER_NOMBRE = "primer_nombre";
    private static final String COLUMN_SEGUNDO_NOMBRE = "segundo_nombre";
    private static final String COLUMN_PRIMER_APELLIDO = "primer_apellido";
    private static final String COLUMN_SEGUNDO_APELLIDO = "segundo_apellido";
    private static final String COLUMN_CORREO = "correo";
    private static final String COLUMN_DIRECCION = "direccion";
    private static final String COLUMN_CELULAR = "celular";
    private static final String COLUMN_VISIBLE_PER = "visible";
    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_USERNAME + " TEXT PRIMARY KEY," +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(createTableQuery);

        String TableQuery = "CREATE TABLE " + TABLE_ACTIVIDADES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_DESCRIPCION + " TEXT, " +
                COLUMN_NOMBRE + " TEXT, " +
                COLUMN_FECHAI + " TEXT, " +
                COLUMN_FECHAF + " TEXT, " +
                COLUMN_VISIBLE + " INTEGER, " +
                COLUMN_USER + " INTEGER)";
        db.execSQL(TableQuery);

        String create = "CREATE TABLE " + TABLE_PERSONAS + "(" +
                COLUMN_ID_PERSONA + " INTEGER PRIMARY KEY," +
                COLUMN_CEDULA + " TEXT," +
                COLUMN_PRIMER_NOMBRE + " TEXT," +
                COLUMN_SEGUNDO_NOMBRE + " TEXT," +
                COLUMN_PRIMER_APELLIDO + " TEXT," +
                COLUMN_SEGUNDO_APELLIDO + " TEXT," +
                COLUMN_CORREO + " TEXT," +
                COLUMN_DIRECCION + " TEXT," +
                COLUMN_CELULAR + " TEXT," +
                COLUMN_VISIBLE_PER + " INTEGER)";
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public List<LoginUser> getAllData() {
        List<LoginUser> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME));
                @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD));

                LoginUser usuario = new LoginUser();
                usuario.setUsername(username);
                usuario.setPassword(password);

                userList.add(usuario);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return userList;
    }
    public List<Actividades> getAllDataActividad() {
        List<Actividades> actividades = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_ACTIVIDADES;
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                @SuppressLint("Range") String nombre = cursor.getString(cursor.getColumnIndex(COLUMN_NOMBRE));
                @SuppressLint("Range") String descripcion = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPCION));
                @SuppressLint("Range") String fechaInicio = cursor.getString(cursor.getColumnIndex(COLUMN_FECHAI));
                @SuppressLint("Range") String fechaFin = cursor.getString(cursor.getColumnIndex(COLUMN_FECHAF));
                @SuppressLint("Range") boolean visible = cursor.getInt(cursor.getColumnIndex(COLUMN_VISIBLE)) == 1;
                @SuppressLint("Range") int userId = cursor.getInt(cursor.getColumnIndex(COLUMN_USER));

                // Crea un objeto Actividades con los valores obtenidos de la base de datos
                Actividades actividad = new Actividades(id, descripcion, nombre, fechaInicio, fechaFin, visible, new Usuar(userId, "sin","conexion"));

                // Agrega la actividad a la lista
                actividades.add(actividad);
            }
        } finally {
            cursor.close();
        }
        return actividades;
    }


    public void saveLoginUser(LoginUser loginUser) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, loginUser.getUsername());
        values.put(COLUMN_PASSWORD, loginUser.getPassword());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void insertDataActividad(List<Actividades> actividades) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_ACTIVIDADES, null, null);
            for (Actividades actividad : actividades) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_ID, actividad.getId_actividad());
                values.put(COLUMN_NOMBRE, actividad.getNombre());
                values.put(COLUMN_DESCRIPCION, actividad.getDescripcion());
                values.put(COLUMN_FECHAI, actividad.getFecha_inicio());
                values.put(COLUMN_FECHAF,actividad.getFecha_fin());
                values.put(COLUMN_VISIBLE,true);
                values.put(COLUMN_USER,1);
                // Agrega más valores según las propiedades de la actividad que deseas almacenar
                db.insert(TABLE_ACTIVIDADES, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void insertDataPersona(Persona persona) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CEDULA, persona.getCedula());
        values.put(COLUMN_PRIMER_NOMBRE, persona.getPrimerNombre());
        values.put(COLUMN_SEGUNDO_NOMBRE, persona.getSegundoNombre());
        values.put(COLUMN_PRIMER_APELLIDO, persona.getPrimerApellido());
        values.put(COLUMN_SEGUNDO_APELLIDO, persona.getSegundoApellido());
        values.put(COLUMN_CORREO, persona.getCorreo());
        values.put(COLUMN_DIRECCION, persona.getDireccion());
        values.put(COLUMN_CELULAR, persona.getCelular());
        values.put(COLUMN_VISIBLE_PER, true);

        int rowsAffected = db.update(TABLE_PERSONAS, values, COLUMN_ID_PERSONA + "=?", new String[]{String.valueOf(persona.getId())});
        if (rowsAffected == 0) {
            // No se encontró un registro existente con el mismo id_persona, se realiza una inserción
            values.put(COLUMN_ID_PERSONA, persona.getId());
            db.insert(TABLE_PERSONAS, null, values);
            System.out.println("Registro insertado: " + persona.toString());
        }

        db.close();
    }


    public List<Persona> getAllDataPersonas() {
        List<Persona> personas = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PERSONAS, null);
        int count = cursor.getCount();
        System.out.println("Cantidad de registros encontrados: " + count);
        try {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID_PERSONA));
                @SuppressLint("Range") String cedula = cursor.getString(cursor.getColumnIndex(COLUMN_CEDULA));
                @SuppressLint("Range") String primerNombre = cursor.getString(cursor.getColumnIndex(COLUMN_PRIMER_NOMBRE));
                @SuppressLint("Range") String segundoNombre = cursor.getString(cursor.getColumnIndex(COLUMN_SEGUNDO_NOMBRE));
                @SuppressLint("Range") String primerApellido = cursor.getString(cursor.getColumnIndex(COLUMN_PRIMER_APELLIDO));
                @SuppressLint("Range") String segundoApellido = cursor.getString(cursor.getColumnIndex(COLUMN_SEGUNDO_APELLIDO));
                @SuppressLint("Range") String correo = cursor.getString(cursor.getColumnIndex(COLUMN_CORREO));
                @SuppressLint("Range") String direccion = cursor.getString(cursor.getColumnIndex(COLUMN_DIRECCION));
                @SuppressLint("Range") String celular = cursor.getString(cursor.getColumnIndex(COLUMN_CELULAR));
                @SuppressLint("Range") boolean visible = cursor.getInt(cursor.getColumnIndex(COLUMN_VISIBLE_PER)) == 1;

                Persona persona = new Persona(id, cedula, primerNombre, segundoNombre, primerApellido,
                        segundoApellido, correo, direccion, celular, visible);
                personas.add(persona);
            }
        } finally {
            cursor.close();
        }

        return personas;
    }


}
