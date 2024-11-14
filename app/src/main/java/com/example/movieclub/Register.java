package com.example.movieclub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;


import com.google.android.gms.common.api.ApiException;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    private Button callSignIn, btn;
    private TextInputLayout regName, regUsername, regEmail, regPassword, regphone;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;
    private final int RC_SIGN_IN = 1001;
    private FirebaseDatabase rootNode;
    private DatabaseReference reference;

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

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_webid))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // UI Element Initialization
        callSignIn = findViewById(R.id.signin);
        btn = findViewById(R.id.register);
        regName = findViewById(R.id.fullname);
        regUsername = findViewById(R.id.username);
        regEmail = findViewById(R.id.email);
        regphone = findViewById(R.id.phone);
        regPassword = findViewById(R.id.password);

        // Handle Sign In button click
        callSignIn.setOnClickListener(view -> {
            Intent intent = new Intent(Register.this, login.class);
            startActivity(intent);
        });

        btn.setOnClickListener(view -> {
            String name = regName.getEditText().getText().toString();
            String username = regUsername.getEditText().getText().toString();
            String email = regEmail.getEditText().getText().toString();
            String password = regPassword.getEditText().getText().toString();
            String phone = regphone.getEditText().getText().toString();

            // Firebase Authentication - Create user with email and password
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Save additional user data to the database
                            rootNode = FirebaseDatabase.getInstance();
                            reference = rootNode.getReference("users");

                            UserHelperClass user = new UserHelperClass(name, username, phone, email, password);

                            // Get the unique user ID from FirebaseAuth
                            String userId = firebaseAuth.getCurrentUser().getUid();

                            // Store user information in the database under the user's UID
                            reference.child(userId).setValue(user)
                                    .addOnSuccessListener(aVoid -> {
                                        // User saved successfully
                                        Toast.makeText(Register.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(Register.this, login.class);
                                        startActivity(intent);
                                    })
                                    .addOnFailureListener(e -> {
                                        // Failed to save user
                                        Toast.makeText(Register.this, "Failed to Register User", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // Registration failed
                            Toast.makeText(Register.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });



        // Handle Google Sign-In button click
        findViewById(R.id.google).setOnClickListener(v -> signInWithGoogle());
    }

    private void registerUser() {
        String name = regName.getEditText().getText().toString();
        String username = regUsername.getEditText().getText().toString();
        String email = regEmail.getEditText().getText().toString();
        String password = regPassword.getEditText().getText().toString();
        String phone = regphone.getEditText().getText().toString();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        rootNode = FirebaseDatabase.getInstance();
                        reference = rootNode.getReference("users");

                        UserHelperClass user = new UserHelperClass(name, username, phone, email, password);
                        String userId = firebaseAuth.getCurrentUser().getUid();

                        reference.child(userId).setValue(user)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(Register.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Register.this, login.class));
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(Register.this, "Failed to Register User", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(Register.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                } else {
                    Log.w("Register", "Google sign-in account is null");
                    if (!isFinishing()) {
                        Toast.makeText(this, "Google sign-in failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (ApiException e) {
                Log.w("Register", "Google sign-in failed", e);
                if (!isFinishing()) {
                    Toast.makeText(this, "Google sign-in failed: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            String email = user.getEmail();
                            String name = user.getDisplayName();
                            String userId = user.getUid();

                            Log.d("Register", "User signed in with Google, email: " + email);

                            // Initialize the Firebase Database reference
                            rootNode = FirebaseDatabase.getInstance();
                            reference = rootNode.getReference("users");

                            // Check if user data already exists in the database
                            reference.child(userId).get().addOnSuccessListener(dataSnapshot -> {
                                if (!dataSnapshot.exists()) {
                                    UserHelperClass userHelper = new UserHelperClass(name, "", "", email, "");
                                    reference.child(userId).setValue(userHelper)
                                            .addOnSuccessListener(aVoid -> {
                                                Log.d("Register", "User data saved to database successfully.");
                                                Toast.makeText(this, "Welcome " + name, Toast.LENGTH_SHORT).show();
                                                updateUI(user);
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.e("Register", "Error saving user data to database", e);
                                                Toast.makeText(this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    Log.d("Register", "User data already exists in database.");
                                    updateUI(user);
                                }
                            });
                        }
                    } else {
                        Log.w("Register", "signInWithCredential:failure", task.getException());
                        if (!isFinishing()) {
                            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                        updateUI(null);
                    }
                });
    }



    private void updateUI(FirebaseUser user) {
        if (user != null) {
            if (!isFinishing()) {
                Toast.makeText(this, "Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
            }
            startActivity(new Intent(Register.this, Home.class));
        } else {
            if (!isFinishing()) {
                Toast.makeText(this, "Sign-in failed. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
