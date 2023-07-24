package com.example.chatfumu.Activities;

import static android.widget.Toast.LENGTH_LONG;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatfumu.Listener.ItemListener;
import com.example.chatfumu.Models.Item;
import com.example.chatfumu.adapter.ListAdapter;
import com.example.chatfumu.databinding.ActivityDiapoBinding;
import com.example.chatfumu.utilities.Constants;
import com.example.chatfumu.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DiapoActivity extends AppCompatActivity implements View.OnClickListener{

    Item item;
    private ActivityDiapoBinding binding;
    private PreferenceManager preferenceManager;
    int position = 0;
    private String[] photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDiapoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        binding.right.setOnClickListener(this);
        binding.left.setOnClickListener(this);
        item = (Item) getIntent().getSerializableExtra(Constants.KEY_ITEM);
        photo = new String[] {item.photo1, item.photo2, item.photo3};
        binding.img.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView myview = new ImageView(DiapoActivity.this);
                Bitmap bitmap = decodedPhoto(photo[position]);
                Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, 3000, 2500, false);
                myview.setImageDrawable(new BitmapDrawable(bitmapResized));
                return myview;
            }
        });

        binding.img.setInAnimation(DiapoActivity.this, android.R.anim.slide_in_left);
        binding.img.setOutAnimation(DiapoActivity.this, android.R.anim.slide_out_right);
    }

    /*private void Switch() {
        getItem();
    }

    private void getItem() {
       // loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_SEARCHED_ITEM)
                .get()
                .addOnCompleteListener(task -> {
                  //  loading(false);
                    String currentId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null){
                        List<Item> items = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            item = new Item();
                            item.pseudo = queryDocumentSnapshot.getString(Constants.KEY_PSEUDO);
                            item.prix = String.valueOf(queryDocumentSnapshot.getLong(Constants.KEY_PRIX));
                            item.adress = queryDocumentSnapshot.getString(Constants.KEY_ADDRESS);
                            item.image = queryDocumentSnapshot.getString(Constants.KEY_PHOTO1);
                            item.description = queryDocumentSnapshot.getString(Constants.KEY_DESCRIPTION);
                            item.id = queryDocumentSnapshot.getId();
                            photo = new String[] {item.photo1, item.photo2, item.photo3};
                        }
                    }else{
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }*/

/*    private void loading(boolean isLoading) {
        if (isLoading){
            binding.progressbar.setVisibility(View.VISIBLE);
        }else{
            binding.progressbar.setVisibility(View.INVISIBLE);
        }
    }*/

    private Bitmap decodedPhoto(String photo) {
        byte[] byteArray = Base64.decode(photo, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        return bitmap;
    }

    @Override
    public void onClick(View view) {
        if(view.equals(binding.right)){
            if(position<photo.length){
                position++;
            }if(position>=photo.length){
                position = photo.length - 1;
            }
            Bitmap bitmap = decodedPhoto(photo[position]);
            Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, 3000, 2500, false);
            binding.img.setImageDrawable(new BitmapDrawable(bitmapResized));
            Toast.makeText(this, "Next Image", LENGTH_LONG).show();
        }else if (view.equals(binding.left)){
            if(position>0){
                position--;
            }else if(position<0) position = 0;
            Bitmap bitmap = decodedPhoto(photo[position]);
            Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, 3000, 2500, false);
            binding.img.setImageDrawable(new BitmapDrawable(bitmapResized));
            Toast.makeText(this, "Previous Image", LENGTH_LONG).show();
        }
    }
}