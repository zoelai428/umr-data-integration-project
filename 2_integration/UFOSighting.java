package org.example;

public class UFOSighting {

    Integer ID;
    Double duration;
    Integer reportID;
    Integer weatherID;
    Integer locationID;

    public boolean doesExist() {
        return duration != null;
    }

    public void setValueForAttribute(String value, String attribute) {

        if (attribute.equals("duration")) {
            duration = Double.parseDouble(value);
        }

    }

}
