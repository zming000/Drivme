package com.example.finalyearproject_drivme;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
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
    String[] brandItems, modelItems, colourItems, transmissionItems, companyItems, typeItems;
    boolean[] selectedCompany, selectedType;
    ArrayList<Integer> companyList, typeList;
    Button mbtnConfirm;
    SharedPreferences spDrivme;

    //key name
    private static final String SP_NAME = "drivmePref";
    private static final String KEY_ID = "userID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourist_car_details);

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

    //brand dropdown menu
    private void brandDropdown(){
        mtvCBrand.setOnClickListener(view -> {
            brandItems = new String[]{"Volvo", "Ford", "Honda", "Mazda", "Nissan", "Perodua", "Proton", "Toyota", "Volkswagen", "BMW"};
            Arrays.sort(brandItems);

            AlertDialog.Builder brandBuilder = new AlertDialog.Builder(TouristCarDetails.this);
            brandBuilder.setTitle("Choose a Car Brand");
            brandBuilder.setIcon(R.drawable.ic_list_bulleted);
            brandBuilder.setSingleChoiceItems(brandItems, -1, (dialogInterface, i) -> {
                mtvCBrand.setText(brandItems[i]);
                dialogInterface.dismiss();
            });
            brandBuilder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

            brandBuilder.show();
        });
    }

    //model dropdown menu
    private void modelDropdown(){
        mtvCModel.setOnClickListener(view -> {
            String getBrand = mtvCBrand.getText().toString().trim();

            //check if car brand field is empty
            if(!getBrand.equals("")) {
                switch (getBrand) {
                    case "BMW":
                        modelItems = new String[]{"BMW X1", "BMW X5", "BMW iX", "BMW 3 Series Sedan", "BMW 5 Series Sedan"};
                        break;
                    case "Ford":
                        modelItems = new String[]{"Ford Ranger", "Ford Everest", "Ford Fiesta", "Ford Mustang", "Ford Kuga"};
                        break;
                    case "Honda":
                        modelItems = new String[]{"Honda City", "Honda City Hatchback", "Honda Civic", "Honda CR-V", "Honda Accord"};
                        break;
                    case "Mazda":
                        modelItems = new String[]{"Mazda 2 Sedan", "Mazda 3 Sedan", "Mazda CX-3", "Mazda CX-8", "Mazda Biante"};
                        break;
                    case "Nissan":
                        modelItems = new String[]{"Nissan Almera", "Nissan Navara", "Nissan X-Trail", "Nissan Terra", "Nissan Teana"};
                        break;
                    case "Perodua":
                        modelItems = new String[]{"Perodua Axia", "Perodua Bezza", "Perodua Ativa", "Perodua Aruz", "Perodua Myvi"};
                        break;
                    case "Proton":
                        modelItems = new String[]{"Proton Saga", "Proton Iriz", "Proton X50", "Proton X70", "Proton Persona"};
                        break;
                    case "Toyota":
                        modelItems = new String[]{"Toyota Yaris", "Toyota Vios", "Toyota Hilux", "Toyota Avanza", "Toyota Innova"};
                        break;
                    case "Volkswagen":
                        modelItems = new String[]{"Volkswagen Passat", "Volkswagen Golf GTI", "Volkswagen Arteon", "Volkswagen Jetta", "Volkswagen Tiguan"};
                        break;
                    case "Volvo":
                        modelItems = new String[]{"Volvo XC40", "Volvo S90", "Volvo V40", "Volvo XC60", "Volvo V60"};
                        break;
                }

                Arrays.sort(modelItems);

                AlertDialog.Builder brandBuilder = new AlertDialog.Builder(TouristCarDetails.this);
                brandBuilder.setTitle("Choose a Car Model");
                brandBuilder.setIcon(R.drawable.ic_list_bulleted);
                brandBuilder.setSingleChoiceItems(modelItems, -1, (dialogInterface, i) -> {
                    mtvCModel.setText(modelItems[i]);
                    dialogInterface.dismiss();
                });
                brandBuilder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

                brandBuilder.show();
            }
            else{
                mtilCBrand.setError("Please select a car brand first!");
            }
        });
    }

    //colour dropdown menu
    private void colourDropdown(){
        mtvCColour.setOnClickListener(view -> {
            colourItems = new String[]{"Black", "Gray", "Silver", "Blue", "Red", "Brown", "Yellow", "Green", "White"};
            Arrays.sort(colourItems);

            AlertDialog.Builder brandBuilder = new AlertDialog.Builder(TouristCarDetails.this);
            brandBuilder.setTitle("Choose a Car Brand");
            brandBuilder.setIcon(R.drawable.ic_list_bulleted);
            brandBuilder.setSingleChoiceItems(colourItems, -1, (dialogInterface, i) -> {
                mtvCColour.setText(colourItems[i]);
                dialogInterface.dismiss();
            });
            brandBuilder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

            brandBuilder.show();
        });
    }

    //transmission dropdown menu
    private void transmissionDropdown(){
        mtvCTransmission.setOnClickListener(view -> {
            transmissionItems = new String[]{"Manual Transmission (MT)", "Automatic Transmission (AT)",
                    "Automated Manual Transmission (AM)", "Continuously Variable Transmission (CVT)"};

            AlertDialog.Builder brandBuilder = new AlertDialog.Builder(TouristCarDetails.this);
            brandBuilder.setTitle("Choose a Car Transmission");
            brandBuilder.setIcon(R.drawable.ic_list_bulleted);
            brandBuilder.setSingleChoiceItems(transmissionItems, -1, (dialogInterface, i) -> {
                mtvCTransmission.setText(transmissionItems[i]);
                dialogInterface.dismiss();
            });
            brandBuilder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

            brandBuilder.show();
        });
    }

    //car petrol company dropdown menu
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

    //car petrol type dropdown menu
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

            Map<String,Object> carDetails = new HashMap<>();
            carDetails.put("Car Plate", carPlate);
            carDetails.put("Car Model", mtvCModel.getText().toString());
            carDetails.put("Car Colour", mtvCColour.getText().toString());
            carDetails.put("Car Transmission", mtvCTransmission.getText().toString());
            carDetails.put("Petrol Company", mtvCPCompany.getText().toString());
            carDetails.put("Petrol Type", mtvCPType.getText().toString());

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
}