package org.example.Data;

public class BigfootSighting {

    public Integer id;
    public String classification;
    public Integer report_id;
    public Integer weather_id;
    public Integer location_id;

    public void setValueForAttribute(String value, String attribute) {

        if (attribute.equals("classification")) {
            classification = value;
        }

    }

}
