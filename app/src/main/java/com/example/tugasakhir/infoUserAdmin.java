 package com.example.tugasakhir;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tugasakhir.adapter.barangAdapter;
import com.example.tugasakhir.adapter.userAdapter;
import com.example.tugasakhir.model.barang;
import com.example.tugasakhir.model.user;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

 public class infoUserAdmin extends AppCompatActivity {
    private RecyclerView recyclerUserAdmin;
     private FirebaseFirestore db;
     private List<user> userList = new ArrayList<>();
     private userAdapter UserAdapter;
     private ProgressBar progressBar;
     private Button tambahUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_user_admin);

        db = FirebaseFirestore.getInstance();
        recyclerUserAdmin = findViewById(R.id.itemInfoUser);
        tambahUser = (Button) findViewById(R.id.tambahUserIUA);
        progressBar = findViewById(R.id.loadingUA);
        UserAdapter = new userAdapter(infoUserAdmin.this, userList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL, false);
        recyclerUserAdmin.setLayoutManager(layoutManager);
        recyclerUserAdmin.setAdapter(UserAdapter);

        tambahUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpanDataUser();
            }
        });
    }

     @Override
     protected void onStart() {
         super.onStart();
         ambilUser();
     }

     private void simpanDataUser() {
         View view = LayoutInflater.from(infoUserAdmin.this).inflate(R.layout.dialog_tambah_user, null);
         EditText nama, nomor, email;
         Button simpan;
         FirebaseFirestore db;

         nama = view.findViewById(R.id.tambahNamaU);
         nomor = view.findViewById(R.id.tambahNomorU);
         email = view.findViewById(R.id.tambahEmailU);
         simpan = view.findViewById(R.id.simpanDataUser);

         db = FirebaseFirestore.getInstance();

         AlertDialog.Builder builder = new AlertDialog.Builder(infoUserAdmin.this);
         builder.setView(view);
         AlertDialog dialog = builder.create();
         dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
         dialog.show();

         simpan.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 progressBar.setVisibility(View.VISIBLE);
                 if(nama.getText().toString().length()>0
                         && email.getText().toString().length()>0
                         && nomor.getText().toString().length()>0 ){

                     Map<String, Object> userInfo = new HashMap<>();
                     userInfo.put("Nama User", nama.getText().toString());
                     userInfo.put("Email", email.getText().toString());
                     userInfo.put("Nomor Telepon", nomor.getText().toString());
                     //validasi user sebagai user biasa
                     userInfo.put("isUser", "1");

                     db.collection("userInfo")
                             .add(userInfo)
                             .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                 @SuppressLint("NotifyDataSetChanged")
                                 @Override
                                 public void onComplete(@NonNull Task<DocumentReference> task) {
                                     Toast.makeText(infoUserAdmin.this, "Data Member Berhasil Di Tambahkan !",
                                             Toast.LENGTH_SHORT).show();
                                     dialog.dismiss();
                                     UserAdapter.notifyDataSetChanged();
                                     ambilUser();
                                     progressBar.setVisibility(View.GONE);
                                 }
                             })
                             .addOnFailureListener(new OnFailureListener() {
                                 @Override
                                 public void onFailure(@NonNull Exception e) {
                                     Toast.makeText(infoUserAdmin.this, "Data Member Gagal Di Tambahkan !",
                                             Toast.LENGTH_SHORT).show();
                                 }
                             });
                 }else{
                     Toast.makeText(infoUserAdmin.this, "Data Member Belum Lengkap !",
                             Toast.LENGTH_SHORT).show();
                 }

             }
         });
     }

     private void ambilUser() {
         progressBar.setVisibility(View.VISIBLE);
         db.collection("userInfo")
                 .get()
                 .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                     @SuppressLint("NotifyDataSetChanged")
                     @Override
                     public void onComplete(@NonNull Task<QuerySnapshot> task) {
                         userList.clear();
                         if (task.isSuccessful()) {
                             for (QueryDocumentSnapshot document : task.getResult()) {
                                 user User = new user(document.getId(),
                                         document.getString("Email"),
                                         document.getString("Nama User"),
                                         document.getString("Nomor Telepon"));
                                 if(document.getString("isAdmin")!=null){
                                      User.setStatus("Admin");
                                 }else if(document.getString("isUser")!=null){
                                     User.setStatus("User");
                                 }
                                 userList.add(User);
                             }
                             UserAdapter.notifyDataSetChanged();
                             progressBar.setVisibility(View.GONE);
                         } else {
                             Toast.makeText(infoUserAdmin.this, "Data Gagal Diambil !",
                                     Toast.LENGTH_SHORT).show();
                             progressBar.setVisibility(View.GONE);
                         }
                     }
                 });
     }
 }
