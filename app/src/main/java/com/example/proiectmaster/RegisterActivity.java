package com.example.proiectmaster;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    EditText email, password;
    Button btnSignUp;
    TextView tvSignUp;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        tvSignUp = findViewById(R.id.signUp);
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String emailAddress = email.getText().toString();
                String pwd = password.getText().toString();

                if (emailAddress.isEmpty() && pwd.isEmpty())
                {
                    Toast.makeText(RegisterActivity.this, "Datele de autentificare nu sunt completate!", Toast.LENGTH_SHORT).show();
                }
                else if (emailAddress.isEmpty())
                {
                    email.setError("Introdu adresa de email!");
                    email.requestFocus();
                }
                else if (pwd.isEmpty())
                {
                    email.setError("Introdu parola!");
                    email.requestFocus();
                }
                else if (!(emailAddress.isEmpty() || pwd.isEmpty()))
                {
                    mFirebaseAuth.createUserWithEmailAndPassword(emailAddress, pwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful())
                            {
                                Toast.makeText(RegisterActivity.this, "Înregistrare eșuată!", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(RegisterActivity.this, "Eroare la înregistrare!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
