package edu.msu.hujiahui.team13;

import static java.lang.String.valueOf;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GhostActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener, RoutingListener{

    private LocationManager locationManager = null;

    private GoogleMap mMap;

    private double latitude = 0;
    private double longitude = 0;
    private boolean valid = false;

    private double toLatitude = 0;
    private double toLongitude = 0;
    private String to = "";

    //support preferences
    private SharedPreferences settings = null;

    //preferences needs a name
    private final static String TO = "to";
    private final static String TOLAT = "tolat";
    private final static String TOLONG = "tolong";


    private ActiveListener activeListener = new ActiveListener();
    private Character mode = null;
    /*----------function------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);

        //read the address from the preferences
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        to = settings.getString(TO, "2250 Engineering");

        toLatitude = Double.parseDouble(settings.getString(TOLAT, "42.724303"));
        toLongitude = Double.parseDouble(settings.getString(TOLONG, "-84.480507"));

//        latitude = 42.731138;
//        longitude = -84.487508;
//        valid = true;
        /*request permission*/
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Also, dont forget to add overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //   int[] grantResults)
                // to handle the case where the user grants the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }

        // Get the location manager
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        // Force the screen to say on and bright
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //set default modes of transportation ('d' = drive)
        mode = 'd';

        // add location
        addLocation();
        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        addLocation();
    }




    /**
     * Set all user interface components to the current state
     */
    private void setUI() {
        TextView viewLatitude = (TextView)findViewById(R.id.textLatitude);
        TextView viewLongitude = (TextView)findViewById(R.id.textLongitude);
        TextView viewDistance = (TextView)findViewById(R.id.textDistance);
        TextView viewTextTo = (TextView)findViewById(R.id.textTo);
        //viewProvider.setText("");
        viewTextTo.setText(to);

        if(!valid){
            viewLatitude.setText(null);
            viewLongitude.setText(null);
            viewDistance.setText(null);
        } else{
            viewLatitude.setText(String.format("%1$6.6f", latitude));
            viewLongitude.setText(String.format("%1$6.6f", longitude));
//            double distance = Math.sqrt(Math.abs(latitude-toLatitude)*Math.abs(latitude-toLatitude) +
//                    Math.abs(longitude-toLongitude)*Math.abs(longitude-toLongitude));
            /*--------------calculate distance between two place*/
            double distance = calculateDistance();
            viewDistance.setText(String.format("%1$6.1fm", distance));
        }
    }

    private double calculateDistance(){
        Location locationA = new Location("point A");
        locationA.setLatitude(latitude);
        locationA.setLongitude(longitude);

        Location locationB = new Location("point B");
        locationB.setLatitude(toLatitude);
        locationB.setLongitude(toLongitude);

        return locationA.distanceTo(locationB);
    }

    /**
     * Called when this application becomes foreground again.
     */
    @Override
    protected void onResume() {
        super.onResume();


        setUI();
        registerListeners();
    }

    /**
     * Called when this application is no longer the foreground application.
     */
    @Override
    protected void onPause() {
        unregisterListeners();
        super.onPause();
    }

    private LatLng[] markedPoints = new LatLng[5];
    private String[] markerTag = new String[5];
    private String[] Captured = new String[5];

    private void addLocation(){
        markedPoints[0] = new LatLng(42.732549, -84.477018);
        markerTag[0] = "Eli and Edythe Broad Art Museum";
        markedPoints[1] = new LatLng(42.727170, -84.485196);
        markerTag[1] = "Spartan Stadium";
        markedPoints[2] = new LatLng(42.731366, -84.481561);
        markerTag[2] = "MSU Museum";
        markedPoints[3] = new LatLng(42.727430, -84.482619);
        markerTag[3] = "Wells Hall";
        markedPoints[4] = new LatLng(42.724524, -84.488030);
        markerTag[4] = "Case Hall";
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng sydney = new LatLng(latitude, longitude);
        mMap = googleMap;

        mMap.setOnMarkerClickListener(this);

        googleMap.addMarker(new MarkerOptions()
                .position(sydney)
                        .icon(BitmapFromVector(getApplicationContext(), R.drawable.ic_android_black_24dp))
                .title("Current Location"));

        int[] drawables = new int[]{
                R.drawable.ghost1,
                R.drawable.ghost2,
                R.drawable.ghost3,
                R.drawable.ghost4,
                R.drawable.ghost5,
                R.drawable.ghost6
        };
        int i = 0;
        for (LatLng loc: markedPoints){
            if (loc!= null) {
                googleMap.addMarker(new MarkerOptions()
                        .position(loc)
                        .icon(BitmapFromVector(getApplicationContext(), drawables[i]))
                        .title(markerTag[i]));
                i++;
            }else{
                break;
            }
        }

        // Zoom in to a specific level
        animateLatLngZoom(sydney,13,0,10);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        // Get the marker's position
        LatLng position = marker.getPosition();
        double lat = position.latitude;
        double lng = position.longitude;
        to = marker.getTitle();
        toLatitude = lat;
        toLongitude = lng;
        setUI();
        //Toast.makeText(this, "Point Chosen: " + lat + " " + lng, Toast.LENGTH_LONG).show();+

        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(GhostActivity.this)
                .waypoints(new LatLng(latitude, longitude), marker.getPosition())
                .key("AIzaSyA4U_EoGMD8nsry6J5OXTV_YHcUisFsuJQ")
                .build();
        routing.execute();
        return true;

//        toLatitude = lat;
//        toLongitude = lng;
//        mode = 'w';
//        onOpenMap(findViewById(R.id.ghostview));
//        return true;
    }


    private void animateLatLngZoom(LatLng latlng, int reqZoom, int offsetX, int offsetY) {

        // Save current zoom
        float originalZoom = mMap.getCameraPosition().zoom;

        // Move temporarily camera zoom
        mMap.moveCamera(CameraUpdateFactory.zoomTo(reqZoom));

        Point pointInScreen = mMap.getProjection().toScreenLocation(latlng);

        Point newPoint = new Point();
        newPoint.x = pointInScreen.x + offsetX;
        newPoint.y = pointInScreen.y + offsetY;

        LatLng newCenterLatLng = mMap.getProjection().fromScreenLocation(newPoint);

        // Restore original zoom
        mMap.moveCamera(CameraUpdateFactory.zoomTo(originalZoom));

        // Animate a camera with new latlng center and required zoom.
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newCenterLatLng, reqZoom));

    }

    private BitmapDescriptor BitmapFromVector(Context context, int vectorResId) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    /**
     * The location services expect to send results to a class that
     * implements the LocationListener interface.
     */
    private class ActiveListener implements LocationListener {


        @Override
        public void onLocationChanged(Location location) {
            onLocation(location);
            distanceCheck();
            renderCapturedMark();


        }

        @Override
        public void onStatusChanged(String s, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {
            registerListeners();
        }
    }

    private void renderCapturedMark() {

        int j=0;

        if (Captured[0] != null){ findViewById(R.id.mark1).setVisibility(View.VISIBLE);j++;}
        if (Captured[1] != null){ findViewById(R.id.mark2).setVisibility(View.VISIBLE);j++;}
        if (Captured[2] != null){ findViewById(R.id.mark3).setVisibility(View.VISIBLE);j++;}
        if (Captured[3] != null){ findViewById(R.id.mark4).setVisibility(View.VISIBLE);j++;}
        if (Captured[4] != null){ findViewById(R.id.mark5).setVisibility(View.VISIBLE);j++;}

        if (j==5){
            findViewById(R.id.mark6).setVisibility(View.VISIBLE);
        }
    }


    private void registerListeners() {
        unregisterListeners();

        // Create a Criteria object
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(false);

        String bestAvailable = locationManager.getBestProvider(criteria, true);

        if(bestAvailable != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(bestAvailable, 500, 1, activeListener);
            Location location = locationManager.getLastKnownLocation(bestAvailable);
            onLocation(location);
        }
    }

    private void unregisterListeners() {
        locationManager.removeUpdates(activeListener);
    }

    /**
     * handle new location updates
     * @param location
     */
    private void onLocation(Location location) {
        if(location == null) {
            return;
        }

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        valid = true;

        setUI();
    }

//    /**
//     * create menu item
//     * @param menu
//     * @return
//     */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main, menu);
//        return true;
//    }

//    /**
//     * Handle an options menu selection
//     */
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        switch (id) {
//            case R.id.itemSparty:
//                newTo("Sparty", 42.731138, -84.487508);
//                return true;
//
//            case R.id.itemHome:
//                newTo("Univeristy Edge", 42.733141, -84.504208);
//                return true;
//
//            case R.id.item2250:
//                newTo("2250 Engineering", 42.724303, -84.480507);
//                return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

//    /**
//     * Handle setting a new "to" location.
//     * @param address Address to display
//     * @param lat latitude
//     * @param lon longitude
//     */
//    private void newTo(String address, double lat, double lon) {
//        to = address;
//        toLatitude = lat;
//        toLongitude = lon;
//
//        setUI();
//        saveCurrentState();
//    }

    /**
     * save current info to preference settings
     */
    public void saveCurrentState(){
        SharedPreferences.Editor editor=settings.edit();
        editor.putString(TO, to);
        editor.putString(TOLAT, String.format("%1$6.6f", toLatitude));
        editor.putString(TOLONG, String.format("%1$6.6f", toLongitude));
        editor.commit();
    }

//    public void onNew(View view) {
//        EditText location = (EditText)findViewById(R.id.editLocation);
//        final String address = location.getText().toString().trim();
//        newAddress(address);
//    }

//    private void newAddress(final String address) {
//        if(address.equals("")) {
//            // Don't do anything if the address is blank
//            return;
//        }
//
//        new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                lookupAddress(address);
//
//            }
//
//        }).start();
//    }

//    /**
//     * Look up the provided address. This works in a thread!
//     * @param address Address we are looking up
//     */
//    private void lookupAddress(String address) {
//        Geocoder geocoder = new Geocoder(GhostActivity.this, Locale.US);
//        boolean exception = false;
//        List<Address> locations;
//        try {
//            locations = geocoder.getFromLocationName(address, 1);
//        } catch(IOException ex) {
//            // Failed due to I/O exception
//            locations = null;
//            exception = true;
//        }
//
//        // call function from user interface thread...
//        final boolean exception_final = exception;
//        final List<Address> locations_final = locations;
//        GhostActivity activity = (GhostActivity)this;
//        activity.runOnUiThread(new Runnable() {
//            public void run() {
//                newLocation(address,exception_final,locations_final);
//            }
//        });
//    }

//    private void newLocation(String address, boolean exception, List<Address> locations) {
//
//        if(exception) {
//            Toast.makeText(GhostActivity.this, R.string.exception, Toast.LENGTH_SHORT).show();
//        } else {
//            if(locations == null || locations.size() == 0) {
//                Toast.makeText(this, R.string.couldnotfind, Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            EditText location = (EditText)findViewById(R.id.editLocation);
//            location.clearAnimation();
//
//            // We have a valid new location
//            Address a = locations.get(0);
//            newTo(address, a.getLatitude(), a.getLongitude());
//        }
//    }

//    public void onOpenMap(View view){
//        // Create a Uri from an intent string. Use the result to create an Intent.
//        Uri gmmIntentUri = Uri.parse(String.format("google.navigation:q=%f,%f&mode=%c",toLatitude,toLongitude,mode));
//
//        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
//        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//        // Make the Intent explicit by setting the Google Maps package
//        mapIntent.setPackage("com.google.android.apps.maps");
//
//        // Attempt to start an activity that can handle the Intent
//        if (mapIntent.resolveActivity(getPackageManager()) != null) {
//            startActivity(mapIntent);
//        }
//
//    }
    private void addCaptured(String target) {
        int i = 0;
        while (true) {
            if (Captured[i] == null) {
                Captured[i] = target;
                break;
            } else
                i++;
        }
    }

    private void distanceCheck(){

        double distance = calculateDistance();

        if (distance<75){
            if (Arrays.asList(Captured).contains(to)){

            }else{
                CaptureDlg dlg1 = new CaptureDlg();
                dlg1.show(getFragmentManager(), "capture");
                addCaptured(to);
           }

        }
    }

    private boolean captured = false;
    public void setCaptured(boolean b){
        captured = b;
    }


    @Override
    public void onRoutingFailure(RouteException e) {
        Log.e("check", e.getMessage());
    }

    @Override
    public void onRoutingStart() {
        Log.e("check", "onRoutingStart");
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        Log.e("check", "onRoutingSuccess");
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(latitude,longitude));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        List<Polyline> polylines = new ArrayList<>();

        mMap.moveCamera(center);


        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(R.color.colorRouteLine));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);
        }
    }

    @Override
    public void onRoutingCancelled() {
        Log.e("check", "onRoutingCancelled");
    }
}

