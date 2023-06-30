package org.example.Data;

public class Tools {

    public static Double parseDouble(String value) {

        value = value.replaceAll("[^\\d.]", "");

        if(value.equals("")) {
            return null;
        } else {
            return Double.parseDouble(value);
        }
    }

}
