package com.example.earthquakewatcher;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.earthquakewatcher.Model.Earthquake;
import com.example.earthquakewatcher.UI.CustomInfoWindow;
import com.example.earthquakewatcher.Util.Constants;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;

    private LocationManager locationManager;
    private LocationListener locationListener;
private RequestQueue queue;
private AlertDialog.Builder dialogBuilder;
private AlertDialog dialog;
private BitmapDescriptor[] iconcolors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        iconcolors=new BitmapDescriptor[]
                {BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET),
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE),
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)};
    queue= Volley.newRequestQueue(this);

    getEarthQuake();
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setInfoWindowAdapter(new CustomInfoWindow(getApplicationContext()));
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (Build.VERSION.SDK_INT < 23) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }
        else
        {if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
            else

        {  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
Location location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
//mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
        //.title("Hello"));
mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,8));

        }}


        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0&& grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                    ==PackageManager.PERMISSION_GRANTED)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }
    }
    public void getEarthQuake() {
        final Earthquake earthquake=new Earthquake();
       JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, Constants.URL, null,
               new Response.Listener<JSONObject>() {
                   @Override
                   public void onResponse(JSONObject response) {
                       try {
                           JSONArray features=response.getJSONArray("features");
                           for(int i=0;i<Constants.LIMIT;i++){
                               JSONObject properties=features.getJSONObject(i).getJSONObject("properties");
                               JSONObject geometry =features.getJSONObject(i).getJSONObject("geometry");
                               JSONArray coordinates=geometry.getJSONArray("coordinates");
                               double lon=coordinates.getDouble(0);
                               double lat=coordinates.getDouble(1);

                               //Log.d("Quake : ",lon+";"+lat);
                               earthquake.setPlace(properties.getString("place"));
                               earthquake.setType(properties.getString("type"));
                               earthquake.setTime(properties.getLong("time"));
                               earthquake.setMagnitude(properties.getDouble("mag"));
                               earthquake.setDetailLink(properties.getString("detail"));
                               java.text.DateFormat dateFormat=java.text.DateFormat.getDateInstance();
                               String formattedDate=dateFormat.format(new Date(Long.valueOf(properties.getLong("time"))).getTime());
                               MarkerOptions markerOptions=new MarkerOptions();
                               markerOptions.icon(iconcolors[Constants.randomInt(iconcolors.length,0)]);
                               markerOptions.title(earthquake.getPlace());
                               markerOptions.position(new LatLng(lat,lon));
                               markerOptions.snippet("Magnitude : "+earthquake.getMagnitude()+"\n"+"Date :"+formattedDate);
                               Marker marker =mMap.addMarker(markerOptions);
                               marker.setTag(earthquake.getDetailLink());
                               mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lon),1));



                           }
                       } catch (JSONException e) {
                           e.printStackTrace();
                       }


                   }
               }, new Response.ErrorListener() {
           @Override
           public void onErrorResponse(VolleyError error) {

           }
       });
        queue.add(jsonObjectRequest);


    }

    @Override
    public void onInfoWindowClick(Marker marker) {
       // Toast.makeText(this,"here : "+marker.getTag().toString(),Toast.LENGTH_SHORT).show();
      //Log.d("GETTING ",marker.getTag().toString());
         getQuakeDetails(marker.getTag().toString());

    }

    private void getQuakeDetails(String url) {
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
String detailUrl="";
                        try {
                            JSONObject  properties = response.getJSONObject("properties");
                            JSONObject products=properties.getJSONObject("products");
                            JSONArray georeserve=products.getJSONArray("geoserve");
                            for(int i=0;i<georeserve.length();i++)
                            {
                                JSONObject geoObj=georeserve.getJSONObject(i);
                                JSONObject contents=geoObj.getJSONObject("contents");
                                JSONObject geoJson=contents.getJSONObject("geoserve.json");
                                detailUrl=geoJson.getString("url");
                                //getMoredetais(detailUrl);
                            }
                         //   Log.d("URL here ",detailUrl);
                            getMoredetais(detailUrl);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonObjectRequest);
    }
    public void getMoredetais(String url)
    {
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dialogBuilder=new AlertDialog.Builder(MapsActivity.this);
                        View view=getLayoutInflater().inflate(R.layout.popup,null);
                        Button dismiss=view.findViewById(R.id.dismisspop);
                        Button dismisstop=view.findViewById(R.id.dismisspoptop);
                        TextView poplist=view.findViewById(R.id.poplist);
                        WebView htmlpop=view.findViewById(R.id.htmlwebview);
                        StringBuilder stringBuilder=new StringBuilder();
                        try {
                            if(response.has("tectonicSummary")&&response.getString("tectonicSummary")!=null)
                            {
                                JSONObject tectonics=response.getJSONObject("tectonicSummary");
                                if(tectonics.has("text")&&tectonics.getString("text")!=null)
                                {
                                    String text=tectonics.getString("text");
                                    htmlpop.loadDataWithBaseURL(null,text,"text/html","UTF-8",null);


                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            JSONArray cities=response.getJSONArray("cities");
                            for(int i=0;i<cities.length();i++)
                            {
                                JSONObject citiesobj=cities.getJSONObject(i);
                                stringBuilder.append("CIty : "+citiesobj.getString("name")+
                                        "\n Distance : "+citiesobj.getString("distance")+"\n Population :"
                                        +citiesobj.getString("population"));
                                stringBuilder.append("\n\n");
                            }
                   poplist.setText(stringBuilder);
                            dialogBuilder.setView(view);
                            dialog=dialogBuilder.create();
                            dialog.show();
                            dismiss.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
dialog.dismiss();
                                }
                            });

dismisstop.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        dialog.dismiss();
    }
});
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsonObjectRequest);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
}



