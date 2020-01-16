package com.example.firebaseexampleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class EditEventActivity extends AppCompatActivity {

    private FirebaseDatabaseHelper dbHelper;
    private EditText eventNameET;
    private EditText eventDateET;
    private String keyToUpdate;
    private int year;
    private int month;
    private int day;

    public static EditEventActivity currentActivity;
    public static final int LATEST_TO_EARLIEST = 1;
    public static final int EARLIEST_TO_LATEST = 2;
    public static int sortingOption = LATEST_TO_EARLIEST;

    public static void sort(ArrayList<Event> a){
        if (sortingOption == LATEST_TO_EARLIEST)
            MainActivity.quickSortD(a, 0, a.size());
        else if (sortingOption == EARLIEST_TO_LATEST)
            MainActivity.quickSortA(a, 0, a.size());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        dbHelper = new FirebaseDatabaseHelper();
        Intent intent = getIntent();
        Event event = intent.getParcelableExtra("event");

        String eventNameToUpdate = event.getEventName();
        String eventDateToUpdate = event.getEventDate();
        keyToUpdate = event.getKey();

        eventNameET = (EditText)findViewById(R.id.eventName);
        eventDateET = (EditText)findViewById(R.id.eventDate);

        eventNameET.setText(eventNameToUpdate);
        eventDateET.setText(eventDateToUpdate);

        year =  event.getYear();
        month = event.getMonth();
        day = event.getDay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentActivity = this;
    }

    public void updateEventData(View v) {
        String newName = eventNameET.getText().toString();
        String newDate = eventDateET.getText().toString();

        // error checking to ensure date is of the form 01/17/1979 etc.
        if (newName.length() == 0)
            Toast.makeText(EditEventActivity.this, "Please enter a name for the event", Toast.LENGTH_SHORT).show();
        else
        {
            // prevents the app from crashing if something goes wrong
            try{
                dbHelper.updateEvent(keyToUpdate, newName, newDate, month, day, year);
                Toast.makeText(EditEventActivity.this, "Updated event.", Toast.LENGTH_SHORT).show();
            }
            catch (Exception e) {
                Toast.makeText(EditEventActivity.this, "Please enter date as MM/DD/YYYY", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // adapted from https://developer.android.com/guide/topics/ui/dialogs#java
    public void deleteEventData(View v) {
        final View vFinal = v;
        final Activity thisActivity = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you sure?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked delete button
                dbHelper.deleteEvent(keyToUpdate);
                onHome(vFinal);
                Toast.makeText(thisActivity, "Event deleted.", Toast.LENGTH_LONG).show();
                thisActivity.finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        builder.create().show();
    }

    public void onHome(View v){
        Intent intent = new Intent(EditEventActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void onRetrieve(View v){
        Intent intent = new Intent(EditEventActivity.this, DisplayEventsActivity.class);
        intent.putExtra("events", dbHelper.getEventsArrayList());
        startActivity(intent);
    }

    /**
     * Adapted from https://developer.android.com/guide/topics/ui/controls/pickers
     * Class to pick a date
     */
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, currentActivity.getYear(), currentActivity.getMonth(), currentActivity.getDay());
        }

        public void onDateSet(DatePicker view, int year, int month, int date){
            currentActivity.setYear(year);
            currentActivity.setMonth(month + 1);
            currentActivity.setDay(date);
            currentActivity.updateDisplayDate();
        }
    }

    public void updateDisplayDate(){
        eventDateET.setText(month + "/" + day + "/" + year);
    }

    public void pickTime(View v){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }



    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
