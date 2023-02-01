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
import com.example.tugasakhir.model.DiskonDetail;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class DiskonDetailAdapter extends RecyclerView.Adapter<DiskonDetailAdapter.MyViewHolder> {
    Context context;
    List<DiskonDetail> list;

    public DiskonDetailAdapter(Context context, List<DiskonDetail> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_detail_diskon, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DiskonDetail diskonDetail = list.get(position);
        String idDiskon = diskonDetail.getId();
        String jumlahDiskon = diskonDetail.getJumlah();
        String banyakDiskon = diskonDetail.getDiskon();

        holder.id.setText(idDiskon);
        holder.jumlah.setText(jumlahDiskon);
        holder.diskon.setText(banyakDiskon);
        holder.hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hapusDiskon(idDiskon);
            }
        });
    }

    private void hapusDiskon(String idDiskon) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference reference = db.collection("detail_diskon").document(idDiskon);
        reference.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(context, "Diskon Berhasil Dihapus !",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(context, "Diskon Gagal Dihapus !",
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
        private TextView id, jumlah, diskon;
        private ImageButton hapus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.idDiskonDD);
            jumlah = (TextView) itemView.findViewById(R.id.jumlahPembelianDD);
            diskon = (TextView) itemView.findViewById(R.id.banyakDiskonDD);
            hapus = (ImageButton) itemView.findViewById(R.id.btnHapusDD);
        }
    }
}
