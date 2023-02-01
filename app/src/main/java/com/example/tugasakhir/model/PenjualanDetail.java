package com.example.tugasakhir.model;

public class PenjualanDetail {
    String id_penjualan, id_keranjang, id_barang, jumlah_barang, sub_total, total_modal, untung, hasilTotal, diskon;

    public PenjualanDetail (){
    }

    public PenjualanDetail(String id_penjualan, String id_keranjang, String id_barang, String jumlah_barang, String sub_total, String total_modal, String untung, String hasilTotal, String diskon) {
        this.id_penjualan = id_penjualan;
        this.id_keranjang = id_keranjang;
        this.id_barang = id_barang;
        this.jumlah_barang = jumlah_barang;
        this.sub_total = sub_total;
        this.total_modal = total_modal;
        this.untung = untung;
        this.hasilTotal = hasilTotal;
        this.diskon = diskon;
    }

    public String getHasilTotal() {
        return hasilTotal;
    }

    public void setHasilTotal(String hasilTotal) {
        this.hasilTotal = hasilTotal;
    }

    public String getDiskon() {
        return diskon;
    }

    public void setDiskon(String diskon) {
        this.diskon = diskon;
    }

    public String getId_penjualan() {
        return id_penjualan;
    }

    public void setId_penjualan(String id_penjualan) {
        this.id_penjualan = id_penjualan;
    }

    public String getId_keranjang() {
        return id_keranjang;
    }

    public void setId_keranjang(String id_keranjang) {
        this.id_keranjang = id_keranjang;
    }

    public String getId_barang() {
        return id_barang;
    }

    public void setId_barang(String id_barang) {
        this.id_barang = id_barang;
    }

    public String getJumlah_barang() {
        return jumlah_barang;
    }

    public void setJumlah_barang(String jumlah_barang) {
        this.jumlah_barang = jumlah_barang;
    }

    public String getSub_total() {
        return sub_total;
    }

    public void setSub_total(String sub_total) {
        this.sub_total = sub_total;
    }

    public String getTotal_modal() {
        return total_modal;
    }

    public void setTotal_modal(String total_modal) {
        this.total_modal = total_modal;
    }

    public String getUntung() {
        return untung;
    }

    public void setUntung(String untung) {
        this.untung = untung;
    }
}
