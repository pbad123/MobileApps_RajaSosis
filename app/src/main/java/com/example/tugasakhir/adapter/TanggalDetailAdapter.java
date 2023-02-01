package com.example.tugasakhir.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tugasakhir.R;
import com.example.tugasakhir.model.TanggalDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class TanggalDetailAdapter extends RecyclerView.Adapter<TanggalDetailAdapter.MyViewHolder>{
    private Context context;
    private List<TanggalDetail> list;

    public TanggalDetailAdapter(Context context, List<TanggalDetail> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_detail_tanggal, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TanggalDetail tanggalDetail = list.get(position);
        String tanggal = tanggalDetail.getTanggal();
        String stok = tanggalDetail.getStok();
        String id = tanggalDetail.getId();

        holder.tanggal.setText(tanggal);
        holder.id.setText(id);
        holder.stok.setText(stok);

        holder.hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hapusTanggal(id);
            }
        });
    }

    private void hapusTanggal(String id) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference reference = db.collection("detail_tanggal").document(id);
        reference.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(context, "Tanggal Berhasil Dihapus !",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, "Tanggal Gagal Dihapus !",
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
        private TextView tanggal, id, stok;
        private ImageButton hapus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tanggal = (TextView) itemView.findViewById(R.id.tanggalDT);
            id = (TextView) itemView.findViewById(R.id.idTanggalDT);
            stok = (TextView) itemView.findViewById(R.id.stokDT);
            hapus = (ImageButton) itemView.findViewById(R.id.btnHapusDT);
        }
    }
}
