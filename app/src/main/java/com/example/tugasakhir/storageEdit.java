package com.example.tugasakhir;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.MonthDay;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.grpc.Context;

public class storageEdit extends AppCompatActivity {
    private FirebaseFirestore db;
    private EditText editNama, editJumlah, editHarga, editBeli, editExp;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat simpleDateFormat;
    private Button buttonSave;
    private ImageView fotoBarang,logo_tambah;
    private ProgressBar loading;
    private String id="", URL="";
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_edit);

        db= FirebaseFirestore.getInstance();

        fotoBarang = (ImageView)findViewById(R.id.fotoBarang);
        logo_tambah = (ImageView)findViewById(R.id.logo_tambah_foto);
        editNama= (EditText) findViewById(R.id.nama);
        editJumlah =(EditText) findViewById(R.id.jumlah);
        editHarga=(EditText) findViewById(R.id.harga);
        editBeli=(EditText) findViewById(R.id.beli);
        editExp=(EditText) findViewById(R.id.exp);
        buttonSave=(Button) findViewById(R.id.save);
        loading = findViewById(R.id.loading);
        linearLayout = (LinearLayout) findViewById(R.id.tanggalKedaluarsa);

        Intent intent = getIntent();
        if(intent.getStringExtra("id")!=null){
            logo_tambah.setVisibility(View.GONE);
            id = intent.getStringExtra("id");
            editNama.setText(intent.getStringExtra("Nama Barang"));
            editHarga.setText(intent.getStringExtra("Harga Barang"));
            editBeli.setText(intent.getStringExtra("Harga Beli"));
            Glide.with(getApplicationContext()).load(intent.getStringExtra("Foto Barang")).into(fotoBarang);
            URL=intent.getStringExtra("Foto Barang");

            ambitDataKedaluarsa(intent.getStringExtra("id"));
        }

        fotoBarang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simpanFoto();
            }
        });

        simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
        editExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editNama.getText().length()>0 &&
                        editJumlah.getText().length()>0 &&
                        editHarga.getText().length()>0 &&
                        editBeli.getText().length()>0){
                    String nama=editNama.getText().toString();
                    String jumlah=editJumlah.getText().toString();
                    String harga=editHarga.getText().toString();
                    String beli=editBeli.getText().toString();
                    String exp;
                    if (editExp.getText().length()>0){
                        exp=editExp.getText().toString();
                    }else {
                        exp="0";
                    }
                    if (URL.equals("")){
                        uploadData(nama, jumlah, harga, beli, exp);
                    }else{
                        FirebaseStorage.getInstance().getReferenceFromUrl(URL).delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        loading.setVisibility(View.GONE);
                                        uploadData(nama, jumlah, harga, beli, exp);                                    }
                                });
                    }

                }else{
                    Toast.makeText(storageEdit.this, "Data Barang Belum Lengkap !",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ambitDataKedaluarsa(String id) {
        db.collection("detail_kedaluarsa")
                .whereEqualTo("Id Barang", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String tanggal = document.getString("Tanggal Kedaluarsa");
                                String jumlah = document.getString("Jumlah Barang");
                                TextView textTanggal = new TextView(storageEdit.this);
                                textTanggal.setText("Tanggal Kedaluarsa\t:\t"+tanggal+"\t("+jumlah+")");
                                linearLayout.addView(textTanggal);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(storageEdit.this, "Data Tanggal Gagal Diambil !",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDateDialog() {
        Calendar calendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(storageEdit.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                Calendar newDate=Calendar.getInstance();
                newDate.set(i, i1, i2);
                editExp.setText(simpleDateFormat.format(newDate.getTime()));
            }
        },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void simpanFoto (){
        final CharSequence[] items = {"Ambil Foto", "Pilih dari Galeri", "Batal"};
        AlertDialog.Builder builder = new AlertDialog.Builder(storageEdit.this);
        builder.setTitle(getString(R.string.app_name));
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Ambil Foto")){
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, 10);
            }else if (items[item].equals("Pilih dari Galeri")){
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "Pilih Foto"), 20);
            }else if (items[item].equals("Batal")){
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==10 && resultCode==RESULT_OK){
            final Bundle extras = data.getExtras();
            Thread thread = new Thread(() -> {
                Bitmap bitmap = (Bitmap) extras.get("data");
                fotoBarang.post(() ->{
                    fotoBarang.setImageBitmap(bitmap);
                    logo_tambah.setVisibility(View.GONE);
                });
            });
            thread.start();
        }
        if (requestCode==20 && resultCode==RESULT_OK && data!=null){
            final Uri path = data.getData();
            Thread thread = new Thread(()-> {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(path);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    fotoBarang.post(() -> {
                        fotoBarang.setImageBitmap(bitmap);
                        logo_tambah.setVisibility(View.GONE);
                    });
                }catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }
    }

    private void uploadData(String Nama, String Jumlah, String Harga, String Beli, String Exp){
        loading.setVisibility(View.VISIBLE);

        fotoBarang.setDrawingCacheEnabled(true);
        fotoBarang.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) fotoBarang.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference("fotoBarang").child("BRG"+new Date().getTime()+".jpeg");

        UploadTask uploadTask = reference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loading.setVisibility(View.GONE);
                Toast.makeText(storageEdit.this, e.getLocalizedMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getMetadata()!=null){
                    if (taskSnapshot.getMetadata().getReference()!=null){
                        taskSnapshot.getMetadata().getReference().getDownloadUrl()
                                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.getResult()!=null){
                                            simpanBarang(Nama, Jumlah, Harga, Beli, Exp, task.getResult().toString());
                                        }else {
                                            loading.setVisibility(View.GONE);
                                            Toast.makeText(storageEdit.this, "Gagal Mengupload Data !",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }else {
                        loading.setVisibility(View.GONE);
                        Toast.makeText(storageEdit.this, "Gagal Mengupload Data !",
                                Toast.LENGTH_SHORT).show();
                    }
                }else {
                    loading.setVisibility(View.GONE);
                    Toast.makeText(storageEdit.this, "Gagal Mengupload Data !",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void simpanBarang(String Nama, String Jumlah, String Harga, String Beli, String Exp, String fotoBarang){
        Map<String, Object> barang = new HashMap<>();
        barang.put("Nama Barang", Nama);
        barang.put("Harga Barang", Harga);
        barang.put("Harga Beli", Beli);
        barang.put("Foto Barang", fotoBarang);

        loading.setVisibility(View.VISIBLE);
        if (!id.equals("")){
            DocumentReference document = db.collection("barang").document(id);
                    document.set(barang)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Map<String, Object> detail_kedaluarsa = new HashMap<>();
                                detail_kedaluarsa.put("Id Barang", id);
                                detail_kedaluarsa.put("Tanggal Kedaluarsa", Exp);
                                detail_kedaluarsa.put("Jumlah Barang", Jumlah);
                                db.collection("detail_kedaluarsa").add(detail_kedaluarsa);

                                Toast.makeText(storageEdit.this, "Data Berhasil Diedit !",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                Toast.makeText(storageEdit.this, "Data Gagal  Diedit !",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else{
            db.collection("barang")
                    .add(barang)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Map<String, Object> detail_kedaluarsa = new HashMap<>();
                            detail_kedaluarsa.put("Id Barang", documentReference.getId());
                            detail_kedaluarsa.put("Tanggal Kedaluarsa", Exp);
                            detail_kedaluarsa.put("Jumlah Barang", Jumlah);
                            db.collection("detail_kedaluarsa").add(detail_kedaluarsa);

                            Toast.makeText(storageEdit.this, "Data Berhasil Disimpan !",
                                    Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.GONE);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(storageEdit.this, e.getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.GONE);
                        }
                    });
        }
    }
}
