package org.example;

import org.apache.commons.csv.CSVRecord;
import org.example.Data.*;

import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IntegrateData {

    static int bigfootID = 1;
    static int ufoID = 1;
    static int locationID = 1;
    static int reportID = 1;
    static int weatherID = 1;

    public List<String> sources;

    public static List<BigfootSighting> bigfootSightingList = new ArrayList<>();
    public static List<Location> locationList = new ArrayList<>();
    public static List<Report> reportList = new ArrayList<>();
    public static List<UFOSighting> ufoSightingList = new ArrayList<>();
    public static List<Weather> weatherList = new ArrayList<>();


    public IntegrateData(List<String> sources) {
        this.sources = sources;
    }

    public void createDatabaseObjects(CSVRecord record, Matching matching, String entity) throws ParseException {

        BigfootSighting bigfootSighting = new BigfootSighting();
        Location location = new Location();
        Report report = new Report();
        UFOSighting ufoSighting = new UFOSighting();
        Weather weather = new Weather();

        if (entity.equals("bigfoot")) {
            for (Map.Entry<String, String> bigfootAttribute : matching.bigfootAttributes.entrySet()) {
                String value = record.get(bigfootAttribute.getKey());
                bigfootSighting.setValueForAttribute(value, bigfootAttribute.getValue());
            }
        }

        if (entity.equals("ufo")) {
            for (Map.Entry<String, String> ufoAttribute : matching.ufoAttributes.entrySet()) {
                String value = record.get(ufoAttribute.getKey());
                ufoSighting.setValueForAttribute(value, ufoAttribute.getValue());
            }
        }

        for (Map.Entry<String, String> locationAttribute : matching.locationAttributes.entrySet()) {
            String value = record.get(locationAttribute.getKey());
            location.setValueForAttribute(value, locationAttribute.getValue());
        }

        for (Map.Entry<String, String> reportAttribute : matching.reportAttributes.entrySet()) {
            String value = record.get(reportAttribute.getKey());
            report.setValueForAttribute(value, reportAttribute.getValue());
        }

        for (Map.Entry<String, String> weatherAttribute : matching.weatherAttributes.entrySet()) {
            String value = record.get(weatherAttribute.getKey());
            weather.setValueForAttribute(value, weatherAttribute.getValue());
        }

        //Entsprechende IDs setzen
        //TODO Use Skolem Functions?

        //TODO Problem das auch Daten ohne Class existieren können
        if (entity.equals("bigfoot")) {
            bigfootSighting.id = bigfootID;

            if (location.doesExist()) {
                bigfootSighting.location_id = locationID;
            }

            if (report.doesExist()) {
                bigfootSighting.report_id = reportID;
            }

            if (weather.doesExist()) {
                bigfootSighting.weather_id = weatherID;
            }

            bigfootSightingList.add(bigfootSighting);
            bigfootID++;
        }

        if (entity.equals("ufo")) {

            ufoSighting.id = ufoID;

            if (location.doesExist()) {
                ufoSighting.location_id = locationID;
            }

            if (report.doesExist()) {
                ufoSighting.report_id = reportID;
            }

            if (weather.doesExist()) {
                ufoSighting.weather_id = weatherID;
            }

            ufoSightingList.add(ufoSighting);
            ufoID++;
        }

        if (location.doesExist()) {
            location.id = locationID;
            locationList.add(location);
            locationID++;
        }

        if (report.doesExist()) {
            report.id = reportID;
            reportList.add(report);
            reportID++;
        }

        if (weather.doesExist()) {
            weather.id = weatherID;
            weatherList.add(weather);
            weatherID++;
        }
    }

    public void start() throws IOException, SQLException, IllegalAccessException, ParseException {

        for (String source : sources) {

            String entity = source.toLowerCase().contains("ufo") ? "ufo" : "bigfoot";
            Matching matching = new Matching(source, true);

            for (CSVRecord record : CSVTools.readCSV(source)) {
                createDatabaseObjects(record, matching, entity);
            }
        }

        DatabaseConnection.injectDatabaseObjectsInDatabase();
    }


}
