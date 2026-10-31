package com.example.doemaisui01;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class information extends AppCompatActivity {

    Button btnVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);

        btnVoltar = findViewById(R.id.button6);

        btnVoltar.setOnClickListener(v -> {
            Intent intent = new Intent(information.this, dashboradFuncionario.class);
            startActivity(intent);
        });


    }
}