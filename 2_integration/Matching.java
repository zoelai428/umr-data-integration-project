package org.example;

import org.simmetrics.metrics.StringMetrics;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Matching {

    public Map<String, String> bigfootAttributes = new HashMap<>();
    public Map<String, String> locationAttributes = new HashMap<>();
    public Map<String, String> reportAttributes = new HashMap<>();
    public Map<String, String> ufoAttributes = new HashMap<>();
    public Map<String, String> weatherAttributes = new HashMap<>();


    public Matching(String source, boolean manual) throws SQLException, IOException {

        if(manual) {
            loadManualMappings(source);
        } else {
            loadAutomaticMappings(source);
        }
    }


    //S
    public void loadManualMappings(String filename) {

        bigfootAttributes = new HashMap<>();
        locationAttributes = new HashMap<>();
        reportAttributes = new HashMap<>();
        ufoAttributes = new HashMap<>();
        weatherAttributes = new HashMap<>();

        if(filename.toLowerCase().contains("bigfoot1_reports.csv")) {
            //report
            reportAttributes.put("CombinedDate", "date");
            reportAttributes.put("Author", "author");
            reportAttributes.put("Headline", "headline");
            reportAttributes.put("CombinedDescription", "description");

            //location
            locationAttributes.put("County", "county");
            locationAttributes.put("State", "state");
            locationAttributes.put("Nearest Town", "nearest_town");
            locationAttributes.put("Nearest Road", "nearest_road");
            locationAttributes.put("Location Details", "details");
            locationAttributes.put("Environment", "details");

            //bigfoot_sighting
            bigfootAttributes.put("Class", "classification");

        } else if(filename.toLowerCase().contains("bigfoot2_bfro_locations.csv")) {
            //report
            reportAttributes.put("timestamp", "date");
            reportAttributes.put("title", "headline");

            //location
            locationAttributes.put("longitude", "longitude");
            locationAttributes.put("latitude", "latitude");

            //bigfoot
            bigfootAttributes.put("classification", "classification");

        } else if(filename.toLowerCase().contains("bigfoot2_bfro_reports_geocoded.csv")) {
            //report
            reportAttributes.put("date", "date");
            reportAttributes.put("title", "headline");
            reportAttributes.put("observed", "description");

            //weather
            weatherAttributes.put("temperature_mid", "temperature");
            weatherAttributes.put("visibility", "visibility");
            weatherAttributes.put("humidity", "humidity");
            weatherAttributes.put("precip_intensity", "precip_intensity");
            weatherAttributes.put("precip_type", "precip_type");
            weatherAttributes.put("cloud_cover", "cloud_cover");
            weatherAttributes.put("uv_index", "uv_index");
            weatherAttributes.put("moon_phase", "moon_phase");
            weatherAttributes.put("summary", "summary");
            weatherAttributes.put("conditions", "conditions");

            //location
            locationAttributes.put("longitude", "longitude");
            locationAttributes.put("latitude", "latitude");
            locationAttributes.put("county", "county");
            locationAttributes.put("state", "state");
            locationAttributes.put("location_details", "details");

            //bigfoot
            bigfootAttributes.put("classification", "classification");

        } else if(filename.toLowerCase().contains("bigfoot3_bigfoot_sightings.csv")) {
            //report
            reportAttributes.put("TimeWhen", "date");
            reportAttributes.put("Descr", "headline");

            //location
            locationAttributes.put("X", "longitude");
            locationAttributes.put("Y", "latitude");

            //bigfoot
            bigfootAttributes.put("Class", "classification");

        } else if(filename.contains("ufo1_nuforc_reports.csv")) {
            //report
            reportAttributes.put("date_time", "date");
            reportAttributes.put("summary", "headline");
            reportAttributes.put("text", "description");

            //location
            locationAttributes.put("city_latitude", "latitude");
            locationAttributes.put("city_longitude", "longitude");
            locationAttributes.put("country", "country");
            locationAttributes.put("state", "state");
            locationAttributes.put("city", "nearest_town");

            //ufo
            ufoAttributes.put("duration", "duration");

        } else if(filename.contains("ufo2_ufo_sighting_data.csv")) {
            //report
            reportAttributes.put("Date_time", "date");
            reportAttributes.put("description", "description");

            //location
            locationAttributes.put("longitude", "longitude");
            locationAttributes.put("latitude", "latitude");
            locationAttributes.put("country", "country");
            locationAttributes.put("state/province", "state");
            locationAttributes.put("city", "nearest_town");

            //ufo
            ufoAttributes.put("length_of_encounter_seconds", "duration");

        } else if(filename.toLowerCase().contains("ufo3_nuforc_reports.csv")) {
            //report
            reportAttributes.put("date_time", "date");
            reportAttributes.put("summary", "headline");
            reportAttributes.put("text", "description");

            //location
            locationAttributes.put("country", "country");
            locationAttributes.put("state", "state");
            locationAttributes.put("city", "nearest_town");

            //ufo_sighting
            ufoAttributes.put("duration", "duration");
        }


    }

    //TODO Calc Similarity by comparing value sets
    //TODO Calc Similarity by comparing Metadata (Similarity Flooding)
    //TODO COMA ausprobieren?
    //TODO Similarity Matrix -> Best Matching by Greedy/Max Weighted Sum (Hungarian Method)


    public void loadAutomaticMappings(String filename) throws SQLException, IOException {

        List<String> header = CSVTools.getHeader(filename);

        this.bigfootAttributes = getAllMatchingAttributes(header, DatabaseConnection.returnColumnNames("bigfoot_sighting"));
        this.locationAttributes = getAllMatchingAttributes(header, DatabaseConnection.returnColumnNames("location"));
        this.reportAttributes = getAllMatchingAttributes(header, DatabaseConnection.returnColumnNames("report"));
        this.ufoAttributes = getAllMatchingAttributes(header, DatabaseConnection.returnColumnNames("ufo_sighting"));
        this.weatherAttributes = getAllMatchingAttributes(header, DatabaseConnection.returnColumnNames("weather"));

    }

    //attribut1 -> attribut2

    private Map<String, String> getAllMatchingAttributes(List<String> attributeList1, List<String> attributeList2) {

        double threshold = 0.75;

        Map<String, String> matchingAttributes = new HashMap<>();

        for (String attribute1 : attributeList1) {
            for (String attribute2 : attributeList2) {

                double similarity = calcSimilarity(attribute1, attribute2);

                if (similarity > threshold) {
                    matchingAttributes.putIfAbsent(attribute1, attribute2);
                }

            }
        }
        return matchingAttributes;

    }

    private static double calcSimilarity(String s1, String s2) {

        double score1 = StringMetrics.cosineSimilarity().compare(s1, s2);
        double score2 = StringMetrics.mongeElkan().compare(s1, s2);
        double score3 = StringMetrics.dice().compare(s1, s2);
        double score4 = StringMetrics.generalizedJaccard().compare(s1, s2);

        return score2;
    }

}
