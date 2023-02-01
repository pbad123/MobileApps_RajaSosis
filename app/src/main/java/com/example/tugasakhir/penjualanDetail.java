package com.example.tugasakhir;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tugasakhir.adapter.PenjualanDetailAdapter;
import com.example.tugasakhir.model.Penjualan;
import com.example.tugasakhir.model.PenjualanDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class penjualanDetail extends AppCompatActivity {
    private String id_penjualan;
    private TextView penjualanID, tanggalPenjualan, pembeli, alamat, admin, total, dibayar, kembalian, status;
    private RecyclerView recyclerPenjualanDetail;
    private FirebaseFirestore db;
    private PenjualanDetailAdapter penjualanDetailAdapter;
    private List<PenjualanDetail> penjualanDetailList = new ArrayList<>();
    private Button bayarHutang, printPdf;
    private int pageWidth = 1200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penjualan_detail);

        penjualanID = (TextView) findViewById(R.id.idPenjualanPD);
        tanggalPenjualan = (TextView) findViewById(R.id.tanggalPenjualanPD);
        pembeli = (TextView) findViewById(R.id.namaPembeliPD);
        alamat = (TextView) findViewById(R.id.alamatPD);
        admin = (TextView) findViewById(R.id.namaAdminPD);
        total = (TextView) findViewById(R.id.totalPenjualanPD);
        dibayar = (TextView) findViewById(R.id.dibayarPD);
        kembalian = (TextView) findViewById(R.id.kembalianPD);
        status = (TextView) findViewById(R.id.statusPD);
        bayarHutang = (Button) findViewById(R.id.bayarHutangPD);
        printPdf = (Button) findViewById(R.id.printPdfPD);
        recyclerPenjualanDetail = findViewById(R.id.itemPenjualanDetail);

        db = FirebaseFirestore.getInstance();
        penjualanDetailAdapter = new PenjualanDetailAdapter(penjualanDetail.this, penjualanDetailList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL, false);
        recyclerPenjualanDetail.setLayoutManager(layoutManager);
        recyclerPenjualanDetail.setAdapter(penjualanDetailAdapter);

//        isStoragePermissionGranted();

        ActivityCompat.requestPermissions(penjualanDetail.this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        Intent intent = getIntent();
        id_penjualan = intent.getStringExtra("id_penjualan");
        
        printPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = penjualanID.getText().toString();
                String tanggal = tanggalPenjualan.getText().toString();
                String pembeliUser = pembeli.getText().toString();
                String lokasi = alamat.getText().toString();
                String melayani = admin.getText().toString();
                String totalBelanja = total.getText().toString();
                String bayar = dibayar.getText().toString();
                String kembali = kembalian.getText().toString();
                String statusPenjualan = status.getText().toString();

                buatPdf(id, tanggal, pembeliUser, lokasi, melayani, totalBelanja, bayar, kembali, statusPenjualan);
            }
        });

        bayarHutang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = penjualanID.getText().toString();
                String nama = pembeli.getText().toString();
                String hutang = kembalian.getText().toString();
                bayarHutangMember(id, nama, hutang);
            }
        });
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(penjualanDetail.this, "Permission is granted", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                Toast.makeText(penjualanDetail.this, "Permission is revoke", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Toast.makeText(penjualanDetail.this, "Permission is granted", Toast.LENGTH_SHORT).show();
            return true;
        }


    }
    private void buatPdf(String id, String tanggal, String pembeliUser, String lokasi, String melayani, String totalBelanja, String bayar, String kembali, String statusPenjualan) {
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1200, 2010, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        Paint paint = new Paint();
        Paint titlePaint = new Paint();
        Canvas canvas = page.getCanvas();

        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(70);
        canvas.drawText("Toko Raja Sosis", pageWidth / 2, 160, titlePaint);

        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(50);
        canvas.drawText("Jalan Tukad Pakerisan No.69 B Denpasar, Bali", pageWidth / 2, 240, titlePaint);
        canvas.drawText("Denpasar, Bali", pageWidth / 2, 290, titlePaint);

        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextSize(50);
        canvas.drawText("Stroke Pembelian", pageWidth / 2, 500, titlePaint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.BLACK);
        paint.setTextSize(35f);
        canvas.drawText("Pembeli : " + pembeliUser, 20, 590, paint);
        canvas.drawText("Admin : " + melayani, 20, 640, paint);

        Paint paint1 = new Paint();
        if(statusPenjualan.equals("ngutang")){
            paint.setTextAlign(Paint.Align.LEFT);
            paint1.setColor(Color.RED);
            paint1.setTextSize(35f);
            canvas.drawText("Status : " + statusPenjualan, 20, 690, paint1);
        }else if(statusPenjualan.equals("selesai")){
            paint.setTextAlign(Paint.Align.LEFT);
            paint1.setColor(Color.GREEN);
            paint1.setTextSize(35f);
            canvas.drawText("Status : " + statusPenjualan, 20, 690, paint1);
        }

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Id : " + id, pageWidth - 20, 590, paint);
        canvas.drawText("Tanggal: " + tanggal, pageWidth - 20, 640, paint);
        canvas.drawText("Lokasi Transaksi : " + lokasi, pageWidth - 20, 690, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawRect(20, 780, pageWidth - 20, 860, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText("No.", 40, 830, paint);
        canvas.drawText("Barang yang Dibeli", 200, 830, paint);
        canvas.drawText("Harga", 700, 830, paint);
        canvas.drawText("Jumlah", 900, 830, paint);
        canvas.drawText("Total", 1050, 830, paint);

        canvas.drawLine(180, 790, 180, 840, paint);
        canvas.drawLine(680, 790, 680, 840, paint);
        canvas.drawLine(880, 790, 880, 840, paint);
        canvas.drawLine(1030, 790, 1030, 840, paint);

        int p = 950;
        for (int i =0; i<penjualanDetailList.size();i++){
            canvas.drawText(Integer.toString(i+1), 40, p, paint);
            //cetakNamaUser(penjualanDetailList.get(i).getId_barang(), canvas, paint, p);
            String namaBarang = ((TextView) recyclerPenjualanDetail.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.namaBarangPD)).getText().toString();
            String hargaBarang = ((TextView) recyclerPenjualanDetail.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.hargaPD)).getText().toString();
            canvas.drawText(namaBarang, 200, p, paint);
            canvas.drawText(hargaBarang, 700, p, paint);
            //canvas.drawText(penjualanDetailList.get(i).get, 200, 950, paint);
            //canvas.drawText(String.valueOf(harga[itemSpinnerOne.getSelectedItemPosition()]), 700, 950, paint);
            canvas.drawText(penjualanDetailList.get(i).getJumlah_barang(), 900, p, paint);
            //totalOne = Float.parseFloat(etJmlOne.getText().toString()) * harga[itemSpinnerOne.getSelectedItemPosition()];
            paint.setTextAlign(Paint.Align.RIGHT);
            if(!penjualanDetailList.get(i).getDiskon().equals("0")){
                paint.setStrikeThruText(true);
                canvas.drawText(penjualanDetailList.get(i).getSub_total(), pageWidth - 40, p, paint);
                paint.setStrikeThruText(false);
                int hasilDiskon = Integer.parseInt(penjualanDetailList.get(i).getSub_total())-Integer.parseInt(penjualanDetailList.get(i).getDiskon());
                canvas.drawText(Integer.toString(hasilDiskon), pageWidth - 40, p-35, paint);
            }else{
                canvas.drawText(penjualanDetailList.get(i).getSub_total(), pageWidth - 40, p, paint);
            }
            paint.setTextAlign(Paint.Align.LEFT);
            p=p+100;
        }

        canvas.drawLine(400, 1200, pageWidth - 20, 1200, paint);
        canvas.drawText("Sub Total", 700, 1250, paint);
        canvas.drawText(":", 900, 1250, paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(totalBelanja, pageWidth - 40, 1250, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Dibayar", 700, 1300, paint);
        canvas.drawText(":", 900, 1300, paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(bayar, pageWidth - 40, 1300, paint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.rgb(247, 147, 30));
        canvas.drawRect(680, 1350, pageWidth - 20, 1450, paint);

        paint.setColor(Color.BLACK);
        paint.setTextSize(50f);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Kembalian", 700, 1415, paint);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(kembali, pageWidth - 40, 1415, paint);

        pdfDocument.finishPage(page);
//        File dir = new File(Environment.getExternalStoragePublicDirectory(MediaStore.Downloads) + "/Caramel");
//        dir.mkdir();
        String namaFile = System.currentTimeMillis() + ".pdf";
        File file = new File(Environment.getExternalStoragePublicDirectory("/Download"), namaFile);
        try {
            pdfDocument.writeTo(new FileOutputStream(file));

        } catch (IOException e) {
            e.printStackTrace();
        }

        pdfDocument.close();
        Toast.makeText(penjualanDetail.this, "PDF Berhasil Dibuat",
                Toast.LENGTH_LONG).show();
    }

    private void cetakNamaUser(String id_barang, Canvas canvas, Paint paint, int p) {
        DocumentReference reference = FirebaseFirestore.getInstance().collection("barang").document(id_barang);
        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            String nama = document.getString("Nama Barang");
                            String harga = document.getString("Harga Barang");
                            canvas.drawText(nama, 200, p, paint);
                            canvas.drawText(harga, 700, p, paint);
                        }else {
                            Toast.makeText(penjualanDetail.this, "Gagal Mengambil Nama dan Harga Barang !",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadDataPenjualan(id_penjualan);
        loadDataDetailPenjualan(id_penjualan);
    }

    private void bayarHutangMember(String id, String nama, String hutang) {
        View view = LayoutInflater.from(penjualanDetail.this).inflate(R.layout.dialog_bayar_hutang, null);
        TextView namaMember, hutangMember, kembalian;
        EditText dibayar;
        Button bayar;

        namaMember = view.findViewById(R.id.dialogNamaBH);
        hutangMember = view.findViewById(R.id.dialogHutangBH);
        kembalian = view.findViewById(R.id.dialogKembalianBH);
        dibayar = view.findViewById(R.id.dialogDibayarBH);
        bayar = view.findViewById(R.id.dialogBayarBH);

        db = FirebaseFirestore.getInstance();

        AlertDialog.Builder builder = new AlertDialog.Builder(penjualanDetail.this);
        builder.setView(view);

        namaMember.setText(nama);
        hutangMember.setText("Rp."+hutang);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        dibayar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (dibayar.length()>0){
                    int Dibayar = (Integer.parseInt(hutangMember.getText().toString().replace("Rp.", "").trim()))+(Integer.parseInt(dibayar.getText().toString().trim()));
                    kembalian.setText("Rp."+Dibayar);
                }
            }

        });
        bayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dibayar.getText().toString().length()>0){
                    if(kembalian.getText().toString().replace("Rp.", "").contains("-")){
                        String status = "ngutang";
                        updateDataPenjualan(id, kembalian.getText().toString().replace("Rp.", ""), status, dibayar.getText().toString());
                        dialog.dismiss();
                        onStart();
                    }else{
                        String status = "selesai";
                        updateDataPenjualan(id, kembalian.getText().toString().replace("Rp.", ""), status, dibayar.getText().toString());
                        dialog.dismiss();
                        onStart();
                    }
                }else{
                    Toast.makeText(penjualanDetail.this, "Uang Yang Dibayar Belum Diisi !",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void updateDataPenjualan(String id, String kembalian, String status, String dibayar) {
        DocumentReference reference= db.collection("penjualan").document(id);
        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            int dibayar1 = Integer.parseInt(document.getString("Dibayar"));
                            int bayar = dibayar1 + Integer.parseInt(dibayar);
                            String dibayar2 = Integer.toString(bayar);

                            HashMap<String, Object> penjualan = new HashMap<>();
                            penjualan.put("Tanggal", document.getString("Tanggal"));
                            penjualan.put("Total Transaksi", document.getString("Total Transaksi"));
                            penjualan.put("User Id", document.getString("User Id"));
                            penjualan.put("Dibayar", dibayar2);
                            penjualan.put("Kembalian", kembalian);
                            penjualan.put("Admin Melayani", document.getString("Admin Melayani"));
                            penjualan.put("Alamat", document.getString("Alamat"));
                            penjualan.put("Status", status);
                            reference.set(penjualan)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(penjualanDetail.this, "Data Berhasil DiUpdate !",
                                                    Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(penjualanDetail.this, "Data Gagal DiUpdate !",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
    }

    private void loadDataDetailPenjualan(String id_penjualan) {
        DocumentReference reference= db.collection("penjualan").document(id_penjualan);
                reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            penjualanID.setText(document.getId());
                            tanggalPenjualan.setText(document.getString("Tanggal"));
                            if(document.getString("User Id").equals("Bukan Member")){
                                pembeli.setText("Bukan Member");
                            }else{
                                cariNamaUser(document.getString("User Id"), pembeli);
                            }
                            alamat.setText(document.getString("Alamat"));
                            cariNamaUser(document.getString("Admin Melayani"), admin);
                            total.setText("Rp."+document.getString("Total Transaksi"));
                            dibayar.setText("Rp."+document.getString("Dibayar"));
                            kembalian.setText("Rp."+document.getString("Kembalian"));
                            status.setText(document.getString("Status"));
                            if(document.getString("Status").equals("ngutang")){
                                status.setTextColor(penjualanDetail.this.getResources().getColor(R.color.red));
                                bayarHutang.setVisibility(View.VISIBLE);
                            }else if(document.getString("Status").equals("selesai")){
                                status.setTextColor(penjualanDetail.this.getResources().getColor(R.color.hijautua));
                            }
                        }else{
                            Toast.makeText(penjualanDetail.this, "Data Gagal Diambil !",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void cariNamaUser(String id, TextView view) {
        DocumentReference reference = FirebaseFirestore.getInstance().collection("userInfo").document(id);
        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            String nama = document.getString("Nama User");
                            view.setText(nama);
                        }else {
                            Toast.makeText(penjualanDetail.this, "Gagal Mengambil Nama User !",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void loadDataPenjualan(String id_penjualan) {
        db.collection("detail_penjualan")
                .whereEqualTo("Id Penjualan", id_penjualan)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        penjualanDetailList.clear();
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                PenjualanDetail penjualanDetail = new PenjualanDetail(document.getString("Id Penjualan"),
                                        document.getString("Id Keranjang"),
                                        document.getString("Id Barang"),
                                        document.getString("Jumlah Barang"),
                                        document.getString("Sub Total"),
                                        document.getString("Total Harga Beli"),
                                        document.getString("Keuntungan"),
                                        document.getString("Hasil Total"),
                                        document.getString("Diskon"));
                                penjualanDetailList.add(penjualanDetail);
                            }
                            penjualanDetailAdapter.notifyDataSetChanged();
                        }else{
                            Toast.makeText(penjualanDetail.this, "Data Gagal Diambil !",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
