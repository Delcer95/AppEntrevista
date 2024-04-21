package com.example.entrevistas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.pm1e3dr.R;

public class ActivityInicial extends AppCompatActivity {

    Button btnNueva, btnListar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicial);


        btnNueva = (Button) findViewById(R.id.btnAgregar);
        btnListar = (Button) findViewById(R.id.btnListar);

        btnNueva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CrearEntrevista C = new CrearEntrevista();
                C.show(getSupportFragmentManager(), "Crear una entrevista");
                //Intent intent = new Intent(getApplicationContext(), CrearEntrevista.class);
            }
        });

        btnListar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityListE.class);
                startActivity(intent);
            }
        });
    }
}