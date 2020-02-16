package com.example.gohack;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.gohack.Player.cList;
import static com.example.gohack.Player.clueOn;

public class ClueList extends AppCompatActivity {

    private TextView message;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_clue);
        message = (TextView)findViewById(R.id.textView9);
        message.setText(cList[clueOn]);
    }
}
