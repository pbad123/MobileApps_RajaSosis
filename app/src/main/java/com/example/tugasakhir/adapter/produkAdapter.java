package com.example.tugasakhir.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tugasakhir.R;
import com.example.tugasakhir.model.barang;
import com.example.tugasakhir.produk;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class produkAdapter extends RecyclerView.Adapter<produkAdapter.MyViewHolder>{
    private Context context;
    private List<barang> list;

    public produkAdapter(Context context, List<barang> list){
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_produk, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        barang Barang = list.get(position);
        String id = list.get(position).getId();
        String namaProduk = list.get(position).getNama();
        String hargaProduk = list.get(position).getHarga();
        String fotoProduk = list.get(position).getBarangFoto();

        holder.nama.setText(namaProduk);
        ambilDataStok(id, holder.jumlah);
        //holder.jumlah.setText("Stok  : "+stokProduk);
        holder.harga.setText("Harga : Rp."+hargaProduk);
        Glide.with(context).load(fotoProduk).into(holder.barangFoto);

        holder.tambahKeranjang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menampilkanDialogProduk(Barang);
            }
        });
    }
    private int total = 0;
    private void ambilDataStok(String id, TextView view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("detail_kedaluarsa")
                .whereEqualTo("Id Barang", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        total = 0;
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                String jumlah = document.getString("Jumlah Barang");
                                total = total + Integer.parseInt(jumlah);
                                view.setText(""+total);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Data Tanggal Gagal Diambil !",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private int bayar = 0;
    private int totalBayar = 0;
    private int totalProduk = 0;
    private void menampilkanDialogProduk(barang Barang) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.dialog_produk_keranjang, null);
        ImageView dialogFotoProduk;
        TextView dialogNamaProduk, dialogStokProduk, dialogJumlahBeli, dialogHargaProduk, dialogSubTotal, dialogDiskon;
        ImageButton dialogKurangProduk, dialogTambahProduk;
        Spinner spinnerTanggal;
        List <String> listTanggal = new ArrayList<>();
        ArrayAdapter <String> adapter;
        Button dialogTambahKeranjang;

        dialogFotoProduk = (ImageView) itemView.findViewById(R.id.dialogFotoProduk);
        dialogNamaProduk = (TextView) itemView.findViewById(R.id.dialogNamaProduk);
        dialogStokProduk = (TextView) itemView.findViewById(R.id.dialogStokProduk);
        dialogJumlahBeli = (TextView) itemView.findViewById(R.id.dialogJumlahBeli);
        dialogHargaProduk = (TextView) itemView.findViewById(R.id.dialogHargaProduk);
        dialogDiskon = (TextView) itemView.findViewById(R.id.dialogDiskonProduk);
        dialogSubTotal = (TextView) itemView.findViewById(R.id.dialogSubTotal);
        dialogKurangProduk = (ImageButton) itemView.findViewById(R.id.dialogKurangProduk);
        dialogTambahProduk = (ImageButton) itemView.findViewById(R.id.dialogTambahProduk);
        dialogTambahKeranjang = (Button) itemView.findViewById(R.id.dialogTambahKeranjang);
        spinnerTanggal = (Spinner) itemView.findViewById(R.id.dialogTanggalExp);

        String produkId = Barang.getId();
        String produkNama = Barang.getNama();
        String produkFoto = Barang.getBarangFoto();
        String produkHarga = Barang.getHarga();
        String produkBeli = Barang.getBeli();

        bayar = Integer.parseInt(produkHarga);
        totalBayar = Integer.parseInt(produkHarga);
        totalProduk = 1;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(itemView);
        try {
            Glide.with(context).load(produkFoto).into(dialogFotoProduk);
        }catch (Exception e){
            dialogFotoProduk.setImageResource(R.drawable.ic_tambah_gambar_barang);
        }
        dialogNamaProduk.setText(""+produkNama);
        //ambilDataStok(produkId, dialogStokProduk);
        //dialogStokProduk.setText("Stok : "+produkStok);
        dialogJumlahBeli.setText(""+totalProduk);
        dialogHargaProduk.setText("Rp."+produkHarga);
        dialogSubTotal.setText("Rp."+totalBayar);

        listTanggal.add("Pilih Tanggal");
        ambilDataTanggal(produkId, listTanggal);
        adapter = new ArrayAdapter<>(context, R.layout.text_spinner, listTanggal);
        adapter.setDropDownViewResource(R.layout.text_spinner);
        spinnerTanggal.setPrompt("Tanggal Kedaluarsa");
        spinnerTanggal.setAdapter(adapter);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        spinnerTanggal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                String kedaluarsa = adapterView.getItemAtPosition(pos).toString();
                if(!kedaluarsa.equals("Pilih Tanggal")){
                    ambilDataStokDetail(produkId, kedaluarsa, dialogStokProduk);
                }else if (kedaluarsa.equals("Pilih Tanggal")){
                    dialogStokProduk.setText("");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dialogTambahProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!dialogStokProduk.getText().toString().equals("")){
                    if(Integer.parseInt(dialogStokProduk.getText().toString()) > totalProduk){
                        totalBayar=totalBayar+bayar;
                        totalProduk++;
                        ambilDiskon(produkId, totalBayar, totalProduk, dialogSubTotal, dialogDiskon);

                        dialogSubTotal.setText("Rp."+totalBayar);
                        dialogJumlahBeli.setText(""+totalProduk);
                    }else{
                        Toast.makeText(context, "Stok Tidak Cukup !",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context, "Silahkan Pilih Tanggal Kedaluarsa Terlebih Dahulu !",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialogKurangProduk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalProduk>1){
                    totalBayar= totalBayar-bayar;
                    totalProduk--;
                    dialogDiskon.setVisibility(View.GONE);
                    dialogSubTotal.setPaintFlags(dialogSubTotal.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));

                    ambilDiskon(produkId, totalBayar, totalProduk, dialogSubTotal, dialogDiskon);

                    dialogSubTotal.setText("Rp."+totalBayar);
                    dialogJumlahBeli.setText(""+totalProduk);
                }
            }
        });
        dialogTambahKeranjang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nama = dialogNamaProduk.getText().toString().trim();
                String beli= dialogJumlahBeli.getText().toString().trim();
                String harga= dialogHargaProduk.getText().toString().replace("Rp.", "").trim();
                String total = dialogSubTotal.getText().toString().replace("Rp.", "").trim();
                String tanggalExp = spinnerTanggal.getSelectedItem().toString();
                String diskon;

                if(dialogDiskon.getVisibility()== View.VISIBLE){
                    int hasilDiskon = Integer.parseInt(dialogDiskon.getText().toString().replace("Rp.", "").trim());
                    int totalDiskon = Integer.parseInt(total) - hasilDiskon;
                    diskon = Integer.toString(totalDiskon);
                }else{
                    diskon = "0";
                }
                if(spinnerTanggal.getSelectedItem().toString().equals("Pilih Tanggal")){
                    Toast.makeText(context, "Pilih Tanggal Kedaluarsa Produk Terlebih Dahulu",
                            Toast.LENGTH_SHORT).show();
                }else{
                    tambahKeranjang(produkId, nama, beli, harga, total, produkFoto, produkBeli, tanggalExp, diskon);
                    dialog.dismiss();
                }
            }
        });
    }

    private void ambilDiskon(String id, int totalBayar, int totalProduk, TextView viewSubTotal, TextView view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("detail_diskon")
                .whereEqualTo("Id Barang", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                String pembelian = document.getString("Jumlah Pembelian");
                                if(Integer.parseInt(pembelian)<=totalProduk){
                                    String diskon = document.getString("Diskon");
                                    int hasilDiskon = totalBayar - Integer.parseInt(diskon);
                                    view.setText("Rp."+hasilDiskon);
                                    viewSubTotal.setPaintFlags(viewSubTotal.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                    view.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                });
    }

    private void ambilDataStokDetail(String produkId, String kedaluarsa, TextView dialogStokProduk) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("detail_kedaluarsa")
                .whereEqualTo("Id Barang", produkId)
                .whereEqualTo("Tanggal Kedaluarsa", kedaluarsa)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                String stok = document.getString("Jumlah Barang");
                                dialogStokProduk.setText(stok);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Data Tanggal Gagal Diambil !",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void ambilDataTanggal(String produkId, List<String> listTanggal) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("detail_kedaluarsa")
                .whereEqualTo("Id Barang", produkId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                String tanggal = document.getString("Tanggal Kedaluarsa");
                                listTanggal.add(tanggal);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Data Tanggal Gagal Diambil !",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private int id = 0;
    private void tambahKeranjang(String produkId, String nama, String beli, String harga, String total, String fotoP, String hargaBeli, String tanggal, String diskon) {
        id++;

        EasyDB easyDB = EasyDB.init(context, "db_tokoSembako")
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
        Boolean b= easyDB.addData("id_item", id)
                .addData("id_barang", produkId)
                .addData("nama_barang", nama)
                .addData("jumlah_beli", beli)
                .addData("harga", harga)
                .addData("total_harga", total)
                .addData("URL_foto", fotoP)
                .addData("harga_beli", hargaBeli)
                .addData("tanggal_kedaluarsa", tanggal)
                .addData("diskon", diskon)
                .doneDataAdding();
        Toast.makeText(context, "Menambahkan "+nama+ " ke Keranjang",
                Toast.LENGTH_SHORT).show();

        ((produk)context).badge.setNumber(easyDB.getAllData().getCount());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView nama, jumlah, harga, diskon;
        ImageView barangFoto;
        Button tambahKeranjang;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nama=itemView.findViewById(R.id.namaP);
            jumlah=itemView.findViewById(R.id.jumlahP);
            harga=itemView.findViewById(R.id.hargaP);
            barangFoto=itemView.findViewById(R.id.fotoBarangP);
            tambahKeranjang=itemView.findViewById(R.id.tambahKeranjang);

        }
    }
}
