package com.example.gohack;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

import java.util.Comparator;
import java.util.Map;

import static com.example.gohack.MainActivity.gameID;


public class Player {

    private String id;
    public static int clueOn = 0;
    private String name;
    private double latitude;
    private double longitude;
    private boolean newClue = false;
    private int t = 0;
    public static int size = 3;
    private int clueDisplay = 0;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference  myRef = database.getReference();
    public static String[][] qList = new String[size][4];
    public static String[] cList = new String[size];

    public static double dist(double lat1,double lon1,double lat2,double lon2){
        double R = 6378.137; // Radius of earth in KM
        double dLat = lat2 * Math.PI / 180 - lat1 * Math.PI / 180;
        double dLon = lon2 * Math.PI / 180 - lon1 * Math.PI / 180;
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c;
        return d * 1000; // meters
    }

    public Player(String id, int clueOn,String name,double latitude,double longitude){
        this.id = id;
        this.clueOn = clueOn;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void updateLocation(double latitud, double longitud){
        this.latitude = latitud;
        this.longitude = longitud;
        t++;
        DatabaseReference qRef = myRef.child("Games").child(gameID).child("Waypoint");
        DatabaseReference cRef = myRef.child("Games").child(gameID).child("Clues");

        qRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot p: dataSnapshot.getChildren()) {

                    Map<String, String> questions = (Map) p.getValue();
                    qList[count][0] = questions.get("Question");
                    qList[count][1] = questions.get("Answer");
                    qList[count][2] = questions.get("Latitude");
                    qList[count][3] = questions.get("Longitude");
                    count++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        cRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int count = 0;
                for (DataSnapshot p : dataSnapshot.getChildren()) {
                    cList[count] = p.getValue().toString();
                    count++;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.d("Player",qList[0][0]+qList[0][1]+qList[0][2]+" "+qList[0][3]);
        Log.d("Player",qList[1][0]+qList[1][1]+qList[1][2]+" "+qList[1][3]);
        Log.d("Player",cList[0] + " " + cList[1]);
        if (t > 3) {
            Log.v("Player",""+dist(latitud,longitud,Double.parseDouble(qList[clueOn][2]), Double.parseDouble(qList[clueOn][3])));
            if (dist(latitud,longitud,Double.parseDouble(qList[clueOn][2]), Double.parseDouble(qList[clueOn][3])) < 250) {
                updateClue();
                Log.d("Player", "Clue updated");
            }
        }
    }

//    public boolean near(){
//
//    }

    public String getID(){
        return id;
    }

    public String getClueOn(){
        return ""+clueDisplay;
    }

    public String getName(){
        return name;
    }

    public String getLatitude(){
        return ""+latitude;
    }

    public String getLongitude(){
        return ""+longitude;
    }

    public void setNewClue(boolean b){ newClue = b;}

    public void updateClue(){
        //Move to next clue
        if (clueOn < cList.length) {
            clueOn++;
        }
        if (clueDisplay <= cList.length){
            clueDisplay++;
        }
        newClue = true;
    }

    public boolean getNewClue(){
        if (newClue)
            return true;
        return false;
    }

//    class descending implements Comparator<Player>{
//        public long compare(Player a, Player b){
//            return b.time - a.time;
//        }
//    }
}
