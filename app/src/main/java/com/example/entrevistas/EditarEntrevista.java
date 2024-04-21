package com.example.entrevistas;

import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.HashMap;
import java.util.Map;


public class EditarEntrevista extends DialogFragment {
    Button btnGuardar;
    EditText orden, descripcion, periodista, fecha;
    ImageView Img;
    FloatingActionButton btnGrabarAudio;

    FirebaseFirestore mfirestore;
    String currentPhotoPath;

    private static final int REQUEST_PERMISSION_CODE = 1;

    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    static final int Peticion_ElegirGaleria = 103;

    static final int Peticion_AccesoCamara = 101;
    static final int Peticion_TomarFoto = 102;
    private String audioFilePath;
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
        View v = inflater.inflate(R.layout.fragment_crear_entrevista, container, false);

        Intent intent = getActivity().getIntent();
        String id = intent.getStringExtra("id_entrevista");
        mfirestore = FirebaseFirestore.getInstance();

        orden = v.findViewById(R.id.edtOrden);
        descripcion = v.findViewById(R.id.edtDescripcion);
        periodista = v.findViewById(R.id.edtPeriodista);
        fecha=  v.findViewById(R.id.edtFecha);
        btnGuardar =  v.findViewById(R.id.btnGuardar);
        Img =  v.findViewById(R.id.imageView);
        btnGrabarAudio = v.findViewById(R.id.floatingActionButton);

         Img.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 MostrarDialosoOpciones();
             }
         });
        if(id_entrevista != null || id.equals("")){
            btnGuardar.setText("Actualizar Entrevista");
            obtenerEntrevista();
            btnGuardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String Orden = orden.getText().toString().trim();
                    String Descrip = descripcion.getText().toString().trim();
                    String Periodista = periodista.getText().toString().trim();
                    String Fecha = fecha.getText().toString().trim();
                    if (Orden.isEmpty() || Descrip.isEmpty() || Periodista.isEmpty() || Fecha.isEmpty()) {
                        Toast.makeText(getContext(),
                                "Debe llenar todos los campos", Toast.LENGTH_LONG).show();
                    } else {
                        actualizarDatos(Orden, Descrip, Periodista, Fecha);
                    }
                }
            });

        }

        return v;
    }

    private void MostrarDialosoOpciones() {
    }

    private void actualizarDatos(String Orden, String Descrip, String Periodista, String Fecha) {
        Map<String, Object> map = new HashMap<>();
        map.put("Orden", Orden);
        map.put("Descripcion", Descrip);
        map.put("Periodista", Periodista);
        map.put("Fecha", Fecha);


        mfirestore.collection("Entrevista").document(id_entrevista).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getContext(), "Datos actualizados correctamente", Toast.LENGTH_LONG).show();
                getDialog().dismiss();
                //finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_LONG).show();
                getDialog().dismiss();
            }
        });
    }

    private void obtenerEntrevista(){
        mfirestore.collection("Entrevista").document(id_entrevista).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
                    // Si la URL de la imagen es nula o vacía, puedes manejarlo aquí (por ejemplo, establecer una imagen de marcador de posición)
                    // Picasso.get().load(R.drawable.placeholder_image).into(Img);
                }



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error al obtener datos", Toast.LENGTH_LONG).show();
                getDialog().dismiss();
            }
        });
    }



}