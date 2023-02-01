package com.example.tugasakhir;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tugasakhir.adapter.KeranjangBarangAdapter;
import com.example.tugasakhir.adapter.produkAdapter;
import com.example.tugasakhir.model.KeranjangBarang;
import com.example.tugasakhir.model.barang;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class kasir extends AppCompatActivity {
    private Spinner pelanggan;
    private FirebaseFirestore db;
    private String namaMember;
    private List<String> namaPelanggan;
    private ArrayAdapter<String> adapter;
    private RecyclerView recyclerKeranjang;
    public TextView totalHarga, totalDiskon, kembali, uid;
    private EditText dibayar;
    private Button btnSelesai;
    public int TotalBayar;
    private ProgressDialog progressDialog;
    private List<KeranjangBarang> listKeranjang;
    private KeranjangBarangAdapter keranjangBarangAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kasir);

        db = FirebaseFirestore.getInstance();
        pelanggan = (Spinner) findViewById(R.id.pelangganK);
        recyclerKeranjang = findViewById(R.id.itemKeranjangK);
        totalHarga = (TextView) findViewById(R.id.totalK);
        totalDiskon = (TextView) findViewById(R.id.diskonK);
        kembali = (TextView) findViewById(R.id.kembaliK);
        uid = (TextView) findViewById(R.id.idUserK);
        dibayar = (EditText) findViewById(R.id.dibayarK);
        btnSelesai = (Button) findViewById(R.id.btnSelesaiK);
        progressDialog = new ProgressDialog(kasir.this);
        progressDialog.setTitle("Mohon Tunggu");
        progressDialog.setCanceledOnTouchOutside(false);

        TotalBayar =0;

        namaPelanggan = new ArrayList<>();
        namaPelanggan.add("Bukan Member");
        ambilUser();
        adapter = new ArrayAdapter<>(kasir.this, R.layout.text_spinner, namaPelanggan);
        adapter.setDropDownViewResource(R.layout.text_spinner);
        pelanggan.setPrompt("Nama Member");
        pelanggan.setAdapter(adapter);

        listKeranjang = new ArrayList<>();

        EasyDB easyDB = EasyDB.init(kasir.this, "db_tokoSembako")
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

        Cursor cursor = easyDB.getAllData();
        while (cursor.moveToNext()){
            String id = cursor.getString(1);
            String idBarang = cursor.getString(2);
            String nama = cursor.getString(3);
            String jumlah = cursor.getString(4);
            String harga = cursor.getString(5);
            String total = cursor.getString(6).replace(".0", "");
            String foto = cursor.getString(7);
            String hargaBeli = cursor.getString(8);
            String tanggalExp = cursor.getString(9);
            String diskon = cursor.getString(10);

            if(!diskon.equals("0")){
                TotalBayar = TotalBayar+Integer.parseInt(total)-Integer.parseInt(diskon);
            }else if(diskon.equals("0")){
                TotalBayar = TotalBayar+Integer.parseInt(total);
            }
            //TotalBayar = TotalBayar+Integer.parseInt(total);

            KeranjangBarang keranjangBarang = new KeranjangBarang(id, idBarang, nama, harga, jumlah, total, foto, hargaBeli, tanggalExp, diskon);
            listKeranjang.add(keranjangBarang);
        }
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL, false);
        RecyclerView.ItemDecoration decoration= new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        recyclerKeranjang.setLayoutManager(layoutManager);
        recyclerKeranjang.addItemDecoration(decoration);
        keranjangBarangAdapter = new KeranjangBarangAdapter(kasir.this, listKeranjang);
        recyclerKeranjang.setAdapter(keranjangBarangAdapter);
        totalHarga.setText("Rp."+TotalBayar);


        pelanggan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                namaMember = adapterView.getItemAtPosition(pos).toString();
                if (pelanggan.getSelectedItem().toString().equals("Bukan Member")){
                    uid.setText("Bukan Member");
                }else{
                    ambilUserID(namaMember);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
                    if (totalDiskon.getVisibility()==View.VISIBLE){
                        int Dibayar = (Integer.parseInt(dibayar.getText().toString().trim()))-(Integer.parseInt(totalDiskon.getText().toString().replace("Rp.", "").trim())) ;
                        kembali.setText(Integer.toString(Dibayar));
                    }else if (totalDiskon.getVisibility()==View.GONE){
                        int Dibayar = (Integer.parseInt(dibayar.getText().toString().trim()))-(Integer.parseInt(totalHarga.getText().toString().replace("Rp.", "").trim())) ;
                        kembali.setText(Integer.toString(Dibayar));
                    }
                }
            }
        });
        totalHarga.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if (dibayar.length()>0){
                    int Dibayar = (Integer.parseInt(dibayar.getText().toString().trim())) - (Integer.parseInt(totalHarga.getText().toString().replace("Rp.", "").trim()));
                    kembali.setText("Rp."+Integer.toString(Dibayar));
                }
            }
        });
        btnSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listKeranjang.size()==0){
                    Toast.makeText(kasir.this, "Barang Pada Keranjang Kosong !",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                simpanTransaksi();
                easyDB.deleteAllDataFromTable();
                finish();
            }
        });
    }

    private void simpanTransaksi() {
        progressDialog.setMessage("Menyimpan Transakasi....");
        progressDialog.show();

        String timestamp = ""+System.currentTimeMillis();
        String tanggal = getHari();
        String total = totalHarga.getText().toString().replace("Rp.", "").trim();
        String dibayarUser = dibayar.getText().toString().trim();
        String kembalian = kembali.getText().toString().replace("Rp.", "").trim();
        String AdminID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String UserID = uid.getText().toString();
        String status;

        if (kembali.getText().toString().replace("Rp.", "").trim().contains("-")){
            status = "ngutang";
        }else{
            status = "selesai";
        }

        HashMap<String, Object> penjualan = new HashMap<>();
        penjualan.put("Tanggal", tanggal);
        penjualan.put("Total Transaksi", total);
        penjualan.put("User Id", UserID);
        penjualan.put("Dibayar", dibayarUser);
        penjualan.put("Kembalian", kembalian);
        penjualan.put("Admin Melayani", AdminID);
        penjualan.put("Alamat", "Toko Raja Sosis");
        penjualan.put("Status", status);

        db.collection("penjualan")
                .document(timestamp)
                .set(penjualan)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        for (int i=0; i<listKeranjang.size(); i++){
                            String id = listKeranjang.get(i).getId();
                            String barangId = listKeranjang.get(i).getIdBarang();
                            String beli = listKeranjang.get(i).getBeli();
                            String jumlah = listKeranjang.get(i).getJumlah();
                            String subTotal = listKeranjang.get(i).getTotal();
                            String diskon = listKeranjang.get(i).getDiskon();
                            String hasilDiskon = Integer.toString(Integer.parseInt(subTotal)-Integer.parseInt(diskon));
                            String exp = listKeranjang.get(i).getTanggal();
                            String beliTotal = Integer.toString((Integer.parseInt(beli))*(Integer.parseInt(jumlah)));
                            String untung = Integer.toString(Integer.parseInt(subTotal)-Integer.parseInt(beliTotal));

                            HashMap<String, Object> detail_penjualan = new HashMap<>();
                            detail_penjualan.put("Id Penjualan", timestamp);
                            detail_penjualan.put("Id Keranjang", id);
                            detail_penjualan.put("Id Barang", barangId);
                            detail_penjualan.put("Jumlah Barang", jumlah);
                            detail_penjualan.put("Tanggal Kedaluarsa", exp);
                            detail_penjualan.put("Sub Total", subTotal);
                            detail_penjualan.put("Diskon", diskon);
                            detail_penjualan.put("Hasil Total", hasilDiskon);
                            detail_penjualan.put("Total Harga Beli", beliTotal);
                            detail_penjualan.put("Keuntungan", untung);

                            db.collection("detail_penjualan")
                                    .add(detail_penjualan);
                            triggerKurangStok(barangId, jumlah, exp);
                        }
                        progressDialog.dismiss();
                        Toast.makeText(kasir.this, "Penjualan Berhasil Disimpan !",
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(kasir.this, penjualanDetail.class);
                        intent.putExtra("id_penjualan", timestamp);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(kasir.this, "Penjualan Gagal Disimpan, Silahkan Coba Lagi !",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void triggerKurangStok(String barangId, String jumlah, String exp) {
        db.collection("detail_kedaluarsa")
                .whereEqualTo("Id Barang", barangId)
                .whereEqualTo("Tanggal Kedaluarsa", exp)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                String id = document.getId();
                                String stok = document.getString("Jumlah Barang");
                                String sisaStok = Integer.toString((Integer.parseInt(stok))-(Integer.parseInt(jumlah)));

                                Map<String, Object> detail_kedaluarsa = new HashMap<>();
                                detail_kedaluarsa.put("Id Barang", barangId);
                                detail_kedaluarsa.put("Tanggal Kedaluarsa", exp);
                                detail_kedaluarsa.put("Jumlah Barang", sisaStok);
                                DocumentReference reference = db.collection("detail_kedaluarsa").document(id);
                                reference.set(detail_kedaluarsa);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(kasir.this, "Gagal Mengurangi Stok !",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getHari() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy HH:mm:ss");
        String date = dateFormat.format(calendar.getTime());
        return date;
    }
    private void ambilUser(){
        db.collection("userInfo")
                .whereEqualTo("isUser", "1")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String namaP = document.getString("Nama User");
                                namaPelanggan.add(namaP);
                            }
                        }else {
                            Toast.makeText(kasir.this, "Gagal Mengambil User !",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void ambilUserID(String member){
        db.collection("userInfo")
                .whereEqualTo("Nama User", member)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String memberUid = document.getId();
                                uid.setText(memberUid);
                            }
                        }else {
                            Toast.makeText(kasir.this, "Gagal Mengambil User ID !",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
