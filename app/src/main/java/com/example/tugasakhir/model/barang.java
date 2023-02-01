package com.example.tugasakhir.model;

public class barang {
    private String id, nama, harga, beli, barangFoto;

    public barang(){

    }
    public barang(String nama, String harga, String beli, String barangFoto){
        this.nama=nama;
        this.harga=harga;
        this.beli=beli;
        this.barangFoto=barangFoto;
    }

    public String getBarangFoto() {
        return barangFoto;
    }

    public void setBarangFoto(String barangFoto) {
        this.barangFoto = barangFoto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getBeli() {
        return beli;
    }

    public void setBeli(String beli) {
        this.beli = beli;
    }
}
