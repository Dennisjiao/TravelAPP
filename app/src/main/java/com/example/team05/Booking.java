/**
 * This is the booking page the user will be sent to when they click on any of the book buttons
 *
 * Functionalities include three spinners to select castle, quantity of tickets and date.
 *
 * There is also a search button that takes the user to the BookOutbound page to select their
 * bus time options
 *
 * The castle spinner will autofill will a selection if the user has accessed the page via
 * the information page of a specific castle.
 *
 * TO DO:
 * - DENNIS TO COMPLETE UI
 *
 * Changelog:
 * - OLI fixed bug where date did not correctly identify day of week
 * - OLI updated date display for current date
 * - OLI error handling for if date is in past.
 * - Ruipeng (Dennis) completes Navmore button function the page jump
 *
 * created by Harry Akitt 16/03/2022
 *
 * Refernces:
 * - https://www.javatpoint.com/android-alert-dialog-example
 * **/

package com.example.team05;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Booking extends AppCompatActivity {
    private TextView displayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    Calendar cal = Calendar.getInstance();
    private final String TAG = "DebuggingDayOfWeek";
    //package All information to Confirmation Page



    //Alert dialog for if date is before current time
    AlertDialog.Builder builder;
    Button closeButton;



    //making variable dayOfWeek to be passed to outbound bookings
    String selectedDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking);

        //Setting day to current day
        displayDate = (TextView) findViewById(R.id.date_input);
        resetDate();

        //Setting up error message if day before current time
        builder = new AlertDialog.Builder(this);

        int currentTime =Integer.parseInt(String.valueOf(cal.get(Calendar.HOUR_OF_DAY))+String.valueOf(cal.get(Calendar.MINUTE)));
        String currentDate = String.valueOf(cal.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(cal.get(Calendar.MONTH)+1) + "/"
                + String.valueOf(cal.get(Calendar.YEAR));

        // set action bar
        ActionBar bar = getSupportActionBar();
        if(bar!=null){
            TextView tv = new TextView(getApplicationContext());
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, // Width of TextView
                    RelativeLayout.LayoutParams.WRAP_CONTENT); // Height of TextView
            tv.setLayoutParams(lp);
            tv.setText(bar.getTitle());
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
            bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            bar.setCustomView(tv);
        }

        // search button
        // passes the chosen castle and day of week from the spinners to be searched in the
        // database
        Button searchBtn = (Button) findViewById(R.id.search_button);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bd = new Bundle();
                Intent confirmIntent = new Intent(Booking.this,ConfirmationPage.class);
                Intent intent = new Intent(Booking.this, BookOutbound.class);
                Spinner spinner = (Spinner)findViewById(R.id.castleList);
                String text = spinner.getSelectedItem().toString();

                intent.putExtra("Castle", text);
                bd.putCharSequence("CastleName",text);
                confirmIntent.putExtras(bd);
                startActivity(confirmIntent);

                intent.putExtra("DayName",selectedDay);

                intent.putExtra("CurrentTime",currentTime);

                intent.putExtra("currentDate", currentDate);

                intent.putExtra("selectedDate", displayDate.getText());

                startActivity(intent);
            }
        });

        //create spinner with ticket quantities
        //value must be between 1 and 5
        Spinner numberSpinner = (Spinner) findViewById(R.id.quantity);
        ArrayList<Integer> numbers = new ArrayList<>();
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);
        numbers.add(4);
        numbers.add(5);
        ArrayAdapter<Integer> numAdapter = new ArrayAdapter<>(Booking.this,
                android.R.layout.simple_list_item_1, numbers);
        numberSpinner.setAdapter(numAdapter);


        //create spinner with list of castles
        //default will be Alnwick, unless accessed through a specific castle page
        Spinner mySpinner = (Spinner) findViewById(R.id.castleList);
        ArrayList<String> CastleName = new ArrayList<>();
        CastleName.add("Alnwick Castle");
        CastleName.add("Auckland Castle");
        CastleName.add("Bamburgh Castle");
        CastleName.add("Barnard Castle");

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(Booking.this,
                android.R.layout.simple_list_item_1, CastleName);
        mySpinner.setAdapter(myAdapter);

        // get intent from specific castle page to autofill chosen castle
        Intent incomingIntent = getIntent();
        String castleName = incomingIntent.getStringExtra("castle");
        int spinnerPosition = myAdapter.getPosition(castleName);
        mySpinner.setSelection(spinnerPosition);

        // set spinner to chose date
        displayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(Booking.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int day, int month, int year) {
                month = month + 1;
                String date = year + "/" + month + "/" + day;
                String selectedDate = String.valueOf(day) + String.valueOf(month) + String.valueOf(year);

                Log.d(TAG, String.valueOf(day));
                //Corrects problem with finding date
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
                Calendar calendar = new GregorianCalendar(datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth(),0,0,0);
                selectedDay = sdf.format(calendar.getTime());

                calendar.add(Calendar.DATE,+1);
                if(calendar.before(cal)){
                    Log.d(TAG,"Before");
                    displayError();
                }else{
                    Log.d(TAG, "After");
                }
                displayDate.setText(date);
            }
        };


        // set bottom nav bar
        BottomNavigationView bottomNavBar = (BottomNavigationView) findViewById(R.id.bottomNav);
        bottomNavBar.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.mainActivityNav:
                        Intent intent3 = new Intent(Booking.this, MainActivity.class);
                        startActivity(intent3);
                        break;

                    case R.id.bookingNav:
                        Intent intent = new Intent(Booking.this, Booking.class);
                        startActivity(intent);
                        break;

                    case R.id.moreNav:
                        Intent intent2 = new Intent(Booking.this, More.class);
                        startActivity(intent2);
                        break;

                }
                return false;
            }
        });


    }

    public void resetDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        selectedDay = String.valueOf(sdf.format(cal.getTime()));

        displayDate.setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH))+"/"+String.valueOf(cal.get(Calendar.MONTH)+1)+"/"+String.valueOf(cal.get(Calendar.YEAR)));


    }

    public void displayError(){
        builder.setMessage("Bookings cannot be placed in the past.")
                .setCancelable(false)
                .setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        resetDate();
                        dialog.cancel();
                        Toast.makeText(getApplicationContext(),"Date reset to today.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Error - Date is in the past.");
        alert.show();
        Log.d(TAG,"This has run ");

    }


}