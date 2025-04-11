package com.example.Splitzer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView registerTextView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        emailEditText = findViewById(R.id.edit_text_email);
        passwordEditText = findViewById(R.id.edit_text_password);
        loginButton = findViewById(R.id.button_login);
        registerTextView = findViewById(R.id.text_view_register);  // "Don't have an account? Register here"

        // Set up login button listener
        loginButton.setOnClickListener(v -> loginWithEmail());

        // Set up register link listener
        registerTextView.setOnClickListener(v -> {
            // Go to RegisterActivity
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void loginWithEmail() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading indication (optional, e.g., ProgressDialog)
        loginButton.setEnabled(false);  // Disable login button while attempting login

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // Enable the login button again
                    loginButton.setEnabled(true);

                    if (task.isSuccessful()) {
                        // Navigate to MainActivity after successful login
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();  // Finish LoginActivity so the user can't go back to it
                    } else {
                        // Show detailed error message
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Login Failed";
                        Toast.makeText(this, "Login Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
