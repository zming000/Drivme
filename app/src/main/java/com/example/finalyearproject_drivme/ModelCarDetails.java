package com.example.finalyearproject_drivme;

import java.util.ArrayList;
import java.util.Arrays;

public class ModelCarDetails {
    private static ArrayList<ModelCarDetails> cModelArrayList;
    private final String modelOption;

    public ModelCarDetails(String modelOption) {
        this.modelOption = modelOption;
    }

    //Initialize car brand array list
    public static void initBrand(){
        cModelArrayList = new ArrayList<>();
        String[] brandItems = new String[]{"Honda", "Mazda", "Perodua", "Proton", "BMW"};
        Arrays.sort(brandItems);

        for (String brandItem : brandItems) {
            ModelCarDetails brd = new ModelCarDetails(brandItem);
            cModelArrayList.add(brd);
        }
    }

    //Initialize model bmw array list
    public static void initBMW(){
        cModelArrayList = new ArrayList<>();
        String[] modelItems = new String[]{"BMW X1", "BMW X5", "BMW 5 Series Sedan", "BMW 3 Series Sedan", "BMW iX"};
        Arrays.sort(modelItems);

        for (String modelItem : modelItems) {
            ModelCarDetails bmw = new ModelCarDetails(modelItem);
            cModelArrayList.add(bmw);
        }
    }

    //Initialize model honda array list
    public static void initHonda(){
        cModelArrayList = new ArrayList<>();
        String[] modelItems = new String[]{"Honda City", "Honda City Hatchback", "Honda Civic", "Honda CR-V", "Honda Accord"};
        Arrays.sort(modelItems);

        for (String modelItem : modelItems) {
            ModelCarDetails hd = new ModelCarDetails(modelItem);
            cModelArrayList.add(hd);
        }
    }

    //Initialize model mazda array list
    public static void initMazda(){
        cModelArrayList = new ArrayList<>();
        String[] modelItems = new String[]{"Mazda 2 Sedan", "Mazda 3 Sedan", "Mazda CX-3", "Mazda CX-8", "Mazda Biante"};
        Arrays.sort(modelItems);

        for (String modelItem : modelItems) {
            ModelCarDetails md = new ModelCarDetails(modelItem);
            cModelArrayList.add(md);
        }
    }

    //Initialize model perodua array list
    public static void initPerodua(){
        cModelArrayList = new ArrayList<>();
        String[] modelItems = new String[]{"Perodua Axia", "Perodua Bezza", "Perodua Ativa", "Perodua Aruz", "Perodua Myvi"};
        Arrays.sort(modelItems);

        for (String modelItem : modelItems) {
            ModelCarDetails prd = new ModelCarDetails(modelItem);
            cModelArrayList.add(prd);
        }
    }

    //Initialize model proton array list
    public static void initProton(){
        cModelArrayList = new ArrayList<>();
        String[] modelItems = new String[]{"Proton Saga", "Proton Iriz", "Proton X50", "Proton X70", "Proton Persona"};
        Arrays.sort(modelItems);

        for (String modelItem : modelItems) {
            ModelCarDetails pt = new ModelCarDetails(modelItem);
            cModelArrayList.add(pt);
        }
    }

    //Initialize car colour array list
    public static void initColour(){
        cModelArrayList = new ArrayList<>();
        String[] colourItems = new String[]{"Black", "Gray", "Silver", "Blue", "Red", "Brown", "Yellow", "Green", "White"};
        Arrays.sort(colourItems);

        for (String colourItem : colourItems) {
            ModelCarDetails cl = new ModelCarDetails(colourItem);
            cModelArrayList.add(cl);
        }
    }

    //Initialize car transmission array list
    public static void initTransmission(){
        cModelArrayList = new ArrayList<>();
        String[] transmissionItems = new String[]{"Manual Transmission (MT)", "Automatic Transmission (AT)",
                "Automated Manual Transmission (AM)", "Continuously Variable Transmission (CVT)"};
        Arrays.sort(transmissionItems);

        for (String transmissionItem : transmissionItems) {
            ModelCarDetails tms = new ModelCarDetails(transmissionItem);
            cModelArrayList.add(tms);
        }
    }

    public static ArrayList<ModelCarDetails> getModelArrayList() {
        return cModelArrayList;
    }

    //return the name
    public static String[] detailName(){
        String[] modelOpt = new String[cModelArrayList.size()];
        for(int i = 0; i < cModelArrayList.size(); i++){
            modelOpt[i] = cModelArrayList.get(i).modelOption;
        }

        return modelOpt;
    }

    public String getModelOption() {
        return modelOption;
    }
}
