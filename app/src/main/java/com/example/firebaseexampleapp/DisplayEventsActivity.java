package com.example.firebaseexampleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


// a useful link
// https://www.techotopia.com/index.php/A_Firebase_Realtime_Database_List_Data_Tutorial

public class DisplayEventsActivity extends AppCompatActivity {

    private ArrayList<Event> myEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_events);

        // This Activity received an arraylist of all the events pulled from firebase to populate the ListView with
        Intent intent = getIntent();
        myEvents = intent.getParcelableArrayListExtra("events");

        // Get a reference to the ListView element to display all Events in firebase
        ListView allEventsListView = (ListView) findViewById(R.id.eventList);
        // CustomAdapter is an inner class defined below that details how to adapt this arraylist of data
        CustomAdapter customAdapter = new CustomAdapter();
        allEventsListView.setAdapter(customAdapter);

    }



    public void backToHome(View v) {
        Intent intent = new Intent(DisplayEventsActivity.this, MainActivity.class);
        startActivity(intent);
    }

    /**Referred to this video regarding CustomAdapter and creating the custom class:
     * https://www.youtube.com/watch?v=FKUlw7mFXRM -->
     **/

    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return myEvents.size();       // amount of elements in database
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        /**
         *Documentation:
         * https://developer.android.com/reference/android/widget/Adapter.html#getView(int,%20android.view.View,%20android.view.ViewGroup)
         *
         *
         * Get a View that displays the data at the specified position in the data set.
         * You can either create a View manually or inflate it from an XML layout file.
         * When the View is inflated, the parent View (GridView, ListView...) will apply
         * default layout parameters unless you use LayoutInflater.inflate(int, android.view.ViewGroup,
         * boolean) to specify a root view and to prevent attachment to the root.
         *
         * @param i         The element in the array you are displaying
         * @param view      The old view to reuse if possible.
         * @param viewGroup The parent group that this view will eventually be attached to
         * @return          The correct view laid out according to the xml file with the
         *                  data from the current entry i
         */
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            // attaches the custom xml design to this View you are creating
            view = getLayoutInflater().inflate(R.layout.row_data, null);

            // creates references to each element in the custom xml design.  That is why you
            // need to say view.findViewById because you have to reference the element that
            // was gotten from the LayoutInflater above

            ImageView eventImageView = (ImageView)view.findViewById(R.id.eventImageView);
            TextView eventNameTV = (TextView) view.findViewById(R.id.eventNameTV);
            TextView eventDateTV = (TextView) view.findViewById(R.id.eventDateTV);

            // Here I am getting the specific element in the database we are currently displaying
            Event e = myEvents.get(i);

            // Set the correct image, event name, and event date for the Event object we are
            // displaying in the list
            eventImageView.setImageResource(getMonth(e));
            eventNameTV.setText(e.getEventName());
            eventDateTV.setText(e.getEventDate());

            // return this view element with the correct data inserted
            return view;
        }

        /**
         * Helper method used in custom listview class to determine which
         * season photo to use with the date.  The photo chosen based on
         * the month of the year.
         *
         * @param e - the Event object we need to find an image for
         * @return the int imageResourceId telling the custom adapter
         *         which image to use
         */
        private int getMonth(Event e) {
            int month = e.getMonth();
            if (month == 12 || month == 1 || month == 2)
                return R.drawable.winter;
            else if (month >= 3 && month <= 5)
                return R.drawable.spring;
            else if (month >=6 && month <=9)
                return R.drawable.summer;
            else
                return R.drawable.fall;
        }
    }

}
