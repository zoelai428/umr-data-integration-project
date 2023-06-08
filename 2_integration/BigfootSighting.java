package org.example;

public class BigfootSighting {

    Integer ID;
    String sightingClass;
    Integer reportID;
    Integer weatherID;
    Integer locationID;

    public boolean doesExist() {
        return sightingClass != null;
    }

    public void setValueForAttribute(String value, String attribute) {

        if (attribute.equals("class")) {
            sightingClass = value;
        }

    }

}
