package com.lawrenxiusbenny.atmabbq.model;

import java.io.Serializable;

public class Menu {
    public int id_menu;
    public String nama_menu;
    public double harga_menu;
    public String deskripsi_menu;
    public String gambar_menu;
    public int serving_size;
    public int stok_bahan;

    public Menu() {
    }

    public Menu(int id_menu, String nama_menu, double harga_menu, String deskripsi_menu, String gambar_menu, int serving_size, int stok_bahan) {
        this.id_menu = id_menu;
        this.nama_menu = nama_menu;
        this.harga_menu = harga_menu;
        this.deskripsi_menu = deskripsi_menu;
        this.gambar_menu = gambar_menu;
        this.serving_size = serving_size;
        this.stok_bahan = stok_bahan;
    }

    public int getId_menu() {
        return id_menu;
    }

    public void setId_menu(int id_menu) {
        this.id_menu = id_menu;
    }

    public String getNama_menu() {
        return nama_menu;
    }

    public void setNama_menu(String nama_menu) {
        this.nama_menu = nama_menu;
    }

    public double getHarga_menu() {
        return harga_menu;
    }

    public String getStringHarga(){
        if(harga_menu == 0 ){
            return "";
        }else{
            return String.valueOf(harga_menu);
        }
    }

    public void setHarga_menu(double harga_menu) {
        this.harga_menu = harga_menu;
    }

    public String getDeskripsi_menu() {
        return deskripsi_menu;
    }

    public void setDeskripsi_menu(String deskripsi_menu) {
        this.deskripsi_menu = deskripsi_menu;
    }

    public String getGambar_menu() {
        return gambar_menu;
    }

    public void setGambar_menu(String gambar_menu) {
        this.gambar_menu = gambar_menu;
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
