package com.example.tugasakhir;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tugasakhir.adapter.DiskonDetailAdapter;
import com.example.tugasakhir.adapter.TanggalDetailAdapter;
import com.example.tugasakhir.model.DiskonDetail;
import com.example.tugasakhir.model.TanggalDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class storage_detail extends AppCompatActivity {
    private TextView id, nama, harga, beli, jumlah;
    private RecyclerView recyclerDetailBarang, recyclerDetailDiskon;
    private String id_barang, nama_barang, harga_barang, beli_barang, foto_barang;
    private ImageView foto;
    private Button edit, hapus;
    private FirebaseFirestore db;
    private List<TanggalDetail> tanggalDetailList = new ArrayList<>();
    private List<DiskonDetail> diskonDetailList = new ArrayList<>();
    private TanggalDetailAdapter tanggalDetailAdapter;
    private DiskonDetailAdapter diskonDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_detail);

        id = (TextView) findViewById(R.id.idBarangBD);
        nama = (TextView) findViewById(R.id.namaBarangBD);
        harga = (TextView) findViewById(R.id.hargaBarangBD);
        beli = (TextView) findViewById(R.id.beliBarangBD);
        jumlah = (TextView) findViewById(R.id.stokBarangBD);
        foto = (ImageView) findViewById(R.id.fotoBarangBD);
        hapus = (Button) findViewById(R.id.hapusBarangBD);
        edit = (Button) findViewById(R.id.editBarangBD);
        recyclerDetailBarang = findViewById(R.id.itemDetailBarangBD);
        recyclerDetailDiskon = findViewById(R.id.itemDetailDiskonBD);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();
        id_barang = intent.getStringExtra("Id Barang");
        nama_barang = intent.getStringExtra("Nama Barang");
        harga_barang = intent.getStringExtra("Harga Barang");
        beli_barang = intent.getStringExtra("Harga Beli");
        foto_barang = intent.getStringExtra("Foto Barang");

        id.setText(id_barang);
        nama.setText(nama_barang);
        harga.setText(harga_barang);
        beli.setText(beli_barang);
        Glide.with(storage_detail.this).load(foto_barang).into(foto);
        ambilDataStok(id_barang, jumlah);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL, false);
        recyclerDetailBarang.setLayoutManager(layoutManager);
        tanggalDetailAdapter = new TanggalDetailAdapter(storage_detail.this, tanggalDetailList);
        recyclerDetailBarang.setAdapter(tanggalDetailAdapter);

        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL, false);
        recyclerDetailDiskon.setLayoutManager(layoutManager2);
        diskonDetailAdapter = new DiskonDetailAdapter(storage_detail.this, diskonDetailList);
        recyclerDetailDiskon.setAdapter(diskonDetailAdapter);


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(storage_detail.this, storageEdit.class);
                intent.putExtra("id", id_barang);
                intent.putExtra("Nama Barang", nama_barang);
                intent.putExtra("Harga Barang", harga_barang);
                intent.putExtra("Harga Beli", beli_barang);
                intent.putExtra("Foto Barang", foto_barang);
                startActivity(intent);
            }
        });
        hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hapusBarang(id_barang, foto_barang);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ambilDataTanggal(id_barang);
        ambilDataDiskon(id_barang);
    }

    private void ambilDataDiskon(String id_barang) {
        db.collection("detail_diskon")
                .whereEqualTo("Id Barang",id_barang)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        diskonDetailList.clear();
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                DiskonDetail diskonDetail = new DiskonDetail(document.getId(),
                                        document.getString("Jumlah Pembelian"),
                                        document.getString("Diskon"));
                                diskonDetailList.add(diskonDetail);
                            }
                            diskonDetailAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(storage_detail.this, "Data Stok Gagal Diambil !",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void ambilDataTanggal(String id) {
        db.collection("detail_kedaluarsa")
                .whereEqualTo("Id Barang", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        tanggalDetailList.clear();
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                TanggalDetail tanggalDetail = new TanggalDetail(document.getString("Tanggal Kedaluarsa"),
                                        document.getString("Jumlah Barang"),
                                        document.getId());
                                tanggalDetailList.add(tanggalDetail);
                            }
                            tanggalDetailAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(storage_detail.this, "Data Stok Gagal Diambil !",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private int totalStok = 0;
    private void ambilDataStok(String id_barang, TextView view) {
        db.collection("detail_kedaluarsa")
                .whereEqualTo("Id Barang", id_barang)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                String stok = document.getString("Jumlah Barang");
                                totalStok = totalStok + Integer.parseInt(stok);
                                view.setText(""+totalStok);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(storage_detail.this, "Data Stok Gagal Diambil !",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private  void  hapusBarang(String id, String fotoBarang){
        db.collection("barang").document(id)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            if (fotoBarang==null){
                                db.collection("detail_kedaluarsa")
                                        .whereEqualTo("Id Barang", id)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()){
                                                    for(QueryDocumentSnapshot document : task.getResult()){
                                                        db.collection("detail tanggal").document(document.getId()).delete();
                                                    }
                                                }
                                            }
                                        });
                                db.collection("detail_diskon")
                                        .whereEqualTo("Id Barang", id)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()){
                                                    for(QueryDocumentSnapshot document : task.getResult()){
                                                        db.collection("detail_diskon").document(document.getId()).delete();
                                                    }
                                                }
                                            }
                                        });
                                Toast.makeText(storage_detail.this,"Barang Berhasil Dihapus !",
                                        Toast.LENGTH_SHORT).show();
                            }else{
                                FirebaseStorage.getInstance().getReferenceFromUrl(fotoBarang).delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                            }
                                        });
                                Toast.makeText(storage_detail.this,"Barang Berhasil Dihapus !",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(storage_detail.this,"Barang Gagal Dihapus !",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
