package com.example.movieclub;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.movieclub.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Profile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private TextView fullnameTextView, usernameTextView, watchlistCountTextView;
    private TextInputEditText fullNameEditText, emailEditText, phoneEditText;
    private Button updateButton;
    private ImageView profileImageView;

    private Uri imageUri;
    private DatabaseReference userRef;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Views
        fullnameTextView = findViewById(R.id.fullname);
        usernameTextView = findViewById(R.id.username);
        watchlistCountTextView = findViewById(R.id.watchlistcount);
        fullNameEditText = findViewById(R.id.nom);
        emailEditText = findViewById(R.id.email);
        phoneEditText = findViewById(R.id.phone);
        updateButton = findViewById(R.id.update);
        profileImageView = findViewById(R.id.profileimg);

                // Initialize Firebase References
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Fetch current user ID
        userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        storageRef = FirebaseStorage.getInstance().getReference("profile_images").child(userId + ".jpg");

        // Fetch data from Firebase
        fetchUserData();

        // Set up listeners
        profileImageView.setOnClickListener(view -> openImagePicker());
        updateButton.setOnClickListener(view -> {
            updateUserData();
            uploadProfileImage();
        });
    }

    private void fetchUserData() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String fullName = snapshot.child("fullName").getValue(String.class);
                    String username = snapshot.child("username").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    String watchlistCount = snapshot.child("watchlistCount").getValue(String.class);
                    String profileImageUrl = snapshot.child("profileImage").getValue(String.class);

                    // Set data to views
                    fullnameTextView.setText(fullName);
                    usernameTextView.setText(username);
                    watchlistCountTextView.setText(watchlistCount);
                    fullNameEditText.setText(fullName);
                    emailEditText.setText(email);
                    phoneEditText.setText(phone);

                    // Load profile image
                    if (profileImageUrl != null) {
                        Glide.with(Profile.this).load(profileImageUrl).into(profileImageView);
                    }
                } else {
                    Toast.makeText(Profile.this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Profile.this, "Failed to fetch data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserData() {
        String fullName = fullNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String phone = phoneEditText.getText().toString();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update data in Firebase
        userRef.child("fullName").setValue(fullName);
        userRef.child("email").setValue(email);
        userRef.child("phone").setValue(phone);
        userRef.child("watchlistCount").setValue(watchlistCountTextView.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Profile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Profile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImageView.setImageURI(imageUri);
        }
    }

    private void uploadProfileImage() {
        if (imageUri != null) {
            storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        userRef.child("profileImage").setValue(imageUrl).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(Profile.this, "Profile image updated!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Profile.this, "Failed to update profile image", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
            ).addOnFailureListener(e -> {
                Toast.makeText(Profile.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }
}
