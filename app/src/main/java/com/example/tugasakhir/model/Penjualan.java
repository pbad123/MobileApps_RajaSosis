package com.example.tugasakhir.model;

public class Penjualan {
    String id_penjualan, tanggal_penjualan, total_penjuanan, userID, dibayar, kembalian, adminID, alamat, status_penjualan;

    public Penjualan () {
    }

    public Penjualan(String id_penjualan, String tanggal_penjualan, String total_penjuanan, String userID, String dibayar, String kembalian, String adminID, String alamat, String status_penjualan) {
        this.id_penjualan = id_penjualan;
        this.tanggal_penjualan = tanggal_penjualan;
        this.total_penjuanan = total_penjuanan;
        this.userID = userID;
        this.dibayar = dibayar;
        this.kembalian = kembalian;
        this.adminID = adminID;
        this.alamat = alamat;
        this.status_penjualan = status_penjualan;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getId_penjualan() {
        return id_penjualan;
    }

    public void setId_penjualan(String id_penjualan) {
        this.id_penjualan = id_penjualan;
    }

    public String getTanggal_penjualan() {
        return tanggal_penjualan;
    }

    public void setTanggal_penjualan(String tanggal_penjualan) {
        this.tanggal_penjualan = tanggal_penjualan;
    }

    public String getTotal_penjuanan() {
        return total_penjuanan;
    }

    public void setTotal_penjuanan(String total_penjuanan) {
        this.total_penjuanan = total_penjuanan;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDibayar() {
        return dibayar;
    }

    public void setDibayar(String dibayar) {
        this.dibayar = dibayar;
    }

    public String getKembalian() {
        return kembalian;
    }

    public void setKembalian(String kembalian) {
        this.kembalian = kembalian;
    }

    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    public String getStatus_penjualan() {
        return status_penjualan;
    }

    public void setStatus_penjualan(String status_penjualan) {
        this.status_penjualan = status_penjualan;
    }
}
