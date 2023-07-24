package com.example.chatfumu.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.chatfumu.Listener.ItemListener;
import com.example.chatfumu.Models.Item;
import com.example.chatfumu.adapter.ListAdapter;
import com.example.chatfumu.databinding.ActivitySearchBinding;
import com.example.chatfumu.utilities.Constants;
import com.example.chatfumu.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements ItemListener{

    PreferenceManager preferenceManager;

    private ActivitySearchBinding binding;
    private ArrayList<Item> searchList;
    private ListAdapter listAdapter;
    private FirebaseFirestore database;
    private Item item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        init();
        getItem();
    }

    private void init(){
        searchList = new ArrayList<>();
        listAdapter = new ListAdapter(searchList, (ItemListener) this);
        binding.searchRecycleView.setAdapter(listAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void getItem() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_SEARCHED_ITEM)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null){
                        List<Item> items = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            item = new Item();
                            item.pseudo = queryDocumentSnapshot.getString(Constants.KEY_PSEUDO);
                            item.prix = String.valueOf(queryDocumentSnapshot.getLong(Constants.KEY_PRIX));
                            item.adress = queryDocumentSnapshot.getString(Constants.KEY_ADDRESS);
                            item.image = queryDocumentSnapshot.getString(Constants.KEY_PHOTO1);
                            item.photo1 = queryDocumentSnapshot.getString(Constants.KEY_PHOTO1);
                            item.photo2 = queryDocumentSnapshot.getString(Constants.KEY_PHOTO2);
                            item.photo3 = queryDocumentSnapshot.getString(Constants.KEY_PHOTO3);
                            item.description = queryDocumentSnapshot.getString(Constants.KEY_DESCRIPTION);
                            item.id = queryDocumentSnapshot.getId();
                            items.add(item);
                        }
                        if(items.size() > 0){
                            ListAdapter listAdapter = new ListAdapter(items, (ItemListener) this);
                            binding.searchRecycleView.setAdapter(listAdapter);
                            binding.searchRecycleView.setVisibility(View.VISIBLE);
                        }else{
                            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();;
                        }
                    }else{
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClickedItem(Item item) {
        Intent intent = new Intent(getApplicationContext(), DescriptionActivity.class);
        intent.putExtra(Constants.KEY_ITEM, item);
        startActivity(intent);
    }
}