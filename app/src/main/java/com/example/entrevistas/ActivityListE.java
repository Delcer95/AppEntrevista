package com.example.entrevistas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.entrevistas.Models.Entrevista;
import com.example.pm1e3dr.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ActivityListE extends AppCompatActivity {
    RecyclerView mRecycle;
    Adapter mAdapter;
    FirebaseFirestore mFirebasestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_e);
        mFirebasestore = FirebaseFirestore.getInstance();
        mRecycle = findViewById(R.id.RecyclerView);
        mRecycle.setLayoutManager(new LinearLayoutManager(this));
        Query query = mFirebasestore.collection("entrevista");
        FirestoreRecyclerOptions<Entrevista> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Entrevista>
                ().setQuery(query,Entrevista.class).build();
        mAdapter = new Adapter(firestoreRecyclerOptions, this, getSupportFragmentManager());

        mAdapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {
                String documentId = documentSnapshot.getId();
                openVerEntrevistaFragment(documentId);
            }
        });

        mAdapter.notifyDataSetChanged();
        mRecycle.setAdapter(mAdapter);


    }
    private void openVerEntrevistaFragment(String documentId) {
        VerEntrevista verEntrevista = new VerEntrevista();

        Bundle bundle = new Bundle();
        bundle.putString("id_entrevista", documentId);
        verEntrevista.setArguments(bundle);

        verEntrevista.show(getSupportFragmentManager(), "VerEntrevista");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}