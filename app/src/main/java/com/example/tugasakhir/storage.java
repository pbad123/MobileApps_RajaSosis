package com.example.tugasakhir;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tugasakhir.adapter.barangAdapter;
import com.example.tugasakhir.model.barang;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class storage extends AppCompatActivity {
    private Button edit;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private List<barang> list = new ArrayList<>();
    private barangAdapter BarangAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage);

        db= FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recycle_view);
        edit=(Button) findViewById(R.id.edit);
        BarangAdapter = new barangAdapter(getApplicationContext(), list);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL, false);
        //RecyclerView.ItemDecoration decoration= new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.addItemDecoration(decoration);
        recyclerView.setAdapter(BarangAdapter);
        progressBar=(ProgressBar) findViewById(R.id.loadingS);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(storage.this, storageEdit.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ambilBarang();
    }

    private void ambilBarang (){
        progressBar.setVisibility(View.VISIBLE);
        db.collection("barang")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        list.clear();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                barang Barang= new barang(document.getString("Nama Barang"),
                                        document.getString("Harga Barang"),
                                        document.getString("Harga Beli"),
                                        document.getString("Foto Barang"));
                                Barang.setId(document.getId());
                                list.add(Barang);
                            }
                            BarangAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(storage.this, "Data Gagal Diambil !",
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }


}
