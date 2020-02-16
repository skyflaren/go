package com.example.gohack;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class Username extends AppCompatActivity {

    private Button nUserNextButton;
    private EditText nUserInput;
    public static String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);
        nUserNextButton = (Button)findViewById(R.id.userNextBtn);
        nUserInput = (EditText)findViewById(R.id.usernameInput);
        nUserNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = nUserInput.getText().toString();
                startActivity(new Intent(getApplicationContext(), GameplayActivity.class));
            }
        });
    }
}
