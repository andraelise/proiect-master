package com.example.proiectmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnLogout;
    ImageView imgProfil;
    CardView cardRecomandari, cardCalendar, cardParametrii, cardAlarme;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnLogout = findViewById(R.id.btnLogout);
//        btnLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(HomeActivity.this, RegisterActivity.class));
//            }
//        });

        imgProfil = findViewById(R.id.imgProfil);
//        imgProfil.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(HomeActivity.this, ProfilActivity.class));
//            }
//        });

        cardRecomandari = findViewById(R.id.cardRecomandari);
        cardCalendar = findViewById(R.id.cardCalendar);
        cardParametrii = findViewById(R.id.cardParametrii);
        cardAlarme = findViewById(R.id.cardAlarme);

        btnLogout.setOnClickListener(this);
        imgProfil.setOnClickListener(this);
        cardRecomandari.setOnClickListener(this);
        cardCalendar.setOnClickListener(this);
        cardParametrii.setOnClickListener(this);
        cardAlarme.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.cardRecomandari:
                startActivity(new Intent(HomeActivity.this, RecomandariActivity.class));
                break;
            case R.id.cardCalendar:
                startActivity(new Intent(HomeActivity.this, CalendarActivity.class));
                break;
            case R.id.cardParametrii:
                startActivity(new Intent(HomeActivity.this, ParametriiActivity.class));
                break;
            case R.id.cardAlarme:
                startActivity(new Intent(HomeActivity.this, IstoricAlarmeActivity.class));
                break;
            case R.id.imgProfil:
                startActivity(new Intent(HomeActivity.this, ProfilActivity.class));
                break;
            case R.id.btnLogout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeActivity.this, RegisterActivity.class));
                break;
            default:
                break;
        }
    }
}
