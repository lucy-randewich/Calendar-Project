package com.lucy.calendarproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements AsyncTaskListener{
    //todo check that comments are all good
    //todo clear up blank space and unused imports etc.
    //todo sort out php, to remove unused functions
    //todo sort out variable names i.e. content view ones start with btn. and stuff
    //todo make access private where possible

    ArrayList<String> usersGroupsIDs = new ArrayList<String>();
    ArrayList<String> groupNames = new ArrayList<String>();
    ArrayList<String> groupNamesAndOwners = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get username from intent of activity that called MainActivity
        final String username = getIntent().getStringExtra("USERNAME");

        background bg = new background (MainActivity.this);
        bg.execute("username","blank",username,"blank","findGroupsOfUser");        // Blank is used as a placeholder since this SQL only needs one variable


        // Change to createGroup page when button clicked
        Button createGroup = (Button) findViewById(R.id.createGroup);
        createGroup.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent Intent = new Intent(MainActivity.this, CreateGroup.class);
                Intent.putExtra("USERNAME", username);
                startActivity(Intent);
            }
        });

        // Change to ViewCalendar page when button clicked
        Button viewCalendar = (Button) findViewById(R.id.myCalendar);
        viewCalendar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent Intent = new Intent(MainActivity.this, ViewCalendar.class);
                Intent.putExtra("USERNAME", username);
                startActivity(Intent);
            }
        });

    }

    // This method gets called after background.java finishes
    @Override
    public void updateResult(String result){
        //System.out.println("I'm in the updateResult method!!!!!");
        System.out.println("The result is: "+result);

        if(result.contains("fgou")) {      // if retrieving result from bg
            result = result.substring(4, result.length() - 1);      // Remove slash from end of string (perhaps redundant but oh well)
            usersGroupsIDs = new ArrayList<String>(Arrays.asList(result.split("\\s*,\\s*")));
            String groupIDsString = usersGroupsIDs.toString();
            groupIDsString = groupIDsString.substring(1, groupIDsString.length() - 1);
            groupIDsString = groupIDsString.replaceAll("\\s+","");
            System.out.println("The String of groupIDs is "+groupIDsString);

            groupNamesAndOwners = new ArrayList<String>(Arrays.asList(groupIDsString.split("\\s*,\\s*")));
            try {
                for (int i = 0; i <= groupNamesAndOwners.size() - 1; i++) {
                    String groupNameAndOwner = groupNamesAndOwners.get(i);
                    String groupName = groupNameAndOwner.substring(0, groupNameAndOwner.indexOf("-"));
                    groupNames.add(groupName);
                }
                displayGroups(groupNames, groupIDsString);
            }catch(Exception e){
                // User doesn't have any groups yet
                Toast.makeText(MainActivity.this, "You don't have any groups yet- click on CREATE GROUP to get started.", Toast.LENGTH_LONG).show();
            }


        }else {         // if retrieving result from bg2
            //retrievedGroupNames = new ArrayList<String>(Arrays.asList(result.split("\\s*,\\s*")));
            //displayGroups(retrievedGroupNames, result);
        }
    }

    public void displayGroups(ArrayList<String> retrievedGroupNames, String result) {

        // Get id of radio group
        final RadioGroup rg = (RadioGroup) findViewById(R.id.myRadioGroup);

        LinearLayout linearLayout = findViewById(R.id.rootContainer);


        // Populate radio group with groups by looping over ArrayList retrievedGroupNames
        for(int i=0;i<retrievedGroupNames.size();i++){

            // Get name of group to add
            String groupName = retrievedGroupNames.get(i);

            // Initialize a new RadioButton, set padding, text, and image
            RadioButton radioButton = new RadioButton(getApplicationContext());
            radioButton.setPadding(28,0,0,0);
            radioButton.setTextAppearance(MainActivity.this, android.R.style.TextAppearance_DeviceDefault_Medium);
            radioButton.setText(groupName);
            radioButton.setButtonDrawable(R.drawable.radio_button_selector);

            System.out.println("Adding a radio button that says "+groupName);

            // Add radio button to group
            rg.addView(radioButton);
        }

        // Change to viewGroup page when button clicked
        Button viewGroup = (Button) findViewById(R.id.viewGroup);
        viewGroup.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int radioButtonId = rg.getCheckedRadioButtonId();
                try {
                    String groupName = groupNames.get(radioButtonId-1);
                        Intent Intent = new Intent(MainActivity.this, ViewGroup.class);
                        Intent.putExtra("GROUP_NAME", groupName);
                        String selectedGroupNameAndOwner = groupNamesAndOwners.get(radioButtonId-1);
                        Intent.putExtra("GROUP_NAME_AND_OWNER", selectedGroupNameAndOwner);
                        startActivity(Intent);
                } catch (Exception e){
                    Toast.makeText(MainActivity.this, "No group selected", Toast.LENGTH_SHORT).show();
                    System.out.println("the error is " +e);
                }
            }
        });
    }
}