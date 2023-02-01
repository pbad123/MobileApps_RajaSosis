package com.example.tugasakhir;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class registerActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button login, register;
    private EditText username, password, passwordConf, nama, notlp;
    private ProgressBar loading;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        nama = (EditText)findViewById(R.id.name);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        passwordConf = (EditText)findViewById(R.id.c_password);
        notlp = (EditText)findViewById(R.id.nomor);
        login = (Button)findViewById(R.id.btn_login);
        register = (Button)findViewById(R.id.btn_regist);
        loading=findViewById(R.id.loading);

        //login
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(registerActivity.this, MainActivity.class);
                startActivity(loginIntent);
                //finish();
            }
        });

        //register
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                loading.setVisibility(View.VISIBLE);
                String email = username.getText().toString();
                String password1 = password.getText().toString();
                String password2 = passwordConf.getText().toString();
                String nama1 = nama.getText().toString();
                String notlp1 = notlp.getText().toString();

                if (email.equals("")){
                    loading.setVisibility(View.GONE);
                    Toast.makeText(registerActivity.this, "Username Belum Terisi !",
                            Toast.LENGTH_SHORT).show();
                }else if (password1.equals("")) {
                    loading.setVisibility(View.GONE);
                    Toast.makeText(registerActivity.this, "Password Belum Terisi !",
                            Toast.LENGTH_SHORT).show();
                }else if (!password2.equals(password1)) {
                    loading.setVisibility(View.GONE);
                    Toast.makeText(registerActivity.this, "Konfirmasi Pssword Anda Belum Sesuai !",
                            Toast.LENGTH_SHORT).show();
                }else if (nama1.equals("")) {
                    loading.setVisibility(View.GONE);
                    Toast.makeText(registerActivity.this, "Nama Belum Terisi !",
                            Toast.LENGTH_SHORT).show();
                }else if (notlp1.equals("")) {
                    loading.setVisibility(View.GONE);
                    Toast.makeText(registerActivity.this, "Nomor Telepon Belum Terisi !",
                            Toast.LENGTH_SHORT).show();
                }else {
                    mAuth.createUserWithEmailAndPassword(email, password1)
                        .addOnCompleteListener(registerActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    loading.setVisibility(View.GONE);
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    DocumentReference documentReference = db.collection("userInfo")
                                            .document(user.getUid());
                                    Map<String, Object> userInfo = new HashMap<>();
                                    userInfo.put("Nama User", nama1);
                                    userInfo.put("Email", email);
                                    userInfo.put("Nomor Telepon", notlp1);
                                    //validasi user sebagai user biasa
                                    userInfo.put("isUser", "1");

                                    documentReference.set(userInfo)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(registerActivity.this, "Data Berhasil Disimpan !",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(registerActivity.this, "Data Gagal Disimpan !",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                    });

                                    Toast.makeText(registerActivity.this, "Register berhasil",
                                            Toast.LENGTH_SHORT).show();
                                    Intent loginIntent1 = new Intent(registerActivity.this, MainActivity.class);
                                    startActivity(loginIntent1);
                                    finish();
                                } else {
                                    loading.setVisibility(View.GONE);
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(registerActivity.this, "Register Gagal",
                                            Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                        });
                }
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}
