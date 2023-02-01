package com.example.tugasakhir.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tugasakhir.R;
import com.example.tugasakhir.model.Penjualan;
import com.example.tugasakhir.penjualanDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class PenjualanAdapter extends RecyclerView.Adapter<PenjualanAdapter.MyViewHolder> {
    private Context context;
    private List<Penjualan> list;

    public PenjualanAdapter(Context context, List<Penjualan> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_penjualan, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Penjualan penjualan = list.get(position);
        String id_penjualan = penjualan.getId_penjualan();
        String tanggal_penjualan = penjualan.getTanggal_penjualan();
        String total_penjualan = penjualan.getTotal_penjuanan();
        String userID = penjualan.getUserID();
        String kembalian = penjualan.getKembalian();
        String status_penjualan = penjualan.getStatus_penjualan();

        holder.id.setText(id_penjualan);
        if (userID.equals("Bukan Member")){
            holder.namaPembeli.setText(userID);
        }else{
            cariNama(userID, holder.namaPembeli);
        }
        holder.total.setText(total_penjualan);
        holder.kembalian.setText(kembalian);
        holder.tanggal.setText(tanggal_penjualan);
        holder.status.setText(status_penjualan);
        if(status_penjualan.equals("ngutang")){
            holder.status.setTextColor(context.getResources().getColor(R.color.red));
            holder.textKembalian.setText("Hutang\t\t\t\t:");
        }else if(status_penjualan.equals("selesai")){
            holder.status.setTextColor(context.getResources().getColor(R.color.hijautua));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, penjualanDetail.class);
                intent.putExtra("id_penjualan", id_penjualan);
                context.startActivity(intent);
            }
        });
    }

    private void cariNama(String userID, TextView view) {
        DocumentReference reference = FirebaseFirestore.getInstance().collection("userInfo").document(userID);
        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            String nama = document.getString("Nama User");
                            view.setText(nama);
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

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView id, namaPembeli, total, kembalian, tanggal, status, textKembalian;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.idPenjualanT);
            namaPembeli = itemView.findViewById(R.id.namaPembeliT);
            total = itemView.findViewById(R.id.totalBelanjaanT);
            kembalian = itemView.findViewById(R.id.kembalianT);
            tanggal = itemView.findViewById(R.id.tanggalPenjualanT);
            status = itemView.findViewById(R.id.statusT);
            textKembalian = itemView.findViewById(R.id.textT3);
        }
    }
}
