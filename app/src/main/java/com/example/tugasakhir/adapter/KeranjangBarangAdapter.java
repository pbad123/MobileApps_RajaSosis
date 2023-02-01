package com.example.tugasakhir.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tugasakhir.R;
import com.example.tugasakhir.kasir;
import com.example.tugasakhir.model.KeranjangBarang;

import java.util.List;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class KeranjangBarangAdapter extends RecyclerView.Adapter<KeranjangBarangAdapter.MyViewHolder>{
    private Context context;
    private List<KeranjangBarang> list;

    public KeranjangBarangAdapter(Context context, List<KeranjangBarang> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_keranjang_barang, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        KeranjangBarang keranjangBarang = list.get(position);
        String id = keranjangBarang.getId();
        String idBarang = keranjangBarang.getIdBarang();
        String nama = keranjangBarang.getNama();
        String harga = keranjangBarang.getHarga();
        String jumlah = keranjangBarang.getJumlah();
        String total = keranjangBarang.getTotal();
        String foto = keranjangBarang.getFoto();
        String hargaBeli = keranjangBarang.getBeli();
        String tanggal = keranjangBarang.getTanggal();
        String diskon = keranjangBarang.getDiskon();

        Glide.with(context).load(foto).into(holder.fotoBarangKeranjang);
        holder.idBarangKeranjang.setText(idBarang);
        holder.namaBarangKeranjang.setText(nama);
        holder.hargaBarangKeranjang.setText(harga);
        holder.jumlahBarangKeranjang.setText(jumlah);
        holder.totalBelanjaanKeranjang.setText(total);
        holder.tanggalKedaluarsaKeranjang.setText(tanggal);

        if(!diskon.equals("0")){
            holder.totalBelanjaanKeranjang.setPaintFlags(holder.totalBelanjaanKeranjang.getPaintFlags() | (Paint.STRIKE_THRU_TEXT_FLAG));
            holder.diskonBelanjaanKeranjang.setText(diskon);
            holder.diskonBelanjaanKeranjang.setVisibility(View.VISIBLE);
        }

        holder.btnHapusKeranjang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                easyDB.deleteRow(1, id);
                Toast.makeText(context, "Produk Berhasil Dihapus dari Keranjang !",
                        Toast.LENGTH_SHORT).show();
                list.remove(holder.getAdapterPosition());
                notifyItemChanged(holder.getAdapterPosition());
                notifyDataSetChanged();

                int TB = Integer.parseInt((((kasir)context).totalHarga.getText().toString().trim().replace("Rp.", "")));
                int totalBelanja = TB - Integer.parseInt(total);
                ((kasir)context).TotalBayar = 0;
                ((kasir)context).totalHarga.setText("Rp."+totalBelanja);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView fotoBarangKeranjang;
        TextView idBarangKeranjang, namaBarangKeranjang, hargaBarangKeranjang, jumlahBarangKeranjang, totalBelanjaanKeranjang,
                tanggalKedaluarsaKeranjang, diskonBelanjaanKeranjang;
        ImageButton btnHapusKeranjang;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            fotoBarangKeranjang = (ImageView) itemView.findViewById(R.id.fotoBarangKeranjang);
            idBarangKeranjang = (TextView) itemView.findViewById(R.id.idBarangKeranjang);
            namaBarangKeranjang = (TextView) itemView.findViewById(R.id.namaBarangKeranjang);
            hargaBarangKeranjang = (TextView) itemView.findViewById(R.id.hargaBarangKeranjang);
            jumlahBarangKeranjang = (TextView) itemView.findViewById(R.id.jumlahBarangKeranjang);
            totalBelanjaanKeranjang = (TextView) itemView.findViewById(R.id.totalBelanjaanKeranjang);
            diskonBelanjaanKeranjang = (TextView) itemView.findViewById(R.id.diskonBelanjaanKeranjang);
            tanggalKedaluarsaKeranjang = (TextView) itemView.findViewById(R.id.tanggalKedaluarsaKeranjang);
            btnHapusKeranjang = (ImageButton) itemView.findViewById(R.id.btnHapusKeranjang);
        }
    }
}
