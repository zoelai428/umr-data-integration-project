package org.example.Data;

public class Weather {

    public Integer id;
    public Double temperature;
    public Double visibility;
    public Double humidity;
    public Double precip_intensity;
    public String precip_type;
    public Double cloud_cover;
    public Double uv_index;
    public Double moon_phase;

    public String summary;
    public String conditions;

    public boolean doesExist() {
        return temperature != null || visibility != null || humidity != null ||
                precip_intensity != null || precip_type != null  || cloud_cover != null || uv_index != null ||
                moon_phase != null || summary != null || conditions != null;
    }

    public void setValueForAttribute(String value, String attribute) {

        switch (attribute) {
            case "temperature" -> temperature = Tools.parseDouble(value);
            case "visibility" -> visibility = Tools.parseDouble(value);
            case "humidity" -> humidity = Tools.parseDouble(value);
            case "precip_intensity" -> precip_intensity = Tools.parseDouble(value);
            case "precip_type" -> precip_type = value;
            case "cloud_cover" -> cloud_cover = Tools.parseDouble(value);
            case "uv_index" -> uv_index = Tools.parseDouble(value);
            case "moon_phase" ->  moon_phase = Tools.parseDouble(value);
            case "summary" -> summary = value;
            case "conditions" -> conditions = value;
        }

    }
}
