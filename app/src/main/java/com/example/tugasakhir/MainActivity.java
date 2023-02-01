package com.example.tugasakhir;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Button ButtonLogin, ButtonRegister;
    private EditText username, password;
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ButtonLogin = (Button) findViewById(R.id.ButtonLogin);
        ButtonRegister = (Button) findViewById(R.id.ButtonRegister);
        username = (EditText) findViewById(R.id.editUsername);
        password = (EditText) findViewById(R.id.editPassword);
        loading=findViewById(R.id.loading);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        ButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, registerActivity.class);
                startActivity(i);
            }
        });
        ButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                String email = username.getText().toString();
                String password1 = password.getText().toString();
                if (email.equals("")){
                    loading.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Username Belum Terisi !",
                            Toast.LENGTH_SHORT).show();
                } else if (password1.equals("")) {
                    loading.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Password Belum Terisi !",
                            Toast.LENGTH_SHORT).show();
                }else {
                    mAuth.signInWithEmailAndPassword(email, password1)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        loading.setVisibility(View.GONE);
                                        AuthResult authResult = task.getResult();
                                        cekUserAdmin(authResult.getUser().getUid());
                                        Toast.makeText(MainActivity.this, "Login berhasil",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        loading.setVisibility(View.GONE);
                                        Toast.makeText(MainActivity.this, "Login Gagal",
                                                Toast.LENGTH_SHORT).show();
                                    }
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
    private  void  cekUserAdmin (String uid){
        DocumentReference documentReference = db.collection("userInfo").document(uid);
        documentReference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.getString("isAdmin")!=null){
                                Intent i = new Intent(MainActivity.this, home.class);
                                startActivity(i);
                                finish();
                            }if (document.getString("isUser")!=null){
                                Intent i = new Intent(MainActivity.this, userHome.class);
                                startActivity(i);
                                finish();
                            }
                        }else{
                            
                        }
                    }
                });
    }

}