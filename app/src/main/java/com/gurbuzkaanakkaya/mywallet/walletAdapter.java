package com.gurbuzkaanakkaya.mywallet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gurbuzkaanakkaya.mywallet.databinding.RecyclerRowBinding;

import java.sql.Blob;
import java.util.ArrayList;

public class walletAdapter extends RecyclerView.Adapter<walletAdapter.WalletHolder>{
    ArrayList<Wallet> arrayList;
    public  walletAdapter(ArrayList<Wallet> arrayList){
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public WalletHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new WalletHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull WalletHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.recyclerViewImageView.setImageBitmap(arrayList.get(position).bitmap);
        holder.binding.recyclerViewTextView.setText(arrayList.get(position).name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(),WalletActivity.class);
                intent.putExtra("info","old");
                intent.putExtra("walletId",arrayList.get(position).id);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class WalletHolder extends  RecyclerView.ViewHolder{
        private  RecyclerRowBinding binding;
        public WalletHolder(RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
