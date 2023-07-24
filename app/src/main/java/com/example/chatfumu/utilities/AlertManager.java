package com.example.chatfumu.utilities;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatfumu.Activities.CreateAlertActivity;
import com.example.chatfumu.Models.Item;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AlertManager extends AppCompatActivity {

    PreferenceManager preferenceManager;
    private Item item, mItem;

    public AlertManager(Item item) {
        this.item = item;
    }



    private void search(){

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_ALERT_ITEM)
                .get()
                .addOnCompleteListener(task -> {
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null){
                        List<Item> items = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currentUserId.equals(queryDocumentSnapshot.getId())){
                                continue;
                            }
                            mItem = new Item();
                            mItem.pseudo = queryDocumentSnapshot.getString(Constants.KEY_PSEUDO);
                            mItem.identifier = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            mItem.id = queryDocumentSnapshot.getId();
                            items.add(mItem);
                        }
                    }else{
                        /* To be completed */
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        /* To be completed */
                    }
                });
    }

    private void LookInPost(){
        CreateAlertActivity createAlertActivity = new CreateAlertActivity();

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_POSTS)
                .whereEqualTo(Constants.KEY_PSEUDO, mItem.pseudo)
                /*.whereGreaterThan(Constants.KEY_PRIX, Integer.parseInt(binding.prixMin.getText().toString()))
                .whereLessThan(Constants.KEY_PRIX, binding.prixMax.getText().toString())*/
                .get()
                .addOnCompleteListener(task -> {
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null){
                        List<Item> items = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currentUserId.equals(queryDocumentSnapshot.getId())){
                                continue;
                            }
                            item = new Item();
                            item.pseudo = queryDocumentSnapshot.getString(Constants.KEY_PSEUDO);
                            item.identifier = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            item.id = queryDocumentSnapshot.getId();
                            items.add(item);
                        }
                    }else{
                        /* To be completed */
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        /* To be completed */
                    }
                });
    }

    private void SendMail() {

        String recipients = mItem.identifier;
        String subject = "Votre Alerte TRADE";
        String message = item.pseudo + "\n" + item.description + "\n" + item.photo1;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose an identifier client"));
    }
}
