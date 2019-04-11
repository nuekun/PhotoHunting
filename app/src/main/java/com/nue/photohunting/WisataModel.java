package com.nue.photohunting;

public class WisataModel {
    private String nama , id;

    public WisataModel() {

    }

    public WisataModel(String nama, String id) {
        this.nama = nama;
        this.id = id;
    }

    public String getnama() {
        return nama;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setnama(String nama) {
        this.nama = nama;
    }
}
