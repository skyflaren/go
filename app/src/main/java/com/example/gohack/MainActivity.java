package com.example.gohack;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {

    private int[] validIDs = {65536, 72854, 12345, 89745}; // Should be initialized via the database. Initialized with arbitrary 5 digit numbers for now.
    private EditText nCodeInput;
    private static final int ERROR_DIALOG_REQUEST = 9001;
    public static String gameID;


    private boolean isValid(String str) {
        try {
            int currentID = Integer.parseInt(str);
            for (int i : validIDs) {
                if (i == currentID) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false; // Default return value
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button usernameActivity = findViewById(R.id.btn);

        usernameActivity.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                nCodeInput = (EditText) findViewById(R.id.codeInput);
                String code = nCodeInput.getText().toString();
                if (isValid(code)) {
                    gameID = code;
                    startActivity(new Intent(MainActivity.this, Username.class));
                }
            }

        });
    }
}
