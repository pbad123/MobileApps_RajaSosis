package com.example.tugasakhir.model;

public class KeranjangBarang {
    String id, idBarang, nama, harga, jumlah, total, foto, beli, tanggal, diskon;

    public KeranjangBarang(){

    }

    public KeranjangBarang(String id, String idBarang, String nama, String harga, String jumlah, String total, String foto, String beli, String tanggal, String diskon) {
        this.id = id;
        this.idBarang = idBarang;
        this.nama = nama;
        this.harga = harga;
        this.jumlah = jumlah;
        this.total = total;
        this.foto = foto;
        this.beli = beli;
        this.tanggal = tanggal;
        this.diskon = diskon;
    }

    public String getDiskon() {
        return diskon;
    }

    public void setDiskon(String diskon) {
        this.diskon = diskon;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getBeli() {
        return beli;
    }

    public void setBeli(String beli) {
        this.beli = beli;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdBarang() {
        return idBarang;
    }

    public void setIdBarang(String idBarang) {
        this.idBarang = idBarang;
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

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
