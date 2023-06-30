package org.example.Data;

import org.example.Matching;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UFOSighting {

    public Integer id;
    public Integer duration;
    public Integer report_id;
    public Integer weather_id;
    public Integer location_id;

    public void setValueForAttribute(String value, String attribute) {


        if (attribute.equals("duration")) {

            if(onlyIncludesNumbers(value)) { //Dann handelt es sich um Sekunden

                this.duration = Integer.parseInt(value);

            } else { //Ansonsten Werte wie 30 Sekunden
                Pattern pattern = Pattern.compile("\\d+(\\.\\d+)?");
                Matcher matcher = pattern.matcher(value);
                String number = null;

                while (matcher.find()) {
                    number = matcher.group();
                }

                if (number == null) {
                    return;
                }

                if (value.toLowerCase().contains("sec")) {

                    double duration = Double.parseDouble(number);
                    this.duration = (int) Math.round(duration);

                } else if (value.toLowerCase().contains("min")) {
                    double duration = Double.parseDouble(number);
                    duration = duration * 60;
                    this.duration = (int) Math.round(duration);

                } else if (value.toLowerCase().contains("hour")) {
                    double duration = Double.parseDouble(number);
                    duration = duration * 3600;
                    this.duration = (int) Math.round(duration);
                }
            }

        }

    }

    private boolean onlyIncludesNumbers(String value) {
        return value.matches("\\d+");
    }

}
