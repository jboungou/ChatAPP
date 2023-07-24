package com.example.chatfumu.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatfumu.Listener.ItemListener;
import com.example.chatfumu.Models.Item;
import com.example.chatfumu.databinding.ItemActivityHomeBinding;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.elementmodel> {

    List<Item> mylist;
    ItemListener itemListener;

    public ListAdapter(List<Item> mylist, ItemListener itemListener){
        this.mylist = mylist;
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public elementmodel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemActivityHomeBinding itemActivityHomeBinding = ItemActivityHomeBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new elementmodel(itemActivityHomeBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull elementmodel holder, int position) {

        holder.setItemData(mylist.get(position));
    }

    @Override
    public int getItemCount() {
        return mylist.size();
    }

    public class elementmodel extends RecyclerView.ViewHolder {

        private ItemActivityHomeBinding binding;

        public elementmodel(ItemActivityHomeBinding itemActivityHomeBinding) {
            super(itemActivityHomeBinding.getRoot());
            binding = itemActivityHomeBinding;
        }

        public void setItemData(Item item) {

            binding.affichquartier.setText(item.adress);
            binding.affichprix.setText(String.valueOf(item.prix));
            binding.titre.setText(item.article);
            binding.photo.setImageBitmap(getItemImage(item.image));
            binding.getRoot().setOnClickListener(v -> itemListener.onClickedItem(item));
        }

        private Bitmap getItemImage(String encodedphoto) {

            byte[] bytes = android.util.Base64.decode(encodedphoto, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }

    }
}