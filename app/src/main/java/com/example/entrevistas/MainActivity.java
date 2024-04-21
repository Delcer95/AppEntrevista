package com.example.entrevistas;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pm1e3dr.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
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
    String url = "https://ejemplo.com/imagen.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mfirestore = FirebaseFirestore.getInstance();

        orden = (EditText) findViewById(R.id.edtOrden);
        descripcion = (EditText) findViewById(R.id.edtDescripcion);
        periodista = (EditText) findViewById(R.id.edtPeriodista);
        fecha= (EditText) findViewById(R.id.edtFecha);
        btnGuardar = (Button)findViewById(R.id.btnGuardar);
        Img = (ImageView)findViewById(R.id.imageView);
        btnGrabarAudio = (FloatingActionButton)findViewById(R.id.floatingActionButton);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Orden = orden.getText().toString().trim();
                String Descrip = descripcion.getText().toString().trim();
                String Periodista = periodista.getText().toString().trim();
                String Fecha = fecha.getText().toString().trim();

                if(Orden.isEmpty() || Descrip.isEmpty() || Periodista.isEmpty() || Fecha.isEmpty()){
                    Toast.makeText(getApplicationContext(),
                            "Debe llenar todos los campos", Toast.LENGTH_LONG).show();
                }else{
                    // Aquí debes ajustar tu estructura de datos según cómo quieras guardar la información en Firestore
                    Map<String, Object> map = new HashMap<>();
                    map.put("Orden", Orden);
                    map.put("Desrip", Descrip);
                    map.put("Periodista", Periodista);
                    map.put("Fecha", Fecha);
                    map.put("FotoURL", url);// Agrega la URL de la foto o el audio a Firestore

                    mfirestore.collection("entrevista").add(map)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(getApplicationContext(), "Datos ingresados correctamente", Toast.LENGTH_LONG).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getApplicationContext(), "Error al ingresar datos", Toast.LENGTH_LONG).show();
                            });
                }

            }
        });
   }

}