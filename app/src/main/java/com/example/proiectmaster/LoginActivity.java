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
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText email, password;
    Button btnSignIn;
    TextView parolaUitata;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Logare");

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        parolaUitata = findViewById(R.id.parolaUitata);
        btnSignIn = findViewById(R.id.btnSignIn);

        parolaUitata.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if (mFirebaseUser != null)
                {
                    Toast.makeText(LoginActivity.this, "Ești logat!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Loghează-te!", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignIn:
                signIn(email.getText().toString(), password.getText().toString());
                break;
            case R.id.parolaUitata:
                sendResetEmail(email.getText().toString());
                break;
            default:
                break;
        }
    }

    private void signIn(String emailAddress, String pwd)
    {
        if (!validateForm(emailAddress, pwd)) {
            return;
        }

        mFirebaseAuth.signInWithEmailAndPassword(emailAddress, pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Logare eșuată!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                }
            }
        });
    }

    private void sendResetEmail(String emailAddress)
    {
        if (emailAddress.isEmpty())
        {
            email.setError("Introdu adresa de email!");
            email.requestFocus();
            return;
        }
        else if (!isValidEmail(emailAddress))
        {
            email.setError("Introdu o adresă de email validă!");
            email.requestFocus();
            return;
        }

        mFirebaseAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Verifica-ti email-ul pentru a reseta parola!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Eroare la trimiterea email-ului de resetare a parolei!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateForm(String emailAddress, String pwd)
    {
        if (emailAddress.isEmpty() && pwd.isEmpty())
        {
            Toast.makeText(LoginActivity.this, "Datele de autentificare nu sunt completate!", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (emailAddress.isEmpty())
        {
            email.setError("Introdu adresa de email!");
            email.requestFocus();
            return false;
        }
        else if (!isValidEmail(emailAddress))
        {
            email.setError("Introdu o adresă de email validă!");
            email.requestFocus();
            return false;
        }
        else if (pwd.isEmpty())
        {
            password.setError("Introdu parola!");
            password.requestFocus();
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }
}
