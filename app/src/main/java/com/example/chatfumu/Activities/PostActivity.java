package com.example.chatfumu.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatfumu.Models.Item;
import com.example.chatfumu.Models.User;
import com.example.chatfumu.databinding.ActivityPostBinding;
import com.example.chatfumu.utilities.Constants;
import com.example.chatfumu.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PostActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private ActivityPostBinding binding;
    private String encodedPhoto1;
    private String encodedPhoto2;
    private String encodedPhoto3;
    private AlertDialog.Builder builder;
    private int token = 1;
    private ChatActivity chatActivity;
    private CreateAlertActivity createAlertActivity;
    private Item mItem;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        builder = new AlertDialog.Builder(this);
        setListener();
    }

    private void setListener() {

        binding.Publier.setOnClickListener(view -> {
            PostItems();
        });

        binding.picture1.setOnClickListener(view -> {
            Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent1.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage1.launch(intent1);
        });

        binding.picture2.setOnClickListener(view -> {
            Intent intent2 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage2.launch(intent2);
        });

        binding.picture3.setOnClickListener(view -> {
            Intent intent3 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent3.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage3.launch(intent3);
        });
    }

    private void PostItems() {

        PostOneItem();
        builder.setMessage("Voulez vous publier une autre annonce?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        binding.article.getText().clear();
                        binding.pseudo.getText().clear();
                        binding.prix.getText().clear();
                        binding.description.getText().clear();
                        PostOneItem();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle("Publier une annonce");
        alert.show();
    }

    private void PostOneItem(){

        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> post = new HashMap<>();
        post.put(Constants.KEY_ARTICLE, binding.article.getText().toString());
        post.put(Constants.KEY_ADDRESS, binding.address.getText().toString());
        post.put(Constants.KEY_PRIX, Integer.parseInt(binding.prix.getText().toString()));
        post.put(Constants.KEY_PSEUDO, binding.pseudo.getText().toString());
        post.put(Constants.KEY_DESCRIPTION, binding.description.getText().toString());
        post.put(Constants.KEY_PHOTO1, encodedPhoto1);
        post.put(Constants.KEY_PHOTO2, encodedPhoto2);
        post.put(Constants.KEY_PHOTO3, encodedPhoto3);

        database.collection(Constants.KEY_COLLECTION_POSTS)
            .add(post)
            .addOnSuccessListener(documentReference -> {
                loading(false);
                preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                preferenceManager.putString(Constants.KEY_ARTICLE, binding.article.getText().toString());
                preferenceManager.putString(Constants.KEY_ADDRESS, binding.address.getText().toString());
                preferenceManager.putInteger(Constants.KEY_PRIX, Integer.parseInt(binding.prix.getText().toString()));
                preferenceManager.putString(Constants.KEY_PSEUDO, binding.pseudo.getText().toString());
                preferenceManager.putString(Constants.KEY_PHOTO1, encodedPhoto1);
                preferenceManager.putString(Constants.KEY_PHOTO2, encodedPhoto2);
                preferenceManager.putString(Constants.KEY_PHOTO3, encodedPhoto3);
            })
            .addOnFailureListener(exception ->{
                loading(false);
                Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show();
            });
    }

    private final ActivityResultLauncher<Intent> pickImage1 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result ->{
                if (result.getResultCode() == RESULT_OK){
                    if (result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.picture1.setImageBitmap(bitmap);
                            encodedPhoto1 = encodedPhotos(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }

            });

    private final ActivityResultLauncher<Intent> pickImage2 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result ->{
                if (result.getResultCode() == RESULT_OK){
                    if (result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.picture2.setImageBitmap(bitmap);
                            encodedPhoto2 = encodedPhotos(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }

            });

    private final ActivityResultLauncher<Intent> pickImage3 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result ->{
                if (result.getResultCode() == RESULT_OK){
                    if (result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.picture3.setImageBitmap(bitmap);
                            encodedPhoto3 = encodedPhotos(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }

            });

    private void loading(boolean isLoading) {
        if (isLoading){
            binding.Publier.setVisibility(View.INVISIBLE);
            binding.progressbar.setVisibility(View.VISIBLE);
        }else{
            binding.progressbar.setVisibility(View.INVISIBLE);
            binding.Publier.setVisibility(View.VISIBLE);
        }
    }

    private String encodedPhotos(Bitmap bitmap){

        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth/bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
}
