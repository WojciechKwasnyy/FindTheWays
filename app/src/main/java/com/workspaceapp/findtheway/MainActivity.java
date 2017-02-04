package com.workspaceapp.findtheway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    MyReceiver myReceiver;
    //  Button logoutbutton;
    String latiti;
    String longti;
    String friend_langti ="";
    String friend_longti = "";
    private SupportMapFragment map_frag;
    GoogleMap mmap;
    AlertDialog.Builder builderSingle;
    String jsonpoints;
    String jsonlength;
    LatLng meet_point;
    MarkerOptions meetpoint_marker;
    String parsedDistance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LocalizationService.MYACTION);
        registerReceiver(myReceiver, intentFilter);



        map_frag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        map_frag.getMapAsync(this);

        //logoutbutton = (Button) findViewById(R.id.button2);
        //logoutbutton.setOnClickListener(new View.OnClickListener() {
        //   @Override
        //  public void onClick(View v) {
        //      FirebaseAuth.getInstance().signOut();
        //      finish();
        //   }
        // });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        String email = Config.getInstance().email;
        String displayname = Config.getInstance().username;
        String provider = Config.getInstance().provider;


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        //Menu item_find = menu.getItem(R.id.)
        View hView =  navigationView.getHeaderView(0);
        TextView nav_user = (TextView)hView.findViewById(R.id.draweremail);
        TextView nav_username = (TextView)hView.findViewById(R.id.username);
        nav_username.setText(displayname);
        nav_user.setText(email);
        ImageView providerimg = (ImageView) hView.findViewById(R.id.providerImageView);

        FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("displayname").setValue(displayname);
        FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("provider").setValue(provider);
        FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("email").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        if(provider.equals("facebook.com"))
        {
            providerimg.setImageResource(R.drawable.facebookicon);

        }
        else if(provider.equals("password"))
        {

        }
        else if(provider.equals("google.com"))
        {
            providerimg.setImageResource(R.drawable.googleicon);
        }

        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_findfriend) {
            startActivity(new Intent(getApplicationContext(),FindFriendActivity.class));

        } else if (id == R.id.nav_gallery) {
            startActivity(new Intent(getApplicationContext(),ChatActivity.class));

        }  else if (id == R.id.nav_logout) {
            try {
                //Config.getInstance().sinchClient.terminate();
                FirebaseAuth.getInstance().signOut();
                finish();
                stopService(new Intent(getApplicationContext(),LocalizationService.class));
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mmap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Config.getInstance().connectedwith).child("localization");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot child : dataSnapshot.getChildren())
                    {
                        if(child.getKey().equals("Latitude"))
                        {
                            friend_langti = child.getValue().toString();
                        }
                        else if(child.getKey().equals("Longtitude"))
                        {
                            friend_longti = child.getValue().toString();
                        }
                    }
                    if(Config.getInstance().isfriendlocationenabled) {
                        LatLng friend_actual_position = new LatLng(Double.parseDouble(friend_langti), Double.parseDouble(friend_longti));
                        mmap.addMarker(new MarkerOptions().position(friend_actual_position).title("YOUR FRIEND IS HERE").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });


       // mmap.setMyLocationEnabled(true);


    }

    public class MyReceiver extends BroadcastReceiver {
        MapView mapView;
        String url ="";

        @Override
        public void onReceive(Context context, Intent intent) {

             if(intent.getAction().equals(LocalizationService.MYACTION))
            {
                latiti = intent.getStringExtra("Latitiude");
                longti = intent.getStringExtra("Longtitude");
                Log.i("ReceivedLatitude",latiti);
                LatLng actual_position = new LatLng(Double.parseDouble(latiti), Double.parseDouble(longti));
                if (mmap!=null) {
                   // Marker actual_position_marker = mmap.addMarker(new MarkerOptions().position(actual_position).title("HERE"));
                    mmap.addMarker(new MarkerOptions().position(actual_position));
                    mmap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            (actual_position), 12));
                    setPois();
                }

            }
            else if(intent.getAction().equals(LocalizationService.MYACTIONREQUEST))
             {
                 dialogBox(intent.getStringExtra("Request"));
             }
            if(!friend_langti.equals("")) {
                try {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            JSONParser jParser = new JSONParser();
                            Double sourcelat = Double.parseDouble(latiti);
                            Double sourcelon = Double.parseDouble(longti);
                            Double destlat = Double.parseDouble(friend_langti);
                            Double destlon = Double.parseDouble(friend_longti);
                            jsonpoints = jParser.getJSONFromUrl(makeURL(sourcelat, sourcelon, destlat, destlon));
                           // jsonlength = jParser.getJSONFromUrl(makeURLdistance(sourcelat,sourcelon,destlat,destlon));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    drawPath(jsonpoints);
                                }
                            });
                        }
                    }).start();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        public void dialogBox(final String user) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setMessage(user+ " wants to find you");
            alertDialogBuilder.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            FirebaseDatabase.getInstance().getReference().child(user).child("Messages").child("body").setValue("FIND_RESPONSE_OK");
                            FirebaseDatabase.getInstance().getReference().child(user).child("Messages").child("received").setValue(true);
                            FirebaseDatabase.getInstance().getReference().child(user).child("Messages").child("sender").setValue(Config.getInstance().userID);
                            FirebaseDatabase.getInstance().getReference().child(user).child("Messages").child("timestamp").setValue(ServerValue.TIMESTAMP);
                            Config.getInstance().isfriendlocationenabled = true;
                            Config.getInstance().connectedwith = user;
                        }
                    });

            alertDialogBuilder.setNegativeButton("cancel",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        public void drawPath(String  result) {
            Polyline line2;
            try {
                //Tranform the string into a json object
                final JSONObject json = new JSONObject(result);
                JSONArray routeArray = json.getJSONArray("routes");
                JSONObject routes = routeArray.getJSONObject(0);
                JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
                String encodedString = overviewPolylines.getString("points");

                //String destination = destinationaddress.getString("")
                List<LatLng> list = decodePoly(encodedString);
                meet_point = list.get(list.size()/2);





                Polyline line = mmap.addPolyline(new PolylineOptions()
                        .addAll(list)
                        .width(12)
                        .color(Color.parseColor("#006400"))//Google maps blue color
                        .geodesic(true)
                );

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            JSONParser jParser = new JSONParser();
                            Double sourcelat = meet_point.latitude;
                            Double sourcelon = meet_point.longitude;
                            Double destlat = Double.parseDouble(friend_langti);
                            Double destlon = Double.parseDouble(friend_longti);
                            jsonlength = jParser.getJSONFromUrl(makeURL(sourcelat,sourcelon,destlat, destlon));
                            final JSONObject json = new JSONObject(jsonlength);
                            JSONArray array = json.getJSONArray("routes");
                            JSONObject routes = array.getJSONObject(0);
                            JSONArray legs = routes.getJSONArray("legs");
                            JSONObject steps = legs.getJSONObject(0);
                            JSONObject distance = steps.getJSONObject("distance");
                            parsedDistance=distance.getString("text");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    meetpoint_marker = new MarkerOptions().position(meet_point).title("MEET POINT").snippet(parsedDistance).icon(BitmapDescriptorFactory.fromResource(R.drawable.placeholder));
                                    mmap.addMarker(meetpoint_marker);
                                }
                            });


                        }catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }).start();
/*
           for(int z = 0; z<list.size()-1;z++){
                LatLng src= list.get(z);
                LatLng dest= list.get(z+1);
                 line2 = mmap.addPolyline(new PolylineOptions()
                .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude,   dest.longitude))
                .width(2)
                .color(Color.BLUE).geodesic(true));
            }
*/
            }
            catch (JSONException e) {

            }

        }
        private List<LatLng> decodePoly(String encoded) {

            List<LatLng> poly = new ArrayList<LatLng>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng( (((double) lat / 1E5)),
                        (((double) lng / 1E5) ));
                poly.add(p);
            }
            return poly;
        }
    }
    public String makeURL (double sourcelat, double sourcelog, double destlat, double destlog ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString
                .append(Double.toString( sourcelog));
        urlString.append("&destination=");
        urlString
                .append(Double.toString( destlat));
        urlString.append(",");
        urlString.append(Double.toString( destlog));
        urlString.append("&sensor=false&mode=walking&alternatives=true");
        urlString.append("&key=AIzaSyAvts-3gnuzIMKQK0YQH_0aFLSd0A3uimc"); //
        return urlString.toString();
    }

    public String makeURLdistance(double sourcelat, double sourcelog, double destlat, double destlog)
    {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/distancematrix/json");
        urlString.append("?units=imperial&origins=");
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString.append(Double.toString(sourcelog));
        urlString.append("&destinations=");
        urlString.append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=walking&alternatives=true");
        urlString.append("&key=AIzaSyAvts-3gnuzIMKQK0YQH_0aFLSd0A3uimc"); //
        return urlString.toString();

    }
    public void setPois()
    {
        BitmapDescriptor icon_volleyball = BitmapDescriptorFactory.fromResource(R.drawable.volleyball24);
        BitmapDescriptor icon_gym = BitmapDescriptorFactory.fromResource(R.drawable.weightlifting);
        BitmapDescriptor icon_put = BitmapDescriptorFactory.fromResource(R.drawable.mortarboard);
        BitmapDescriptor icon_hotel = BitmapDescriptorFactory.fromResource(R.drawable.hotel);
        BitmapDescriptor icon_arena = BitmapDescriptorFactory.fromResource(R.drawable.trophy);
        BitmapDescriptor icon_air = BitmapDescriptorFactory.fromResource(R.drawable.humidity);
        BitmapDescriptor icon_market = BitmapDescriptorFactory.fromResource(R.drawable.cart);
        BitmapDescriptor icon_fuel = BitmapDescriptorFactory.fromResource(R.drawable.fuel);

        LatLng volleyball_place = new LatLng(52.393417, 16.950931);
        LatLng volleyball_place2 = new LatLng(52.4290443, 16.8759770);
        LatLng gym_place = new LatLng(52.395478, 16.942527);
        LatLng put_poznan = new LatLng(52.402838, 16.951529);
        LatLng uam_campus = new LatLng(52.467238, 16.923818);
        LatLng hotel = new LatLng(52.401028, 16.926190);
        LatLng arena = new LatLng(52.397498, 16.891832);
        LatLng air_condition = new LatLng(52.420193, 16.877376);
        LatLng market = new LatLng(52.393334, 16.920083);
        LatLng fuel_station = new LatLng(52.426949, 16.917654);

        mmap.addMarker(new MarkerOptions().position(volleyball_place).title("Beach volleyball field").icon(icon_volleyball));
        mmap.addMarker(new MarkerOptions().position(volleyball_place2).title("Beach volleyball field").icon(icon_volleyball));
        mmap.addMarker(new MarkerOptions().position(gym_place).title("Gym").icon(icon_gym));
        mmap.addMarker(new MarkerOptions().position(put_poznan).title("Poznan Universitet of Technology").icon(icon_put));
        mmap.addMarker(new MarkerOptions().position(uam_campus).title("Campus UAM").icon(icon_put));
        mmap.addMarker(new MarkerOptions().position(hotel).title("Andersia hotel & restaurant").icon(icon_hotel));
        mmap.addMarker(new MarkerOptions().position(arena).title("Hala widowiskowa Arena").icon(icon_arena));
        mmap.addMarker(new MarkerOptions().position(air_condition).title("The air quality monitoring system").icon(icon_air));
        mmap.addMarker(new MarkerOptions().position(market).title("market square").icon(icon_market));
        mmap.addMarker(new MarkerOptions().position(fuel_station).title("Gas station").icon(icon_fuel));
    }
}
