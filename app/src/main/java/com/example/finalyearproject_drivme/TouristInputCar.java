package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TouristInputCar extends AppCompatActivity {
    //declare variables
    TextInputLayout mtilCPlate, mtilCBrand, mtilCModel, mtilCColour, mtilCTransmission, mtilCPCompany, mtilCPType;
    TextInputEditText metCPlate, metCBrand, metCModel, metCColour, metCTransmission, metCPCompany, metCPType;
    String[] companyItems, typeItems;
    boolean[] selectedCompany, selectedType;
    ArrayList<Integer> companyList, typeList;
    Button mbtnConfirm, mbtnOK;
    SharedPreferences spDrivme;
    TextView mtvSelect;
    NumberPicker mnpPicker;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_ID = "userID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_input_car);

        //assign variables
        mtilCPlate =findViewById(R.id.tilCPlate);
        mtilCBrand = findViewById(R.id.tilCBrand);
        mtilCModel = findViewById(R.id.tilCModel);
        mtilCColour = findViewById(R.id.tilCColour);
        mtilCTransmission = findViewById(R.id.tilCTransmission);
        mtilCPCompany = findViewById(R.id.tilCPCompany);
        mtilCPType = findViewById(R.id.tilCPType);
        metCPlate = findViewById(R.id.etCPlate);
        metCBrand = findViewById(R.id.etCBrand);
        metCModel = findViewById(R.id.etCModel);
        metCColour = findViewById(R.id.etCColour);
        metCTransmission = findViewById(R.id.etCTransmission);
        metCPCompany = findViewById(R.id.etCPCompany);
        metCPType = findViewById(R.id.etCPType);
        mbtnConfirm = findViewById(R.id.btnConfirm);

        //initialize shared preferences
        spDrivme = getSharedPreferences(SP_NAME, MODE_PRIVATE);

        //dropdown menus
        brandDialog();
        modelDialog();
        colourDialog();
        transmissionDialog();
        companyDialog();
        typeDialog();

        //disable error
        errorChangeOnEachFields();

        mbtnConfirm.setOnClickListener(view -> {
            //check each fields
            checkDropdownMenus();
        });
    }

    //brand pop out menu
    private void brandDialog(){
        metCBrand.setOnClickListener(brandView -> {
            //set layout
            LayoutInflater dialogInflater = getLayoutInflater();
            brandView = dialogInflater.inflate(R.layout.activity_scroll_picker, null);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.dialog);
            AlertDialog brandDialog = dialogBuilder.setView(brandView).create();

            //obtaining the View with specific ID
            mtvSelect = brandView.findViewById(R.id.tvSelectOption);
            mbtnOK = brandView.findViewById(R.id.btnOK);
            mnpPicker = brandView.findViewById(R.id.npPicker);

            //set the values
            mtvSelect.setText("Choose Car Brand");
            ModelCarDetails.initBrand();
            mnpPicker.setMaxValue(ModelCarDetails.getModelArrayList().size() - 1);
            mnpPicker.setMinValue(0);
            mnpPicker.setDisplayedValues(ModelCarDetails.detailName());

            //display dialog
            brandDialog.show();
            brandDialog.getWindow().setLayout(450, 580);

            mbtnOK.setOnClickListener(view1 -> {
                int value = mnpPicker.getValue();
                metCBrand.setText(ModelCarDetails.getModelArrayList().get(value).getModelOption());
                //clear model
                metCModel.setText("");
                brandDialog.dismiss();
            });
        });
    }

    //model pop out menu
    private void modelDialog(){
        metCModel.setOnClickListener(modelView -> {
            String getBrand = Objects.requireNonNull(metCBrand.getText()).toString().trim();

            //set layout
            LayoutInflater dialogInflater = getLayoutInflater();
            modelView = dialogInflater.inflate(R.layout.activity_scroll_picker_long, null);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.dialog);
            AlertDialog modelDialog = dialogBuilder.setView(modelView).create();

            //obtaining the View with specific ID
            mtvSelect = modelView.findViewById(R.id.tvSelectOption);
            mbtnOK = modelView.findViewById(R.id.btnLongOK);
            mnpPicker = modelView.findViewById(R.id.npPicker);

            //set the values
            mtvSelect.setText("Choose Car Model");

            //check if car brand field is empty
            if(!getBrand.equals("")) {
                switch (getBrand) {
                    case "BMW":
                        ModelCarDetails.initBMW();
                        break;
                    case "Honda":
                        ModelCarDetails.initHonda();
                        break;
                    case "Mazda":
                        ModelCarDetails.initMazda();
                        break;
                    case "Perodua":
                        ModelCarDetails.initPerodua();
                        break;
                    case "Proton":
                        ModelCarDetails.initProton();
                        break;
                }
                mnpPicker.setMaxValue(ModelCarDetails.getModelArrayList().size() - 1);
                mnpPicker.setMinValue(0);
                mnpPicker.setDisplayedValues(ModelCarDetails.detailName());

                modelDialog.show();
                modelDialog.getWindow().setLayout(500, 580);

                mbtnOK.setOnClickListener(view1 -> {
                    int value = mnpPicker.getValue();
                    metCModel.setText(ModelCarDetails.getModelArrayList().get(value).getModelOption());
                    modelDialog.dismiss();
                });
            }
            else{
                mtilCBrand.setError("Please select a car brand first!");
            }
        });
    }

    //colour pop out menu
    private void colourDialog(){
        metCColour.setOnClickListener(colourView -> {
            //set layout
            LayoutInflater dialogInflater = getLayoutInflater();
            colourView = dialogInflater.inflate(R.layout.activity_scroll_picker, null);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.dialog);
            AlertDialog colourDialog = dialogBuilder.setView(colourView).create();

            //obtaining the View with specific ID
            mtvSelect = colourView.findViewById(R.id.tvSelectOption);
            mbtnOK = colourView.findViewById(R.id.btnOK);
            mnpPicker = colourView.findViewById(R.id.npPicker);

            //set the values
            mtvSelect.setText("Choose Car Colour");
            ModelCarDetails.initColour();
            mnpPicker.setMaxValue(ModelCarDetails.getModelArrayList().size() - 1);
            mnpPicker.setMinValue(0);
            mnpPicker.setDisplayedValues(ModelCarDetails.detailName());

            colourDialog.show();
            colourDialog.getWindow().setLayout(450, 660);

            mbtnOK.setOnClickListener(view1 -> {
                int value = mnpPicker.getValue();
                metCColour.setText(ModelCarDetails.getModelArrayList().get(value).getModelOption());
                colourDialog.dismiss();
            });
        });
    }

    //transmission pop out menu
    private void transmissionDialog(){
        metCTransmission.setOnClickListener(transmissionView -> {
            //set layout
            LayoutInflater dialogInflater = getLayoutInflater();
            transmissionView = dialogInflater.inflate(R.layout.activity_scroll_picker_extra_long, null);

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.dialog);
            AlertDialog transmissionDialog = dialogBuilder.setView(transmissionView).create();

            //obtaining the View with specific ID
            mtvSelect = transmissionView.findViewById(R.id.tvSelectOption);
            mbtnOK = transmissionView.findViewById(R.id.btnXLongOK);
            mnpPicker = transmissionView.findViewById(R.id.npPicker);

            //set the values
            mtvSelect.setText("Choose Car Transmission");
            ModelCarDetails.initTransmission();
            mnpPicker.setMaxValue(ModelCarDetails.getModelArrayList().size() - 1);
            mnpPicker.setMinValue(0);
            mnpPicker.setDisplayedValues(ModelCarDetails.detailName());

            transmissionDialog.show();
            transmissionDialog.getWindow().setLayout(670, 580);

            mbtnOK.setOnClickListener(view1 -> {
                int value = mnpPicker.getValue();
                metCTransmission.setText(ModelCarDetails.getModelArrayList().get(value).getModelOption());
                transmissionDialog.dismiss();
            });
        });
    }

    //car petrol company pop out menu
    private void companyDialog(){
        metCPCompany.setOnClickListener(view -> {
            companyItems = new String[]{"Petron", "Petronas", "Shell", "BHP", "Caltex"};
            //sort the petrol companies
            Arrays.sort(companyItems);

            //initialize array list
            companyList = new ArrayList<>();

            //Initialize selected petrol company array
            selectedCompany = new boolean[companyItems.length];

            AlertDialog.Builder companyBuilder = new AlertDialog.Builder(TouristInputCar.this);
            companyBuilder.setTitle("Choose Petrol Company");
            companyBuilder.setIcon(R.drawable.ic_list_bulleted);
            companyBuilder.setMultiChoiceItems(companyItems, selectedCompany, (dialogInterface, i, checked) -> {
                if(checked){
                    //add position
                    companyList.add(i);
                    //sort
                    Collections.sort(companyList);
                }
                else{
                    //remove from array list
                    companyList.remove(i);
                }
            });
            companyBuilder.setPositiveButton("OK", (dialogInterface, i) -> {
                //initialize string builder
                StringBuilder typeSB = new StringBuilder();

                for(int j = 0; j < companyList.size(); j++){
                    //concat array value
                    typeSB.append(companyItems[companyList.get(j)]);

                    if(j != companyList.size()-1){
                        typeSB.append(", ");
                    }
                }

                metCPCompany.setText(typeSB.toString());
            });
            companyBuilder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
            companyBuilder.setNeutralButton("Clear All", (dialogInterface, i) -> {
                for(int j = 0; j < selectedCompany.length; j++){
                    //remove all selection
                    selectedCompany[j] = false;
                    //clear list
                    companyList.clear();
                    //clear text
                    metCPCompany.setText("");
                }
            });

            companyBuilder.show();
        });
    }

    //car petrol type pop out menu
    private void typeDialog(){
        metCPType.setOnClickListener(view -> {
            String getCompany = Objects.requireNonNull(metCPCompany.getText()).toString().trim();

            if(!getCompany.equals("")) {
                typeItems = new String[]{"RON95", "RON97", "RON100", "Diesel", "Hybrid (Petrol and Electric)", "Gasoline"};
                //initialize array list
                typeList = new ArrayList<>();
                //Initialize selected petrol type array
                selectedType = new boolean[typeItems.length];

                AlertDialog.Builder typeBuilder = new AlertDialog.Builder(TouristInputCar.this);
                typeBuilder.setTitle("Choose Petrol Type");
                typeBuilder.setIcon(R.drawable.ic_list_bulleted);
                typeBuilder.setMultiChoiceItems(typeItems, selectedType, (dialogInterface, i, checked) -> {
                    if (checked) {
                        //add position
                        typeList.add(i);
                        //sort
                        Collections.sort(typeList);
                    } else {
                        //remove from array list
                        typeList.remove(i);
                    }
                });
                typeBuilder.setPositiveButton("OK", (dialogInterface, i) -> {
                    //initialize string builder
                    StringBuilder typeSB = new StringBuilder();

                    for (int j = 0; j < typeList.size(); j++) {
                        //concat array value
                        typeSB.append(typeItems[typeList.get(j)]);

                        if (j != typeList.size() - 1) {
                            typeSB.append(", ");
                        }
                    }

                    metCPType.setText(typeSB.toString());
                });
                typeBuilder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
                typeBuilder.setNeutralButton("Clear All", (dialogInterface, i) -> {
                    for (int j = 0; j < selectedType.length; j++) {
                        //remove all selection
                        selectedType[j] = false;
                        //clear list
                        typeList.clear();
                        //clear text
                        metCPType.setText("");
                    }
                });

                typeBuilder.show();
            }
            else{
                mtilCPCompany.setError("Please select petrol company!");
            }
        });
    }

    //Set error message on each field to ensure correct input
    private void errorChangeOnEachFields() {
        metCPlate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilCPlate.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metCBrand.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilCBrand.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metCModel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilCModel.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metCColour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilCColour.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metCTransmission.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilCTransmission.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metCPCompany.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilCPCompany.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });

        metCPType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mtilCPType.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Nothing
            }
        });
    }

    //check empty fields
    private void checkDropdownMenus(){
        if(!Objects.requireNonNull(metCPlate.getText()).toString().isEmpty()) {
            FirebaseFirestore checkCar = FirebaseFirestore.getInstance();
            String checkID = spDrivme.getString(KEY_ID, null);
            String checkCarPlate = metCPlate.getText().toString().replaceAll("\\s", "").toUpperCase();

            //check if car plate exist in user's database
            checkCar.collection("User Accounts").document(checkID).collection("Car Details").document(checkCarPlate)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot carResult = task.getResult();

                            //check the existence of ID
                            if (Objects.requireNonNull(carResult).exists()) {
                                mtilCPlate.setError("Car Plate is used!");
                            }
                            else if(Objects.requireNonNull(metCBrand.getText()).toString().isEmpty()){
                                mtilCBrand.setError("Field cannot be empty!");
                            }
                            else if(Objects.requireNonNull(metCModel.getText()).toString().isEmpty()){
                                mtilCModel.setError("Field cannot be empty!");
                            }
                            else if(Objects.requireNonNull(metCColour.getText()).toString().isEmpty()){
                                mtilCColour.setError("Field cannot be empty!");
                            }
                            else if(Objects.requireNonNull(metCTransmission.getText()).toString().isEmpty()){
                                mtilCTransmission.setError("Field cannot be empty!");
                            }
                            else if(Objects.requireNonNull(metCPCompany.getText()).toString().isEmpty()){
                                mtilCPCompany.setError("Field cannot be empty!");
                            }
                            else if(Objects.requireNonNull(metCPType.getText()).toString().isEmpty()){
                                mtilCPType.setError("Field cannot be empty!");
                            }
                            else{
                                //insert database
                                FirebaseFirestore carDB = FirebaseFirestore.getInstance();
                                String id = spDrivme.getString(KEY_ID, null);
                                String carPlate = Objects.requireNonNull(metCPlate.getText()).toString().replaceAll("\\s","").toUpperCase();

                                Map<String,Object> userAcc = new HashMap<>();
                                userAcc.put("loginStatusTourist", 1);

                                Map<String,Object> carDetails = new HashMap<>();
                                carDetails.put("carPlate", carPlate);
                                carDetails.put("carModel", metCModel.getText().toString());
                                carDetails.put("carColour", metCColour.getText().toString());
                                carDetails.put("carTransmission", metCTransmission.getText().toString());
                                carDetails.put("petrolCompany", metCPCompany.getText().toString());
                                carDetails.put("petrolType", metCPType.getText().toString());

                                carDB.collection("User Accounts").document(id)
                                        .update(userAcc);

                                carDB.collection("User Accounts").document(id).collection("Car Details").document(carPlate)
                                        .set(carDetails)
                                        .addOnSuccessListener(unused -> {
                                            Toast.makeText(TouristInputCar.this, "Car Added Successfully!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(TouristInputCar.this, TouristNavCars.class));
                                            finishAffinity();
                                            finish();
                                        });
                            }
                        }
                    });
        }
        else{
            mtilCPlate.setError("Input Car Plate!");
        }
    }

    //quit application
    @Override
    public void onBackPressed() {
        //get user id from shared preference
        String uID = spDrivme.getString(KEY_ID, null);

        if(uID != null){
            finish();
        }
        else {
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
}