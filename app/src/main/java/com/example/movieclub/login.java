package com.example.movieclub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class login extends AppCompatActivity {

    private Button callSignUp, signInButton;
    private TextInputLayout emailInput, passwordInput;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseAuth = FirebaseAuth.getInstance();


        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        callSignUp = findViewById(R.id.signup);
        signInButton = findViewById(R.id.signin);

        callSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(login.this, Register.class);
            startActivity(intent);
        });


        signInButton.setOnClickListener(view -> {

            String email = Objects.requireNonNull(emailInput.getEditText()).getText().toString().trim();
            String password = Objects.requireNonNull(passwordInput.getEditText()).getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(login.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else {
                loginUser(email, password);
            }
        });
    }


    private void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        // Redirect to Home Activity
                        Intent intent = new Intent(login.this, Home.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(login.this, "Login Failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
