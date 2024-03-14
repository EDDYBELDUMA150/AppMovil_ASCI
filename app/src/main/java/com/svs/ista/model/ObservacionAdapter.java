package com.svs.ista.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.svs.ista.ActivityObservaciones;
import com.svs.ista.R;
import com.svs.ista.service.ApiClient;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ObservacionAdapter extends RecyclerView.Adapter<ObservacionAdapter.ObservacionViewHolder> {
    private final String token;
    private final String usuario;
    private final Integer id;
    private final Context mContext;
    private final List<Observacion> mObservaciones;
    private final ApiClient mRetrofitService;

    public ObservacionAdapter(Context context, List<Observacion> observaciones, ApiClient retrofitService) {
        mContext = context;
        mObservaciones = observaciones;
        mRetrofitService = retrofitService;
        SharedPreferences prefs = context.getSharedPreferences("sesiona", Context.MODE_PRIVATE);
        SharedPreferences acti = context.getSharedPreferences("acti", Context.MODE_PRIVATE);
        this.token = prefs.getString("token", null);
        this.usuario=prefs.getString("usuario", null);
        this.id=acti.getInt("idactividad",0);
    }

    @NonNull
    @Override
    public ObservacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.listasimple, parent, false);
        return new ObservacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ObservacionViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Observacion observacion = mObservaciones.get(position);
        holder.tvDescripcion.setText(observacion.getObservacion());
        holder.tvid.setText(String.valueOf(observacion.getId_observacion()));

        //flow menu
        holder.flowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.flowMenu);
                popupMenu.inflate(R.menu.flow_menu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.editar_menu:
                                // Obtener la observación seleccionada
                                Bundle bundle=new Bundle();
                                bundle.putInt("id_observacion", observacion.getId_observacion());
                                bundle.putString("observacion", observacion.getObservacion());
                                Intent intent=new Intent(mContext, ActivityObservaciones.class);
                                intent.putExtra("userdata",bundle);
                                mContext.startActivity(intent);

                                return true;
                            case R.id.eliminar_menu:
                                mRetrofitService.eliminarObservacion("Bearer " + token, observacion.getId_observacion()).enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                        if (response.isSuccessful()) {
                                            Toast.makeText(mContext, "Observación eliminada", Toast.LENGTH_SHORT).show();
                                            int removedPosition = holder.getAdapterPosition();
                                            mObservaciones.remove(removedPosition);
                                            notifyItemRemoved(removedPosition);
                                        } else {
                                            Toast.makeText(mContext, "Error al eliminar observación", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<Void> call, Throwable t) {
                                        Toast.makeText(mContext, "Error "+t, Toast.LENGTH_SHORT).show();
                                        t.printStackTrace();
                                    }
                                });
                                return true;

                            default:
                                return false;
                        }
                    }
                });


                //Mostrar menu
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mObservaciones.size();
    }

    public static class ObservacionViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescripcion;
        EditText obse;
        TextView tvid;
        ImageButton flowMenu;

        public ObservacionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescripcion = itemView.findViewById(R.id.txt_observa);
            tvid=itemView.findViewById(R.id.tvidobservacion);
            obse=itemView.findViewById(R.id.txtobservacion);
            flowMenu = itemView.findViewById(R.id.flowmenu);
        }
    }

    public void cargarObservaciones() {
        System.out.println("Username: " + usuario);
        System.out.println("ID: " + id);
        mRetrofitService.obtenerObs("Bearer " + token, usuario, id)
                .enqueue(new Callback<List<Observacion>>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NonNull Call<List<Observacion>> call, @NonNull Response<List<Observacion>> response) {
                        if (response.isSuccessful()) {
                            mObservaciones.clear(); // Limpiar la lista actual
                            assert response.body() != null;
                            mObservaciones.addAll(response.body()); // Agregar los nuevos datos recibidos desde el servidor
                            notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
                        } else {
                            Toast.makeText(mContext, "No se pudo cargar las observaciones", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Observacion>> call, Throwable t) {
                        Toast.makeText(mContext, "Error al cargar las observaciones: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}