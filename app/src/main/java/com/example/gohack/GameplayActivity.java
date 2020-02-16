package com.example.gohack;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.client.Firebase;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;
import java.util.Map;

import static com.example.gohack.MainActivity.gameID;
import static com.example.gohack.Player.cList;
import static com.example.gohack.Username.username;

public class GameplayActivity extends FragmentActivity implements OnMapReadyCallback {

    private Firebase rootRef;
    private static Player p;

    private boolean first = true;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private static final int REQUEST_CODE = 101;
    private static long start;
    private ImageButton nListClues;
    private TextView nTimer;
    private TextView nUser;
    private TextView nClueFrac;
    private ImageButton nEmergency;
    private long timeElapsed = 0;

    String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    String rationale = "Location services are necessary for you to embark on your go! journey";
    Permissions.Options options = new Permissions.Options()
            .setRationaleDialogTitle("Info")
            .setSettingsDialogTitle("Warning");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scavenger_layout);

        rootRef = new Firebase("https://supple-hulling-268305.firebaseio.com/Games/");
        Firebase game = rootRef.child(gameID);
        Firebase users = game.child("Users");
        Firebase userFB = users.push();
        nListClues = (ImageButton) findViewById(R.id.listClues);
        nTimer = (TextView) findViewById(R.id.timer);
        nUser = (TextView) findViewById(R.id.usernameFill);
        nClueFrac = (TextView) findViewById(R.id.clueFraction);
        nEmergency = (ImageButton) findViewById(R.id.emergency);

        p = new Player(userFB.toString(),0,username,1,1);

        start = System.currentTimeMillis();

        userFB.child("Name").setValue(p.getName());
        userFB.child("ClueOn").setValue(p.getClueOn());
        userFB.child("ID").setValue(p.getID());
        userFB.child("Latitude").setValue(p.getLatitude());
        userFB.child("Longitude").setValue(p.getLongitude());
        callPermissions();

        nEmergency.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                startActivity(new Intent(GameplayActivity.this, Contact.class));
            }
        });

        nListClues.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                p.setNewClue(false);
                startActivity(new Intent(GameplayActivity.this, ClueList.class));
            }
        });
    }

    public void requestLocationUpdates(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PermissionChecker.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PermissionChecker.PERMISSION_GRANTED) {
            fusedLocationProviderClient = new FusedLocationProviderClient(this);
            locationRequest = new LocationRequest();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setFastestInterval(3000);
            locationRequest.setInterval(4000);
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    p.updateLocation(locationResult.getLastLocation().getLatitude(),locationResult.getLastLocation().getLongitude());
                    if (p.getNewClue()){
                        nListClues.setImageResource(R.mipmap.iconscluehighlight);
                    }
                    else{
                        nListClues.setImageResource(R.mipmap.iconsclue);
                    }
                    timeElapsed = System.currentTimeMillis()-start;
                    long second = (timeElapsed / 1000) % 60;
                    long minute = (timeElapsed / (1000 * 60)) % 60;

                    String time = String.format("%02d:%02d", minute, second);

                    nTimer.setText(time);
                    nUser.setText(p.getName());
                    nClueFrac.setText(p.getClueOn()+"/"+(cList.length-1));
                    Log.v("GameplayActivity",p.getLatitude()+" "+p.getLongitude());
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager(). findFragmentById(R.id.google_map);
                    supportMapFragment.getMapAsync(GameplayActivity.this);
                }
            }, getMainLooper());
        } else callPermissions();
    }

    public void callPermissions(){
        Permissions.check(this/*context*/, permissions, rationale, options, new PermissionHandler() {
            @Override
            public void onGranted() {
                requestLocationUpdates();
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                callPermissions();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style));

            if (!success) {
                Log.e("GameplayActivity", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("GameplayActivity", "Can't find style. Error: ", e);
        }




        LatLng latLng = new LatLng(Double.parseDouble(p.getLatitude()),Double.parseDouble(p.getLongitude()));
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("YOU");
        Log.d("Location",Double.parseDouble(p.getLatitude())+" "+Double.parseDouble(p.getLongitude()));
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));




        if(first) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
            first = false;
        }

        googleMap.clear();
        googleMap.addMarker(markerOptions);

    }



}
