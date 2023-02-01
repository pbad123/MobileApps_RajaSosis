package com.example.tugasakhir.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tugasakhir.R;
import com.example.tugasakhir.storage_detail;
import com.example.tugasakhir.model.barang;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class barangAdapter extends RecyclerView.Adapter<barangAdapter.MyHolder>{
    private Context context;
    private List<barang> list;

    public barangAdapter(Context context, List<barang> list){
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_barang, parent, false);
        return new MyHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        barang Barang = list.get(position);
        String id = Barang.getId();
        String nama = Barang.getNama();
        String harga = Barang.getHarga();
        String beli = Barang.getBeli();
        String foto = Barang.getBarangFoto();

        holder.nama.setText(nama);
        ambilDataStok(id, holder.jumlah);
        holder.harga.setText(harga);
        holder.beli.setText(beli);
        Glide.with(context).load(foto).into(holder.barangFoto);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, storage_detail.class);
                intent.putExtra("Id Barang", id);
                intent.putExtra("Nama Barang", nama);
                intent.putExtra("Harga Barang", harga);
                intent.putExtra("Harga Beli", beli);
                intent.putExtra("Foto Barang", foto);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }
    private int stok=0;
    private void ambilDataStok(String id, TextView view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("detail_kedaluarsa")
                .whereEqualTo("Id Barang", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            stok=0;
                            for(QueryDocumentSnapshot document : task.getResult()){
                                String jumlah = document.getString("Jumlah Barang");
                                stok = stok + Integer.parseInt(jumlah);
                                view.setText(""+stok);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Data Stok Gagal Diambil !",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        TextView nama, jumlah, harga, beli, exp;
        ImageView barangFoto;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            nama=itemView.findViewById(R.id.namaR);
            jumlah=itemView.findViewById(R.id.jumlahR);
            harga=itemView.findViewById(R.id.hargaR);
            beli=itemView.findViewById(R.id.beliR);
            barangFoto=itemView.findViewById(R.id.fotoBarangR);
        }
    }
}
