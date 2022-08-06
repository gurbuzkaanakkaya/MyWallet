package com.gurbuzkaanakkaya.mywallet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.gurbuzkaanakkaya.mywallet.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    ArrayList<Wallet> walletArrayList;
    walletAdapter walletAdapter;
    SharedPreferences sharedPreferences;
    int lastpayment;
    int losepayment;
    int payment;
    int lose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Intent intent = getIntent();
        payment = intent.getIntExtra("payment",lastpayment);
        lose = intent.getIntExtra("losepayment",0);
        sharedPreferences = this.getSharedPreferences("com.gurbuzkaanakkaya.mywallet",Context.MODE_PRIVATE);
        lastpayment = sharedPreferences.getInt("payment",0);
        losepayment = sharedPreferences.getInt("losepayment",0);
        binding.textView2.setText("Toplam Tutar: "+lastpayment);
        walletArrayList = new ArrayList<>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        walletAdapter = new walletAdapter(walletArrayList);
        binding.recyclerView.setAdapter(walletAdapter);
        getData();
        storedpay();
    }
    public void storedpay(){
        if(payment != 0 ) {
            lastpayment += payment;}
            if(lose !=0) {
                lastpayment -= lose;
                //System.out.println(lastpayment);
            }
            sharedPreferences.edit().putInt("payment", lastpayment).apply();
            binding.textView2.setText("Toplam Tutar: "+ lastpayment);

    }
    private void getData(){
        try {
            SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("Wallet",MODE_PRIVATE,null);

            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM wallet",null);
            int nameIx = cursor.getColumnIndex("payname");
            int idIx = cursor.getColumnIndex("id");
            int imageIx = cursor.getColumnIndex("image");
            while (cursor.moveToNext()){
                String name = cursor.getString(nameIx);
                int id = cursor.getInt(idIx);
                byte[] bytes = cursor.getBlob(imageIx);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                Wallet wallet = new Wallet(name,id,bitmap);
                walletArrayList.add(wallet);
            }
            walletAdapter.notifyDataSetChanged();
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.wallet_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_wallet){
            Intent intent = new Intent(this,WalletActivity.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}