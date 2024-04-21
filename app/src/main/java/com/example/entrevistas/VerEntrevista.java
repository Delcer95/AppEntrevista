package com.example.entrevistas;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.pm1e3dr.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;


public class VerEntrevista extends DialogFragment {
    TextView orden, descripcion, periodista, fecha;
    ImageView Img;
    FloatingActionButton btnGrabarAudio;

    FirebaseFirestore mfirestore;

    private boolean isRecording = false;
    String id_entrevista;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            id_entrevista = getArguments().getString("id_entrevista");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.ver_entrevista, container, false);

        Intent intent = getActivity().getIntent();
        String id = intent.getStringExtra("id_entrevista");
        mfirestore = FirebaseFirestore.getInstance();

        orden = v.findViewById(R.id.edtOrden);
        descripcion = v.findViewById(R.id.edtDescripcion);
        periodista = v.findViewById(R.id.edtPeriodista);
        fecha=  v.findViewById(R.id.edtFecha);
        Img =  v.findViewById(R.id.imageView);
        btnGrabarAudio = v.findViewById(R.id.floatingActionButton);

        mfirestore.collection("entrevista").document(id_entrevista).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String nOrden = documentSnapshot.getString("Orden");
                String nDescrip = documentSnapshot.getString("Descripcion");
                String nPeriodista = documentSnapshot.getString("Periodista");
                String nFecha = documentSnapshot.getString("Fecha");
                String nFoto = documentSnapshot.getString("FotoURL");
                orden.setText(nOrden);
                descripcion.setText(nDescrip);
                periodista.setText(nPeriodista);
                fecha.setText(nFecha);
                if (nFoto != null && !nFoto.isEmpty()) {
                    Picasso.get().load(nFoto).into(Img);
                } else {
                    Picasso.get().load(R.drawable.user).into(Img);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error al obtener datos", Toast.LENGTH_LONG).show();
                getDialog().dismiss();
            }
        });
        return v;
    }
}