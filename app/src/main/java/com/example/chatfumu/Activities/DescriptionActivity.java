package com.example.chatfumu.Activities;

import static android.widget.Toast.LENGTH_LONG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatfumu.Models.Item;
import com.example.chatfumu.R;
import com.example.chatfumu.databinding.ActivityDescriptionBinding;
import com.example.chatfumu.utilities.Constants;
import com.example.chatfumu.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.HashMap;

public class DescriptionActivity extends AppCompatActivity{

    private ActivityDescriptionBinding binding;
    PreferenceManager preferenceManager;
    private Item item;
    Bitmap bitmapVector;
    private String Favorite;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDescriptionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        getDescription();
        //getItem();
        //getFavorite();
        setListener();
    }

    /*private void getItem() {

        Loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference docRef = database.collection(Constants.KEY_COLLECTION_POSTS).document(item.id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Loading(false);
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        item.photo1 = document.getString(Constants.KEY_PHOTO1);
                        item.photo2 = document.getString(Constants.KEY_PHOTO2);
                        item.photo3 = document.getString(Constants.KEY_PHOTO3);
                        item.description = document.getString(Constants.KEY_DESCRIPTION);
                        item.prix = document.getString(Constants.KEY_PRIX);
                        item.adress = document.getString(Constants.KEY_ADDRESS);

                    } else {
                        Toast.makeText(DescriptionActivity.this, "No such a document", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DescriptionActivity.this, "get data failed with", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
*/
    private void setListener() {
        binding.localiser.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            intent.putExtra("name", item);
            startActivity(intent);
        });

        binding.img.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), DiapoActivity.class);
            intent.putExtra(Constants.KEY_ITEM, item);
            startActivity(intent);
        });

        binding.contact.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
            startActivity(intent);
        });

        binding.favorisIcon.setOnClickListener(v -> {

            binding.favorisIcon.setImageResource(R.drawable.ic_favorite_red);
            Loading(true);
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            HashMap<String, Object> Favorite_register = new HashMap<>();
            Favorite_register.put(Constants.KEY_PSEUDO, item.pseudo);
            Favorite_register.put(Constants.KEY_PRIX, item.prix);
            Favorite_register.put(Constants.KEY_ADDRESS, item.adress);
            Favorite_register.put(Constants.KEY_DESCRIPTION, item.description);
            Favorite_register.put(Constants.KEY_PHOTO1, item.photo1);
            Favorite_register.put(Constants.KEY_PHOTO2, item.photo2);
            Favorite_register.put(Constants.KEY_PHOTO3, item.photo3);
            Favorite_register.put(Constants.KEY_FAVORITES_ID, "Actif");

            database.collection(Constants.KEY_COLLECTION_FAVORITES)
                    .add(Favorite_register)
                    .addOnSuccessListener(documentReference -> {
                        Loading(false);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                        preferenceManager.putString(Constants.KEY_PSEUDO, item.pseudo);
                        preferenceManager.putString(Constants.KEY_PRIX, item.prix);
                        preferenceManager.putString(Constants.KEY_ADDRESS, item.adress);
                        preferenceManager.putString(Constants.KEY_PHOTO1, item.photo1);
                        preferenceManager.putString(Constants.KEY_PHOTO2, item.photo2);
                        preferenceManager.putString(Constants.KEY_PHOTO3, item.photo3);
                    })
                    .addOnFailureListener(exception ->{
                        Loading(false);
                        Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void getDescription() {

        Loading(true);
        item = (Item) getIntent().getSerializableExtra(Constants.KEY_ITEM);
        Loading(false);
        binding.desc.setText(item.description);
        binding.img.setImageBitmap(decodedPhoto(item.image));
    }

    private void Loading(boolean isLoading) {
        if (isLoading){
            binding.progressbar.setVisibility(View.VISIBLE);
        }else{
            binding.progressbar.setVisibility(View.INVISIBLE);
        }
    }

    private Bitmap decodedPhoto(String photo){
        byte[] byteArray = Base64.decode(photo, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        return bitmap;
    }
}
