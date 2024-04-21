package com.example.entrevistas;

import static android.app.Activity.RESULT_OK;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pm1e3dr.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class CrearEntrevista extends DialogFragment {
    Button btnGuardar;
    EditText orden, descripcion, periodista, fecha;
    ImageView Img;
    FloatingActionButton btnGrabarAudio;

    FirebaseFirestore mfirestore;
    String currentPhotoPath;
    private static final int Peticion_GrabarAudio = 104;
    private static final int Peticion_ElegirAudio = 105;

    // Agrega estas variables al principio de tu clase
    private static final String AUDIO_STORAGE_PATH = "entrevistaAudio/";
    private Uri audioFileUri;
    private MediaPlayer mediaPlayer;

    private static final int REQUEST_PERMISSION_CODE = 1;

    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    static final int Peticion_ElegirGaleria = 103;

    static final int Peticion_AccesoCamara = 101;
    static final int Peticion_TomarFoto = 102;
    private String audioFilePath;
    String id_entrevista;
    private StorageReference storageRef;

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
        StorageReference storageReference;
        String storage_dir = "entrevista/*";
        storageRef = FirebaseStorage.getInstance().getReference(); // Agrega esta línea


        orden = v.findViewById(R.id.edtOrden);
        descripcion = v.findViewById(R.id.edtDescripcion);
        periodista = v.findViewById(R.id.edtPeriodista);
        fecha=  v.findViewById(R.id.edtFecha);
        btnGuardar =  v.findViewById(R.id.btnGuardar);
        Img =  v.findViewById(R.id.imageView);
        btnGrabarAudio = v.findViewById(R.id.floatingActionButton);
        btnGrabarAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRecording) {
                    startRecording();
                } else {
                    stopRecording();
                }
            }
        });
        Img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MostrarDialogoOpciones();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String Orden = orden.getText().toString().trim();
                    String Descrip = descripcion.getText().toString().trim();
                    String Periodista = periodista.getText().toString().trim();
                    String Fecha = fecha.getText().toString().trim();

                    if (Orden.isEmpty() || Descrip.isEmpty() || Periodista.isEmpty() || Fecha.isEmpty()) {
                        Toast.makeText(getContext(), "Debe llenar todos los campos", Toast.LENGTH_LONG).show();
                    } else {
                        // Llama al método para subir la imagen a Firebase Storage
                        subirImagenAFirebaseStorage();
                    }
                }
        });

        return v;
    }
    private void startRecording() {
        // Aquí deberías implementar la lógica para iniciar la grabación de audio.
        // Puedes usar la clase MediaRecorder para esto.

        // Asegúrate de solicitar los permisos necesarios para la grabación de audio.

        // Después de iniciar la grabación, actualiza isRecording a true.
        isRecording = true;
    }

    private void stopRecording() {
        // Aquí deberías implementar la lógica para detener la grabación de audio.
        // Puedes usar la clase MediaRecorder para esto.

        // Después de detener la grabación, actualiza isRecording a false.

        // Sube el archivo de audio a Firebase Storage.
        subirAudioAFirebaseStorage();
    }
    private void subirAudioAFirebaseStorage() {
        if (audioFileUri != null) {
            StorageReference audioRef = storageRef.child(AUDIO_STORAGE_PATH + System.currentTimeMillis() + ".3gp");

            try {
                FileInputStream stream = new FileInputStream(new File(audioFileUri.getPath()));
                UploadTask uploadTask = audioRef.putStream(stream);

                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    // El audio se ha subido exitosamente
                    // Puedes obtener la URL de descarga
                    audioRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String audioUrl = uri.toString();

                        // Ahora puedes usar la URL de descarga en tu Firestore
                        actualizarDocumentoConAudio(audioUrl);
                    });

                    Toast.makeText(getContext(), "Audio subido exitosamente", Toast.LENGTH_LONG).show();
                }).addOnFailureListener(exception -> {
                    // Si ocurre un error durante la subida
                    Toast.makeText(getContext(), "Error al subir el audio", Toast.LENGTH_LONG).show();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void actualizarDocumentoConAudio(String audioUrl) {
        // Aquí puedes actualizar tu documento en Firestore para almacenar la URL del audio
        // y otros campos relacionados con la entrevista.
    }


    private void actualizarDatos(String Orden, String Descrip, String Periodista, String Fecha) {
        Map<String, Object> map = new HashMap<>();
        map.put("Orden", Orden);
        map.put("Descripcion", Descrip);
        map.put("Periodista", Periodista);
        map.put("Fecha", Fecha);


        mfirestore.collection("entrevista").document(id_entrevista).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
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
        mfirestore.collection("entrevista").document(id_entrevista).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String nOrden = documentSnapshot.getString("Orden");
                String nDescrip = documentSnapshot.getString("Descripcion");
                String nPeriodista = documentSnapshot.getString("Periodista");
                String nFecha = documentSnapshot.getString("Fecha");
                orden.setText(nOrden);
                descripcion.setText(nDescrip);
                periodista.setText(nPeriodista);
                fecha.setText(nFecha);



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error al obtener datos", Toast.LENGTH_LONG).show();
                getDialog().dismiss();
            }
        });
    }

    private void Permisos() {
        // Metodo para obtener los permisos requeridos de la aplicacion
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, Peticion_AccesoCamara);
        } else {
            dispatchTakePictureIntent();
            //TomarFoto();
        }
    }


    private void MostrarDialogoOpciones() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Elige una opción");
        String[] opciones = {"Tomar foto", "Elegir de la galería"};
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    // Tomar foto
                    Permisos();
                } else {
                    // Abrir galería
                    AbrirGaleria();
                }
            }
        });
        builder.show();
    }

    private void AbrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Peticion_ElegirGaleria);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Peticion_AccesoCamara) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Lógica cuando se concede el permiso
            } else {
                Toast.makeText(getContext(), "Se necesita permiso de la cámara", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Peticion_TomarFoto && resultCode == RESULT_OK) {
            try {
                File foto = new File(currentPhotoPath);
                Img.setImageURI(Uri.fromFile(foto));

                // No llames a subirImagenAFirebaseStorage() aquí
            } catch (Exception ex) {
                ex.toString();
            }
        } else if (requestCode == Peticion_ElegirGaleria && resultCode == RESULT_OK) {
            // Elegir de la galería
            if (data != null) {
                Uri selectedImage = data.getData();
                Img.setImageURI(selectedImage);
            }
        }
    }



    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Asegúrate de que haya una actividad de cámara para manejar el intento
        if (takePictureIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            // Crea el archivo donde debería ir la foto
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.toString();
            }
            // Continúa solo si el archivo se creó correctamente
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(requireContext(),
                        "com.example.pm1e3dr.fileprovider",
                        photoFile);
              //  takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                //  startActivityForResult(takePictureIntent, Peticion_TomarFoto);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, Peticion_TomarFoto);

            }
        }
    }
    private void subirImagenAFirebaseStorage() {
        Uri file = Uri.fromFile(new File(currentPhotoPath));
        StorageReference imagenRef = storageRef.child("entrevista/" + file.getLastPathSegment());

        imagenRef.putFile(file)
                .addOnSuccessListener(taskSnapshot -> {
                    // La imagen se ha subido exitosamente
                    // Puedes obtener la URL de descarga
                    imagenRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String url = uri.toString();

                        // Ahora puedes usar la URL de descarga en tu Firestore
                        actualizarDocumentoConURL(url);
                    });

                    Toast.makeText(getContext(), "Imagen subida exitosamente", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(exception -> {
                    // Si ocurre un error durante la subida
                    Toast.makeText(getContext(), "Error al subir la imagen", Toast.LENGTH_LONG).show();
                });
    }

    private void actualizarDocumentoConURL(String url) {
        String Orden = orden.getText().toString().trim();
        String Descrip = descripcion.getText().toString().trim();
        String Periodista = periodista.getText().toString().trim();
        String Fecha = fecha.getText().toString().trim();

        Map<String, Object> map = new HashMap<>();
        map.put("Orden", Orden);
        map.put("Descripcion", Descrip);
        map.put("Periodista", Periodista);
        map.put("Fecha", Fecha);
        map.put("FotoURL", url); // Agrega la URL de la foto a Firestore

        if (Orden.isEmpty() || Descrip.isEmpty() || Periodista.isEmpty() || Fecha.isEmpty()) {
            // Si es una nueva entrada, utiliza el método add
            Toast.makeText(getContext(), "Llene todos los campos por favor", Toast.LENGTH_LONG).show();
        } else {
            mfirestore.collection("entrevista")
                    .add(map)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Datos ingresados correctamente", Toast.LENGTH_LONG).show();
                        getDialog().dismiss();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error al ingresar datos", Toast.LENGTH_LONG).show();
                    });
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Asegúrate de que el directorio exista
        if (storageDir == null || !storageDir.exists()) {
            storageDir.mkdirs();
        }

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Guarda la ruta del archivo para su uso con intents ACTION_VIEW
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


}