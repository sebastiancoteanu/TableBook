package com.sebas.licenta1.views;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.sebas.licenta1.R;
import com.sebas.licenta1.utils.LoadingDialog;

public class ForgotPasswordActivity extends AppCompatActivity {

    private static final String ERROR_USER_NOT_FOUND = "ERROR_USER_NOT_FOUND";

    private EditText emailAddress;
    private FirebaseAuth firebaseAuth;
    private ImageView backIcon;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        firebaseAuth = FirebaseAuth.getInstance();

        configureUI();
    }

    private void configureUI() {
        defineView();
        createListeners();
    }

    private void createListeners() {
        backIcon.setOnClickListener(new ImageView.OnClickListener() {
            public void onClick(View v) {
                // go to previous state
                finish();
            }
        });
    }

    private void defineView() {
        emailAddress = findViewById(R.id.emailAddress);
        backIcon = findViewById(R.id.backIcon);
        loadingDialog = new LoadingDialog(this);
    }

    private Boolean isValidEmail(String email){
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private String getMessage(FirebaseAuthException fae) {
        switch(fae.getErrorCode()) {
            case ERROR_USER_NOT_FOUND:
                return "There is no user registered for this email.";
            default:
                return fae.getLocalizedMessage();
        }
    }

    public void sendVerificationLink(View v) {
        String emailString = emailAddress.getText().toString();
        if(!isValidEmail(emailString)) {
            Toast.makeText(ForgotPasswordActivity.this, "Invalid email",
                    Toast.LENGTH_LONG).show();
            return;
        }

        loadingDialog.show();
        firebaseAuth.sendPasswordResetEmail(emailString)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this, "Please, check your email",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ForgotPasswordActivity.this, getMessage((FirebaseAuthException) task.getException()),
                                    Toast.LENGTH_LONG).show();
                        }
                        loadingDialog.dismiss();
                    }
                });
    }
}
