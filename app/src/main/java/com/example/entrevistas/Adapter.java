package com.example.entrevistas;

import androidx.annotation.NonNull;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.entrevistas.Models.Entrevista;
import com.example.pm1e3dr.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class Adapter extends FirestoreRecyclerAdapter<Entrevista, Adapter.ViewHolder> {
    private OnItemClickListener onItemClickListener;
    private FirebaseFirestore mfirebastore = FirebaseFirestore.getInstance();
    Activity activity;
    FragmentManager fm;
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public Adapter(@NonNull FirestoreRecyclerOptions<Entrevista> options, Activity actitivy, FragmentManager fm) {
        super(options);
        this.activity = actitivy;
        this.fm = fm;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Entrevista model) {
        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(holder.getAdapterPosition());
        final String id = documentSnapshot.getId();
        holder.orden.setText(model.getOrden());
        holder.descripcion.setText(model.getDesrip());
        holder.periodista.setText(model.getPeriodista());
        holder.fecha.setText(model.getFecha());
        String nFoto = model.getFotoURL();
        if (nFoto != null && !nFoto.isEmpty()) {
            Picasso.get().load(nFoto).into(holder.ImagenEdit);
        } else {
            Picasso.get().load(R.drawable.user).into(holder.ImagenEdit);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(documentSnapshot, holder.getAdapterPosition());
                }
            }
        });
        holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eliminar(id);
            }
        });


        holder.btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, CrearEntrevista.class);
                intent.putExtra("id_entrevista", id);
               // activity.startActivity(intent);

               EditarEntrevista C = new EditarEntrevista();
                Bundle bundle = new Bundle();
                bundle.putString("id_entrevista", id);
                C.setArguments(bundle);
                C.show(fm, "Nuevo Fragmento Actualilzar");

            }
        });
    }


    private void eliminar(String id) {
        mfirebastore.collection("entrevista").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(activity, "Datos eliminados correctamente", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "Error al eliminar", Toast.LENGTH_LONG).show();
            }
        });
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_adapter, parent, false);

        return new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orden, descripcion, periodista, fecha;
        ImageView btnEliminar, btnActualizar, ImagenEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orden = itemView.findViewById(R.id.txtOrden);
            descripcion = itemView.findViewById(R.id.txtDescripcion);
            periodista= itemView.findViewById(R.id.txtPeriodista);
            fecha = itemView.findViewById(R.id.txtFecha);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);

            btnActualizar = itemView.findViewById(R.id.btnActualizar);
            ImagenEdit = itemView.findViewById(R.id.ImgGuardada);
        }
    }
}
