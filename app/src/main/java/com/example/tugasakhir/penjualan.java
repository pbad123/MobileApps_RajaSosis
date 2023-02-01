package com.example.tugasakhir;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.tugasakhir.adapter.PenjualanAdapter;
import com.example.tugasakhir.model.Penjualan;
import com.example.tugasakhir.model.barang;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class penjualan extends AppCompatActivity {
    private RecyclerView recyclerPenjualan;
    private PenjualanAdapter penjualanAdapter;
    private List<Penjualan> penjualanList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penjualan);

        recyclerPenjualan = findViewById(R.id.itemPenjualanP);
        db = FirebaseFirestore.getInstance();
        penjualanAdapter = new PenjualanAdapter(penjualan.this, penjualanList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL, false);
        recyclerPenjualan.setLayoutManager(layoutManager);
        recyclerPenjualan.setAdapter(penjualanAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        ambilPenjualan();
    }

    private void ambilPenjualan() {
        db.collection("penjualan")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        penjualanList.clear();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
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
                        } else {
                            Toast.makeText(penjualan.this, "Data Gagal Diambil !",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
