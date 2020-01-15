package com.example.firebaseexampleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public final String TAG = "MainActivity";
    private String dateSelected = "No date chosen";
    private int dateMonth;
    private int dateDay;
    private int dateYear;
    public static FirebaseDatabaseHelper dbHelper; // change to private when it works.

    static {
        dbHelper = new FirebaseDatabaseHelper();
    }

    /**
     * Quicksorts the portion of an array with indicies in the range [begin, end) in ascending order
     * @param a The array to sort
     * @param begin The first index of the array to sort.
     * @param end The last index of the array to sort, plus 1.
     */
    public static void quickSortA(ArrayList<Event> a, int begin, int end) {
        if (end - begin <= 1)
            return;
        String pivot = a.get(end - 1).toString();
        int i = begin;
        for (int x = begin; x < end - 1; x++)
            if (a.get(x).toString().compareTo(pivot) < 0) {
                Event temp = a.get(x);
                a.set(x, a.get(i));
                a.set(i, temp);
                i++;
            }
        Event temp = a.get(end - 1);
        a.set(end - 1, a.get(i));
        a.set(i, temp);
        quickSortA(a, begin, i);
        quickSortA(a, i + 1, end);
    }

    /**
     * Quicksorts the portion of an array with indicies in the range [begin, end) in descending order
     * @param a The array to sort
     * @param begin The first index of the array to sort.
     * @param end The last index of the array to sort, plus 1.
     */
    public static void quickSortD(ArrayList<Event> a, int begin, int end) {
        if (end - begin <= 1)
            return;
        String pivot = a.get(end - 1).toString();
        int i = begin;
        for (int x = begin; x < end - 1; x++)
            if (a.get(x).toString().compareTo(pivot) > 0) {
                Event temp = a.get(x);
                a.set(x, a.get(i));
                a.set(i, temp);
                i++;
            }
        Event temp = a.get(end - 1);
        a.set(end - 1, a.get(i));
        a.set(i, temp);
        quickSortD(a, begin, i);
        quickSortD(a, i + 1, end);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //  Video to learn basic access to CalendarView Data
        //  https://www.youtube.com/watch?v=WNBE_3ZizaA

        CalendarView calendarView = findViewById(R.id.eventCalendarDate);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
                                                 @Override
                                                 public void onSelectedDayChange(CalendarView calendarView, int year, int month, int day) {
                                                     dateSelected =  (month + 1) + "/" + day + "/" + year;
                                                     dateYear = year;
                                                     dateMonth = month + 1;
                                                     dateDay = day;
                                                     Log.i(TAG, "" + dateSelected);
                                                     closeKeyboard();
                                                 }
                                             }
        );

    }

    public void addEventButtonPressed(View v) {
        EditText eventNameET = (EditText) findViewById(R.id.eventName);
        String eventName = eventNameET.getText().toString();

        // verify there is a name and date
        if (eventName.length() == 0 ) {
            Toast.makeText(MainActivity.this, "Please enter name", Toast.LENGTH_SHORT).show();
        }
        else if (dateSelected.equals("No date chosen")) {
            Toast.makeText(MainActivity.this, "Please select Date", Toast.LENGTH_SHORT).show();
        }
        else {
           Log.i(TAG, "Trying to add: " + eventName + ", " + dateSelected);
           Event newEvent = new Event(eventName, dateSelected, dateYear, dateMonth, dateDay);
           eventNameET.setText(""); // clears out text
            dbHelper.addEvent(newEvent);
        }
    }

    /**
     * This method will be called to minimize the on screen keyboard in the Activity
     * When we get the current view, it is the view that has focus, which is the keyboard
     * Credit - Found by Ram Dixit, 2019
     *
     * Source:  https://www.youtube.com/watch?v=CW5Xekqfx3I
     */
    private void closeKeyboard() {
        View view = this.getCurrentFocus();     // view will refer to the keyboard
        if (view != null ){                     // if there is a view that has focus
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public FirebaseDatabaseHelper getDbHelper() {
        return dbHelper;
    }

    public void setDbHelper(FirebaseDatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void onRetrieve(View v){
        dbHelper.getDatabaseReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Event> currentEvents = new ArrayList<Event>();

                for (DataSnapshot item : dataSnapshot.getChildren())
                {
                    Event e = new Event(
                            item.child("eventName").getValue().toString(),
                            item.child("eventDate").getValue().toString(),
                            Integer.valueOf(item.child("year").getValue().toString()),
                            Integer.valueOf(item.child("month").getValue().toString()),
                            Integer.valueOf(item.child("day").getValue().toString()),
                            item.child("key").getValue().toString());
                    currentEvents.add(e);
                }

                // starts intent that will display this new data that has been saved into the arraylist
                // since we used a single value event the data will not continually update

                Intent intent = new Intent(MainActivity.this, DisplayEventsActivity.class);
                intent.putExtra("events", currentEvents);
                startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("callError", "There has been an Error with database retrieval");
            }
        });


    }
}
