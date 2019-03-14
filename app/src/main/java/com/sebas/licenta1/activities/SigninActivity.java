package com.sebas.licenta1.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.sebas.licenta1.BuildConfig;
import com.sebas.licenta1.R;

public class SigninActivity extends AppCompatActivity {

    private boolean passHidden = true;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "GoogleActivity";
    private FirebaseAuth mAuth;

    private ImageView passIcon;
    private EditText passwordInput;
    private EditText emailInput;
    private ImageView backButton;
    private TextView forgotPassword;
    private TextView signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        String googleOAuthClientId = BuildConfig.GoogleOAuthClientId;

        // prevent opening keyboard on activity start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        configureUI();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(googleOAuthClientId)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            Log.w(TAG, "Google sign in failed", e);
            updateUI(null);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            updateUI(null);
                        }
                    }
                });
    }

    private void configureUI() {
        defineView();
        createListeners();
        createEmailWatcher();
        createPasswordWatcher();
    }

    private void defineView() {
        passIcon = findViewById(R.id.passIcon);
        passwordInput = findViewById(R.id.password);
        emailInput = findViewById(R.id.emailAddress);
        backButton =  findViewById(R.id.backIcon);
        forgotPassword = findViewById(R.id.forgotPassword);
        signup = findViewById(R.id.signup);
    }

    private void createListeners() {
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

        forgotPassword.setOnClickListener(new TextView.OnClickListener() {
            public void onClick(View v) {
                switchActivity(ForgotPasswordActivity.class, false);
            }
        });

        signup.setOnClickListener(new TextView.OnClickListener() {
            public void onClick(View v) {
                switchActivity(SignupActivity.class, false);
            }
        });
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

    public void goToSignUp(View v) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    public void goToForgotPassword(View v) {
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    private void switchActivity(Class c, Boolean closeCurrent) {
        Intent intent = new Intent(this, c);
        if(closeCurrent) {
            finish();
        }
        startActivity(intent);
    }

    public void googleSignIn(View v) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void emailPassSignIn(View v) {
        String emailAddress = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        if(!isValidEmail(emailAddress)) {
            Toast.makeText(SigninActivity.this, "Invalid email",
                    Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(emailAddress, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SigninActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
}
