package com.workspaceapp.findtheway;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FindFriendActivity extends AppCompatActivity {

    ListView listview;
    List<User> userslist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);
        listview = (ListView) findViewById(R.id.userslistview);



        userslist = new ArrayList<>();
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
                    userslist.add(new User(uID,email,provider,displayname));
                }
                listview.setAdapter(new UserListAdapter(getApplicationContext(), userslist));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialogBox(userslist.get(position));
            }
        });



    }

    public void dialogBox(final User user) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Do you want find "+user.getDisplayname());
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                        try{

                            FirebaseDatabase.getInstance().getReference().child(user.getUserID()).child("Messages").child("body").setValue("FIND_REQUEST");
                            FirebaseDatabase.getInstance().getReference().child(user.getUserID()).child("Messages").child("received").setValue(true);
                            FirebaseDatabase.getInstance().getReference().child(user.getUserID()).child("Messages").child("sender").setValue(Config.getInstance().userID);
                            FirebaseDatabase.getInstance().getReference().child(user.getUserID()).child("Messages").child("timestamp").setValue(ServerValue.TIMESTAMP);
                            //Config.getInstance().messageClient.send(message);
                        }catch(Exception e)
                        {
                            e.printStackTrace();
                        }
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
}
