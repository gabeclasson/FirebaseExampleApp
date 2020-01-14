package com.example.firebaseexampleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
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

    private static EditEventActivity currentActivity;

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


    public void deleteEventData(View v) {
        dbHelper.deleteEvent(keyToUpdate);
        onHome(v);              // reloads opening screen
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
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
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
