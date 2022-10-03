package com.example.finalyearproject_drivme;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

public class TouristDayTrip extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    //initialize variables
    TextInputLayout mtilDDuration, mtilDStartDate, mtilDTime, mtilDCarPlate, mtilDState, mtilDLocality, mtilDAddress;
    TextInputEditText metDDuration, metDStartDate, metDEndDate, metDTime, metDCarPlate, metDState, metDLocality, metDAddress, metDComment;

    String dateForID, endDate, muTime, locality, address;
    Button mbtnOK, mbtnDayNext;
    TextView mtvSelect;
    NumberPicker mnpHour, mnpMin, mnpPicker;
    SharedPreferences spDrivme;
    int dayOfWeek, duration;
    ArrayList<String> cpList;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_ID = "userID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_day_trip);

        //assign variables
        mtilDDuration = findViewById(R.id.tilDDuration);
        mtilDStartDate = findViewById(R.id.tilDStartDate);
        mtilDTime = findViewById(R.id.tilDTime);
        mtilDCarPlate = findViewById(R.id.tilDCarPlate);
        mtilDState = findViewById(R.id.tilDState);
        mtilDLocality = findViewById(R.id.tilDLocality);
        mtilDAddress = findViewById(R.id.tilDAddress);
        metDDuration = findViewById(R.id.etDDuration);
        metDStartDate = findViewById(R.id.etDStartDate);
        metDEndDate = findViewById(R.id.etDEndDate);
        metDTime = findViewById(R.id.etDTime);
        metDCarPlate = findViewById(R.id.etDCarPlate);
        metDState = findViewById(R.id.etDState);
        metDLocality = findViewById(R.id.etDLocality);
        metDAddress = findViewById(R.id.etDAddress);
        metDComment = findViewById(R.id.etDComment);
        mbtnDayNext = findViewById(R.id.btnDayNext);

        //initialize shared preference
        spDrivme = getSharedPreferences(SP_NAME, MODE_PRIVATE);

        //initialize places
        Places.initialize(getApplicationContext(), "AIzaSyDmZ_vgR-tIbzE8UZK_sr4Ch1SqDFO0gRI");

        //get inputs
        durationMenu();
        getDate();
        timeMenu();
        carPlateMenu();
        stateMenu();
        getPlaceSearch();
        errorChangeOnEachFields();

        mbtnDayNext.setOnClickListener(view -> checkFields());
    }

    /*duration dialog*/
    private void durationMenu() {
        metDDuration.setOnClickListener(view -> {
            //set layout
            LayoutInflater dialogInflater = getLayoutInflater();
            view = dialogInflater.inflate(R.layout.activity_scroll_picker, null);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TouristDayTrip.this, R.style.dialog);
            AlertDialog durationDialog = dialogBuilder.setView(view).create();

            //obtaining the View with specific ID
            mtvSelect = view.findViewById(R.id.tvSelectOption);
            mnpPicker = view.findViewById(R.id.npPicker);
            mbtnOK = view.findViewById(R.id.btnOK);

            //set the values
            mtvSelect.setText("Set Trip Duration");
            mnpPicker.setMaxValue(7);
            mnpPicker.setMinValue(1);
            mnpPicker.setValue(1);

            //show pop out dialog
            durationDialog.show();
            durationDialog.getWindow().setLayout(450, 580);

            mbtnOK.setOnClickListener(view1 -> {
                duration = mnpPicker.getValue();

                if (duration == 1) {
                    metDDuration.setText(duration + " Day");
                } else {
                    metDDuration.setText(duration + " Days");
                }

                metDStartDate.setText("");
                metDEndDate.setText("");

                durationDialog.dismiss();
            });

        });
    }

    /*Getting Date*/
    private void getDate() {
        metDStartDate.setOnClickListener(view -> {
            if(Objects.requireNonNull(metDDuration.getText()).toString().isEmpty()) {
                mtilDDuration.setError("Set Duration!");
            }
            else{
                showCalendar();
            }
        });

        metDEndDate.setOnClickListener(view -> {
            if(Objects.requireNonNull(metDEndDate.getText()).toString().isEmpty()) {
                mtilDStartDate.setError("Set Date!");
            }
            else{
                Toast.makeText(TouristDayTrip.this, "Only can adjust Duration or Start Date!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCalendar() {
        DatePickerDialog dpd = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            Date currentTimeDate = sdf.parse(sdf.format(new Date()));
            Date endTimeDate = sdf.parse("22:00");
            int state = Objects.requireNonNull(currentTimeDate).compareTo(endTimeDate); // false / current time has not passed end time.
            if(state > 0){
                dpd.getDatePicker().setMinDate(System.currentTimeMillis() + (1000*60*60*24*2));
                dpd.getDatePicker().setMaxDate(System.currentTimeMillis() + (1000*60*60*24*8));
            }
            else{
                dpd.getDatePicker().setMinDate(System.currentTimeMillis() + (1000*60*60*24));
                dpd.getDatePicker().setMaxDate(System.currentTimeMillis() + (1000*60*60*24*7));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        dpd.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        GregorianCalendar gc = new GregorianCalendar(year, month, dayOfMonth - 1);

        //get day of week
        dayOfWeek = gc.get(Calendar.DAY_OF_WEEK);
        String date = String.format("%02d/%02d/" + year, dayOfMonth, (month + 1));
        dateForID = String.format("%02d%02d" + year, dayOfMonth, (month + 1));

        //calculate end date with duration
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();

        try {
            c.setTime(Objects.requireNonNull(sdf.parse(date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        c.add(Calendar.DATE, duration);

        Date resultdate = new Date(c.getTimeInMillis());
        endDate = sdf.format(resultdate);

        metDStartDate.setText(date);
        metDEndDate.setText(endDate);
    }

    /*time dialog*/
    private void timeMenu() {
        metDTime.setOnClickListener(timeView -> {
            //set layout
            LayoutInflater dialogInflater = getLayoutInflater();
            timeView = dialogInflater.inflate(R.layout.activity_time_picker, null);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.dialog);
            AlertDialog startTimeDialog = dialogBuilder.setView(timeView).create();

            //obtaining the View with specific ID
            mbtnOK = timeView.findViewById(R.id.btnOK);
            mnpHour = timeView.findViewById(R.id.npHour);
            mnpMin = timeView.findViewById(R.id.npMin);

            //set the values
            mnpHour.setMaxValue(20);
            mnpHour.setMinValue(6);
            mnpHour.setValue(6);
            mnpHour.setFormatter(i -> String.format("%02d", i));

            mnpMin.setMaxValue(59);
            mnpMin.setMinValue(0);
            mnpMin.setValue(0);
            mnpMin.setFormatter(i -> String.format("%02d", i));

            //show pop out dialog
            startTimeDialog.show();
            startTimeDialog.getWindow().setLayout(450, 500);

            mbtnOK.setOnClickListener(view1 -> {
                muTime = String.format("%02d", mnpHour.getValue()) + ":" + String.format("%02d", mnpMin.getValue());

                    metDTime.setText(muTime);

                    startTimeDialog.dismiss();
            });
        });
    }

    /*car plate dialog*/
    private void carPlateMenu() {
        cpList = new ArrayList<>();
        //get id
        String getID = spDrivme.getString(KEY_ID, null);

        //get specific collection
        FirebaseFirestore drivmeDB = FirebaseFirestore.getInstance();
        CollectionReference crDrivme = drivmeDB.collection("User Accounts").document(getID).collection("Car Details");

        crDrivme.whereNotEqualTo("carStatus", "N/A").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        cpList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            cpList.add(document.getId());
                        }
                        if(cpList.size() == 0){
                            cpList.add("No Cars");
                        }
                        metDCarPlate.setText(cpList.get(0));
                    }
                });

        metDCarPlate.setOnClickListener(cpView -> {
            //set layout
            LayoutInflater dialogInflater = getLayoutInflater();
            cpView = dialogInflater.inflate(R.layout.activity_scroll_picker, null);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.dialog);
            AlertDialog carPlateDialog = dialogBuilder.setView(cpView).create();

            //obtaining the View with specific ID
            mtvSelect = cpView.findViewById(R.id.tvSelectOption);
            mbtnOK = cpView.findViewById(R.id.btnOK);
            mnpPicker = cpView.findViewById(R.id.npPicker);
            // Convert string arraylist to string array.
            String[] carPlate = new String[cpList.size()];
            carPlate = cpList.toArray(carPlate);

            mtvSelect.setText("Select A Car");

            mnpPicker.setMinValue(0);
            mnpPicker.setMaxValue(carPlate.length - 1);
            mnpPicker.setDisplayedValues(carPlate);
            mnpPicker.setValue(0);
            mnpPicker.setWrapSelectorWheel(false);

            carPlateDialog.show();
            carPlateDialog.getWindow().setLayout(450, 580);

            mbtnOK.setOnClickListener(view1 -> {
                int value = mnpPicker.getValue();
                metDCarPlate.setText(cpList.get(value));
                carPlateDialog.dismiss();
            });
        });
    }

    /*state dialog*/
    private void stateMenu() {
        metDState.setOnClickListener(stateView -> {
            //set layout
            LayoutInflater dialogInflater = getLayoutInflater();
            stateView = dialogInflater.inflate(R.layout.activity_scroll_picker_long, null);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.dialog);
            AlertDialog stateDialog = dialogBuilder.setView(stateView).create();

            //obtaining the View with specific ID
            mtvSelect = stateView.findViewById(R.id.tvSelectOption);
            mbtnOK = stateView.findViewById(R.id.btnLongOK);
            mnpPicker = stateView.findViewById(R.id.npPicker);

            //set the values
            ModelDriverDetails.initState();
            mtvSelect.setText("Select a State");
            mnpPicker.setMaxValue(ModelDriverDetails.getDetailsArrayList().size() - 1);
            mnpPicker.setMinValue(0);
            mnpPicker.setDisplayedValues(ModelDriverDetails.detailsName());

            //show pop out dialog
            stateDialog.show();
            stateDialog.getWindow().setLayout(570, 580);

            mbtnOK.setOnClickListener(view1 -> {
                int value = mnpPicker.getValue();
                metDState.setText(ModelDriverDetails.getDetailsArrayList().get(value).getDetailsOption());
                metDLocality.setText("");
                metDAddress.setText("");

                stateDialog.dismiss();
            });
        });
    }

    /*Getting Locality Name and Address*/
    private void getPlaceSearch(){
        metDLocality.setOnClickListener(view -> {
            //initialize place field list
            List<Place.Field> placeList = Arrays.asList(Place.Field.ADDRESS, Place.Field.NAME);

            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, placeList).setCountry("MY").build(TouristDayTrip.this);
            startActivityForResult(intent, 100);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(Objects.requireNonNull(data));
                locality = place.getName();
                address = place.getAddress();
                metDLocality.setText(locality);
                metDAddress.setText(address);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*check fields*/
    private void checkFields() {
        if (Objects.requireNonNull(metDDuration.getText()).toString().isEmpty()) {
            mtilDDuration.setError("Field cannot be empty!");
        }
        else if (Objects.requireNonNull(metDStartDate.getText()).toString().isEmpty()) {
            mtilDStartDate.setError("Field empty!");
        }
        else if (Objects.requireNonNull(metDTime.getText()).toString().isEmpty()) {
            mtilDTime.setError("Field cannot be empty!");
        }
        else if (Objects.requireNonNull(metDCarPlate.getText()).toString().equals("No Cars")) {
            mtilDCarPlate.setError("No Cars Available!");
        }
        else if (Objects.requireNonNull(metDState.getText()).toString().isEmpty()) {
            mtilDState.setError("Field cannot be empty!");
        }
        else if (Objects.requireNonNull(metDLocality.getText()).toString().isEmpty()) {
            mtilDLocality.setError("Field cannot be empty!");
        }
        else if (Objects.requireNonNull(metDAddress.getText()).toString().isEmpty()) {
            mtilDAddress.setError("Field cannot be empty!");
        }
        else {
            //get id and order id
            String getID = spDrivme.getString(KEY_ID, null);
            String carPlate = Objects.requireNonNull(metDCarPlate.getText()).toString();
            String getOrderID = dateForID + carPlate;

            //booking details -> driver list
            Intent intent = new Intent(TouristDayTrip.this, TouristDriverList.class);
            intent.putExtra("orderID", getOrderID);
            intent.putExtra("touristID", getID);
            intent.putExtra("dateID", dateForID);
            intent.putExtra("duration", duration);
            intent.putExtra("startDate", metDStartDate.getText().toString());
            intent.putExtra("endDate", Objects.requireNonNull(metDEndDate.getText()).toString());
            intent.putExtra("time", metDTime.getText().toString());
            intent.putExtra("carPlate", carPlate);
            intent.putExtra("state", metDState.getText().toString());
            intent.putExtra("locality", metDLocality.getText().toString());
            intent.putExtra("address", metDAddress.getText().toString());

            if(Objects.requireNonNull(metDComment.getText()).toString().isEmpty()){
                intent.putExtra("comment", "No comment.");
            }
            else{
                intent.putExtra("comment", Objects.requireNonNull(metDComment.getText()).toString());
            }

            startActivity(intent);
        }
    }

    //Set error message on each field to ensure correct input
    private void errorChangeOnEachFields() {
        metDDuration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilDDuration.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metDStartDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilDStartDate.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metDTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilDTime.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metDCarPlate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilDCarPlate.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metDState.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilDState.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metDLocality.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilDLocality.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metDAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilDAddress.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });
    }

    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(TouristDayTrip.this);
        alertDialogBuilder.setTitle("Discard Process");
        alertDialogBuilder
                .setMessage("Do you wish to discard and go back homepage?")
                .setCancelable(false)
                .setPositiveButton("DISCARD",
                        (dialog, id) -> {
                            startActivity(new Intent(TouristDayTrip.this, TouristNavHomepage.class));
                            finishAffinity();
                            finish();
                        });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}