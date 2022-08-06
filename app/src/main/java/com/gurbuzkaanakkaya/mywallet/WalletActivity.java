package com.gurbuzkaanakkaya.mywallet;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.gurbuzkaanakkaya.mywallet.databinding.ActivityMainBinding;
import com.gurbuzkaanakkaya.mywallet.databinding.ActivityWalletBinding;

import java.io.ByteArrayOutputStream;

public class WalletActivity extends AppCompatActivity {
    private ActivityWalletBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Bitmap selectedImage;
    SQLiteDatabase sqLiteDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWalletBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        registerLauncher();
        binding.button2.setEnabled(false);
        sqLiteDatabase = this.openOrCreateDatabase("Wallet",MODE_PRIVATE,null);
        Intent intent = getIntent();
        String info = intent.getStringExtra("info");
        if(info.equals("new")){
            binding.nameText.setText("");
            binding.payText.setText("");
            binding.yearText.setText("");
            binding.button.setVisibility(View.VISIBLE);
            binding.imageView.setImageResource(R.drawable.select);
        }else{
            int walletId = intent.getIntExtra("walletId",0);
            binding.button.setEnabled(false);
            binding.button2.setEnabled(true);
            try {
                Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM wallet WHERE id = ?",new String[]{String.valueOf(walletId)});
                int nameIx = cursor.getColumnIndex("payname");
                int payIx = cursor.getColumnIndex("pay");
                int yearIx = cursor.getColumnIndex("year");
                int imageIx = cursor.getColumnIndex("image");
                while (cursor.moveToNext()){
                    binding.nameText.setText(cursor.getString(nameIx));
                    binding.payText.setText(cursor.getString(payIx));
                    binding.yearText.setText(cursor.getString(yearIx));
                    byte[] bytes = cursor.getBlob(imageIx);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    binding.imageView.setImageBitmap(bitmap);
                }cursor.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public void delete(View view){
        Intent intent = getIntent();
        int walletId = intent.getIntExtra("walletId",0);
        try {
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS wallet (id INTEGER PRIMARY KEY, payname VARCHAR, pay VARCHAR, year VARCHAR, image BLOB)");
            Cursor cursor = sqLiteDatabase.rawQuery("DELETE FROM wallet WHERE id = ?",new String[]{String.valueOf(walletId)});
            int nameIx = cursor.getColumnIndex("payname");
            int payIx = cursor.getColumnIndex("pay");
            int yearIx = cursor.getColumnIndex("year");
            int imageIx = cursor.getColumnIndex("image");
            while (cursor.moveToNext()){
                binding.nameText.setText(cursor.getString(nameIx));
                binding.payText.setText(cursor.getString(payIx));
                binding.yearText.setText(cursor.getString(yearIx));
                byte[] bytes = cursor.getBlob(imageIx);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                binding.imageView.setImageBitmap(bitmap);
            }cursor.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        int losepayment = 0;
        losepayment = Integer.parseInt(binding.payText.getText().toString());
        Intent loseintent = new Intent(WalletActivity.this,MainActivity.class);
        loseintent.putExtra("losepayment",losepayment);
        loseintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loseintent);

    }
    public void save(View view){
        String name = binding.nameText.getText().toString();
        String pay = binding.payText.getText().toString();
        String year = binding.yearText.getText().toString();
        Bitmap smallImage = makeSmallerImage(selectedImage,300);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] byteArray= outputStream.toByteArray();

        try {
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS wallet (id INTEGER PRIMARY KEY, payname VARCHAR, pay VARCHAR, year VARCHAR, image BLOB)");

            String sqlString = "INSERT INTO wallet (payname, pay, year, image) VALUES (?, ?, ?, ?)";
            SQLiteStatement sqLiteStatement = sqLiteDatabase.compileStatement(sqlString);
            sqLiteStatement.bindString(1,name);
            sqLiteStatement.bindString(2,pay);
            sqLiteStatement.bindString(3,year);
            sqLiteStatement.bindBlob(4,byteArray);
            sqLiteStatement.execute();
        }catch (Exception e){
            e.printStackTrace();
        }
        int payment = 0;
        payment = Integer.parseInt(binding.payText.getText().toString());
        Intent intent = new Intent(WalletActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("payment",payment);
        startActivity(intent);
    }
    public  Bitmap makeSmallerImage(Bitmap image,int maximumSize){
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if(bitmapRatio > 1){
            //landscape
            width = maximumSize;
            height = (int) (width / bitmapRatio);
        }else{
            //portrait
            height = maximumSize;
            width = (int) (height * bitmapRatio);

        }
        return image.createScaledBitmap(image,width,height,true);
    }
    public void  selectImage(View view){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"Galeriye gitmek için izin gerekli.",Snackbar.LENGTH_INDEFINITE).setAction("Onayla", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //request permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            }else{
                //request permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }else{
            //gallery
            Intent intentToGalery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGalery);
        }
    }
    private void registerLauncher(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK){
                    Intent intentFromResult = result.getData();
                    if(intentFromResult != null){
                        Uri imageData = intentFromResult.getData();
                        //binding.imageView.setImageURI(imageData);
                        try {
                            if(Build.VERSION.SDK_INT >= 28){
                                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(),imageData);
                                selectedImage = ImageDecoder.decodeBitmap(source);
                                binding.imageView.setImageBitmap(selectedImage);
                            }else{
                                selectedImage = MediaStore.Images.Media.getBitmap(WalletActivity.this.getContentResolver(),imageData);
                                binding.imageView.setImageBitmap(selectedImage);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    //permision granted
                    Intent intentToGalery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGalery);
                }else{
                    Toast.makeText(WalletActivity.this, "İzin vermeniz gerekli.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}