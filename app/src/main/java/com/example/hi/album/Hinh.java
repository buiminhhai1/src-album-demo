package com.example.hi.album;

import android.support.media.ExifInterface;

import java.io.File;
import java.io.IOException;
import java.util.Date;

// Class chứa thông tin một ảnh trong Project
public class Hinh {

    String duongdan;
    String tenhinh;
    Integer addDate;
    boolean check;
    ExifInterface exif = null;

    public Hinh(String duongdan, String tenhinh, Integer adddDate) {
        this.duongdan = duongdan;
        this.tenhinh = tenhinh;
        this.addDate = adddDate;
        this.check = false;
        try{
            exif = new ExifInterface(duongdan);
        } catch (IOException e) {
            exif = null;
        }
    }

    public String getDuongdan() {
        return duongdan;
    }

    public void setDuongdan(String duongdan) {
        this.duongdan = duongdan;
    }

    public String getTenhinh() {
        return tenhinh;
    }

    public void setTenhinh(String tenhinh) {
        this.tenhinh = tenhinh;
    }

    public Integer getAddDate() {
        return addDate;
    }

    public void setAddDate(Integer adddDate) {
        this.addDate = adddDate;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        Hinh target = (Hinh) obj;
        return (this.tenhinh.equals(target.tenhinh) &&
                this.duongdan.equals(target.duongdan) &&
                this.addDate.equals(target.addDate));
    }
}
