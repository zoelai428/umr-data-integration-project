package org.example;

public class Location {

    int ID;
    Double longitude;
    Double latitude;
    String country;
    String county;
    String state;
    String nearestTown;
    String nearestRoad;
    String locationDetails;

    public boolean doesExist() {
        return longitude != null && latitude != null;
    }

    public void setValueForAttribute(String value, String attribute) {

        switch (attribute) {
            case "longitude" -> longitude = Double.parseDouble(value);
            case "latitude" -> latitude = Double.parseDouble(value);
            case "country" -> country = value;
            case "county" -> county = value;
            case "state" -> state = value;
            case "nearest_town" -> nearestTown = value;
            case "nearest_road" -> nearestRoad = value;
            case "details" ->  locationDetails = value;
        }

    }

}
