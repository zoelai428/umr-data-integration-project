package org.example.Data;

public class Location {

    public Integer id;
    public Double longitude;
    public Double latitude;
    public String country;
    public String county;
    public String state;
    public String nearest_town;
    public String nearest_road;
    public String details;

    public boolean doesExist() {
        return longitude != null && latitude != null;
    }

    public void setValueForAttribute(String value, String attribute) {

        switch (attribute) {
            case "longitude" -> longitude = Tools.parseDouble(value);
            case "latitude" -> latitude = Tools.parseDouble(value);
            case "country" -> country = value;
            case "county" -> county = value;
            case "state" -> state = value;
            case "nearest_town" -> nearest_town = value;
            case "nearest_road" -> nearest_road = value;
            case "details" ->  details = value;
        }

    }

}
