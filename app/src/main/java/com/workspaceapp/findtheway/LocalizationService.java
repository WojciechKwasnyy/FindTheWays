package com.workspaceapp.findtheway;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.crypto.Cipher;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

/**
 * Created by Wojtek-ASUS on 13.01.2017.
 */

public class LocalizationService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    final static String MYACTION = "LOCALIZATION_DATA_SEND";
    final static String MYACTIONREQUEST = "FIND_REQUEST_SEND";
    final static String LOG_MESSAGING = "Messaging";
    FirebaseDatabase database;
    private KeyPair keyPair;

    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        try {
            Initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }

        listenformessages();


    }

    private void listenformessages(){

        DatabaseReference messagesref =  FirebaseDatabase.getInstance().getReference().child(Config.getInstance().userID).child("Messages");

        messagesref.child("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(LOG_MESSAGING,"Messaging initialized");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

/*
        messagesref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i(LOG_MESSAGING, "Child added");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String body = "";
                String received= "";
                String sender = "";
                String timestamp ="";
                Log.i(LOG_MESSAGING, "Child changed");
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    //new User(dataSnapshot.getKey(),dataSnapshot.child("displayname").getValue().toString(),dataSnapshot.child("email").getValue().toString());
                    if(child.getKey().equals("body")) {
                        body = child.getValue().toString();
                    }
                    if(child.getKey().equals("received"))
                    {
                        received = child.getValue().toString();
                    }
                    if(child.getKey().equals("sender"))
                    {
                        sender = child.getValue().toString();
                    }
                    if(child.getKey().equals("timestamp"))
                    {
                        timestamp = child.getValue().toString();
                    }

                    Intent intent = new Intent();
                    intent.setAction(MYACTIONREQUEST);
                    intent.putExtra("Request", sender);
                    if(body.equals("FIND_REQUEST"))
                        sendBroadcast(intent);

                    //String sender = child.child("sender").getValue().toString();

                    //Intent intent = new Intent();
                    // intent.setAction(MYACTIONREQUEST);
                    //if(body.equals("FIND_REQUEST"))

                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.i(LOG_MESSAGING, "Child removed");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.i(LOG_MESSAGING, "Child moved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i(LOG_MESSAGING, "Cancelled");
            }
        });
    }*/


        messagesref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String body = "";
                String received= "";
                String sender = "";
                String timestamp ="";
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    //new User(dataSnapshot.getKey(),dataSnapshot.child("displayname").getValue().toString(),dataSnapshot.child("email").getValue().toString());
                    if(child.getKey().equals("body")) {
                        body = child.getValue().toString();
                    }
                    if(child.getKey().equals("received"))
                    {
                        received = child.getValue().toString();
                    }
                    if(child.getKey().equals("sender"))
                    {
                        sender = child.getValue().toString();
                        if(sender.equals("FIND_REQUEST"))
                        {
                            Intent intent = new Intent();
                            intent.setAction(MYACTIONREQUEST);
                            intent.putExtra("Request",sender);
                            sendBroadcast(intent);

                        }
                    }
                    if(child.getKey().equals("timestamp"))
                    {
                        timestamp = child.getValue().toString();
                    }

                    //String sender = child.child("sender").getValue().toString();



                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });}

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
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

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);


        if (mLastLocation != null) {
            /*Toast.makeText(this, "Latitude: " + String.valueOf(mLastLocation.getLatitude()) + "Longitude: " +
                    String.valueOf(mLastLocation.getLongitude()), Toast.LENGTH_SHORT).show();*/
            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<android.location.Address> addresses = null;
            try {
                addresses = gcd.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                Log.i("Localization: ", String.valueOf(mLastLocation.getLatitude()) + String.valueOf(mLastLocation.getLongitude()));
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("Latitiude",  String.valueOf(mLastLocation.getLatitude()));
                intent.putExtra("Longtitude",  String.valueOf(mLastLocation.getLongitude()));

            } catch (IOException e) {
                e.printStackTrace();
            }
            {if(addresses != null)
                if (addresses.size() > 0)
                    Log.i("Current localization: ", addresses.get(0).getLocality());


            }
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Intent intent = new Intent();
        intent.setAction(MYACTION);
        intent.putExtra("Latitiude",String.valueOf(mLastLocation.getLatitude()));
        intent.putExtra("Longtitude",String.valueOf(mLastLocation.getLongitude()));
        sendBroadcast(intent);
        database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(FirebaseAuth.getInstance().getCurrentUser().getUid());
        DatabaseReference localizationRef = myRef.child("localization");
        DatabaseReference latitudeRef = localizationRef.child("Latitude");
        DatabaseReference longtitudeRef = localizationRef.child("Longtitude");
        //localizationRef.setValue(mLastLocation.getLatitude() +" "+ mLastLocation.getLongitude());

        try {
            String encodedlati = encrypt(String.valueOf(mLastLocation.getLatitude()));
            String encodedlong = encrypt(String.valueOf(mLastLocation.getLongitude()));
            latitudeRef.setValue(mLastLocation.getLatitude());
            longtitudeRef.setValue(mLastLocation.getLongitude());
            Log.i("Crypted Latitude",encodedlati);
            String decodedlati = decrypt(encodedlati);
            Log.i("Decrypted Latitude",decodedlati);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void Initialize() throws Exception
    {
        KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
        keygen.initialize(512);
        keyPair = keygen.generateKeyPair();

        String privatekeyString = savePrivateKey(keyPair.getPrivate());
        String publickeyString = savePublicKey(keyPair.getPublic());

    }
        public String encrypt(String plaintext)  throws Exception
        {
            PublicKey key = keyPair.getPublic();
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes("UTF8"));
            return encodeBASE64(ciphertext);
        }

    public String decrypt(String ciphertext)  throws Exception
    {
        PrivateKey key = keyPair.getPrivate();
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] plaintext = cipher.doFinal(decodeBASE64(ciphertext));
        return new String(plaintext, "UTF8");
    }

    private static String encodeBASE64(byte[] bytes)
    {
        BASE64Encoder b64 = new BASE64Encoder();
        return b64.encode(bytes);
    }

    private static byte[] decodeBASE64(String text) throws Exception
    {
        BASE64Decoder b64 = new BASE64Decoder();
        return b64.decodeBuffer(text);
    }

    public static String savePrivateKey(PrivateKey priv) throws GeneralSecurityException {
        KeyFactory fact = KeyFactory.getInstance("DSA");
        PKCS8EncodedKeySpec spec = fact.getKeySpec(priv,
                PKCS8EncodedKeySpec.class);
        byte[] packed = spec.getEncoded();
        String key64 = encodeBASE64(packed);

        Arrays.fill(packed, (byte) 0);
        return key64;
    }

    public static String savePublicKey(PublicKey publ) throws GeneralSecurityException {
        KeyFactory fact = KeyFactory.getInstance("DSA");
        X509EncodedKeySpec spec = fact.getKeySpec(publ,
                X509EncodedKeySpec.class);
        return encodeBASE64(spec.getEncoded());
    }

    public static PublicKey loadPublicKey(String stored) throws Exception {
        byte[] data = decodeBASE64(stored);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory fact = KeyFactory.getInstance("DSA");
        return fact.generatePublic(spec);
    }

    public static PrivateKey loadPrivateKey(String key64) throws Exception {
        byte[] clear = decodeBASE64(key64);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
        KeyFactory fact = KeyFactory.getInstance("DSA");
        PrivateKey priv = fact.generatePrivate(keySpec);
        Arrays.fill(clear, (byte) 0);
        return priv;
    }

    private String getusername(final String userID)
    {
        final List<User> users = new ArrayList<>();
        String displayname = "0";
        FirebaseDatabase.getInstance().getReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    //new User(dataSnapshot.getKey(),dataSnapshot.child("displayname").getValue().toString(),dataSnapshot.child("email").getValue().toString());
                    String displayname = child.child("displayname").getValue().toString();
                    String email;
                    if(child.child("email").getValue() == null)
                    {
                        email = "no address";
                    }
                    else
                    {
                        email = child.child("email").getValue().toString();
                    }

                    String provider = child.child("provider").getValue().toString();
                    String uID = child.getKey();
                   users.add(new User(uID,email,provider,displayname));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        for(int i =0;i <users.size();i++)
        {
            if(users.get(i).getUserID().equals(userID))
            {
                displayname = users.get(i).getDisplayname();
            }
        }
        return displayname;
    }
}
