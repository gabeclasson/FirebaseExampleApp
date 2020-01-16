package com.example.firebaseexampleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.RadioGroup;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.check(EditEventActivity.sortingOption == EditEventActivity.EARLIEST_TO_LATEST ? R.id.radioEarlyToLate : R.id.radioLateToEarly);
    }

    public void updateSortingMethod(View v){
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        int previousOption = EditEventActivity.sortingOption;
        int radId = radioGroup.getCheckedRadioButtonId();
        if (radId == R.id.radioEarlyToLate)
            EditEventActivity.sortingOption =  EditEventActivity.EARLIEST_TO_LATEST;
        else
            EditEventActivity.sortingOption =  EditEventActivity.LATEST_TO_EARLIEST;
        if (previousOption == EditEventActivity.sortingOption)
            return;
    }
}
