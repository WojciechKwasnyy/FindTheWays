package com.workspaceapp.findtheway;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    List <Message> messages;
    ChatAdapter chatAdapter;
    ListView chatListView;
    ImageButton sendButton;
    EditText textToSendField;
    int messagescounter =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getSupportActionBar().setTitle("Chat with " + Config.getInstance().getDisplayname(Config.getInstance().connectedwith));

        chatListView = (ListView) findViewById(R.id.chat_listview);
        sendButton = (ImageButton) findViewById(R.id.send_button);
        textToSendField = (EditText) findViewById(R.id.chat_ET);
        messages  = new ArrayList<>();
        chatAdapter = new ChatAdapter(messages,this);
        chatListView.setAdapter(chatAdapter);


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    DatabaseReference messagesRef =  FirebaseDatabase.getInstance().getReference().child(Config.getInstance().connectedwith).child("Messages");
                    messagesRef.child("body").setValue(textToSendField.getText().toString());
                    messagesRef.child("received").setValue("true");
                    messagesRef.child("sender").setValue(Config.getInstance().userID);
                    messagesRef.child("timestamp").setValue(ServerValue.TIMESTAMP.toString());
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

                    messages.add(new Message(Config.getInstance().userID, true, ServerValue.TIMESTAMP.toString(), textToSendField.getText().toString()));
                chatAdapter.notifyDataSetChanged();
                textToSendField.setText("");
            }
        });

        DatabaseReference myMessagesRef = FirebaseDatabase.getInstance().getReference().child(Config.getInstance().userID).child("Messages");
        myMessagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Message message = new Message();
                for(DataSnapshot child : dataSnapshot.getChildren())
                {
                    if(child.getKey().equals("body"))
                    {
                        message.setBody(child.getValue().toString());
                    }
                    else if (child.getKey().equals("sender"))
                    {
                        message.setSender(child.getValue().toString());
                    }
                    else if(child.getKey().equals("received"))
                    {
                        message.setReceived(Boolean.valueOf(child.getValue().toString()));
                    }
                    else if(child.getKey().equals("timestamp"))
                    {
                        message.setTimestamp(child.getValue().toString());
                    }
                }
                if(!message.getBody().equals("FIND_REQUEST")) {
                    if(messagescounter>0) {
                        messages.add(message);
                        chatAdapter.notifyDataSetChanged();
                    }
                }
                messagescounter++;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }
}
