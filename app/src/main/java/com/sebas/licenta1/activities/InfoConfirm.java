package com.sebas.licenta1.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.sebas.licenta1.R;

public class InfoConfirm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_confirm);

        configureUI();
    }

    private void configureUI() {
        createListeners();
    }

    private void createListeners() {
        ImageView backButton =  findViewById(R.id.backIcon);

        backButton.setOnClickListener(new ImageView.OnClickListener() {
            public void onClick(View v) {
                // go to previous state
                finish();
            }
        });
    }

    public void confirmInfo(View v) {
        // todo:

        // mock a logout to test authentication
        Intent intent = new Intent(this, SigninActivity.class);
        finish();
        startActivity(intent);
    }


}
