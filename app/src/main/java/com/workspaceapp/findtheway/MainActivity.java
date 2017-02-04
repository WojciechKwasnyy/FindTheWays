package com.workspaceapp.findtheway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    String json;


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

        } else if (id == R.id.nav_slideshow) {



        } else if (id == R.id.nav_logout) {
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

            //if(intent.getAction().equals(LocalizationService.MYACTIONREQUEST))
            //{
                String sender = intent.getStringExtra("Sender");
                String senderdiaplayname = intent.getStringExtra("SenderDisplayName");
               // dialogBox(senderdiaplayname);
            //}
             if(intent.getAction().equals(LocalizationService.MYACTION))
            {
                latiti = intent.getStringExtra("Latitiude");
                longti = intent.getStringExtra("Longtitude");
                Log.i("ReceivedLatitude",latiti);
                LatLng actual_position = new LatLng(Double.parseDouble(latiti), Double.parseDouble(longti));
                if (mmap!=null) {
                   // Marker actual_position_marker = mmap.addMarker(new MarkerOptions().position(actual_position).title("HERE"));
                    mmap.addMarker(new MarkerOptions().position(actual_position).title("YOU ARE HERE"));
                    mmap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            (actual_position), 12));
                }
            }
            else if(intent.getAction().equals(LocalizationService.MYACTIONREQUEST))
             {
                 dialogBox(intent.getStringExtra("Request"));
             }

            //JSONParser jParser = new JSONParser();
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
                            json = jParser.getJSONFromUrl(makeURL(sourcelat, sourcelon, destlat, destlon));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    drawPath(json);
                                }
                            });
                        }
                    }).start();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        public void dialogBox(String user) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setMessage(user+ " wants to find you");
            alertDialogBuilder.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            

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
                List<LatLng> list = decodePoly(encodedString);
                Polyline line = mmap.addPolyline(new PolylineOptions()
                        .addAll(list)
                        .width(12)
                        .color(Color.parseColor("#05b1fb"))//Google maps blue color
                        .geodesic(true)
                );
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
}
