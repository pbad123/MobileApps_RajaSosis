package com.example.tugasakhir.model;

public class TanggalDetail {
    String tanggal, stok, id;

    public TanggalDetail (){
    }

    public TanggalDetail(String tanggal, String stok, String id) {
        this.tanggal = tanggal;
        this.stok = stok;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getStok() {
        return stok;
    }

    public void setStok(String stok) {
        this.stok = stok;
    }
}
