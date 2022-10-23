package com.example.finalyearproject_drivme;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class TouristHourTrip extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    //initialize variables
    TextInputLayout mtilHDate, mtilHDuration, mtilHStartTime, mtilHCarPlate, mtilHState, mtilHLocality, mtilHAddress;
    TextInputEditText metHDate, metHDuration, metHStartTime, metHEndTime, metHCarPlate, metHState, metHLocality, metHAddress, metHComment;
    String dateForID, locality, address;
    Button mbtnOK, mbtnHourNext;
    TextView mtvSelect;
    NumberPicker mnpHour, mnpMin, mnpPicker;
    SharedPreferences spDrivme;
    int valueHourStart, duration;
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
        mtilHDuration = findViewById(R.id.tilHDuration);
        mtilHStartTime = findViewById(R.id.tilHStartTime);
        mtilHCarPlate = findViewById(R.id.tilHCarPlate);
        mtilHState = findViewById(R.id.tilHState);
        mtilHLocality = findViewById(R.id.tilHLocality);
        mtilHAddress = findViewById(R.id.tilHAddress);
        metHDate = findViewById(R.id.etHDate);
        metHDuration = findViewById(R.id.etHDuration);
        metHStartTime = findViewById(R.id.etHStartTime);
        metHEndTime = findViewById(R.id.etHEndTime);
        metHCarPlate = findViewById(R.id.etHCarPlate);
        metHState = findViewById(R.id.etHState);
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
        durationMenu();
        getTime();
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

            //set limit to available date within a week
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
        String date = String.format("%02d/%02d/" + year, dayOfMonth, (month + 1));
        dateForID = String.format("%02d%02d" + year, dayOfMonth, (month + 1));

        metHDate.setText(date);
    }

    /*duration dialog*/
    private void durationMenu() {
        metHDuration.setOnClickListener(view -> {
            //set layout
            LayoutInflater dialogInflater = getLayoutInflater();
            view = dialogInflater.inflate(R.layout.activity_scroll_picker, null);

            androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(TouristHourTrip.this, R.style.dialog);
            androidx.appcompat.app.AlertDialog durationDialog = dialogBuilder.setView(view).create();

            //obtaining the View with specific ID
            mtvSelect = view.findViewById(R.id.tvSelectOption);
            mnpPicker = view.findViewById(R.id.npPicker);
            mbtnOK = view.findViewById(R.id.btnOK);

            //set the values
            mtvSelect.setText("Set Trip Duration");
            mnpPicker.setMaxValue(18);
            mnpPicker.setMinValue(1);
            mnpPicker.setValue(1);

            //show pop out dialog
            durationDialog.show();
            durationDialog.getWindow().setLayout(450, 580);

            mbtnOK.setOnClickListener(view1 -> {
                duration = mnpPicker.getValue();

                if (duration == 1) {
                    metHDuration.setText(duration + " Hour");
                }
                else {
                    metHDuration.setText(duration + " Hours");
                }

                metHStartTime.setText("");
                metHEndTime.setText("");

                durationDialog.dismiss();
            });

        });
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
            mnpHour.setMaxValue(24 - duration);
            mnpHour.setMinValue(5);
            mnpHour.setValue(5);
            mnpHour.setFormatter(i -> String.format("%02d", i));

            mnpMin.setMaxValue(0);
            mnpMin.setMinValue(0);
            mnpMin.setValue(0);
            mnpMin.setFormatter(i -> String.format("%02d", i));

            //show pop out dialog
            startTimeDialog.show();
            startTimeDialog.getWindow().setLayout(450, 500);

            mbtnOK.setOnClickListener(view1 -> {
                valueHourStart = mnpHour.getValue();

                metHStartTime.setText(String.format("%02d", valueHourStart) + ":00");
                metHEndTime.setText(String.format("%02d", valueHourStart + duration) + ":00");
                startTimeDialog.dismiss();

            });
        });
    }

    /*Getting time*/
    private void getTime() {
        metHStartTime.setOnClickListener(view -> {
            if(Objects.requireNonNull(metHDuration.getText()).toString().isEmpty()) {
                mtilHDuration.setError("Set Duration!");
            }
            else{
                startTimeMenu();
            }
        });

        metHEndTime.setOnClickListener(timeView -> {
            if(Objects.requireNonNull(metHEndTime.getText()).toString().isEmpty()) {
                mtilHStartTime.setError("Set Date!");
            }
            else{
                Toast.makeText(TouristHourTrip.this, "Only can adjust Duration or Start Time!", Toast.LENGTH_SHORT).show();
            }
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

        crDrivme.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        cpList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            cpList.add(document.getId());
                        }
                        if(cpList.size() == 0){
                            cpList.add("No Cars");
                        }
                        metHCarPlate.setText(cpList.get(0));
                    }
                });

        metHCarPlate.setOnClickListener(cpView -> {
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
                metHCarPlate.setText(cpList.get(value));
                carPlateDialog.dismiss();
            });
        });
    }

    /*state dialog*/
    private void stateMenu() {
        metHState.setOnClickListener(stateView -> {
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
                metHState.setText(ModelDriverDetails.getDetailsArrayList().get(value).getDetailsOption());

                stateDialog.dismiss();
            });
        });
    }

    /*Getting Locality Name and Address*/
    private void getPlaceSearch(){
        metHLocality.setOnClickListener(view -> {
            //initialize place field list
            List<Place.Field> placeList = Arrays.asList(Place.Field.ADDRESS, Place.Field.NAME);

            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, placeList).setCountry("MY").build(TouristHourTrip.this);
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
        else if (Objects.requireNonNull(metHDuration.getText()).toString().isEmpty()) {
            mtilHDuration.setError("Field cannot empty!");
        }
        else if (Objects.requireNonNull(metHStartTime.getText()).toString().isEmpty()) {
            mtilHStartTime.setError("Field empty!");
        }
        else if (Objects.requireNonNull(metHCarPlate.getText()).toString().equals("No Cars")) {
            mtilHCarPlate.setError("No Cars Available!");
        }
        else if (Objects.requireNonNull(metHState.getText()).toString().isEmpty()) {
            mtilHState.setError("Field cannot be empty!");
        }
        else if (Objects.requireNonNull(metHLocality.getText()).toString().isEmpty()) {
            mtilHLocality.setError("Field cannot be empty!");
        }
        else if (Objects.requireNonNull(metHAddress.getText()).toString().isEmpty()) {
            mtilHAddress.setError("Field cannot be empty!");
        }
        else {
            //get id and order id
            String getID = spDrivme.getString(KEY_ID, null);
            String carPlate = Objects.requireNonNull(metHCarPlate.getText()).toString();
            //get current date time
            DateFormat df = new SimpleDateFormat("ddMMyyyyHHmm");
            String date = df.format(Calendar.getInstance().getTime());
            String getOrderID = date + carPlate;
            FirebaseFirestore orderDB = FirebaseFirestore.getInstance();

            //initialize
            ArrayList<String> time = new ArrayList<>();

            //calculate and add hour into arraylist
            for(int i = 0; i < duration; i++) {
                //add 1 hour
                time.add(String.valueOf(valueHourStart + i));
            }

            //check which driver match the requirements
            orderDB.collection("User Accounts").document(getID).collection("Date Booked").document(dateForID).get()
                    .addOnCompleteListener(task -> {
                        String checkStatus = "true";
                        if (task.isSuccessful()) {
                            DocumentSnapshot doc = task.getResult();

                            if(doc.exists()) {
                                String tripOption = doc.getString("tripOption");

                                if (Objects.requireNonNull(tripOption).equals("Hour")) {
                                    for (int i = 0; i < time.size(); i++) {
                                        String timeState = doc.getString(time.get(i));

                                        if (Objects.requireNonNull(timeState).equals("Not Available")) {
                                            checkStatus = "false";
                                            break;
                                        }
                                    }

                                    //if true only allowed to proceed
                                    if (checkStatus.equals("true")) {
                                        //booking details -> driver list
                                        Intent intent = new Intent(TouristHourTrip.this, TouristDriverList.class);
                                        intent.putExtra("orderID", getOrderID);
                                        intent.putExtra("touristID", getID);
                                        intent.putExtra("dateID", dateForID);
                                        intent.putExtra("duration", duration);
                                        intent.putExtra("date", metHDate.getText().toString());
                                        intent.putExtra("startTime", Objects.requireNonNull(metHStartTime.getText()).toString());
                                        intent.putExtra("endTime", Objects.requireNonNull(metHEndTime.getText()).toString());
                                        intent.putExtra("hourStart", valueHourStart);
                                        intent.putExtra("carPlate", carPlate);
                                        intent.putExtra("state", metHState.getText().toString());
                                        intent.putExtra("locality", metHLocality.getText().toString());
                                        intent.putExtra("address", metHAddress.getText().toString());
                                        intent.putExtra("tripOption", getIntent().getStringExtra("tripOpt"));

                                        if (Objects.requireNonNull(metHComment.getText()).toString().isEmpty()) {
                                            intent.putExtra("comment", "No comment.");
                                        } else {
                                            intent.putExtra("comment", Objects.requireNonNull(metHComment.getText()).toString());
                                        }
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(TouristHourTrip.this, "Time selected have been booked! Please choose another time!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(TouristHourTrip.this, "Date selected have been booked! Please choose another date!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                //booking details -> driver list
                                Intent intent = new Intent(TouristHourTrip.this, TouristDriverList.class);
                                intent.putExtra("orderID", getOrderID);
                                intent.putExtra("touristID", getID);
                                intent.putExtra("dateID", dateForID);
                                intent.putExtra("duration", duration);
                                intent.putExtra("date", metHDate.getText().toString());
                                intent.putExtra("startTime", Objects.requireNonNull(metHStartTime.getText()).toString());
                                intent.putExtra("endTime", Objects.requireNonNull(metHEndTime.getText()).toString());
                                intent.putExtra("hourStart", valueHourStart);
                                intent.putExtra("carPlate", carPlate);
                                intent.putExtra("state", metHState.getText().toString());
                                intent.putExtra("locality", metHLocality.getText().toString());
                                intent.putExtra("address", metHAddress.getText().toString());
                                intent.putExtra("tripOption", getIntent().getStringExtra("tripOpt"));

                                if (Objects.requireNonNull(metHComment.getText()).toString().isEmpty()) {
                                    intent.putExtra("comment", "No comment.");
                                } else {
                                    intent.putExtra("comment", Objects.requireNonNull(metHComment.getText()).toString());
                                }
                                startActivity(intent);
                            }
                        }
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

        metHDuration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilHDuration.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metHCarPlate.addTextChangedListener(new TextWatcher() {
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

        metHState.addTextChangedListener(new TextWatcher() {
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

    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(TouristHourTrip.this);
        alertDialogBuilder.setTitle("Discard Process");
        alertDialogBuilder
                .setMessage("Do you wish to discard and go back homepage?")
                .setCancelable(false)
                .setPositiveButton("DISCARD",
                        (dialog, id) -> {
                            startActivity(new Intent(TouristHourTrip.this, TouristNavHomepage.class));
                            finishAffinity();
                            finish();
                        });

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}