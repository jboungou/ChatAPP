package com.example.chatfumu.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatfumu.Models.Item;
import com.example.chatfumu.Models.User;
import com.example.chatfumu.R;
import com.example.chatfumu.databinding.ActivityStartBinding;
import com.example.chatfumu.utilities.Constants;
import com.example.chatfumu.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StartActivity extends AppCompatActivity {

    private ActivityStartBinding binding;
    PreferenceManager preferenceManager;
    private boolean Status;
    private Item mItem;
    private int Value_inter;
    private ChatActivity chatActivity;
    private FirebaseFirestore database;
    private User receiverId_user;
    private User senderId_user;
    List<Item> items;
    private String Pseudo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Init();
        createUserAlert();
        binding.rechercher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
        setListener();
        listenItemAdded();

    }

    private void Init(){
        preferenceManager = new PreferenceManager(getApplicationContext());
        items = new ArrayList<>();
        database = FirebaseFirestore.getInstance();
    }

    private void createUserAlert() {

        HashMap user = new HashMap<>();
        user.put(Constants.KEY_NAME, "Votre Alerte TRADE");
        user.put(Constants.KEY_EMAIL, "noreply.TradeAPP@yahoo.fr");
        user.put(Constants.KEY_PASSWORD, "12345");
        user.put(Constants.KEY_IMAGE, "");
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_NAME, "Votre Alerte TRADE")
                .get()
                .addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null
                    && task.getResult().getDocuments().size() > 0){
                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                    senderId_user = new User();
                    senderId_user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                    senderId_user.image = queryDocumentSnapshot.getString(Constants.KEY_PHOTO1);
                    senderId_user.id = queryDocumentSnapshot.getId();

                }
            }else{
                database.collection(Constants.KEY_COLLECTION_USERS)
                        .add(user)
                        .addOnSuccessListener(documentReference -> {
                            preferenceManager.putString(Constants.KEY_NAME, "Votre Alerte TRADE");
                            preferenceManager.putString(Constants.KEY_EMAIL, "noreply.TradeAPP@yahoo.fr");
                            preferenceManager.putString(Constants.KEY_PASSWORD, "12345");
                            preferenceManager.putString(Constants.KEY_IMAGE, "");
                        })
                        .addOnFailureListener(e -> {
                            // TBD
                        });
            }
        });

    }

    private void search(){
        loading(true);
        //Value_inter = Integer.parseInt(binding.prixMin.getText().toString());
        database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_POSTS)
                //.whereEqualTo(Constants.KEY_PSEUDO, binding.mItem.getText().toString())
                .whereGreaterThan(Constants.KEY_PRIX, Integer.parseInt(binding.prixMin.getText().toString()))
                //.whereLessThan(Constants.KEY_PRIX, binding.prixMax.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null){
                       // List<Item> items = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currentUserId.equals(queryDocumentSnapshot.getId())){
                                continue;
                            }
                            mItem = new Item();
                            mItem.pseudo = queryDocumentSnapshot.getString(Constants.KEY_PSEUDO);
                            mItem.photo1 = queryDocumentSnapshot.getString(Constants.KEY_PHOTO1);
                            mItem.photo2 = queryDocumentSnapshot.getString(Constants.KEY_PHOTO2);
                            mItem.photo3 = queryDocumentSnapshot.getString(Constants.KEY_PHOTO3);
                            mItem.prix = String.valueOf(queryDocumentSnapshot.getLong(Constants.KEY_PRIX));
                            mItem.description = queryDocumentSnapshot.getString(Constants.KEY_DESCRIPTION);
                            mItem.adress = queryDocumentSnapshot.getString(Constants.KEY_ADDRESS);
                            mItem.id = queryDocumentSnapshot.getId();
                            items.add(mItem);

                            searchedItemRegistered();
                        }
                        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                        startActivity(intent);
                    }else{
                        loading(false);
                        Intent intent = new Intent(getApplicationContext(), PageIntrouvable.class);
                        intent.putExtra("name", "No Result Found");
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loading(false);
                        Intent intent = new Intent(getApplicationContext(), PageIntrouvable.class);
                        intent.putExtra("name", e.toString());
                        startActivity(intent);
                    }
                });
    }

    private void listenItemAdded(){

        database.collection(Constants.KEY_COLLECTION_POSTS)
                .whereEqualTo(Constants.KEY_ARTICLE, "Casque Bose")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null){
                        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currentUserId.equals(queryDocumentSnapshot.getId())){
                                continue;
                            }

                            item = new Item();
                            item.article = queryDocumentSnapshot.getString(Constants.KEY_ARTICLE);
                            item.photo1 = queryDocumentSnapshot.getString(Constants.KEY_PHOTO1);
                            item.photo2 = queryDocumentSnapshot.getString(Constants.KEY_PHOTO2);
                            item.photo3 = queryDocumentSnapshot.getString(Constants.KEY_PHOTO3);
                            item.prix = String.valueOf(queryDocumentSnapshot.getLong(Constants.KEY_PRIX));
                            item.description = queryDocumentSnapshot.getString(Constants.KEY_DESCRIPTION);
                            item.adress = queryDocumentSnapshot.getString(Constants.KEY_ADDRESS);
                            item.pseudo = queryDocumentSnapshot.getString(Constants.KEY_PSEUDO);
                            Pseudo = item.pseudo;
                            items.add(item);
                        }

                    }/* to be completed*/
                });

        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_ITEM, item);
        intent.putExtra(Constants.KEY_SENDER, senderId_user);
        startActivity(intent);
    }


    private Item item;

/*    private void settleMessage() {

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_NAME, item.pseudo)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null
                            && task.getResult().getDocuments().size() > 0){
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            receiverId_user = new User();
                            receiverId_user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            receiverId_user.image = queryDocumentSnapshot.getString(Constants.KEY_PHOTO1);
                            receiverId_user.id = queryDocumentSnapshot.getId();

                        }
                    }
                });


    }*/

    private void searchedItemRegistered() {

        loading(true);
        HashMap<String, Object> searchedItem = new HashMap<>();
        searchedItem.put(Constants.KEY_PHOTO1, mItem.photo1);
        searchedItem.put(Constants.KEY_PHOTO2, mItem.photo2);
        searchedItem.put(Constants.KEY_PHOTO3, mItem.photo3);
        searchedItem.put(Constants.KEY_DESCRIPTION, mItem.description);
        searchedItem.put(Constants.KEY_ADDRESS, mItem.adress);
        searchedItem.put(Constants.KEY_PSEUDO, mItem.pseudo);
        searchedItem.put(Constants.KEY_PRIX, Integer.parseInt(mItem.prix));

        database.collection(Constants.KEY_COLLECTION_SEARCHED_ITEM)
                .add(searchedItem)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                    preferenceManager.putString(Constants.KEY_PSEUDO, mItem.pseudo);
                    preferenceManager.putInteger(Constants.KEY_PRIX, Integer.parseInt(mItem.prix));
                    preferenceManager.putString(Constants.KEY_ADDRESS, mItem.adress);
                    preferenceManager.putString(Constants.KEY_PHOTO1, mItem.photo1);
                    preferenceManager.putString(Constants.KEY_PHOTO2, mItem.photo2);
                    preferenceManager.putString(Constants.KEY_PHOTO3, mItem.photo3);
                })
                .addOnFailureListener(e -> {
                    loading(false);
                    Intent intent = new Intent(getApplicationContext(), PageIntrouvable.class);
                    startActivity(intent);
                });
    }

    private void loading(boolean isLoading) {
        if (isLoading){
            binding.rechercher.setVisibility(View.INVISIBLE);
            binding.progressbar.setVisibility(View.VISIBLE);
        }else{
            binding.progressbar.setVisibility(View.INVISIBLE);
            binding.rechercher.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

       switch(item.getItemId()) {
           case R.id.rechercher:
               Intent intent0 = new Intent(getApplicationContext(), StartActivity.class);
               startActivity(intent0);
               return true;
           case R.id.vendeur:
               Intent intent1 = new Intent(getApplicationContext(), SignInActivity.class);
               intent1.putExtra("NAME", "vendeur");
               startActivity(intent1);
               return true;
           case R.id.favoris:
               Intent intent2 = new Intent(getApplicationContext(), SignInActivity.class);
               intent2.putExtra("NAME", "favoris");
               startActivity(intent2);
               return true;
           case R.id.Recherch_saved:
               Intent intent3 = new Intent(getApplicationContext(), SignInActivity.class);
               intent3.putExtra("NAME", "recherch_saved");
               startActivity(intent3);
               return true;
           case R.id.message:
               Intent intent4 = new Intent(getApplicationContext(), SignInActivity.class);
               intent4.putExtra("NAME", "message");
               startActivity(intent4);
               return true;

           case R.id.alertitem:
               Intent intent5 = new Intent(getApplicationContext(), CreateAlertActivity.class);
               startActivity(intent5);
               return true;

           case R.id.connexion:
               Intent intent6 = new Intent(getApplicationContext(), SignInActivity.class);
               intent6.putExtra("NAME", "connexion");
               startActivity(intent6);
               return true;
       }
        return super.onOptionsItemSelected(item);
    }

    private void setListener() {
        binding.listall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}