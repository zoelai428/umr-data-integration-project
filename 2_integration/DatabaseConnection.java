package org.example;

import org.example.Data.*;
import org.simmetrics.metrics.Levenshtein;
import stage3.Relation;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DatabaseConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/dataintegration";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "123";
    private static final double NUMERICAL_DIFF_THRESHOLD = 5.0;
    private static final int STRING_EDIT_THRESHOLD = 2;


    public static List<String> returnColumnNames(String tableName) throws SQLException {

        Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        DatabaseMetaData metaData = connection.getMetaData();
        List<String> attributes = new ArrayList<>();
        ResultSet rs = metaData.getColumns(null, null, tableName, "%");

        while (rs.next()) {
            attributes.add(rs.getString(4));
        }

        return attributes;
    }

    public static void injectDatabaseObjectsInDatabase() throws SQLException, IllegalAccessException {

        Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

        PreparedStatement bigfootStatement = connection.prepareStatement("INSERT INTO bigfoot_sighting values (?, ?, ?, ?, ?)");
        PreparedStatement locationStatement = connection.prepareStatement("INSERT INTO location values  (?, ?, ?, ?, ?, ?, ?, ?, ?)");
        PreparedStatement reportStatement = connection.prepareStatement("INSERT INTO report values (?, ?, ?, ?, ?)");
        PreparedStatement weatherStatement = connection.prepareStatement("INSERT INTO  weather values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        PreparedStatement ufoStatement = connection.prepareStatement("INSERT INTO ufo_sighting values (?, ?, ?, ?, ?)");

        for (BigfootSighting bigfootSighting : IntegrateData.bigfootSightingList) {
            setParameters(bigfootStatement, bigfootSighting);
        }
        bigfootStatement.executeBatch();

        for (Location location : IntegrateData.locationList) {
            setParameters(locationStatement, location);
        }
        locationStatement.executeBatch();

        for (Report report : IntegrateData.reportList) {
            setParameters(reportStatement, report);
        }
        reportStatement.executeBatch();

        for (Weather weather : IntegrateData.weatherList) {
            setParameters(weatherStatement, weather);
        }
        weatherStatement.executeBatch();

        for (UFOSighting ufoSighting : IntegrateData.ufoSightingList) {
            setParameters(ufoStatement, ufoSighting);
        }
        ufoStatement.executeBatch();

    }

    public static void setParameters(PreparedStatement statement, Object object) throws IllegalAccessException, SQLException {

        Field[] fields = object.getClass().getDeclaredFields();

        for(int i = 0; i < fields.length; i++) {

            Field field = fields[i];

            if(field.getType() == Integer.class) {

                Integer value = (Integer) field.get(object);

                if(value == null) {
                    statement.setNull(i + 1, Types.INTEGER);
                } else {
                    statement.setInt(i + 1, value);
                }


            } else if(field.getType() == Double.class) {

                Double value = (Double) field.get(object);

                if(value == null) {
                    statement.setNull(i + 1, Types.DOUBLE);
                } else {
                    statement.setDouble(i + 1, value);
                }

            } else if(field.getType() == String.class) {

                String value = (String) field.get(object);

                if(value == null) {
                    statement.setNull(i + 1, Types.VARCHAR);
                } else {
                    statement.setString(i + 1, value);
                }

            } else if(field.getType() == java.util.Date.class)  {

                java.util.Date value = (java.util.Date) field.get(object);

                if(value == null) {
                    statement.setNull(i + 1, Types.TIMESTAMP);
                } else {
                    java.sql.Date sqlDate = new java.sql.Date(value.getTime());
                    statement.setDate(i + 1, sqlDate);
                }

            } else {
                throw new UnsupportedOperationException("Type is not declared");
            }
        }

        statement.addBatch();
    }

    public static void printNumRecords(List<Relation> relationList) throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Statement statement = connection.createStatement();
        System.out.println("TOTAL NUMBER OF RECORDS");
        System.out.println("-----------------------");
        for (Relation relation : relationList) {
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM " + relation.getName());
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("# " + relation.getName() + ": " + count);
            }
        }
        System.out.println();
    }

    public static void printNumEmptyRecords(List<Relation> relationList, Set<String> empty_but_not_null) throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Statement statement = connection.createStatement();
        System.out.println("NUMBER OF NULL/EMPTY RECORDS");
        System.out.println("----------------------------");
        for (Relation relation : relationList) {
            String condition = " WHERE ";
            for (String attribute : relation.getAttributes()) {
                if (!condition.equals(" WHERE "))
                    condition = condition + "AND ";
                if (!attribute.equals("id")) {
                    if (empty_but_not_null.contains(attribute))
                        condition = condition + attribute + "=\'\' ";
                    else condition = condition + attribute + " IS NULL ";
                }
            }
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM " + relation.getName() + condition);
            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("# " + relation.getName() + ": " + count);
            }
        }
        System.out.println();
    }

    public static void removeEmptyRecords(List<Relation> relationList, Set<String> empty_but_not_null) throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Statement statement = connection.createStatement();
        for (Relation relation : relationList) {
            String condition = " WHERE ";
            for (String attribute : relation.getAttributes()) {
                if (!condition.equals(" WHERE "))
                    condition = condition + "AND ";
                if (!attribute.equals("id")) {
                    if (empty_but_not_null.contains(attribute))
                        condition = condition + attribute + "=\'\' ";
                    else condition = condition + attribute + " IS NULL ";
                }
            }
            int rowsAffected = statement.executeUpdate("DELETE FROM " + relation.getName() + condition);
            System.out.println(rowsAffected + " record(s) deleted successfully from table \"" + relation.getName() + "\".");
        }
    }


    public static void removeDuplicatesWeather() throws SQLException {
        Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Statement statement = connection.createStatement();
        String selectQuery = "SELECT * FROM weather";
        ResultSet resultSet = statement.executeQuery(selectQuery);

        List<Integer> duplicateIds = new ArrayList<>();
        Levenshtein levenshtein = new Levenshtein();

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            double temperature = resultSet.getDouble("temperature");
            double visibility = resultSet.getDouble("visibility");
            double humidity = resultSet.getDouble("humidity");
            double precip_intensity = resultSet.getDouble("precip_intensity");
            String precip_type = resultSet.getString("precip_type");
            double cloud_cover = resultSet.getDouble("cloud_cover");
            double uv_index = resultSet.getDouble("uv_index");
            double moon_phase = resultSet.getDouble("moon_phase");
            String summary = resultSet.getString("summary");
            String conditions = resultSet.getString("conditions");

            // Compare the current record with the previously processed records
            boolean isDuplicate = false;

            for (int i = 0; i < duplicateIds.size(); i++) {
                int duplicateId = duplicateIds.get(i);
                if (isDuplicateRecord(statement, duplicateId, temperature, visibility, humidity,
                        precip_intensity, precip_type, cloud_cover, uv_index, moon_phase, summary, conditions,
                        levenshtein)) {
                    isDuplicate = true;
                    break;
                }
            }

            if (isDuplicate) {
                duplicateIds.add(id); // Add the current record's ID to the duplicate list
                deleteDuplicateRecord(statement, id); // Remove the duplicate record
            }
        }

        System.out.println("Duplicates removed successfully.");
    }

    private static boolean isDuplicateRecord(Statement statement, int duplicateId, double temperature,
                                             double visibility, double humidity, double precip_intensity,
                                             String precip_type, double cloud_cover, double uv_index,
                                             double moon_phase, String summary, String conditions,
                                             Levenshtein levenshtein) throws SQLException {
        // Fetch the values of the duplicate record using the duplicateId
        String selectQuery = "SELECT * FROM weather WHERE id = " + duplicateId;
        ResultSet resultSet = statement.executeQuery(selectQuery);

        if (resultSet.next()) {
            double duplicateTemperature = resultSet.getDouble("temperature");
            double duplicateVisibility = resultSet.getDouble("visibility");
            double duplicateHumidity = resultSet.getDouble("humidity");
            double duplicatePrecipIntensity = resultSet.getDouble("precip_intensity");
            String duplicatePrecipType = resultSet.getString("precip_type");
            double duplicateCloudCover = resultSet.getDouble("cloud_cover");
            double duplicateUvIndex = resultSet.getDouble("uv_index");
            double duplicateMoonPhase = resultSet.getDouble("moon_phase");
            String duplicateSummary = resultSet.getString("summary");
            String duplicateConditions = resultSet.getString("conditions");

            // Compare the values for duplicate detection
            if (Math.abs(temperature - duplicateTemperature) <= NUMERICAL_DIFF_THRESHOLD
                    && Math.abs(visibility - duplicateVisibility) <= NUMERICAL_DIFF_THRESHOLD
                    && Math.abs(humidity - duplicateHumidity) <= NUMERICAL_DIFF_THRESHOLD
                    && Math.abs(precip_intensity - duplicatePrecipIntensity) <= NUMERICAL_DIFF_THRESHOLD
                    && levenshtein.distance(precip_type, duplicatePrecipType) <= STRING_EDIT_THRESHOLD
                    && Math.abs(cloud_cover - duplicateCloudCover) <= NUMERICAL_DIFF_THRESHOLD
                    && Math.abs(uv_index - duplicateUvIndex) <= NUMERICAL_DIFF_THRESHOLD
                    && Math.abs(moon_phase - duplicateMoonPhase) <= NUMERICAL_DIFF_THRESHOLD
                    && levenshtein.distance(summary, duplicateSummary) <= STRING_EDIT_THRESHOLD
                    && levenshtein.distance(conditions, duplicateConditions) <= STRING_EDIT_THRESHOLD) {
                return true; // The records are duplicates
            }
        }

        return false; // The records are not duplicates
    }

    private static void deleteDuplicateRecord(Statement statement, int id) throws SQLException {
        // Delete the duplicate record
        String deleteQuery = "DELETE FROM weather WHERE id = " + id;
        statement.executeUpdate(deleteQuery);
    }


}
