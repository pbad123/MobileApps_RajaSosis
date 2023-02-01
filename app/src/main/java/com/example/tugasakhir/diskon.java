package com.example.tugasakhir;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class diskon extends AppCompatActivity {
    private Spinner spinnerBarang;
    private TextView barang_id;
    private EditText jumlah, banyakDiskon;
    private Button simpan;
    private List <String> listBarang = new ArrayList<>();
    private ArrayAdapter <String> adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diskon);
        spinnerBarang = (Spinner) findViewById(R.id.spinnerNamaBarang);
        barang_id = (TextView) findViewById(R.id.idBarangDiskon);
        jumlah = (EditText) findViewById(R.id.jumlahBarangDiskon);
        banyakDiskon = (EditText) findViewById(R.id.banyakDiskon);
        simpan = (Button) findViewById(R.id.btnSimpanDiskon);

        db = FirebaseFirestore.getInstance();

        spinnerBarang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                String namaBarang = adapterView.getItemAtPosition(pos).toString();
                if(!spinnerBarang.getSelectedItem().toString().equals("Pilih Barang")){
                    ambilIdBarang(namaBarang);
                }else{
                    barang_id.setText("");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        simpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!barang_id.equals("") &&
                !jumlah.equals("") &&
                !banyakDiskon.equals("")){
                    simpanDataDiskon();
                }else{
                    Toast.makeText(diskon.this, "Data Diatas Belum Lengkap Terisi !",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void simpanDataDiskon() {
        String id = barang_id.getText().toString();
        String banyakBarang = jumlah.getText().toString();
        String diskon = banyakDiskon.getText().toString();

        HashMap<String, Object> detailDiskon = new HashMap<>();
        detailDiskon.put("Id Barang", id);
        detailDiskon.put("Jumlah Pembelian", banyakBarang);
        detailDiskon.put("Diskon", diskon);

        db.collection("detail_diskon")
                .add(detailDiskon)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(diskon.this, "Data Diskon Berhasil Disimpan !",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent( diskon.this, storage.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(diskon.this, "Data Diskon Gagal Disimpan !",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void ambilIdBarang(String namaBarang) {
        db.collection("barang")
                .whereEqualTo("Nama Barang", namaBarang)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                barang_id.setText(document.getId());
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(diskon.this, "Data Id Barang Gagal Diambil !",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        listBarang.add("Pilih Barang");
        ambiDataBarang();
        adapter = new ArrayAdapter<>(diskon.this, R.layout.text_spinner, listBarang);
        adapter.setDropDownViewResource(R.layout.text_spinner);
        spinnerBarang.setPrompt("Nama Member");
        spinnerBarang.setAdapter(adapter);
    }

    private void ambiDataBarang() {
        db.collection("barang")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                String barang = document.getString("Nama Barang");
                                listBarang.add(barang);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(diskon.this, "Data Barang Gagal Diambil !",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
