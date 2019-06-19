package com.example.hi.album;

public class ThongtinAlbum {
    String ten;
    String duongdan;
    Integer soluong;

    public ThongtinAlbum(String ten, String duongdan, Integer soluong) {
        this.ten = ten;
        this.duongdan = duongdan;
        this.soluong = soluong;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getDuongdan() {
        return duongdan;
    }

    public void setDuongdan(String duongdan) {
        this.duongdan = duongdan;
    }

    public Integer getSoluong() {
        return soluong;
    }

    public void setSoluong(Integer soluong) {
        this.soluong = soluong;
    }
}
