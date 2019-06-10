package com.sebas.licenta1.views;

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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sebas.licenta1.R;
import com.sebas.licenta1.utils.LoadingDialog;

public class SignupActivity extends AppCompatActivity {

    private boolean passHidden = true;
    private FirebaseAuth mAuth;
    private EditText passwordInput;
    private EditText emailInput;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        FirebaseAnalytics.getInstance(this).setCurrentScreen(this, "Singup", null);

        configureUI();
    }

    private void configureUI() {
        defineView();
        createListeners();
        createEmailWatcher();
        createPasswordWatcher();
    }

    private void defineView() {
        passwordInput = findViewById(R.id.password);
        emailInput = findViewById(R.id.emailAddress);
        loadingDialog = new LoadingDialog(this);
    }

    private void createListeners() {
        final ImageView passIcon = findViewById(R.id.passIcon);
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
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

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
        String emailAddress = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if(!isValidEmail(emailAddress)) {
            Toast.makeText(SignupActivity.this, "Invalid email",
                    Toast.LENGTH_LONG).show();
            return;
        }

        loadingDialog.show();

        mAuth.createUserWithEmailAndPassword(emailAddress, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        loadingDialog.dismiss();
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        loadingDialog.dismiss();
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
