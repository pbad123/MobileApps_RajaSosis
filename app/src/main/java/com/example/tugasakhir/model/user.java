package com.example.tugasakhir.model;

public class user {
    private String id, email, nama, nomor, status, hutang;

    public user(){
    }

    public user(String id, String email, String nama, String nomor){
        this.id=id;
        this.email=email;
        this.nama=nama;
        this.nomor=nomor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHutang() {
        return hutang;
    }

    public void setHutang(String hutang) {
        this.hutang = hutang;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNomor() {
        return nomor;
    }

    public void setNomor(String nomor) {
        this.nomor = nomor;
    }
}
