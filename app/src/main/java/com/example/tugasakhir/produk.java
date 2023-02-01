package com.example.tugasakhir;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tugasakhir.adapter.KeranjangBarangAdapter;
import com.example.tugasakhir.adapter.barangAdapter;
import com.example.tugasakhir.adapter.produkAdapter;
import com.example.tugasakhir.model.KeranjangBarang;
import com.example.tugasakhir.model.barang;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class produk extends AppCompatActivity {
    private Toolbar toolbar;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private List<barang> list = new ArrayList<>();
    private produkAdapter ProdukAdapter;
    private ProgressBar progressBar;
    public NotificationBadge badge;
    private FrameLayout cart;
    private EasyDB easyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produk);

        db= FirebaseFirestore.getInstance();
        recyclerView=findViewById(R.id.recycle_view_produk);
        ProdukAdapter = new produkAdapter(produk.this, list);

        badge=findViewById(R.id.notifKeranjang);
        cart=findViewById(R.id.keranjangAppBar);
        toolbar=findViewById(R.id.myToolBar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(produk.this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(produk.this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(ProdukAdapter);
        progressBar=(ProgressBar) findViewById(R.id.loadingS);


        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(produk.this, kasir.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ambilBarang();
        easyDB = EasyDB.init(produk.this, "db_tokoSembako")
                .setTableName("tb_penjualan")
                .setTableName("tb_penjualan")
                .addColumn(new Column("id_item", new String[]{"text", "unique"}))
                .addColumn(new Column("id_barang", new String[]{"text", "not null"}))
                .addColumn(new Column("nama_barang", new String[]{"text", "not null"}))
                .addColumn(new Column("jumlah_beli", new String[]{"text", "not null"}))
                .addColumn(new Column("harga", new String[]{"text", "not null"}))
                .addColumn(new Column("total_harga", new String[]{"text", "not null"}))
                .addColumn(new Column("URL_foto", new String[]{"text", "not null"}))
                .addColumn(new Column("harga_beli", new String[]{"text", "not null"}))
                .addColumn(new Column("tanggal_kedaluarsa", new String[]{"text", "not null"}))
                .addColumn(new Column("diskon", new String[]{"text", "not null"}))
                .doneTableColumn();
        badge.setNumber(easyDB.getAllData().getCount());
    }

    private void ambilBarang() {
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
                            ProdukAdapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(produk.this, "Data Gagal Diambil !",
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
}
