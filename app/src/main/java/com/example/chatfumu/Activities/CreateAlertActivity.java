package com.example.chatfumu.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatfumu.Models.Item;
import com.example.chatfumu.Models.User;
import com.example.chatfumu.databinding.ActivityCreateAlertBinding;
import com.example.chatfumu.utilities.Constants;
import com.example.chatfumu.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateAlertActivity extends AppCompatActivity {

    private ActivityCreateAlertBinding binding;
    PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private Item mItem, item;
    private AlertDialog.Builder builder;
    private boolean Creation_Status;
    private String Alert_user;
    private User user;
    private ChatActivity chatActivity;
    public String Article;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateAlertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        builder = new AlertDialog.Builder(this);
        init();
        setListener();
    }

    private void init(){
        database = FirebaseFirestore.getInstance();
    }

    private void setListener() {

        binding.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.creerAlerte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertPostIt();
                builder.setMessage("Votre Alerte a été créée avec success")
                        .setCancelable(false)
                        .setPositiveButton("RETOUR", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.setTitle("CREATION ALERTE");
                alertDialog.show();
            }
        });
    }
/*

    private void createUserAlert(boolean creation_Status) {

        if(!creation_Status) {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            Map<String, Object> user = new HashMap<>();
            user.put(Constants.KEY_NAME, "Votre Alerte Trade");
            database.collection(Constants.KEY_ALERT_USER)
                    .add(user)
                    .addOnSuccessListener(documentReference -> {
                        preferenceManager.putString(Constants.KEY_NAME, "Votre Alerte Trade");
                    });
            Creation_Status = true;
        }
    }

*/

    private void AlertPostIt(){

        Loading(true);
        //user.name = binding.identifier.getText().toString();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> alertItem = new HashMap<>();
        alertItem.put(Constants.KEY_ARTICLE, binding.article.getText().toString());
        alertItem.put(Constants.KEY_PRIX_MAX, Integer.parseInt(binding.prixMax.getText().toString()));
        alertItem.put(Constants.KEY_PRIX_MIN, Integer.parseInt(binding.prixMin.getText().toString()));
        alertItem.put(Constants.KEY_NAME, binding.username.getText().toString());
        database.collection(Constants.KEY_COLLECTION_ALERT_ITEM)
                .add(alertItem)
                .addOnSuccessListener(documentReference -> {
                    Loading(false);
                    preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                    preferenceManager.putString(Constants.KEY_ARTICLE, binding.article.getText().toString());
                    preferenceManager.putInteger(Constants.KEY_PRIX_MAX, Integer.parseInt(binding.prixMax.getText().toString()));
                    preferenceManager.putInteger(Constants.KEY_PRIX_MIN, Integer.parseInt(binding.prixMin.getText().toString()));
                    preferenceManager.putString(Constants.KEY_NAME, binding.username.getText().toString());
                    Article = binding.article.getText().toString();
                    /*createUserAlert(false);*/
                })
                .addOnFailureListener(exception ->{
                    Loading(false);
                    Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();
                });
    }

    private void Loading(boolean isLoading) {
        if (isLoading){
            binding.creerAlerte.setVisibility(View.INVISIBLE);
            binding.progressbar.setVisibility(View.VISIBLE);
        }else{
            binding.progressbar.setVisibility(View.INVISIBLE);
            binding.creerAlerte.setVisibility(View.VISIBLE);
        }
    }
}