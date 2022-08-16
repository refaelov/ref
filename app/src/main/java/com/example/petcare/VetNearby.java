package com.example.petcare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

public class VetNearby extends AppCompatActivity {

    //intialize variable
    Spinner spType;
    Button btFind;
    SupportMapFragment supportMapFragment;
    GoogleMap map;
    FusedLocationProviderClient fusedLocationProviderClient;
    double currentLat=0,currentLong=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vet_nearby);

        //assign variable
        spType=findViewById(R.id.sp_type);
        btFind=findViewById(R.id.bt_find);
        supportMapFragment=(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);

        //initialize array of place type, now onle vet
        String [] placeTypeList= {"veterinary_care"};
        //intialize array of place names ,only vet now
        String  [] placeNameList={"ונטרינר"};

        //set adapter on sppinner
        spType.setAdapter(new ArrayAdapter<>(VetNearby.this, android.R.layout.simple_spinner_dropdown_item,placeNameList));

        //intialize fused location provided client
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(VetNearby.this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            getCurrentLocation();
        }
        else
        {
            //permission denied
            ActivityCompat.requestPermissions(VetNearby.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }
        btFind.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                //get selected pos of spinner
                int i=spType.getSelectedItemPosition();
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" + "?location=" + currentLat + "," + currentLong + "&radius=5000" + "&type=" + placeTypeList[i] + "&sensor=true" + "&key=" + getResources().getString(R.string.google_map_key);                //execute place task methos to download json data
                new PlaceTask().execute(url);

            }
        });

    }

    private void getCurrentLocation() {
        //intialize task location
        @SuppressLint("MissingPermission") Task<Location> task=fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null){
                    //get current latitude
                    currentLat=location.getLatitude();
                    //get current longitude
                    currentLong=location.getLongitude();
                    //synce map
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            map=googleMap;
                            //zoom current location on map
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(currentLat,currentLong),10
                            ));
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==44){
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }
        }
    }

    private class PlaceTask extends AsyncTask<String,Integer,String> {

        @Override
        protected String doInBackground(String... strings) {

            String data=null;

            //initialize data
            try {
                 data=downloadUrl(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            //execute parser task
            new ParserTask().execute(s);
        }

        private String downloadUrl(String string) throws IOException {
            //initialize url

            URL url=new URL(string);
            //initialize connection
            HttpURLConnection connection=(HttpURLConnection) url.openConnection();
            //connect

            connection.connect();


            //initialize input stream

            InputStream stream =connection.getInputStream();

            //initialize buffer reader
            BufferedReader reader=new BufferedReader(new InputStreamReader(stream));
            //initialize string builder
            StringBuilder builder=new StringBuilder();

            String line="";

            while ((line=reader.readLine())!=null){
                builder.append(line);

            }

            //get the append data
            String data=builder.toString();
            //close reader
            reader.close();

            return data;

        }
    }

    private class ParserTask extends AsyncTask<String,Integer, List<HashMap<String,String>>>{


        @Override
        protected List<HashMap<String, String>> doInBackground(String... strings) {
            //create json parser class
            JsonParser jsonParser=new JsonParser();
            //intialize hash map list
            List<HashMap<String,String>> mapList=null;
            JSONObject object=null;
            try {
                //intialize json object


                 object=new JSONObject(strings[0]);


                 //parse json object
                mapList=jsonParser.parseResult(object);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return mapList;

        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> hashMaps) {


            //clear map
//            map.clear();
            Log.d("bbbbbbb","daaaaaa");

            for(int i=0;i<hashMaps.size();i++){
                //intialize hashmap
                HashMap<String,String>hashMapList=hashMaps.get(i);
                //get latitude
                double lat= Double.parseDouble(hashMapList.get("lat"));
                //get longitude
                double lng= Double.parseDouble(hashMapList.get("lng"));
                //get name
                String name=hashMapList.get("name");
                //concat latitude and longitude
                LatLng latLng=new LatLng(lat,lng);

                //intialize marker option
                MarkerOptions options=new MarkerOptions();
                //set pos
                options.position(latLng);
                //set title
                options.title(name);
                //add marker on map
                map.addMarker(options);
            }
        }
    }

}