package com.sebas.licenta1.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sebas.licenta1.R;

public class SignupActivity extends AppCompatActivity {

    private boolean passHidden = true;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        configureUI();
    }

    private void configureUI() {
        createListeners();
        createEmailWatcher();
        createPasswordWatcher();
    }

    private void createListeners() {
        final ImageView passIcon = findViewById(R.id.passIcon);
        final EditText passwordInput = findViewById(R.id.password);
        ImageView backButton =  findViewById(R.id.backIcon);

        passIcon.setOnClickListener(new ImageView.OnClickListener() {
            public void onClick(View v) {
            passHidden = !passHidden;
            int resource = passHidden ? R.drawable.ic_visibility_off_black_24dp : R.drawable.ic_visibility_black_24dp;
            int inputType = passHidden ? (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD) :
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
            passIcon.setImageResource(resource);
            passwordInput.setInputType(inputType);
            passwordInput.setSelection(passwordInput.length());
            }
        });

        backButton.setOnClickListener(new ImageView.OnClickListener() {
            public void onClick(View v) {
                // go to previous state
                finish();
            }
        });
    }

    private void createEmailWatcher() {
        EditText emailInput = findViewById(R.id.lastName);

        checkEmptyString(); // initial validation

        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkEmptyString();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void createPasswordWatcher() {
        EditText passwordInput = findViewById(R.id.password);

        checkEmptyString(); // initial validation

        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkEmptyString();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void checkEmptyString() {
        Button signInButton = findViewById(R.id.signIn);
        String email = ((EditText) findViewById(R.id.lastName)).getText().toString();
        String password = ((EditText) findViewById(R.id.password)).getText().toString();

        if(email.trim().length() == 0 || password.trim().length() == 0){
            signInButton.setEnabled(false);
        } else {
            signInButton.setEnabled(true);
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(this, InfoConfirm.class);
            startActivity(intent);
        }
    }

    private Boolean isValidEmail(String email){
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void emailPassSignUp(View v) {
        String emailAddress = ((EditText)findViewById(R.id.lastName)).getText().toString();
        String password = ((EditText)findViewById(R.id.password)).getText().toString();

        if(!isValidEmail(emailAddress)) {
            Toast.makeText(SignupActivity.this, "Mail invalid",
                    Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(emailAddress, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(SignupActivity.this, task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                }
            });
    }

    public void goToSignIn(View v) {
        Intent intent = new Intent(this, SigninActivity.class);
        startActivity(intent);
    }
}
