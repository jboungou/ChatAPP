package com.example.chatfumu.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatfumu.Listener.ItemListener;
import com.example.chatfumu.Models.Item;
import com.example.chatfumu.adapter.ListAdapter;
import com.example.chatfumu.databinding.ActivityGetFavoritesBinding;
import com.example.chatfumu.utilities.Constants;
import com.example.chatfumu.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class GetFavorites extends AppCompatActivity implements ItemListener{

    PreferenceManager preferenceManager;
    private ActivityGetFavoritesBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGetFavoritesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        getItem();
    }

    private void getItem() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_FAVORITES)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null){
                        List<Item> items = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            /*if (currentId.equals(queryDocumentSnapshot.getId())){
                                continue;
                            }*/
                            Item item = new Item();
                            item.pseudo = queryDocumentSnapshot.getString(Constants.KEY_PSEUDO);
                            item.prix = queryDocumentSnapshot.getString(Constants.KEY_PRIX);
                            item.adress = queryDocumentSnapshot.getString(Constants.KEY_ADDRESS);
                            item.image = queryDocumentSnapshot.getString(Constants.KEY_PHOTO1);
                            item.description = queryDocumentSnapshot.getString(Constants.KEY_DESCRIPTION);
                            item.id = queryDocumentSnapshot.getId();
                            items.add(item);
                        }
                        if(items.size() > 0){
                            ListAdapter listAdapter = new ListAdapter(items, (ItemListener) this);
                            binding.itemRecyclerVew.setAdapter(listAdapter);
                            binding.itemRecyclerVew.setVisibility(View.VISIBLE);
                        }else{
                            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();;
                        }
                    }else{
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();;
                    }
                });
    }

    private void loading(boolean isLoading) {

        if (isLoading){
            binding.itemRecyclerVew.setVisibility(View.INVISIBLE);
            binding.progressbar.setVisibility(View.VISIBLE);
        }else{
            binding.progressbar.setVisibility(View.INVISIBLE);
            binding.itemRecyclerVew.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClickedItem(Item item) {
        Intent intent = new Intent(getApplicationContext(), DescriptionActivity.class);
        intent.putExtra("name", item);
        startActivity(intent);
    }
}
