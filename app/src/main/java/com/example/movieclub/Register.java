package com.example.movieclub;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    private Button callSignIn;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    Button btn;
    private TextInputLayout regName, regUsername, regEmail, regPassword,regphone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.signup), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        callSignIn = findViewById(R.id.signin);
        btn = findViewById(R.id.register);
        regName = findViewById(R.id.fullname);
        regUsername = findViewById(R.id.username);
        regEmail = findViewById(R.id.email);
        regphone= findViewById(R.id.phone);
        regPassword = findViewById(R.id.password);
        callSignIn = findViewById(R.id.signin);
        callSignIn.setOnClickListener(view -> {
            Intent intent = new Intent(Register.this, login.class);
            startActivity(intent);
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rootNode = FirebaseDatabase.getInstance();
                reference = rootNode.getReference("users");

                String name = regName.getEditText().getText().toString();
                String username = regUsername.getEditText().getText().toString();
                String email = regEmail.getEditText().getText().toString();
                String password = regPassword.getEditText().getText().toString();
                String phone = regphone.getEditText().getText().toString();
                UserHelperClass user = new UserHelperClass(name, username, phone,email, password);

                // Save the user to the database
                reference.child(phone).setValue(user);
            }
        });
    }
}
