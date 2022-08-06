package com.gurbuzkaanakkaya.mywallet;

import android.graphics.Bitmap;

import java.sql.Blob;

public class Wallet {
    String name;
    int id;
    Bitmap bitmap;

    public Wallet(String name, int id, Bitmap bitmap) {
        this.name = name;
        this.id = id;
        this.bitmap = bitmap;
    }
}
