package com.example.tugasakhir;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tugasakhir.adapter.PenjualanAdapter;
import com.example.tugasakhir.model.Penjualan;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class infoUserDetail extends AppCompatActivity {
    private TextView id, nama, email, nomor;
    private RecyclerView recyclerPenjualan;
    private String id_user, nama_user, email_user, nomor_user;
    private FirebaseFirestore db;
    private PenjualanAdapter penjualanAdapter;
    private List<Penjualan> penjualanList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_user_detail);

        id = (TextView) findViewById(R.id.idMemberIU);
        nama = (TextView) findViewById(R.id.namaUserIU);
        email = (TextView) findViewById(R.id.emailUserIU);
        nomor = (TextView) findViewById(R.id.nomorUI);
        recyclerPenjualan = findViewById(R.id.itemPenjualanIU);

        db = FirebaseFirestore.getInstance();
        penjualanAdapter = new PenjualanAdapter(infoUserDetail.this, penjualanList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL, false);
        recyclerPenjualan.setLayoutManager(layoutManager);
        recyclerPenjualan.setAdapter(penjualanAdapter);

        Intent intent = getIntent();
        id_user = intent.getStringExtra("id_penjualan");
        nama_user = intent.getStringExtra("Nama User");
        email_user = intent.getStringExtra("Email");
        nomor_user = intent.getStringExtra("Nomor Telepon");

        id.setText(id_user);
        nama.setText(nama_user);
        email.setText(email_user);
        nomor.setText(nomor_user);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ambilDataPenjualan(id_user);
    }

    private void ambilDataPenjualan(String id_user) {
        db.collection("penjualan")
                .whereEqualTo("User Id", id_user)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        penjualanList.clear();
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                Penjualan penjualan = new Penjualan(document.getId(),
                                        document.getString("Tanggal"),
                                        document.getString("Total Transaksi"),
                                        document.getString("User Id"),
                                        document.getString("Dibayar"),
                                        document.getString("Kembalian"),
                                        document.getString("Admin Melayani"),
                                        document.getString("Alamat"),
                                        document.getString("Status"));
                                penjualanList.add(penjualan);
                            }
                            penjualanAdapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(infoUserDetail.this, "Data Penjualan Gagal Diambil !",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
