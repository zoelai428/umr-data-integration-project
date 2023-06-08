package org.example;

public class Weather {

    Integer ID;
    Double temperature;
    Double visibility;
    Double humidity;
    Double precipIntensity;
    Double precipType;
    Double cloudCover;
    Double uvIndex;
    Double moonPhase;

    public boolean doesExist() {
        return temperature != null;
    }

    public void setValueForAttribute(String value, String attribute) {

        switch (attribute) {
            case "temperature" -> temperature = Double.parseDouble(value);
            case "visibility" -> visibility = Double.parseDouble(value);
            case "humidity" -> humidity = Double.parseDouble(value);
            case "precip_intensity" -> precipIntensity = Double.parseDouble(value);
            case "precip_type" -> precipType = Double.parseDouble(value);
            case "cloud_cover" -> cloudCover = Double.parseDouble(value);
            case "uv_index" -> uvIndex = Double.parseDouble(value);
            case "moon_phase" ->  moonPhase = Double.parseDouble(value);
        }

    }
}
