package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DriverAvailability extends AppCompatActivity {
    //declare variables
    TextInputLayout mtilDState, mtilDArea;
    AutoCompleteTextView mtvDState, mtvDArea;
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

        //assign variables
        mtilDState = findViewById(R.id.tilDState);
        mtilDArea = findViewById(R.id.tilDArea);
        mtvDState = findViewById(R.id.tvDState);
        mtvDArea = findViewById(R.id.tvDArea);
        mtvMost = findViewById(R.id.tvMost);
        mbtnDriverDone = findViewById(R.id.btnDriverDone);

        //initialize shared preferences
        spDrivme = getSharedPreferences(SP_NAME, MODE_PRIVATE);

        //dialog menus
        stateMenu();
        areaMenu();

        //disable error
        errorChangeOnEachFields();

        mbtnDriverDone.setOnClickListener(view -> {
            //check each fields
            checkMenus();
        });
    }

    //state dialog
    private void stateMenu() {
        mtvDState.setOnClickListener(stateView -> {
            //initialize layout
            LayoutInflater dialogInflater = getLayoutInflater();
            stateView = dialogInflater.inflate(R.layout.activity_scroll_picker_long, null);

            //initialize dialog builder
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.dialog);
            AlertDialog stateDialog = dialogBuilder.setView(stateView).create();

            //assign variables
            mtvSelect = stateView.findViewById(R.id.tvSelectOption);
            mbtnOK = stateView.findViewById(R.id.btnLongOK);
            mnpPicker = stateView.findViewById(R.id.npPicker);

            //set the values
            ModelDriverDetails.initState();
            mtvSelect.setText("Select State You Stay");
            mnpPicker.setMaxValue(ModelDriverDetails.getDetailsArrayList().size() - 1);
            mnpPicker.setMinValue(0);
            mnpPicker.setDisplayedValues(ModelDriverDetails.detailsName());

            //display dialog with suitable size
            stateDialog.show();
            stateDialog.getWindow().setLayout(570, 580);

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

            //check if state field is empty
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
    }

    //check empty fields
    private void checkMenus(){
        if(mtvDState.getText().toString().isEmpty()){
            mtilDState.setError("Field cannot be empty!");
        }
        else if(mtvDArea.getText().toString().isEmpty()){
            mtilDArea.setError("Field cannot be empty!");
        }
        else{
            //insert database
            FirebaseFirestore drivingDB = FirebaseFirestore.getInstance();
            String id = spDrivme.getString(KEY_ID, null);
            //get text
            String textLan = getIntent().getStringExtra("dLanguage");
            String textArea =  mtvDArea.getText().toString();
            //split text to save into array
            String[] lan = textLan.split(", ");
            String[] area = textArea.split(", ");

            //set data to save into firestore
            Map<String,Object> drivingDetails = new HashMap<>();
            drivingDetails.put("age", getIntent().getStringExtra("dAge"));
            drivingDetails.put("gender", getIntent().getStringExtra("dGender"));
            drivingDetails.put("race", getIntent().getStringExtra("dRace"));
            drivingDetails.put("drivingExperience", getIntent().getStringExtra("dExp"));
            drivingDetails.put("languages", Arrays.asList(lan));
            drivingDetails.put("state", mtvDState.getText().toString());
            drivingDetails.put("familiarAreas", Arrays.asList(area));
            drivingDetails.put("Login Status Driver", 1);
            drivingDetails.put("accountStatus", "Driver");

            drivingDB.collection("User Accounts").document(id)
                    .update(drivingDetails)
                    .addOnSuccessListener(unused -> {
                        //go to homepage
                        Toast.makeText(DriverAvailability.this, "Driver Driving Details Added Successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(DriverAvailability.this, DriverNavHomepage.class));
                        finishAffinity();
                        finish();
                    });
        }
    }

    private void penangIslandMenu(){
        //set layout
        LayoutInflater dialogInflater = getLayoutInflater();
        View islandView = dialogInflater.inflate(R.layout.activity_driver_penang_island, null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.dialog);
        AlertDialog islandDialog = dialogBuilder.setView(islandView).create();

        //assign variables
        CheckBox mcbNorth = islandView.findViewById(R.id.cbNorth);
        CheckBox mcbSouth = islandView.findViewById(R.id.cbSouth);
        Button mbtnIslandOK = islandView.findViewById(R.id.btnIslandOK);

        islandDialog.show();
        islandDialog.getWindow().setLayout(650, 710);

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
            islandDialog.dismiss();
        });
    }

    private void penangMainlandMenu() {
        //set layout
        LayoutInflater dialogInflater = getLayoutInflater();
        View mainlandView = dialogInflater.inflate(R.layout.activity_driver_penang_mainland, null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.dialog);
        AlertDialog mainlandDialog = dialogBuilder.setView(mainlandView).create();

        //assign variables
        CheckBox mcbNorthSB = mainlandView.findViewById(R.id.cbNorthSB);
        CheckBox mcbCentral = mainlandView.findViewById(R.id.cbCentral);
        CheckBox mcbSouthSB = mainlandView.findViewById(R.id.cbSouthSB);
        Button mbtnMainlandOK = mainlandView.findViewById(R.id.btnMainlandOK);
        counter = 0;

        mainlandDialog.show();
        mainlandDialog.getWindow().setLayout(650, 710);

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
            mainlandDialog.dismiss();
        });
    }

    private void perakMenu() {
        //set layout
        LayoutInflater dialogInflater = getLayoutInflater();
        View perakView = dialogInflater.inflate(R.layout.activity_driver_perak, null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.dialog);
        AlertDialog perakDialog = dialogBuilder.setView(perakView).create();

        //assign variables
        CheckBox mcbHulu = perakView.findViewById(R.id.cbHulu);
        CheckBox mcbKerian = perakView.findViewById(R.id.cbKerian);
        CheckBox mcbLarut = perakView.findViewById(R.id.cbLarut);
        CheckBox mcbKuala = perakView.findViewById(R.id.cbKuala);
        CheckBox mcbKinta = perakView.findViewById(R.id.cbKinta);
        CheckBox mcbKampar = perakView.findViewById(R.id.cbKampar);
        CheckBox mcbBatang = perakView.findViewById(R.id.cbBatang);
        CheckBox mcbManjung = perakView.findViewById(R.id.cbManjung);
        CheckBox mcbTengah = perakView.findViewById(R.id.cbTengah);
        CheckBox mcbHilir = perakView.findViewById(R.id.cbHilir);
        CheckBox mcbBagan = perakView.findViewById(R.id.cbBagan);
        CheckBox mcbMuallim = perakView.findViewById(R.id.cbMuallim);
        Button mbtnPerakOK = perakView.findViewById(R.id.btnPerakOK);
        counter = 0;

        perakDialog.show();
        perakDialog.getWindow().setLayout(650, 710);

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
            perakDialog.dismiss();
        });
    }

    private void selangorMenu() {
        //set layout
        LayoutInflater dialogInflater = getLayoutInflater();
        View selangorView = dialogInflater.inflate(R.layout.activity_driver_selangor, null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.dialog);
        AlertDialog selangorDialog = dialogBuilder.setView(selangorView).create();

        //assign variables
        CheckBox mcbSabak = selangorView.findViewById(R.id.cbSabak);
        CheckBox mcbKuala = selangorView.findViewById(R.id.cbKuala);
        CheckBox mcbHulu = selangorView.findViewById(R.id.cbHulu);
        CheckBox mcbKlang = selangorView.findViewById(R.id.cbKlang);
        CheckBox mcbLangat = selangorView.findViewById(R.id.cbLangat);
        CheckBox mcbSepang = selangorView.findViewById(R.id.cbSepang);
        CheckBox mcbHuluLangat = selangorView.findViewById(R.id.cbHuluLangat);
        CheckBox mcbPetaling = selangorView.findViewById(R.id.cbPetaling);
        CheckBox mcbGombak = selangorView.findViewById(R.id.cbGombak);
        CheckBox mcbKL = selangorView.findViewById(R.id.cbKL);
        Button mbtnSelangorOK = selangorView.findViewById(R.id.btnSelangorOK);
        counter = 0;

        selangorDialog.show();
        selangorDialog.getWindow().setLayout(650, 710);

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
            selangorDialog.dismiss();
        });
    }

    private void johorMenu() {
        //set layout
        LayoutInflater dialogInflater = getLayoutInflater();
        View johorView = dialogInflater.inflate(R.layout.activity_driver_johor, null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.dialog);
        AlertDialog johorDialog = dialogBuilder.setView(johorView).create();

        //assign variables
        CheckBox mcbSegamat = johorView.findViewById(R.id.cbSegamat);
        CheckBox mcbLedang = johorView.findViewById(R.id.cbLedang);
        CheckBox mcbMuar = johorView.findViewById(R.id.cbMuar);
        CheckBox mcbBatu = johorView.findViewById(R.id.cbBatu);
        CheckBox mcbPontian = johorView.findViewById(R.id.cbPontian);
        CheckBox mcbKluang = johorView.findViewById(R.id.cbKluang);
        CheckBox mcbMersing = johorView.findViewById(R.id.cbMersing);
        CheckBox mcbKulai = johorView.findViewById(R.id.cbKulai);
        CheckBox mcbKota = johorView.findViewById(R.id.cbKota);
        CheckBox mcbJB = johorView.findViewById(R.id.cbJB);
        Button mbtnJohorOK = johorView.findViewById(R.id.btnJohorOK);
        counter = 0;

        johorDialog.show();
        johorDialog.getWindow().setLayout(650, 710);

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
            johorDialog.dismiss();
        });
    }
}