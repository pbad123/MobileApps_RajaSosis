package com.example.tugasakhir;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;


public class home extends AppCompatActivity {
    private ImageView barangH, kasirH, penjualanH, userH, diskon;
    private ImageButton logout;
    private TextView namaAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        barangH=(ImageView)findViewById(R.id.barang);
        kasirH=(ImageView)findViewById(R.id.kasir);
        penjualanH=(ImageView)findViewById(R.id.penjualan);
        userH=(ImageView)findViewById(R.id.user);
        diskon=(ImageView)findViewById(R.id.diskon);
        logout=(ImageButton) findViewById(R.id.logout);
        namaAdmin=(TextView)findViewById(R.id.namaAdmin);

        barangH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(home.this, storage.class);
                startActivity(i);
            }
        });
        kasirH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(home.this, produk.class);
                startActivity(i);
            }
        });
        penjualanH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(home.this, penjualan.class);
                startActivity(i);
            }
        });
        userH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(home.this, infoUserAdmin.class);
                startActivity(i);
            }
        });
        diskon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(home.this, diskon.class);
                startActivity(i);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(home.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        ambilNamaAdmin();
    }

    private void ambilNamaAdmin() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String id = auth.getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference reference = db.collection("userInfo").document(id);
        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            String nama = document.getString("Nama User");
                            String upper = nama.substring(0,1).toUpperCase() + nama.substring(1).toLowerCase();
                            namaAdmin.setText("Hai, Admin "+upper+" !");
                        }
                    }
                });
    }
}
