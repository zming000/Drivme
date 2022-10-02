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
import android.widget.AutoCompleteTextView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TouristHourTrip extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    //initialize variables
    TextInputLayout mtilHDate, mtilHStartTime, mtilHEndTime, mtilHCarPlate, mtilHState, mtilHLocality, mtilHAddress;
    TextInputEditText metHDate, metHStartTime, metHEndTime, metHLocality, metHAddress, metHComment;
    AutoCompleteTextView mtvHCarPlate, mtvHState;
    String dateForID, locality, address;
    Button mbtnOK, mbtnHourNext;
    TextView mtvSelect;
    NumberPicker mnpHour, mnpMin, mnpPicker;
    SharedPreferences spDrivme;
    int dayOfWeek, valueHourStart, valueMinStart, valueHourEnd, valueMinEnd, duration;
    ArrayList<String> cpList;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_ID = "userID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_hour_trip);

        //assign variables
        mtilHDate = findViewById(R.id.tilHDate);
        mtilHStartTime = findViewById(R.id.tilHStartTime);
        mtilHEndTime = findViewById(R.id.tilHEndTime);
        mtilHCarPlate = findViewById(R.id.tilHCarPlate);
        mtilHState = findViewById(R.id.tilHState);
        mtilHLocality = findViewById(R.id.tilHLocality);
        mtilHAddress = findViewById(R.id.tilHAddress);
        metHDate = findViewById(R.id.etHDate);
        metHStartTime = findViewById(R.id.etHStartTime);
        metHEndTime = findViewById(R.id.etHEndTime);
        mtvHCarPlate = findViewById(R.id.tvHCarPlate);
        mtvHState = findViewById(R.id.tvHState);
        metHLocality = findViewById(R.id.etHLocality);
        metHAddress = findViewById(R.id.etHAddress);
        metHComment = findViewById(R.id.etHComment);
        mbtnHourNext = findViewById(R.id.btnHourNext);

        //initialize shared preference
        spDrivme = getSharedPreferences(SP_NAME, MODE_PRIVATE);

        //initialize places
        Places.initialize(getApplicationContext(), "AIzaSyDmZ_vgR-tIbzE8UZK_sr4Ch1SqDFO0gRI");

        //get inputs
        getDate();
        startTimeMenu();
        endTimeMenu();
        carPlateMenu();
        stateMenu();
        getPlaceSearch();
        errorChangeOnEachFields();

        mbtnHourNext.setOnClickListener(view -> checkFields());
    }

    /*Getting Date*/
    private void getDate() {
        metHDate.setOnClickListener(view -> showCalendar());
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
                dpd.getDatePicker().setMaxDate(System.currentTimeMillis() + (1000*60*60*24*2));
            }
            else{
                dpd.getDatePicker().setMinDate(System.currentTimeMillis() + (1000*60*60*24));
                dpd.getDatePicker().setMaxDate(System.currentTimeMillis() + (1000*60*60*24));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        dpd.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        GregorianCalendar gc = new GregorianCalendar(year, month, dayOfMonth - 1);

        dayOfWeek = gc.get(Calendar.DAY_OF_WEEK);
        String date = dayOfMonth + "/" + (month + 1) + "/" + year;
        dateForID = dayOfMonth + "" + (month + 1) + "" + year;

        metHDate.setText(date);
    }

    /*start time dialog*/
    private void startTimeMenu() {
        metHStartTime.setOnClickListener(timeView -> {
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
            mnpHour.setMaxValue(23);
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
                valueHourStart = mnpHour.getValue();
                valueMinStart = mnpMin.getValue();

                if(valueHourStart == 23 && valueMinStart > 0){
                    Toast.makeText(TouristHourTrip.this, "Latest Start Time is 23:00!", Toast.LENGTH_SHORT).show();
                }
                else{
                    metHStartTime.setText(String.format("%02d", valueHourStart) + ":" + String.format("%02d", valueMinStart));
                    metHEndTime.setText("");
                    startTimeDialog.dismiss();
                }
            });
        });
    }

    /*end time dialog*/
    private void endTimeMenu() {
        metHEndTime.setOnClickListener(timeView -> {
            String getTime = Objects.requireNonNull(metHStartTime.getText()).toString();
            if(!getTime.equals("")){
                //set layout
                LayoutInflater dialogInflater = getLayoutInflater();
                timeView = dialogInflater.inflate(R.layout.activity_time_picker, null);

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.dialog);
                AlertDialog timeDialog = dialogBuilder.setView(timeView).create();

                //obtaining the View with specific ID
                mbtnOK = timeView.findViewById(R.id.btnOK);
                mnpHour = timeView.findViewById(R.id.npHour);
                mnpMin = timeView.findViewById(R.id.npMin);

                //set the values
                if (valueMinStart > 0) {
                    mnpHour.setMaxValue(23);
                } else {
                    mnpHour.setMaxValue(24);
                }
                mnpHour.setMinValue(valueHourStart + 1);
                mnpHour.setValue(valueHourStart + 1);
                mnpHour.setFormatter(i -> String.format("%02d", i));

                mnpMin.setMaxValue(valueMinStart);
                mnpMin.setMinValue(valueMinStart);
                mnpMin.setValue(valueMinStart);
                mnpMin.setFormatter(i -> String.format("%02d", i));

                //show pop out dialog
                timeDialog.show();
                timeDialog.getWindow().setLayout(450, 500);

                mbtnOK.setOnClickListener(view1 -> {
                    valueHourEnd = mnpHour.getValue();
                    valueMinEnd = mnpMin.getValue();

                    if (valueHourEnd == valueHourStart) {
                        Toast.makeText(TouristHourTrip.this, "At least 1 hour duration!", Toast.LENGTH_SHORT).show();
                    } else if (valueHourEnd == 24 && valueMinEnd > 0) {
                        Toast.makeText(TouristHourTrip.this, "Latest End Time is 24:00!", Toast.LENGTH_SHORT).show();
                    } else {
                        metHEndTime.setText(String.format("%02d", valueHourEnd) + ":" + String.format("%02d", valueMinEnd));
                        duration = valueHourEnd - valueHourStart;
                        if(duration == 1){
                            Toast.makeText(TouristHourTrip.this, "Duration: " + duration + " Hour", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(TouristHourTrip.this, "Duration: " + duration + " Hours", Toast.LENGTH_SHORT).show();
                        }
                        timeDialog.dismiss();
                    }
                });
            }
            else {
                mtilHStartTime.setError("Set Time!");
            }
        });
    }

    /*car plate dialog*/
    private void carPlateMenu() {
        cpList = new ArrayList<>();
        //get id
        String getID = "sad02167";
        //spDrivme.getString(KEY_ID, null);

        //get specific collection
        FirebaseFirestore drivmeDB = FirebaseFirestore.getInstance();
        CollectionReference crDrivme = drivmeDB.collection("User Accounts").document(getID).collection("Car Details");

        crDrivme.whereNotEqualTo("Car Status", "N/A").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        cpList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            cpList.add(document.getId());
                        }
                        if(cpList.size() == 0){
                            cpList.add("No Cars");
                        }
                        mtvHCarPlate.setText(cpList.get(0));
                    }
                });

        mtvHCarPlate.setOnClickListener(cpView -> {
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
                mtvHCarPlate.setText(cpList.get(value));
                carPlateDialog.dismiss();
            });
        });
    }

    /*state dialog*/
    private void stateMenu() {
        mtvHState.setOnClickListener(stateView -> {
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
                mtvHState.setText(ModelDriverDetails.getDetailsArrayList().get(value).getDetailsOption());

                stateDialog.dismiss();
            });
        });
    }

    /*Getting Locality Name and Address*/
    private void getPlaceSearch(){
        metHLocality.setOnClickListener(view -> {
            //initialize place field list
            List<Place.Field> placeList = Arrays.asList(Place.Field.ADDRESS, Place.Field.NAME);

            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, placeList).build(TouristHourTrip.this);
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
                metHLocality.setText(locality);
                metHAddress.setText(address);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*check fields*/
    private void checkFields() {

        if (Objects.requireNonNull(metHDate.getText()).toString().isEmpty()) {
            mtilHDate.setError("Field cannot be empty!");
        }
        else if (Objects.requireNonNull(metHStartTime.getText()).toString().isEmpty()) {
            mtilHStartTime.setError("Field empty!");
        }
        else if (Objects.requireNonNull(metHEndTime.getText()).toString().isEmpty()) {
            mtilHEndTime.setError("Field empty!");
        }
        else if (mtvHCarPlate.getText().toString().equals("No Cars")) {
            mtilHCarPlate.setError("No Cars Available!");
        }
        else if (mtvHState.getText().toString().isEmpty()) {
            mtilHState.setError("Field cannot be empty!");
        }
        else if (Objects.requireNonNull(metHLocality.getText()).toString().isEmpty()) {
            mtilHLocality.setError("Field cannot be empty!");
        }
        else if (Objects.requireNonNull(metHAddress.getText()).toString().isEmpty()) {
            mtilHAddress.setError("Field cannot be empty!");
        }
        else {
            //get id
            String getID = "sad02167";
            //spDrivme.getString(KEY_ID, null);

            //insert database
            FirebaseFirestore orderDB = FirebaseFirestore.getInstance();
            String carPlate = Objects.requireNonNull(mtvHCarPlate.getText()).toString();
            String getOrderID = dateForID + carPlate;

            Map<String,Object> orderDetails = new HashMap<>();
            orderDetails.put("Tourist ID", getID);
            orderDetails.put("Date", Objects.requireNonNull(metHDate.getText()).toString());
            orderDetails.put("Start Time", Objects.requireNonNull(metHStartTime.getText()).toString());
            orderDetails.put("End Time", Objects.requireNonNull(metHEndTime.getText()).toString());
            orderDetails.put("Car Plate", Objects.requireNonNull(mtvHCarPlate.getText()).toString());
            orderDetails.put("State", Objects.requireNonNull(mtvHState.getText()).toString());
            orderDetails.put("Locality Name", Objects.requireNonNull(metHLocality.getText()).toString());
            orderDetails.put("Address", Objects.requireNonNull(metHAddress.getText()).toString());
            if(Objects.requireNonNull(metHComment.getText()).toString().isEmpty()){
                orderDetails.put("Comment", "No comment.");
            }
            else{
                orderDetails.put("Comment", Objects.requireNonNull(metHComment.getText()).toString());
            }

            orderDB.collection("Order Details").document(getOrderID)
                    .set(orderDetails)
                    .addOnSuccessListener(unused -> {
                        Intent intent = new Intent(TouristHourTrip.this, TouristDriverList.class);
                        intent.putExtra("orderID", getOrderID);
                        intent.putExtra("touristID", getID);

                        startActivity(intent);
                        finish();
                    });
        }
    }

    //Set error message on each field to ensure correct input
    private void errorChangeOnEachFields() {
        metHDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilHDate.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metHStartTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilHStartTime.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metHEndTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilHEndTime.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        mtvHCarPlate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilHCarPlate.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        mtvHState.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilHState.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metHLocality.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilHLocality.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metHAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilHAddress.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });
    }

    //tourist sign up -> tourist login
    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(TouristHourTrip.this);
        alertDialogBuilder.setTitle("Discard Process");
        alertDialogBuilder
                .setMessage("Do you wish to discard and go back homepage?")
                .setCancelable(false)
                .setPositiveButton("DISCARD",
                        (dialog, id) -> {
                            startActivity(new Intent(TouristHourTrip.this, TouristLogin.class));
                            finish();
                        });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}