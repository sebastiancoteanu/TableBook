package com.sebas.licenta1.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;
import com.sebas.licenta1.R;

public class SigninActivity extends AppCompatActivity {

    private boolean passHidden = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        // prevent opening keyboard on activity start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        configureUI();
    }

    private void configureUI() {
        createPassIconListener();
        createEmailWatcher();
        createPasswrodWatcher();
    }

    private void createPassIconListener() {
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

    public void signIn(View v) {
        String emailInputString = ((EditText)findViewById(R.id.emailAddress)).getText().toString();
//        TextView errorText = findViewById(R.id.errorText);

        if(!isValidEmail(emailInputString)) {
//            errorText.setText("Please enter a valid email");
            Toast.makeText(SigninActivity.this, "Mail invalid",
                    Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(SigninActivity.this, "Buton apasat",
                Toast.LENGTH_LONG).show();
    }

    private Boolean isValidEmail(String email){
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void createEmailWatcher() {
        EditText emailInput = findViewById(R.id.emailAddress);

        checkEmptyString(emailInput.getText().toString()); // initial validation

        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkEmptyString(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void createPasswrodWatcher() {
        EditText passwordInput = findViewById(R.id.password);

        checkEmptyString(passwordInput.getText().toString()); // initial validation

        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkEmptyString(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    // todo: proper validation
    private void checkEmptyString(String s) {
        Button signInButton = findViewById(R.id.signIn);

        if(s.trim().length() == 0){
            signInButton.setEnabled(false);
        } else {
            signInButton.setEnabled(true);
        }
    }
}
