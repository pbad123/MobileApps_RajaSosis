package com.example.tugasakhir.model;

public class DiskonDetail {
    String id, jumlah, diskon;

    public DiskonDetail (){
    }

    public DiskonDetail(String id, String jumlah, String diskon) {
        this.id = id;
        this.jumlah = jumlah;
        this.diskon = diskon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public String getDiskon() {
        return diskon;
    }

    public void setDiskon(String diskon) {
        this.diskon = diskon;
    }
}
