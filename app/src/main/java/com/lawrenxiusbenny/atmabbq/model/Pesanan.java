package com.lawrenxiusbenny.atmabbq.model;

import java.util.Date;

public class Pesanan {
    public int id_pesanan;
    public int id_menu;
    public int id_reservasi;
    public Date tanggal_pemesanan;
    public int jumlah;
    public Double sub_total;
    public int id_stok_keluar;

    public String nama_customer;

    public String gambar_menu;
    public Double harga_menu;
    public String nama_menu;
    public int serving_size;

    public int stok_bahan;


    public Pesanan() {
    }

    public Pesanan(String nama_customer) {
        this.nama_customer = nama_customer;
    }

    public Pesanan(int id_pesanan,int id_menu, int id_reservasi, int jumlah, Double sub_total, int id_stok_keluar,
                   String nama_customer, String gambar_menu, Double harga_menu, String nama_menu, int serving_size, int stok_bahan) {
        this.id_pesanan = id_pesanan;
        this.id_menu = id_menu;
        this.id_reservasi = id_reservasi;
        this.jumlah = jumlah;
        this.sub_total = sub_total;
        this.id_stok_keluar = id_stok_keluar;
        this.nama_customer = nama_customer;
        this.gambar_menu = gambar_menu;
        this.harga_menu = harga_menu;
        this.nama_menu = nama_menu;
        this.serving_size = serving_size;
        this.stok_bahan = stok_bahan;
    }

    public Pesanan(int id_menu, int id_reservasi, Date tanggal_pemesanan, int jumlah, Double sub_total, int id_stok_keluar, String nama_customer, String gambar_menu, Double harga_menu, String nama_menu) {
        this.id_pesanan = id_pesanan;
        this.id_menu = id_menu;
        this.id_reservasi = id_reservasi;
        this.tanggal_pemesanan = tanggal_pemesanan;
        this.jumlah = jumlah;
        this.sub_total = sub_total;
        this.id_stok_keluar = id_stok_keluar;
        this.nama_customer = nama_customer;
        this.gambar_menu = gambar_menu;
        this.harga_menu = harga_menu;
        this.nama_menu = nama_menu;
    }

    public int getId_pesanan() {
        return id_pesanan;
    }

    public void setId_pesanan(int id_pesanan) {
        this.id_pesanan = id_pesanan;
    }

    public int getId_menu() {
        return id_menu;
    }

    public void setId_menu(int id_menu) {
        this.id_menu = id_menu;
    }

    public int getId_reservasi() {
        return id_reservasi;
    }

    public void setId_reservasi(int id_reservasi) {
        this.id_reservasi = id_reservasi;
    }

    public Date getTanggal_pemesanan() {
        return tanggal_pemesanan;
    }

    public void setTanggal_pemesanan(Date tanggal_pemesanan) {
        this.tanggal_pemesanan = tanggal_pemesanan;
    }

    public int getJumlah() {
        return jumlah;
    }

    public void setJumlah(int jumlah) {
        this.jumlah = jumlah;
    }

    public Double getSub_total() {
        return sub_total;
    }

    public void setSub_total(Double sub_total) {
        this.sub_total = sub_total;
    }

    public String getStringSubTotal(){
        if(sub_total == 0 ){
            return "";
        }else{
            return String.valueOf(sub_total);
        }
    }

    public int getId_stok_keluar() {
        return id_stok_keluar;
    }

    public void setId_stok_keluar(int id_stok_keluar) {
        this.id_stok_keluar = id_stok_keluar;
    }

    public String getNama_customer() {
        return nama_customer;
    }

    public void setNama_customer(String nama_customer) {
        this.nama_customer = nama_customer;
    }

    public String getGambar_menu() {
        return gambar_menu;
    }

    public void setGambar_menu(String gambar_menu) {
        this.gambar_menu = gambar_menu;
    }

    public Double getHarga_menu() {
        return harga_menu;
    }

    public void setHarga_menu(Double harga_menu) {
        this.harga_menu = harga_menu;
    }

    public String getStringHarga(){
        if(harga_menu == 0 ){
            return "";
        }else{
            return String.valueOf(harga_menu);
        }
    }

    public String getNama_menu() {
        return nama_menu;
    }

    public void setNama_menu(String nama_menu) {
        this.nama_menu = nama_menu;
    }

    public int getServing_size() {
        return serving_size;
    }

    public void setServing_size(int serving_size) {
        this.serving_size = serving_size;
    }

    public int getStok_bahan() {
        return stok_bahan;
    }

    public void setStok_bahan(int stok_bahan) {
        this.stok_bahan = stok_bahan;
    }
}
