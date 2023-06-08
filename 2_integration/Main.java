package org.example;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.text.similarity.JaccardSimilarity;
import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.simmetrics.StringMetric;
import org.simmetrics.metrics.StringMetrics;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    private static final String URL = "jdbc:postgresql://localhost:5432/dataintegration";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "";

    static int bigfootID = 1;
    static int ufoID = 1;
    static int locationID = 1;
    static int reportID = 1;
    static int weatherID = 1;

    public static void main(String[] args) throws SQLException, IOException {


       //List of all File Sources to be added to the Library
        importFilesToDatabase(List.of(""));

    }


    /** Returns all Attribute Names for a given Relation
     * @param tableName relation
     * @return List of all Attributes
     * @throws SQLException
     */
    public static List<String> returnColumnNames(String tableName) throws SQLException {

        Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        DatabaseMetaData metaData = connection.getMetaData();
        List<String> attributes = new ArrayList<>();
        ResultSet rs = metaData.getColumns(null, null, tableName, "%");

        while(rs.next()) {
            attributes.add(rs.getString(4));
        }

        return attributes;
    }

    public static void importFilesToDatabase(List<String> sources) throws SQLException, IOException {

        for(String source : sources) {
            String entity = source.toLowerCase().contains("ufo") ? "ufo" : "bigfoot";
            importFile(source, entity);
        }

    }

    public static void importFile(String path, String entity) throws IOException, SQLException {

        BigfootSighting bigfootSighting;
        Location location;
        Report report;
        UFOSighting ufoSighting;
        Weather weather;

        Reader reader = new FileReader(path);

        int dotIndex = path.lastIndexOf('.');
        String fileEnding = path.substring(dotIndex + 1);

        CSVFormat csvFormat;

        if(fileEnding.equals("xlsx")) {
            csvFormat = CSVFormat.EXCEL.builder()
                    .setHeader()
                    .setSkipHeaderRecord(false)
                    .build();
        } else {
            csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(false)
                    .build();
        }


        CSVParser csvParser = new CSVParser(reader, csvFormat);
        List<String> header = csvParser.getHeaderNames();
        Iterable<CSVRecord> records = csvParser.getRecords();

        List<BigfootSighting> bigfootSightingList = new ArrayList<>();
        List<Location> locationList = new ArrayList<>();
        List<Report> reportList = new ArrayList<>();
        List<UFOSighting> ufoSightingList = new ArrayList<>();
        List<Weather> weatherList = new ArrayList<>();

        //Mapping of Table Attribute to Database Attribute
        Map<String, String> bigfootAttributes = getAllMatchingAttributes(header, returnColumnNames("bigfoot_sighting"));
        Map<String, String> locationAttributes = getAllMatchingAttributes(header, returnColumnNames("location"));
        Map<String, String> reportAttributes = getAllMatchingAttributes(header, returnColumnNames("report"));
        Map<String, String> ufoAttributes = getAllMatchingAttributes(header, returnColumnNames("ufo_sighting"));
        Map<String, String> weatherAttributes = getAllMatchingAttributes(header, returnColumnNames("weather"));

        for(CSVRecord record : records) {

            bigfootSighting = new BigfootSighting();
            location = new Location();
            report = new Report();
            ufoSighting = new UFOSighting();
            weather = new Weather();

            //IDs nach den Matchings setzen

            if(entity.equals("bigfoot")) {
                for(Map.Entry<String, String> bigfootAttribute : bigfootAttributes.entrySet()) {
                    String value = record.get(bigfootAttribute.getKey());
                    bigfootSighting.setValueForAttribute(value, bigfootAttribute.getValue());
                }
            }

            if(entity.equals("ufo")) {
                for(Map.Entry<String, String> ufoAttribute : ufoAttributes.entrySet()) {
                    String value = record.get(ufoAttribute.getKey());
                    ufoSighting.setValueForAttribute(value, ufoAttribute.getValue());
                }
            }

            for(Map.Entry<String, String> locationAttribute : locationAttributes.entrySet()) {
                String value = record.get(locationAttribute.getKey());
                location.setValueForAttribute(value, locationAttribute.getValue());
            }

            for(Map.Entry<String, String> reportAttribute : reportAttributes.entrySet()) {
                String value = record.get(reportAttribute.getKey());
                report.setValueForAttribute(value, reportAttribute.getValue());
            }

            for(Map.Entry<String, String> weatherAttribute : weatherAttributes.entrySet()) {
                String value = record.get(weatherAttribute.getKey());
                weather.setValueForAttribute(value, weatherAttribute.getValue());
            }

            //Entsprechende IDs setzen

            if(bigfootSighting.doesExist()) {
                bigfootSighting.ID = bigfootID;

                if(location.doesExist()) {
                    bigfootSighting.locationID = locationID;
                }

                if(report.doesExist()) {
                    bigfootSighting.reportID = reportID;
                }

                if(weather.doesExist()) {
                    bigfootSighting.weatherID = weatherID;
                }

                bigfootSightingList.add(bigfootSighting);
                bigfootID++;
            }

            if(ufoSighting.doesExist()) {

                ufoSighting.ID = ufoID;

                if(location.doesExist()) {
                    ufoSighting.locationID = locationID;
                }

                if(report.doesExist()) {
                    ufoSighting.reportID = reportID;
                }

                if(weather.doesExist()) {
                    ufoSighting.weatherID = weatherID;
                }

                ufoSightingList.add(ufoSighting);
                ufoID++;
            }

            if(location.doesExist()) {
                location.ID = locationID;
                locationList.add(location);
                locationID++;
            }

            if(report.doesExist()) {
                report.ID = reportID;
                reportList.add(report);
                reportID++;
            }

            if(weather.doesExist()) {
                weather.ID = weatherID;
                weatherList.add(weather);
                weatherID++;
            }

        }

        addObjectsToDatabase(ufoSightingList, bigfootSightingList, locationList, weatherList, reportList);
    }

    public static void addObjectsToDatabase(List<UFOSighting> ufoList, List<BigfootSighting> bigfootList, List<Location> locationList, List<Weather> weatherList, List<Report> reportList) throws SQLException {

        Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

        PreparedStatement bigfootStatement = connection.prepareStatement("INSERT INTO bigfoot_sighting values (?, ?, ?, ?, ?)");

        for(BigfootSighting bs : bigfootList) {
            bigfootStatement.setInt(1, bs.ID);

            if(bs.sightingClass == null) {
                bigfootStatement.setNull(2, java.sql.Types.VARCHAR);
            } else {
                bigfootStatement.setString(2, bs.sightingClass);
            }

            if(bs.reportID == null) {
                bigfootStatement.setNull(3, Types.INTEGER);
            } else {
                bigfootStatement.setInt(3, bs.reportID);
            }

            if(bs.weatherID == null) {
                bigfootStatement.setNull(4, Types.INTEGER);
            } else {
                bigfootStatement.setInt(4, bs.weatherID);
            }

            if(bs.locationID == null) {
                bigfootStatement.setNull(5, Types.INTEGER);
            } else {
                bigfootStatement.setInt(5, bs.locationID);
            }

            bigfootStatement.addBatch();
        }






        PreparedStatement ufoStatement = connection.prepareStatement("INSERT INTO ufo_sighting values (?, ?, ?, ?, ?)");
        PreparedStatement locationStatement = connection.prepareStatement("INSERT INTO location values  (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        PreparedStatement reportStatement = connection.prepareStatement("INSERT INTO report values (?, ?, ?, ?, ?)");
        PreparedStatement weatherStatement = connection.prepareStatement("INSERT INTO  weather values (?, ?, ?, ?, ?, ?, ?, ?, ?)");


        bigfootStatement.executeBatch();

        for(UFOSighting us : ufoList) {

            ufoStatement.setInt(1, us.ID);

            if(us.duration == null) {
                ufoStatement.setNull(2, Types.DOUBLE);
            } else {
                ufoStatement.setDouble(2, us.duration);
            }

            if(us.reportID == null) {
                ufoStatement.setNull(3, Types.INTEGER);
            } else {
                ufoStatement.setInt(3, us.reportID);
            }

            if(us.weatherID == null) {
                ufoStatement.setNull(4, Types.INTEGER);
            } else {
                ufoStatement.setInt(4, us.weatherID);
            }

            if(us.locationID == null) {
                ufoStatement.setNull(5, Types.INTEGER);
            } else {
                ufoStatement.setInt(5, us.locationID);
            }

            ufoStatement.addBatch();
        }

        ufoStatement.executeBatch();

    }

    public static Map<String, String> getAllMatchingAttributes(List<String> attributes1, List<String> attributes2) {

        double threshold = 0.75;

        Map<String, String> matchingAttributes = new HashMap<>();

        for(String attribute1 : attributes1) {
            for(String attribute2 : attributes2) {

                double similarity = calcSimilarity(attribute1, attribute2);

                if(similarity > threshold) {
                    matchingAttributes.putIfAbsent(attribute1, attribute2);
                }

            }
        }
        return matchingAttributes;
    }

    //We found that Hybrid Similarity Measures, especially Monge-Elkan to perform quite well with a Threshold of 0.75

    private static double calcSimilarity(String s1, String s2) {

        double score1 = StringMetrics.cosineSimilarity().compare(s1, s2);
        double score2 = StringMetrics.mongeElkan().compare(s1, s2);
        double score3 = StringMetrics.dice().compare(s1, s2);
        double score4 = StringMetrics.generalizedJaccard().compare(s1, s2);

        return score2;
    }

}