package com.nue.photohunting;

public class EventModel {
    private String id, nama, mulai;

    public EventModel() {

    }

    public EventModel(String id, String nama, String mulai) {
        this.id = id;
        this.nama = nama;
        this.mulai = mulai;
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

    public String getMulai() {
        return mulai;
    }

    public void setMulai(String mulai) {
        this.mulai = mulai;
    }
}
