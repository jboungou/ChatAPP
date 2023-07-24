package com.example.chatfumu.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatfumu.Listener.UserListener;
import com.example.chatfumu.Models.User;
import com.example.chatfumu.databinding.ItemContainerUserBinding;
import com.example.chatfumu.utilities.Constants;

import java.util.Base64;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.userViewHolder>{

     private final List<User> users;
     private final UserListener userlistener;

     public UserAdapter(List<User> users, UserListener userlistener) {
          this.users = users;
          this.userlistener = userlistener;
     }

     @NonNull
     @Override
     public UserAdapter.userViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
          ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
                  LayoutInflater.from(parent.getContext()),
                  parent,
                  false
          );
          return new userViewHolder(itemContainerUserBinding);
     }

     @Override
     public void onBindViewHolder(@NonNull UserAdapter.userViewHolder holder, int position) {
          holder.setUserData(users.get(position));
     }

     @Override
     public int getItemCount() {
          return users.size();
     }

     class userViewHolder extends RecyclerView.ViewHolder{

          ItemContainerUserBinding binding;

          userViewHolder(ItemContainerUserBinding itemContainerUserBinding) {
               super(itemContainerUserBinding.getRoot());
               binding = itemContainerUserBinding;
          }

          void setUserData(User user){
               binding.textName.setText(user.name);
               binding.textEmail.setText(user.email);
               binding.imageProfile.setImageBitmap(getuserImage(user.image));
               binding.getRoot().setOnClickListener(view -> userlistener.onUserClicked(user));
          }
     }

     private Bitmap getuserImage(String  encodedImage){
          byte[] bytes = android.util.Base64.decode(encodedImage, android.util.Base64.DEFAULT);
          return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
     }
}
