package com.example.chatfumu.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatfumu.Listener.ItemListener;
import com.example.chatfumu.Models.Item;
import com.example.chatfumu.adapter.ListAdapter;
import com.example.chatfumu.databinding.ActivityHomeBinding;
import com.example.chatfumu.utilities.Constants;
import com.example.chatfumu.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements ItemListener{

    private ActivityHomeBinding binding;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        getItem();
    }

    private void getItem() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_POSTS)
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
                            item.article = queryDocumentSnapshot.getString(Constants.KEY_ARTICLE);
                            item.adress = queryDocumentSnapshot.getString(Constants.KEY_ADDRESS);
                            item.prix = String.valueOf(queryDocumentSnapshot.getLong(Constants.KEY_PRIX));
                            item.pseudo = queryDocumentSnapshot.getString(Constants.KEY_PSEUDO);
                            item.image = queryDocumentSnapshot.getString(Constants.KEY_PHOTO1);
                            item.description = queryDocumentSnapshot.getString(Constants.KEY_DESCRIPTION);
                            item.photo1 = queryDocumentSnapshot.getString(Constants.KEY_PHOTO1);
                            item.photo2 = queryDocumentSnapshot.getString(Constants.KEY_PHOTO2);
                            item.photo3 = queryDocumentSnapshot.getString(Constants.KEY_PHOTO3);
                            item.id = queryDocumentSnapshot.getId();
                            items.add(item);
                        }
                        if(items.size() > 0){
                            ListAdapter listAdapter = new ListAdapter(items, (ItemListener) this);
                            binding.itemRecyclerVew.setAdapter(listAdapter);
                            binding.itemRecyclerVew.setVisibility(View.VISIBLE);
                        }else{
                            showErrorMessage();
                        }
                    }else{
                        showErrorMessage();
                    }
                });
    }

    private void loading(boolean isLoading) {

        if (isLoading){
            binding.progressbar.setVisibility(View.VISIBLE);
        }else{
            binding.progressbar.setVisibility(View.INVISIBLE);
        }
    }

    private void showErrorMessage() {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickedItem(Item item) {

        Intent intent = new Intent(getApplicationContext(), DescriptionActivity.class);
        intent.putExtra(Constants.KEY_ITEM, item);
        startActivity(intent);
    }
}