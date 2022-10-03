package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public class DriverDrivingDetails extends AppCompatActivity {
    //declare variables
    TextInputLayout mtilDAge, mtilDGender, mtilDRace, mtilDExp, mtilDLanguage;
    TextInputEditText metDAge, metDGender, metDRace, metDExp, metDLanguage;
    Button mbtnDriverNext, mbtnOK;
    TextView mtvSelect;
    NumberPicker mnpPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_driving_details);

        //obtaining the View with specific ID
        mtilDAge = findViewById(R.id.tilDAge);
        mtilDGender = findViewById(R.id.tilDGender);
        mtilDRace = findViewById(R.id.tilDRace);
        mtilDExp = findViewById(R.id.tilDExp);
        mtilDLanguage = findViewById(R.id.tilDLanguage);
        metDAge = findViewById(R.id.etDAge);
        metDGender = findViewById(R.id.etDGender);
        metDRace = findViewById(R.id.etDRace);
        metDExp = findViewById(R.id.etDExp);
        metDLanguage = findViewById(R.id.etDLanguage);
        mbtnDriverNext = findViewById(R.id.btnDriverNext);

        //pop out menus
        ageMenu();
        genderMenu();
        raceMenu();
        drivingExpMenu();
        languageMenu();

        //disable error
        errorChangeOnEachFields();

        mbtnDriverNext.setOnClickListener(view -> {
            //check each fields
            checkMenus();
        });
    }

    //age dialog
    private void ageMenu() {
        metDAge.setOnClickListener(ageView -> {
            LayoutInflater dialogInflater = getLayoutInflater();
            ageView = dialogInflater.inflate(R.layout.activity_scroll_picker, null);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.dialog);
            AlertDialog ageDialog = dialogBuilder.setView(ageView).create();

            //assign variables
            mtvSelect = ageView.findViewById(R.id.tvSelectOption);
            mbtnOK = ageView.findViewById(R.id.btnOK);
            mnpPicker = ageView.findViewById(R.id.npPicker);

            //set the values
            mtvSelect.setText("Select Your Age");
            mnpPicker.setMaxValue(60);
            mnpPicker.setMinValue(21);
            mnpPicker.setValue(21);

            //show pop out dialog
            ageDialog.show();
            ageDialog.getWindow().setLayout(450, 580);

            mbtnOK.setOnClickListener(view1 -> {
                int value = mnpPicker.getValue();
                metDAge.setText(String.valueOf(value));
                ageDialog.dismiss();
            });
        });
    }

    //gender dialog
    private void genderMenu() {
        metDGender.setOnClickListener(genderView -> {
            LayoutInflater dialogInflater = getLayoutInflater();
            genderView = dialogInflater.inflate(R.layout.activity_scroll_picker, null);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.dialog);
            AlertDialog genderDialog = dialogBuilder.setView(genderView).create();

            //assign variables
            mtvSelect = genderView.findViewById(R.id.tvSelectOption);
            mbtnOK = genderView.findViewById(R.id.btnOK);
            mnpPicker = genderView.findViewById(R.id.npPicker);

            //set the values
            ModelDriverDetails.initGender();
            mtvSelect.setText("Select Your Gender");
            mnpPicker.setMaxValue(ModelDriverDetails.getDetailsArrayList().size() - 1);
            mnpPicker.setMinValue(0);
            mnpPicker.setDisplayedValues(ModelDriverDetails.detailsName());

            //display dialog
            genderDialog.show();
            genderDialog.getWindow().setLayout(470, 580);

            mbtnOK.setOnClickListener(view1 -> {
                int value = mnpPicker.getValue();
                metDGender.setText(ModelDriverDetails.getDetailsArrayList().get(value).getDetailsOption());
                genderDialog.dismiss();
            });
        });
    }

    //race dialog
    private void raceMenu() {
        metDRace.setOnClickListener(raceView -> {
            LayoutInflater dialogInflater = getLayoutInflater();
            raceView = dialogInflater.inflate(R.layout.activity_scroll_picker, null);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.dialog);
            AlertDialog raceDialog = dialogBuilder.setView(raceView).create();

            //assign variables
            mtvSelect = raceView.findViewById(R.id.tvSelectOption);
            mbtnOK = raceView.findViewById(R.id.btnOK);
            mnpPicker = raceView.findViewById(R.id.npPicker);

            //set the values
            ModelDriverDetails.initRace();
            mtvSelect.setText("Select Your Race");
            mnpPicker.setMaxValue(ModelDriverDetails.getDetailsArrayList().size() - 1);
            mnpPicker.setMinValue(0);
            mnpPicker.setDisplayedValues(ModelDriverDetails.detailsName());

            //display dialog
            raceDialog.show();
            raceDialog.getWindow().setLayout(470, 580);

            mbtnOK.setOnClickListener(view1 -> {
                int value = mnpPicker.getValue();
                metDRace.setText(ModelDriverDetails.getDetailsArrayList().get(value).getDetailsOption());
                raceDialog.dismiss();
            });
        });
    }

    //driving experience dialog
    private void drivingExpMenu() {
        metDExp.setOnClickListener(expView -> {
            LayoutInflater dialogInflater = getLayoutInflater();
            expView = dialogInflater.inflate(R.layout.activity_scroll_picker, null);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.dialog);
            AlertDialog expDialog = dialogBuilder.setView(expView).create();

            //assign variables
            mtvSelect = expView.findViewById(R.id.tvSelectOption);
            mbtnOK = expView.findViewById(R.id.btnOK);
            mnpPicker = expView.findViewById(R.id.npPicker);

            //set the values
            mtvSelect.setText("Select Your Driving Experience");
            mnpPicker.setMaxValue(42);
            mnpPicker.setMinValue(3);
            mnpPicker.setValue(3);

            //display dialog
            expDialog.show();
            expDialog.getWindow().setLayout(470, 640);

            mbtnOK.setOnClickListener(view1 -> {
                int value = mnpPicker.getValue();
                metDExp.setText(String.valueOf(value));
                expDialog.dismiss();
            });
        });
    }

    //language dialog (multi selection)
    private void languageMenu() {
        metDLanguage.setOnClickListener(view -> {
            String[] languageItems = new String[]{"Malay", "Mandarin Chinese", "English", "Tamil/Hindi", "Hokkien", "Cantonese", "Hakka", "Hainanese", "Hokchew", "Sign Language", "Spanish", "French", "Arabic", "Bengali", "Russian", "Portuguese", "Indonesian"};
            //sort the languages
            Arrays.sort(languageItems);

            //initialize array list
            ArrayList<Integer> langList = new ArrayList<>();

            //Initialize selected languages array
            boolean[] selectedLang = new boolean[languageItems.length];

            AlertDialog.Builder companyBuilder = new AlertDialog.Builder(DriverDrivingDetails.this);
            companyBuilder.setTitle("Choose Languages You Know");
            companyBuilder.setIcon(R.drawable.ic_list_bulleted);
            companyBuilder.setMultiChoiceItems(languageItems, selectedLang, (dialogInterface, i, checked) -> {
                if(checked){
                    //add position
                    langList.add(i);
                    //sort
                    Collections.sort(langList);
                }
                else{
                    //remove from array list
                    langList.remove(i);
                }
            });
            companyBuilder.setPositiveButton("OK", (dialogInterface, i) -> {
                //initialize string builder
                StringBuilder typeSB = new StringBuilder();

                for(int j = 0; j < langList.size(); j++){
                    //concat array value
                    typeSB.append(languageItems[langList.get(j)]);

                    if(j != langList.size()-1){
                        typeSB.append(", ");
                    }
                }

                metDLanguage.setText(typeSB.toString());
            });
            companyBuilder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
            companyBuilder.setNeutralButton("Clear All", (dialogInterface, i) -> {
                for(int j = 0; j < selectedLang.length; j++){
                    //remove all selection
                    selectedLang[j] = false;
                    //clear list
                    langList.clear();
                    //clear text
                    metDLanguage.setText("");
                }
            });

            companyBuilder.show();
        });
    }

    //Set error message on each field to ensure correct input
    private void errorChangeOnEachFields() {
        metDAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilDAge.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metDGender.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilDGender.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metDRace.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilDRace.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metDExp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilDExp.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metDLanguage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilDLanguage.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });
    }

    //check empty fields
    private void checkMenus(){
        if(Objects.requireNonNull(metDAge.getText()).toString().isEmpty()){
            mtilDAge.setError("Field cannot be empty!");
        }
        else if(Objects.requireNonNull(metDGender.getText()).toString().isEmpty()){
            mtilDGender.setError("Field cannot be empty!");
        }
        else if(Objects.requireNonNull(metDRace.getText()).toString().isEmpty()){
            mtilDRace.setError("Field cannot be empty!");
        }
        else if(Objects.requireNonNull(metDExp.getText()).toString().isEmpty()){
            mtilDExp.setError("Field cannot be empty!");
        }
        else if(Objects.requireNonNull(metDLanguage.getText()).toString().isEmpty()){
            mtilDLanguage.setError("Field cannot be empty!");
        }
        else{
            //proceed to next activity
            Intent intent = new Intent(DriverDrivingDetails.this, DriverAvailability.class);
            intent.putExtra("dAge", metDAge.getText().toString());
            intent.putExtra("dGender", metDGender.getText().toString());
            intent.putExtra("dRace", metDRace.getText().toString());
            intent.putExtra("dExp", metDExp.getText().toString());
            intent.putExtra("dLanguage", metDLanguage.getText().toString());

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    //quit application
    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Leaving Drivme?");
        alertDialogBuilder
                .setMessage("Click yes to exit!")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    finishAffinity();
                    finish();
                })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}