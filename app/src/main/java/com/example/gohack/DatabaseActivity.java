package com.example.gohack;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Map;


public class DatabaseActivity extends AppCompatActivity {

    private Button nAddButton;
    private EditText nLongitudeField;
    private EditText nLatitudeField;
    private EditText nNameField;

    private Firebase rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        rootRef = new Firebase("https://supple-hulling-268305.firebaseio.com/Games");
        nAddButton = (Button) findViewById(R.id.addBtn);
        nLongitudeField = (EditText) findViewById(R.id.longitudeInput);
        nLatitudeField = (EditText) findViewById(R.id.latitudeInput);
        nNameField = (EditText) findViewById(R.id.nameInput);

        nAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nNameField.getText().toString();
                String longitude = nLongitudeField.getText().toString();
                String latitude = nLatitudeField.getText().toString();
                Firebase game = rootRef.push();
                Firebase users = game.push();
                Firebase user = users.push();
                user.child("Name").setValue(name);
                user.child("Longitude").setValue(longitude);
                user.child("Latitude").setValue(latitude);
            }
        });

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v("E_Value:","Data: "+ dataSnapshot.getValue());
                Map<String,String> values = dataSnapshot.getValue(Map.class);

                String name = values.get("Name");
                String longitude = values.get("Longitude");
                String latitude = values.get("Latitude");

                Log.v("name:","Data: "+ dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
