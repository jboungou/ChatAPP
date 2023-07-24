package com.example.chatfumu.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatfumu.R;

public class PageIntrouvable extends AppCompatActivity {

    private TextView pageIntrouvable;
    private ImageView imagestop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_introuvable);
        pageIntrouvable = findViewById(R.id.pageIntrouvable);
        imagestop = findViewById(R.id.imagestop);
        Intent intent = this.getIntent();
        String nom = intent.getStringExtra("NAME");
        pageIntrouvable.setText(nom);
    }
}