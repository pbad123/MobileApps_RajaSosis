package com.example.tugasakhir.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tugasakhir.R;
import com.example.tugasakhir.infoUserAdmin;
import com.example.tugasakhir.infoUserDetail;
import com.example.tugasakhir.model.user;
import com.example.tugasakhir.penjualanDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class userAdapter extends RecyclerView.Adapter<userAdapter.MyViewHolder>{
    private Context context;
    private List<user> list;

    public userAdapter(Context context, List<user> list){
        this.context=context;
        this.list=list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        user User = list.get(position);
        String id = User.getId();
        String email = User.getEmail();
        String nama = User.getNama();
        String nomor = User.getNomor();
        String status = User.getStatus();
        String hutang = User.getHutang();

        holder.email.setText(email);
        holder.nama.setText(nama);
        holder.nomor.setText(nomor);
        holder.status.setText(status);
        cariHutang(id, holder.hutang);
        if(holder.hutang.getText().equals("Hutang")){
            holder.hutang.setText("0");
        }

        //holder.hutang.setText(hutang);

        holder.hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hapusUser(id);
                list.remove(holder.getAdapterPosition());
                notifyItemChanged(holder.getAdapterPosition());
                notifyDataSetChanged();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, infoUserDetail.class);
                intent.putExtra("id_penjualan", id);
                intent.putExtra("Nama User", nama);
                intent.putExtra("Email", email);
                intent.putExtra("Nomor Telepon", nomor);
                context.startActivity(intent);
            }
        });
    }
    private int totalHutang;
    private void cariHutang(String id, TextView view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("penjualan")
                .whereEqualTo("User Id", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        totalHutang = 0;
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if(document.getString("Status").equals("ngutang")){
                                    String hutang = document.getString("Kembalian");
                                    double hutang1 = Double.parseDouble(hutang);
                                    totalHutang = totalHutang + (int) hutang1;
                                    view.setText("Rp."+totalHutang);
                                    view.setTextColor(context.getResources().getColor(R.color.red));
                                }else{
                                    String hutang ="0";
                                    view.setText(hutang);
                                }
                            }
                        }else{
                            String hutang ="0";
                            view.setText(hutang);
                        }
                    }
                });
    }

    private void hapusUser(String id) {
        DocumentReference document = FirebaseFirestore.getInstance().collection("userInfo").document(id);
        document.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(context, "Data Berhasil Dihapus !",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, "Data Gagal Dihapus !",
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
        private TextView email, nama, nomor, status, hutang;
        private ImageButton hapus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            email = itemView.findViewById(R.id.emailUser);
            nama = itemView.findViewById(R.id.namaUser);
            nomor = itemView.findViewById(R.id.nomorUser);
            status = itemView.findViewById(R.id.statusUser);
            hutang = itemView.findViewById(R.id.hutangUser);
            hapus = itemView.findViewById(R.id.hapusUser);
        }
    }
}
