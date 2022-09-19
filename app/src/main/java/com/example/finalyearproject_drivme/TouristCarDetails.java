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

public class TouristCarDetails extends AppCompatActivity {
    //declare variables
    TextInputLayout mtilCPlate, mtilCBrand, mtilCModel, mtilCColour, mtilCTransmission, mtilCPCompany, mtilCPType;
    TextInputEditText metCPlate;
    AutoCompleteTextView mtvCBrand, mtvCModel, mtvCColour, mtvCTransmission, mtvCPCompany, mtvCPType;
    String[] companyItems, typeItems;
    boolean[] selectedCompany, selectedType;
    ArrayList<Integer> companyList, typeList;
    Button mbtnConfirm, mbtnOK;
    SharedPreferences spDrivme;
    Dialog brandDialog, modelDialog, colourDialog, transmissionDialog;
    TextView mtvSelect;
    NumberPicker mnpPicker;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_ID = "userID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_car_details);

        modelDialog = new Dialog(this);
        brandDialog = new Dialog(this);
        colourDialog = new Dialog(this);
        transmissionDialog = new Dialog(this);

        //obtaining the View with specific ID
        mtilCPlate =findViewById(R.id.tilCPlate);
        mtilCBrand = findViewById(R.id.tilCBrand);
        mtilCModel = findViewById(R.id.tilCModel);
        mtilCColour = findViewById(R.id.tilCColour);
        mtilCTransmission = findViewById(R.id.tilCTransmission);
        mtilCPCompany = findViewById(R.id.tilCPCompany);
        mtilCPType = findViewById(R.id.tilCPType);
        metCPlate = findViewById(R.id.etCPlate);
        mtvCBrand = findViewById(R.id.tvCBrand);
        mtvCModel = findViewById(R.id.tvCModel);
        mtvCColour = findViewById(R.id.tvCColour);
        mtvCTransmission = findViewById(R.id.tvCTransmission);
        mtvCPCompany = findViewById(R.id.tvCPCompany);
        mtvCPType = findViewById(R.id.tvCPType);
        mbtnConfirm = findViewById(R.id.btnConfirm);

        //initialize shared preferences
        spDrivme = getSharedPreferences(SP_NAME, MODE_PRIVATE);

        //dropdown menus
        brandDropdown();
        modelDropdown();
        colourDropdown();
        transmissionDropdown();
        companyDropdown();
        typeDropdown();

        //disable error
        errorChangeOnEachFields();

        mbtnConfirm.setOnClickListener(view -> {
            //check each fields
            checkCarPlate();
            checkDropdownMenus();
        });
    }

    //check car plate
    private void checkCarPlate(){
        String cPlate = Objects.requireNonNull(metCPlate.getText()).toString().replaceAll("\\s","");

        //check length of car plate
        if (cPlate.length() == 7) {
            //check first 3 characters
            if(!digitExist(cPlate.substring(0, 3))){
                //check last 4 numbers
                if(cPlate.substring(3, 7).matches("[0-9]+")){
                    FirebaseFirestore checkCar = FirebaseFirestore.getInstance();
                    String checkID = spDrivme.getString(KEY_ID, null);
                    String checkCarPlate = metCPlate.getText().toString().replaceAll("\\s","").toUpperCase();

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
                                }
                            });

                }
                else{
                    mtilCPlate.setError("Invalid Numbers in Car Plate! ");
                }
            }
            else{
                mtilCPlate.setError("Invalid Characters in Car Plate! ");
            }
        }
        else{
            mtilCPlate.setError("Invalid Car Plate!");
        }

    }

    //brand pop out menu
    private void brandDropdown(){
        mtvCBrand.setOnClickListener(view -> {
            brandDialog.setContentView(R.layout.activity_scroll_picker_short);

            //obtaining the View with specific ID
            mtvSelect = brandDialog.findViewById(R.id.tvSelectOption);
            mbtnOK = brandDialog.findViewById(R.id.btnShortOK);
            mnpPicker = brandDialog.findViewById(R.id.npPicker);

            //set the values
            mtvSelect.setText("Choose Car Brand");
            ModelCarDetails.initBrand();
            mnpPicker.setMaxValue(ModelCarDetails.getModelArrayList().size() - 1);
            mnpPicker.setMinValue(0);
            mnpPicker.setDisplayedValues(ModelCarDetails.detailName());

            brandDialog.show();

            mbtnOK.setOnClickListener(view1 -> {
                int value = mnpPicker.getValue();
                mtvCBrand.setText(ModelCarDetails.getModelArrayList().get(value).getModelOption());
                brandDialog.dismiss();
            });
        });
    }

    //model pop out menu
    private void modelDropdown(){
        mtvCModel.setOnClickListener(view -> {
            String getBrand = mtvCBrand.getText().toString().trim();

            modelDialog.setContentView(R.layout.activity_scroll_picker_long);

            //obtaining the View with specific ID
            mtvSelect = modelDialog.findViewById(R.id.tvSelectOption);
            mbtnOK = modelDialog.findViewById(R.id.btnLongOK);
            mnpPicker = modelDialog.findViewById(R.id.npPicker);

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

                mbtnOK.setOnClickListener(view1 -> {
                    int value = mnpPicker.getValue();
                    mtvCModel.setText(ModelCarDetails.getModelArrayList().get(value).getModelOption());
                    modelDialog.dismiss();
                });
            }
            else{
                mtilCBrand.setError("Please select a car brand first!");
            }
        });
    }

    //colour pop out menu
    private void colourDropdown(){
        mtvCColour.setOnClickListener(view -> {
            colourDialog.setContentView(R.layout.activity_scroll_picker_short);

            //obtaining the View with specific ID
            mtvSelect = colourDialog.findViewById(R.id.tvSelectOption);
            mbtnOK = colourDialog.findViewById(R.id.btnShortOK);
            mnpPicker = colourDialog.findViewById(R.id.npPicker);

            //set the values
            mtvSelect.setText("Choose Car Colour");
            ModelCarDetails.initColour();
            mnpPicker.setMaxValue(ModelCarDetails.getModelArrayList().size() - 1);
            mnpPicker.setMinValue(0);
            mnpPicker.setDisplayedValues(ModelCarDetails.detailName());

            colourDialog.show();

            mbtnOK.setOnClickListener(view1 -> {
                int value = mnpPicker.getValue();
                mtvCColour.setText(ModelCarDetails.getModelArrayList().get(value).getModelOption());
                colourDialog.dismiss();
            });
        });
    }

    //transmission pop out menu
    private void transmissionDropdown(){
        mtvCTransmission.setOnClickListener(view -> {
            transmissionDialog.setContentView(R.layout.activity_scroll_picker_extra_long);

            //obtaining the View with specific ID
            mtvSelect = transmissionDialog.findViewById(R.id.tvSelectOption);
            mbtnOK = transmissionDialog.findViewById(R.id.btnXLongOK);
            mnpPicker = transmissionDialog.findViewById(R.id.npPicker);

            //set the values
            mtvSelect.setText("Choose Car Transmission");
            ModelCarDetails.initTransmission();
            mnpPicker.setMaxValue(ModelCarDetails.getModelArrayList().size() - 1);
            mnpPicker.setMinValue(0);
            mnpPicker.setDisplayedValues(ModelCarDetails.detailName());

            transmissionDialog.show();

            mbtnOK.setOnClickListener(view1 -> {
                int value = mnpPicker.getValue();
                mtvCTransmission.setText(ModelCarDetails.getModelArrayList().get(value).getModelOption());
                transmissionDialog.dismiss();
            });
        });
    }

    //car petrol company pop out menu
    private void companyDropdown(){
        mtvCPCompany.setOnClickListener(view -> {
            companyItems = new String[]{"Petron", "Petronas", "Shell", "BHP", "Caltex"};
            //sort the petrol companies
            Arrays.sort(companyItems);

            //initialize array list
            companyList = new ArrayList<>();

            //Initialize selected petrol company array
            selectedCompany = new boolean[companyItems.length];

            AlertDialog.Builder companyBuilder = new AlertDialog.Builder(TouristCarDetails.this);
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

                mtvCPCompany.setText(typeSB.toString());
            });
            companyBuilder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
            companyBuilder.setNeutralButton("Clear All", (dialogInterface, i) -> {
                for(int j = 0; j < selectedCompany.length; j++){
                    //remove all selection
                    selectedCompany[j] = false;
                    //clear list
                    companyList.clear();
                    //clear text
                    mtvCPCompany.setText("");
                }
            });

            companyBuilder.show();
        });
    }

    //car petrol type pop out menu
    private void typeDropdown(){
        mtvCPType.setOnClickListener(view -> {
            String getCompany = mtvCPCompany.getText().toString().trim();

            if(!getCompany.equals("")) {
                typeItems = new String[]{"RON95", "RON97", "RON100", "Diesel", "Hybrid (Petrol and Electric)", "Gasoline"};
                //initialize array list
                typeList = new ArrayList<>();
                //Initialize selected petrol type array
                selectedType = new boolean[typeItems.length];

                AlertDialog.Builder typeBuilder = new AlertDialog.Builder(TouristCarDetails.this);
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

                    mtvCPType.setText(typeSB.toString());
                });
                typeBuilder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
                typeBuilder.setNeutralButton("Clear All", (dialogInterface, i) -> {
                    for (int j = 0; j < selectedType.length; j++) {
                        //remove all selection
                        selectedType[j] = false;
                        //clear list
                        typeList.clear();
                        //clear text
                        mtvCPType.setText("");
                    }
                });

                typeBuilder.show();
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

        mtvCBrand.addTextChangedListener(new TextWatcher() {
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

        mtvCModel.addTextChangedListener(new TextWatcher() {
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

        mtvCColour.addTextChangedListener(new TextWatcher() {
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

        mtvCTransmission.addTextChangedListener(new TextWatcher() {
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

        mtvCPCompany.addTextChangedListener(new TextWatcher() {
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

        mtvCPType.addTextChangedListener(new TextWatcher() {
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
        if(mtvCBrand.getText().toString().isEmpty()){
            mtilCBrand.setError("Field cannot be empty!");
        }
        else if(mtvCModel.getText().toString().isEmpty()){
            mtilCModel.setError("Field cannot be empty!");
        }
        else if(mtvCColour.getText().toString().isEmpty()){
            mtilCColour.setError("Field cannot be empty!");
        }
        else if(mtvCTransmission.getText().toString().isEmpty()){
            mtilCTransmission.setError("Field cannot be empty!");
        }
        else if(mtvCPCompany.getText().toString().isEmpty()){
            mtilCPCompany.setError("Field cannot be empty!");
        }
        else if(mtvCPType.getText().toString().isEmpty()){
            mtilCPType.setError("Field cannot be empty!");
        }
        else{
            //insert database
            FirebaseFirestore carDB = FirebaseFirestore.getInstance();
            String id = spDrivme.getString(KEY_ID, null);
            String carPlate = Objects.requireNonNull(metCPlate.getText()).toString().replaceAll("\\s","").toUpperCase();

            Map<String,Object> userAcc = new HashMap<>();
            userAcc.put("Login Status Tourist", 1);

            Map<String,Object> carDetails = new HashMap<>();
            carDetails.put("Car Plate", carPlate);
            carDetails.put("Car Model", mtvCModel.getText().toString());
            carDetails.put("Car Colour", mtvCColour.getText().toString());
            carDetails.put("Car Transmission", mtvCTransmission.getText().toString());
            carDetails.put("Petrol Company", mtvCPCompany.getText().toString());
            carDetails.put("Petrol Type", mtvCPType.getText().toString());

            carDB.collection("User Accounts").document(id)
                    .update(userAcc);

            carDB.collection("User Accounts").document(id).collection("Car Details").document(carPlate)
                    .set(carDetails)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(TouristCarDetails.this, "Car Added Successfully!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(TouristCarDetails.this, Role.class));
                        finish();
                    });
        }
    }

    //check digit
    private boolean digitExist(String text){
        return text.matches(".*\\d.*");
    }

    //quit application
    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Leaving Drivme?");
        alertDialogBuilder
                .setMessage("Click yes to exit!")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> finish())
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());

        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}