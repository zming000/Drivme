package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DriverAvailability extends AppCompatActivity {
    //declare variables
    Dialog stateDialog, areaDialog, dayDialog;
    TextInputLayout mtilDState, mtilDArea, mtilDDay;
    AutoCompleteTextView mtvDState, mtvDArea, mtvDDay;
    Button mbtnDriverDone, mbtnOK;
    TextView mtvSelect, mtvMost;
    NumberPicker mnpPicker;
    Integer counter;
    SharedPreferences spDrivme;
    ArrayList<String> areaList;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_ID = "userID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_availability);

        //Initialize dialog
        stateDialog = new Dialog(this);
        areaDialog = new Dialog(this);
        dayDialog = new Dialog(this);

        //obtaining the View with specific ID
        mtilDState = findViewById(R.id.tilDState);
        mtilDArea = findViewById(R.id.tilDArea);
        mtilDDay = findViewById(R.id.tilDDay);
        mtvDState = findViewById(R.id.tvDState);
        mtvDArea = findViewById(R.id.tvDArea);
        mtvMost = findViewById(R.id.tvMost);
        mtvDDay = findViewById(R.id.tvDDay);
        mbtnDriverDone = findViewById(R.id.btnDriverDone);

        //initialize shared preferences
        spDrivme = getSharedPreferences(SP_NAME, MODE_PRIVATE);

        //pop out menus
        stateMenu();
        areaMenu();
        dayMenu();

        //disable error
        errorChangeOnEachFields();

        mbtnDriverDone.setOnClickListener(view -> {
            //check each fields
            checkMenus();
        });
    }

    //state dialog
    private void stateMenu() {
        mtvDState.setOnClickListener(view -> {
            stateDialog.setContentView(R.layout.activity_scroll_picker_long);

            //obtaining the View with specific ID
            mtvSelect = stateDialog.findViewById(R.id.tvSelectOption);
            mbtnOK = stateDialog.findViewById(R.id.btnLongOK);
            mnpPicker = stateDialog.findViewById(R.id.npPicker);

            //set the values
            ModelDriverDetails.initState();
            mtvSelect.setText("Select State You Stay");
            mnpPicker.setMaxValue(ModelDriverDetails.getDetailsArrayList().size() - 1);
            mnpPicker.setMinValue(0);
            mnpPicker.setDisplayedValues(ModelDriverDetails.detailsName());

            //show pop out dialog
            stateDialog.show();

            mbtnOK.setOnClickListener(view1 -> {
                int value = mnpPicker.getValue();
                mtvDState.setText(ModelDriverDetails.getDetailsArrayList().get(value).getDetailsOption());

                //set text based on state selected
                switch (mtvDState.getText().toString()) {
                    case "Penang (Island)":
                    case "Penang (Mainland)":
                        mtvMost.setText("Select at most 2");
                        mtvDArea.setText("");
                        break;
                    case "Perak":
                    case "Selangor":
                    case "Johor":
                        mtvMost.setText("Select at most 3");
                        mtvDArea.setText("");
                        break;
                }
                stateDialog.dismiss();
            });
        });
    }

    //area dialog
    private void areaMenu() {
        mtvDArea.setOnClickListener(view -> {
            String getState = mtvDState.getText().toString();
            areaList = new ArrayList<>();

            //check if car brand field is empty
            if(!getState.equals("")) {
                switch (getState) {
                    case "Penang (Island)":
                        penangIslandMenu();
                        break;
                    case "Penang (Mainland)":
                        penangMainlandMenu();
                        break;
                    case "Perak":
                        perakMenu();
                        break;
                    case "Selangor":
                        selangorMenu();
                        break;
                    case "Johor":
                        johorMenu();
                        break;
                }
            }
            else{
                mtilDState.setError("Please select a state first!");
            }
        });
    }

    //day dialog (multi selection)
    private void dayMenu() {
        mtvDDay.setOnClickListener(view -> {
            String[] dayItems = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

            //initialize array list
            ArrayList<Integer> dayList = new ArrayList<>();

            //Initialize selected day array
            boolean[] selectedDay = new boolean[dayItems.length];

            AlertDialog.Builder companyBuilder = new AlertDialog.Builder(DriverAvailability.this);
            companyBuilder.setTitle("Choose Day Available");
            companyBuilder.setIcon(R.drawable.ic_list_bulleted);
            companyBuilder.setMultiChoiceItems(dayItems, selectedDay, (dialogInterface, i, checked) -> {
                if(checked){
                    //add position
                    dayList.add(i);
                    //sort
                    Collections.sort(dayList);
                }
                else{
                    //remove from array list
                    dayList.remove(i);
                }
            });
            companyBuilder.setPositiveButton("OK", (dialogInterface, i) -> {
                //initialize string builder
                StringBuilder typeSB = new StringBuilder();

                for(int j = 0; j < dayList.size(); j++){
                    //concat array value
                    typeSB.append(dayItems[dayList.get(j)]);

                    if(j != dayList.size()-1){
                        typeSB.append(", ");
                    }
                }

                mtvDDay.setText(typeSB.toString());
            });
            companyBuilder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
            companyBuilder.setNeutralButton("Clear All", (dialogInterface, i) -> {
                for(int j = 0; j < selectedDay.length; j++){
                    //remove all selection
                    selectedDay[j] = false;
                    //clear list
                    dayList.clear();
                    //clear text
                    mtvDDay.setText("");
                }
            });
            companyBuilder.show();
        });
    }

    //Set error message on each field to ensure correct input
    private void errorChangeOnEachFields() {
        mtvDState.addTextChangedListener(new TextWatcher() {
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

        mtvDArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilDArea.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        mtvDDay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilDDay.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });
    }

    //check empty fields
    private void checkMenus(){
        if(mtvDState.getText().toString().isEmpty()){
            mtilDState.setError("Field cannot be empty!");
        }
        else if(mtvDArea.getText().toString().isEmpty()){
            mtilDArea.setError("Field cannot be empty!");
        }
        else if(mtvDDay.getText().toString().isEmpty()){
            mtilDDay.setError("Field cannot be empty!");
        }
        else{
            //insert database
            FirebaseFirestore drivingDB = FirebaseFirestore.getInstance();
            String id = spDrivme.getString(KEY_ID, null);

            Map<String,Object> drivingDetails = new HashMap<>();
            drivingDetails.put("Age", getIntent().getStringExtra("dAge"));
            drivingDetails.put("Gender", getIntent().getStringExtra("dGender"));
            drivingDetails.put("Race", getIntent().getStringExtra("dRace"));
            drivingDetails.put("Driving Experience", getIntent().getStringExtra("dExp"));
            drivingDetails.put("Languages", getIntent().getStringExtra("dLanguage"));
            drivingDetails.put("State", mtvDState.getText().toString());
            drivingDetails.put("Familiar Areas", mtvDArea.getText().toString());
            drivingDetails.put("Day", mtvDDay.getText().toString());
            drivingDetails.put("Login Status Driver", 1);

            drivingDB.collection("User Accounts").document(id)
                    .update(drivingDetails)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(DriverAvailability.this, "Driver Driving Details Updated Successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(DriverAvailability.this, Role.class));
                        finish();
                    });
        }
    }

    private void penangIslandMenu(){
        //set layout
        areaDialog.setContentView(R.layout.activity_driver_penang_island);

        //obtaining the View with specific ID
        CheckBox mcbNorth = areaDialog.findViewById(R.id.cbNorth);
        CheckBox mcbSouth = areaDialog.findViewById(R.id.cbSouth);
        Button mbtnIslandOK = areaDialog.findViewById(R.id.btnIslandOK);

        areaDialog.show();

        //checkboxes
        mcbNorth.setOnClickListener(view12 -> {
            if(mcbNorth.isChecked())              {
                areaList.add(mcbNorth.getText().toString());
            }
            else{
                areaList.remove(mcbNorth.getText().toString());
            }
        });

        mcbSouth.setOnClickListener(view13 -> {
            if(mcbSouth.isChecked()){
                areaList.add(mcbSouth.getText().toString());
            }
            else{
                areaList.remove(mcbSouth.getText().toString());
            }
        });

        mbtnIslandOK.setOnClickListener(view1 -> {
            //initialize string builder
            StringBuilder areaSB = new StringBuilder();

            for(int j = 0; j < areaList.size(); j++){
                //concat array value
                areaSB.append(areaList.get(j));

                if(j != areaList.size()-1){
                    areaSB.append(", ");
                }
            }
            mtvDArea.setText(areaSB.toString());
            areaDialog.dismiss();
        });
    }

    private void penangMainlandMenu() {
        //set layout
        areaDialog.setContentView(R.layout.activity_driver_penang_mainland);

        //obtaining the View with specific ID
        CheckBox mcbNorthSB = areaDialog.findViewById(R.id.cbNorthSB);
        CheckBox mcbCentral = areaDialog.findViewById(R.id.cbCentral);
        CheckBox mcbSouthSB = areaDialog.findViewById(R.id.cbSouthSB);
        Button mbtnMainlandOK = areaDialog.findViewById(R.id.btnMainlandOK);
        counter = 0;

        areaDialog.show();

        //checkboxes
        mcbNorthSB.setOnClickListener(view12 -> {
            if(counter != 2) {
                if (mcbNorthSB.isChecked()) {
                    areaList.add(mcbNorthSB.getText().toString());
                    counter++;
                } else {
                    areaList.remove(mcbNorthSB.getText().toString());
                    counter--;
                }
            }
            else{
                mcbNorthSB.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 2 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbCentral.setOnClickListener(view13 -> {
            if(counter != 2) {
                if(mcbCentral.isChecked()){
                    areaList.add(mcbCentral.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbCentral.getText().toString());
                    counter--;
                }
            }
            else{
                mcbCentral.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 2 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbSouthSB.setOnClickListener(view13 -> {
            if(counter != 2) {
                if(mcbSouthSB.isChecked()){
                    areaList.add(mcbSouthSB.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbSouthSB.getText().toString());
                    counter--;
                }
            }
            else{
                mcbSouthSB.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 2 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mbtnMainlandOK.setOnClickListener(view1 -> {
            //initialize string builder
            StringBuilder areaSB = new StringBuilder();

            for(int j = 0; j < areaList.size(); j++){
                //concat array value
                areaSB.append(areaList.get(j));

                if(j != areaList.size()-1){
                    areaSB.append(", ");
                }
            }
            mtvDArea.setText(areaSB.toString());
            areaDialog.dismiss();
        });
    }

    private void perakMenu() {
        //set layout
        areaDialog.setContentView(R.layout.activity_driver_perak);

        //obtaining the View with specific ID
        CheckBox mcbHulu = areaDialog.findViewById(R.id.cbHulu);
        CheckBox mcbKerian = areaDialog.findViewById(R.id.cbKerian);
        CheckBox mcbLarut = areaDialog.findViewById(R.id.cbLarut);
        CheckBox mcbKuala = areaDialog.findViewById(R.id.cbKuala);
        CheckBox mcbKinta = areaDialog.findViewById(R.id.cbKinta);
        CheckBox mcbKampar = areaDialog.findViewById(R.id.cbKampar);
        CheckBox mcbBatang = areaDialog.findViewById(R.id.cbBatang);
        CheckBox mcbManjung = areaDialog.findViewById(R.id.cbManjung);
        CheckBox mcbTengah = areaDialog.findViewById(R.id.cbTengah);
        CheckBox mcbHilir = areaDialog.findViewById(R.id.cbHilir);
        CheckBox mcbBagan = areaDialog.findViewById(R.id.cbBagan);
        CheckBox mcbMuallim = areaDialog.findViewById(R.id.cbMuallim);
        Button mbtnPerakOK = areaDialog.findViewById(R.id.btnPerakOK);
        counter = 0;

        areaDialog.show();

        //checkboxes
        mcbHulu.setOnClickListener(view12 -> {
            if(counter != 3) {
                if (mcbHulu.isChecked()) {
                    areaList.add(mcbHulu.getText().toString());
                    counter++;
                } else {
                    areaList.remove(mcbHulu.getText().toString());
                    counter--;
                }
            }
            else{
                mcbHulu.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbKerian.setOnClickListener(view13 -> {
            if(counter != 3) {
                if(mcbKerian.isChecked()){
                    areaList.add(mcbKerian.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbKerian.getText().toString());
                    counter--;
                }
            }
            else{
                mcbKerian.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbLarut.setOnClickListener(view13 -> {
            if(counter != 3) {
                if(mcbLarut.isChecked()){
                    areaList.add(mcbLarut.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbLarut.getText().toString());
                    counter--;
                }
            }
            else{
                mcbLarut.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbKuala.setOnClickListener(view13 -> {
            if(counter != 3) {
                if(mcbKuala.isChecked()){
                    areaList.add(mcbKuala.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbKuala.getText().toString());
                    counter--;
                }
            }
            else{
                mcbKuala.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbKinta.setOnClickListener(view13 -> {
            if(counter != 3) {
                if(mcbKinta.isChecked()){
                    areaList.add(mcbKinta.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbKinta.getText().toString());
                    counter--;
                }
            }
            else{
                mcbKinta.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbKampar.setOnClickListener(view13 -> {
            if(counter != 3) {
                if(mcbKampar.isChecked()){
                    areaList.add(mcbKampar.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbKampar.getText().toString());
                    counter--;
                }
            }
            else{
                mcbKampar.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbBatang.setOnClickListener(view13 -> {
            if(counter != 3) {
                if(mcbBatang.isChecked()){
                    areaList.add(mcbBatang.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbBatang.getText().toString());
                    counter--;
                }
            }
            else{
                mcbBatang.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbManjung.setOnClickListener(view13 -> {
            if(counter != 3) {
                if(mcbManjung.isChecked()){
                    areaList.add(mcbManjung.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbManjung.getText().toString());
                    counter--;
                }
            }
            else{
                mcbManjung.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbTengah.setOnClickListener(view13 -> {
            if(counter != 3) {
                if(mcbTengah.isChecked()){
                    areaList.add(mcbTengah.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbTengah.getText().toString());
                    counter--;
                }
            }
            else{
                mcbTengah.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbHilir.setOnClickListener(view13 -> {
            if(counter != 3) {
                if(mcbHilir.isChecked()){
                    areaList.add(mcbHilir.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbHilir.getText().toString());
                    counter--;
                }
            }
            else{
                mcbHilir.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbBagan.setOnClickListener(view13 -> {
            if(counter != 3) {
                if(mcbBagan.isChecked()){
                    areaList.add(mcbBagan.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbBagan.getText().toString());
                    counter--;
                }
            }
            else{
                mcbBagan.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbMuallim.setOnClickListener(view13 -> {
            if(counter != 3) {
                if(mcbMuallim.isChecked()){
                    areaList.add(mcbMuallim.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbMuallim.getText().toString());
                    counter--;
                }
            }
            else{
                mcbMuallim.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mbtnPerakOK.setOnClickListener(view1 -> {
            //initialize string builder
            StringBuilder areaSB = new StringBuilder();

            for(int j = 0; j < areaList.size(); j++){
                //concat array value
                areaSB.append(areaList.get(j));

                if(j != areaList.size()-1){
                    areaSB.append(", ");
                }
            }
            mtvDArea.setText(areaSB.toString());
            areaDialog.dismiss();
        });
    }

    private void selangorMenu() {
        //set layout
        areaDialog.setContentView(R.layout.activity_driver_selangor);

        //obtaining the View with specific ID
        CheckBox mcbSabak = areaDialog.findViewById(R.id.cbSabak);
        CheckBox mcbKuala = areaDialog.findViewById(R.id.cbKuala);
        CheckBox mcbHulu = areaDialog.findViewById(R.id.cbHulu);
        CheckBox mcbKlang = areaDialog.findViewById(R.id.cbKlang);
        CheckBox mcbLangat = areaDialog.findViewById(R.id.cbLangat);
        CheckBox mcbSepang = areaDialog.findViewById(R.id.cbSepang);
        CheckBox mcbHuluLangat = areaDialog.findViewById(R.id.cbHuluLangat);
        CheckBox mcbPetaling = areaDialog.findViewById(R.id.cbPetaling);
        CheckBox mcbGombak = areaDialog.findViewById(R.id.cbGombak);
        CheckBox mcbKL = areaDialog.findViewById(R.id.cbKL);
        Button mbtnSelangorOK = areaDialog.findViewById(R.id.btnSelangorOK);
        counter = 0;

        areaDialog.show();

        //checkboxes
        mcbSabak.setOnClickListener(view12 -> {
            if(counter != 3) {
                if (mcbSabak.isChecked()) {
                    areaList.add(mcbSabak.getText().toString());
                    counter++;
                } else {
                    areaList.remove(mcbSabak.getText().toString());
                    counter--;
                }
            }
            else{
                mcbSabak.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbKuala.setOnClickListener(view13 -> {
            if(counter != 3) {
                if(mcbKuala.isChecked()){
                    areaList.add(mcbKuala.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbKuala.getText().toString());
                    counter--;
                }
            }
            else{
                mcbKuala.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbHulu.setOnClickListener(view13 -> {
            if(counter != 3) {
                if(mcbHulu.isChecked()){
                    areaList.add(mcbHulu.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbHulu.getText().toString());
                    counter--;
                }
            }
            else{
                mcbHulu.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbKlang.setOnClickListener(view13 -> {
            if(counter != 3) {
                if(mcbKlang.isChecked()){
                    areaList.add(mcbKlang.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbKlang.getText().toString());
                    counter--;
                }
            }
            else{
                mcbKlang.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbLangat.setOnClickListener(view13 -> {
            if(counter != 3) {
                if(mcbLangat.isChecked()){
                    areaList.add(mcbLangat.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbLangat.getText().toString());
                    counter--;
                }
            }
            else{
                mcbLangat.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbSepang.setOnClickListener(view13 -> {
            if(counter != 3) {
                if(mcbSepang.isChecked()){
                    areaList.add(mcbSepang.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbSepang.getText().toString());
                    counter--;
                }
            }
            else{
                mcbSepang.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbHuluLangat.setOnClickListener(view13 -> {
            if(counter != 3) {
                if(mcbHuluLangat.isChecked()){
                    areaList.add(mcbHuluLangat.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbHuluLangat.getText().toString());
                    counter--;
                }
            }
            else{
                mcbHuluLangat.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbPetaling.setOnClickListener(view13 -> {
            if(counter != 3) {
                if(mcbPetaling.isChecked()){
                    areaList.add(mcbPetaling.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbPetaling.getText().toString());
                    counter--;
                }
            }
            else{
                mcbPetaling.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbGombak.setOnClickListener(view13 -> {
            if(counter != 3) {
                if(mcbGombak.isChecked()){
                    areaList.add(mcbGombak.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbGombak.getText().toString());
                    counter--;
                }
            }
            else{
                mcbGombak.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbKL.setOnClickListener(view13 -> {
            if(counter != 3) {
                if(mcbKL.isChecked()){
                    areaList.add(mcbKL.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbKL.getText().toString());
                    counter--;
                }
            }
            else{
                mcbKL.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mbtnSelangorOK.setOnClickListener(view1 -> {
            //initialize string builder
            StringBuilder areaSB = new StringBuilder();

            for(int j = 0; j < areaList.size(); j++){
                //concat array value
                areaSB.append(areaList.get(j));

                if(j != areaList.size()-1){
                    areaSB.append(", ");
                }
            }
            mtvDArea.setText(areaSB.toString());
            areaDialog.dismiss();
        });
    }

    private void johorMenu() {
        //set layout
        areaDialog.setContentView(R.layout.activity_driver_johor);

        //obtaining the View with specific ID
        CheckBox mcbSegamat = areaDialog.findViewById(R.id.cbSegamat);
        CheckBox mcbLedang = areaDialog.findViewById(R.id.cbLedang);
        CheckBox mcbMuar = areaDialog.findViewById(R.id.cbMuar);
        CheckBox mcbBatu = areaDialog.findViewById(R.id.cbBatu);
        CheckBox mcbPontian = areaDialog.findViewById(R.id.cbPontian);
        CheckBox mcbKluang = areaDialog.findViewById(R.id.cbKluang);
        CheckBox mcbMersing = areaDialog.findViewById(R.id.cbMersing);
        CheckBox mcbKulai = areaDialog.findViewById(R.id.cbKulai);
        CheckBox mcbKota = areaDialog.findViewById(R.id.cbKota);
        CheckBox mcbJB = areaDialog.findViewById(R.id.cbJB);
        Button mbtnJohorOK = areaDialog.findViewById(R.id.btnJohorOK);
        counter = 0;

        areaDialog.show();

        //checkboxes
        mcbSegamat.setOnClickListener(view12 -> {
            if(counter != 3) {
                if (mcbSegamat.isChecked()) {
                    areaList.add(mcbSegamat.getText().toString());
                    counter++;
                } else {
                    areaList.remove(mcbSegamat.getText().toString());
                    counter--;
                }
            }
            else{
                mcbSegamat.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbLedang.setOnClickListener(view13 -> {
            if(counter != 3) {
                if(mcbLedang.isChecked()){
                    areaList.add(mcbLedang.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbLedang.getText().toString());
                    counter--;
                }
            }
            else{
                mcbLedang.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbMuar.setOnClickListener(view13 -> {
            if(counter != 3) {
                if(mcbMuar.isChecked()){
                    areaList.add(mcbMuar.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbMuar.getText().toString());
                    counter--;
                }
            }
            else{
                mcbMuar.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbBatu.setOnClickListener(view13 -> {
            if(counter != 3) {
                if(mcbBatu.isChecked()){
                    areaList.add(mcbBatu.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbBatu.getText().toString());
                    counter--;
                }
            }
            else{
                mcbBatu.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbPontian.setOnClickListener(view13 -> {
            if(counter != 3) {
                if(mcbPontian.isChecked()){
                    areaList.add(mcbPontian.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbPontian.getText().toString());
                    counter--;
                }
            }
            else{
                mcbPontian.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbKluang.setOnClickListener(view13 -> {
            if(counter != 3) {
                if(mcbKluang.isChecked()){
                    areaList.add(mcbKluang.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbKluang.getText().toString());
                    counter--;
                }
            }
            else{
                mcbKluang.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbMersing.setOnClickListener(view13 -> {
            if(counter != 3) {
                if(mcbMersing.isChecked()){
                    areaList.add(mcbMersing.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbMersing.getText().toString());
                    counter--;
                }
            }
            else{
                mcbMersing.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbKulai.setOnClickListener(view13 -> {
            if(counter != 3) {
                if(mcbKulai.isChecked()){
                    areaList.add(mcbKulai.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbKulai.getText().toString());
                    counter--;
                }
            }
            else{
                mcbKulai.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbKota.setOnClickListener(view13 -> {
            if(counter != 3) {
                if(mcbKota.isChecked()){
                    areaList.add(mcbKota.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbKota.getText().toString());
                    counter--;
                }
            }
            else{
                mcbKota.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mcbJB.setOnClickListener(view13 -> {
            if(counter != 3) {
                if(mcbJB.isChecked()){
                    areaList.add(mcbJB.getText().toString());
                    counter++;
                }
                else{
                    areaList.remove(mcbJB.getText().toString());
                    counter--;
                }
            }
            else{
                mcbJB.setChecked(false);
                Toast.makeText(DriverAvailability.this, "Only allowed to select 3 areas at most!", Toast.LENGTH_SHORT).show();
            }
        });

        mbtnJohorOK.setOnClickListener(view1 -> {
            //initialize string builder
            StringBuilder areaSB = new StringBuilder();

            for(int j = 0; j < areaList.size(); j++){
                //concat array value
                areaSB.append(areaList.get(j));

                if(j != areaList.size()-1){
                    areaSB.append(", ");
                }
            }
            mtvDArea.setText(areaSB.toString());
            areaDialog.dismiss();
        });
    }
}