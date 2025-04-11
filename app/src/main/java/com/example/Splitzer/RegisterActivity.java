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

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button registerButton;
    private TextView loginTextView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        emailEditText = findViewById(R.id.edit_text_email);
        passwordEditText = findViewById(R.id.edit_text_password);
        registerButton = findViewById(R.id.button_register);
        loginTextView = findViewById(R.id.text_view_login);  // "Already have an account? Login here"

        // Set up register button listener
        registerButton.setOnClickListener(v -> registerWithEmail());

        // Set up login link listener
        loginTextView.setOnClickListener(v -> {
            // Go to LoginActivity
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });
    }

    private void registerWithEmail() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        // Navigate to MainActivity after successful registration
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();  // Finish RegisterActivity so the user can't go back to it
                    } else {
                        Toast.makeText(this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
