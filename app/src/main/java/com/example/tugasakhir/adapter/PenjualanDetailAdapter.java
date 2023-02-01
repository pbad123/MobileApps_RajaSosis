package com.example.tugasakhir.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tugasakhir.R;
import com.example.tugasakhir.model.PenjualanDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PenjualanDetailAdapter extends RecyclerView.Adapter<PenjualanDetailAdapter.MyViewHolder> {
    private Context context;
    private List<PenjualanDetail> list;

    public PenjualanDetailAdapter(Context context, List<PenjualanDetail> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_penjualan_detail, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        PenjualanDetail penjualanDetail = list.get(position);
        String id_penjualan = penjualanDetail.getId_penjualan();
        String id_keranjang = penjualanDetail.getId_keranjang();
        String id_barang = penjualanDetail.getId_barang();
        String jumlah_barang = penjualanDetail.getJumlah_barang();
        String sub_total = penjualanDetail.getSub_total();
        String total_modal = penjualanDetail.getTotal_modal();
        String untung = penjualanDetail.getUntung();
        String hasilTotal = penjualanDetail.getHasilTotal();
        String diskon = penjualanDetail.getDiskon();

        cariNama(id_barang, holder);
        holder.jumlah.setText("x "+jumlah_barang);
        holder.total.setText("Rp."+sub_total);
        holder.modal.setText("Rp."+total_modal);
        holder.untung.setText("Rp."+untung);

        if(!diskon.equals("0")){
            holder.hasilTotal.setText(hasilTotal);
            holder.diskon.setText("-"+diskon);
            holder.total.setPaintFlags(holder.total.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.hasilTotal.setVisibility(View.VISIBLE);
            holder.diskon.setVisibility(View.VISIBLE);
        }

        holder.show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.modal.setVisibility(View.VISIBLE);
                holder.untung.setVisibility(View.VISIBLE);
                holder.text1.setVisibility(View.VISIBLE);
                holder.text2.setVisibility(View.VISIBLE);
                holder.show.setVisibility(View.GONE);
                holder.hide.setVisibility(View.VISIBLE);
            }
        });
        holder.hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.modal.setVisibility(View.GONE);
                holder.untung.setVisibility(View.GONE);
                holder.text1.setVisibility(View.GONE);
                holder.text2.setVisibility(View.GONE);
                holder.show.setVisibility(View.VISIBLE);
                holder.hide.setVisibility(View.GONE);
            }
        });
    }
    private void cariNama(String barangID, MyViewHolder view) {
        DocumentReference reference = FirebaseFirestore.getInstance().collection("barang").document(barangID);
        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            String nama = document.getString("Nama Barang");
                            String harga = document.getString("Harga Barang");
                            view.nama.setText(nama);
                            view.harga.setText(harga);
                        }else {
                            Toast.makeText(context, "Gagal Mengambil Nama User !",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView nama, harga, jumlah, total, modal, untung, text1, text2, hasilTotal, diskon;
        ImageButton show, hide;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nama = itemView.findViewById(R.id.namaBarangPD);
            jumlah = itemView.findViewById(R.id.jumlahPD);
            harga = itemView.findViewById(R.id.hargaPD);
            total = itemView.findViewById(R.id.totalPD);
            hasilTotal = itemView.findViewById(R.id.hasilTotalPD);
            diskon = itemView.findViewById(R.id.diskonPD);
            modal = itemView.findViewById(R.id.modalPD);
            untung = itemView.findViewById(R.id.keuntunganPD);
            show = itemView.findViewById(R.id.showPD);
            hide = itemView.findViewById(R.id.hidePD);
            text1 =itemView.findViewById(R.id.textPD1);
            text2 =itemView.findViewById(R.id.textPD2);
        }
    }
}
