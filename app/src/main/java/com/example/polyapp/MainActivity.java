package com.example.polyapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.auth.AuthResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, OnMapReadyCallback {
    GoogleMap gMap;
    CheckBox checkBox;
    SeekBar seekRed, seekGreen, seekBlue;
    Button btDraw, btClear,btOut,btSave;
    Polygon polygon = null;
    Polyline polyline  = null;
    Polygon polygon2 = null;
    LatLng gv_latLng = null;
    public FirebaseAuth auth1;
    List<LatLng> gridPoints ;
    List<LatLng> latLngList = new ArrayList<>();
    List<LatLng> latLngList1 = new ArrayList<>();
    List<Marker> markerList = new ArrayList<>();
    List<Marker> markerList1 = new ArrayList<>();
    int red = 0, green = 0, blue = 0;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });
        auth1 = FirebaseAuth.getInstance();
        checkBox = findViewById(R.id.check_box);
        seekRed = findViewById(R.id.seek_red);
        seekGreen = findViewById(R.id.seek_green);
        seekBlue = findViewById(R.id.seek_blue);
        btDraw = findViewById(R.id.bt_draw);
        btOut = findViewById(R.id.bt_logout);
        btClear = findViewById(R.id.bt_clear);
        btSave = findViewById(R.id.bt_save);

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (b) {
                    if (polygon == null) return;
                    polygon.setFillColor(Color.rgb(red, green, blue));
                } else {
                    polygon.setFillColor(Color.TRANSPARENT);
                }
            }
        });


        btDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (polygon != null) polygon.remove();

                PolygonOptions polygonOptions = new PolygonOptions().addAll(latLngList)
                        .clickable(true);


                polygon = gMap.addPolygon(polygonOptions);
                polygon.setStrokeColor(Color.rgb(red, green, blue));
                if (checkBox.isChecked())
                    polygon.setFillColor(Color.rgb(red, green, blue));

                //polygon2.setStrokeColor(red);
                /*               Polyline polyline = gMap.addPolyline(new PolylineOptions()
/*                        .addAll(latLngList1)
                        .color(Color.GREEN)
                        .width(5));*/
          /*          List<LatLng> points = PointGenerator.generatePointsInsidePolygon(latLngList, 15);
                if (polyline != null) {
                    polyline.remove();
                }
                PolylineOptions polylineOptions = new PolylineOptions().addAll(points).color(0xFFFF0000);
                polyline = gMap.addPolyline(polylineOptions); */

                gridPoints = RectangleGridGenerator.generateRectangleGridInsidePolygon(latLngList, 0.0001); // Adjust stepSize as needed
                if (polyline != null) {
                    polyline.remove();
                }

                PolylineOptions polylineOptions = new PolylineOptions().addAll(gridPoints).color(Color.YELLOW );
                polyline = gMap.addPolyline(polylineOptions);
/*
                List<LatLng> pathPoints = DroneScanPathGenerator.generateDroneScanPathInsidePolygon(latLngList, 0.001); // Adjust stepSize as needed
                if (polyline != null) {
                    polyline.remove();
                }
                PolylineOptions polylineOptions = new PolylineOptions().addAll(pathPoints).color(0xFFFF0000);
                polyline = gMap.addPolyline(polylineOptions); */

            }
        });
        btOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth1.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivty.class));
            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePolygonAndPolylineToFirebase( mDatabase );
            }
        });

        btClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (polygon != null) polygon.remove();
                if (polyline != null) polyline.remove();
                for (Marker marker : markerList) marker.remove();
            //    for (Marker marker : markerList1) marker.remove();
                latLngList.clear();
                latLngList1.clear();
                markerList.clear();
                markerList1.clear();
                checkBox.setChecked(false);
                seekRed.setProgress(0);
                seekGreen.setProgress(0);
                seekBlue.setProgress(0);
            }
        });
        seekRed.setOnSeekBarChangeListener(this);
        seekGreen.setOnSeekBarChangeListener(this);
        seekBlue.setOnSeekBarChangeListener(this);
    }

    // Method to save both polygon and polyline points to Firebase
    private void savePolygonAndPolylineToFirebase( DatabaseReference dbase) {
        // Create a map to store both polygon and polyline data
        Map<String, Object> dataMap = new HashMap<>();

        // Save polygon points to the map
        Map<String, Object> polygonMap = new HashMap<>();
        for (int i = 0; i < latLngList.size(); i++) {
            LatLng point = latLngList.get(i);
            Map<String, Double> pointMap = new HashMap<>();
            pointMap.put("latitude", point.latitude);
            pointMap.put("longitude", point.longitude);
            polygonMap.put("point" + (i + 1), pointMap);
        }
        dataMap.put("areaShape", polygonMap);

        // Save polyline points to the map
        Map<String, Object> polylineMap = new HashMap<>();
        for (int i = 0; i < gridPoints.size(); i++) {
            LatLng point = gridPoints.get(i);
            Map<String, Double> pointMap = new HashMap<>();
            pointMap.put("latitude", point.latitude);
            pointMap.put("longitude", point.longitude);
            polylineMap.put("point" + (i + 1), pointMap);
        }
        dataMap.put("geoPath", polylineMap);

        // Save both polygon and polyline data to Firebase
        dbase.child("spatialData ").push().setValue(dataMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Polygon and Polyline saved to Firebase!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to save data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        LatLng point = new LatLng(34.020882, -6.8539);
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point,12));
        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
//Create MarkerOptions
                MarkerOptions markerOptions = new MarkerOptions().position(latLng);
//Create Marker
                LatLng latLng1 = new LatLng(latLng.latitude + 1, latLng.longitude   ) ;
                MarkerOptions markerOptions1 = new MarkerOptions().position(latLng1);
                Marker marker = gMap.addMarker(markerOptions);
                latLngList.add(latLng);
                markerList.add(marker);

            }
        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        int id = seekBar.getId();

        if (id == R.id.seek_red) {
            red = i;
        } else if (id == R.id.seek_green) {
            green = i;
        } else if (id == R.id.seek_blue) {
            blue = i;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }


}