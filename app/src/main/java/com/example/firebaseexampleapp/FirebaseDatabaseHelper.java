package com.example.firebaseexampleapp;

import android.util.Log;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;


public class FirebaseDatabaseHelper {
    private DatabaseReference mReferenceEvents;
    private ArrayList<Event> eventsArrayList = new ArrayList<>();

    public FirebaseDatabaseHelper() {
        // this will reference the node called events and all its children.
        mReferenceEvents = FirebaseDatabase.getInstance().getReference("events");

        // this adds a listener on the db reference that will continually update the arraylist so it is always current
        mReferenceEvents.addValueEventListener(new ValueEventListener() {

            /**
             * In this method we will clear the arraylist of all events each time the data is changed
             * Event object from firebase so that we can add it to our arraylist of events
             *
             * This method onDataChange is an asynchonos method.  It is called when the data is changed.
             *
             * @param dataSnapshot
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                eventsArrayList.clear();
                for (DataSnapshot item: dataSnapshot.getChildren()) {
                    Event e = new Event(
                            item.child("eventName").getValue().toString(),
                            item.child("eventDate").getValue().toString(),
                            Integer.valueOf(item.child("year").getValue().toString()),
                            Integer.valueOf(item.child("month").getValue().toString()),
                            Integer.valueOf(item.child("day").getValue().toString()),
                            item.child("key").getValue().toString());
                    eventsArrayList.add(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public DatabaseReference getDatabaseReference() {
        return mReferenceEvents;
    }


    public void addEvent(Event event) {
        // This gets the unique key of where to push the element and then sets the value at
        // this key with the newEvent object we want to add to the database

        String key = mReferenceEvents.push().getKey();
        event.setKey(key);
        mReferenceEvents.child(key).setValue(event);
    }

    /**
     * Method can expect that the month is between 1-12 and the day is between 1-31.  The code does not verify that
     * the day is correct for the month (i.e. if they day 4-31 it won't tell you that is invalid).
     * All parameters represent the values to update the Event object with at the location with the unique key
     *
     * @param key
     * @param eventName
     * @param eventDate
     * @param month
     * @param day
     * @param year
     */

    public void updateEvent(String key, String eventName, String eventDate, int month, int day, int year) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("events").child(key);
        ref.child("eventName").setValue(eventName);
        ref.child("eventDate").setValue(eventDate);
        ref.child("month").setValue(month);
        ref.child("day").setValue(day);
        ref.child("year").setValue(year);
    }


    public void deleteEvent(String key) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("events").child(key);
        ref.removeValue();
    }
}




