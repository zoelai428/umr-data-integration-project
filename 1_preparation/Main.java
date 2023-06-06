import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.math.BigDecimal;

public class Main {

    private final static String url = "jdbc:postgresql://localhost:5432/dataintegration";
    private final static String username = "postgres";
    private final static String password = "123";

//    static final String DB_URL = "jdbc:postgresql://localhost:5432/northwind";
//    static final String USER = "workshop";
//    static final String PASS = "secret";

    /** This function reads a csv file from the designated folder "..\\umr-data-integration-project\\0_datasets".
     *
     * @param filename should contain the exact file name including ".csv" e.g. "bigfoot1_reports.csv"
     * @return a list of arrays of String extracted directly from the given file
     * @throws IOException
     */
    public static List<String[]> read_csv_file(String filename) throws IOException {
        String filepath = "../umr-data-integration-project/0_datasets/" + filename;
        List<String[]> result = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line = "";
            while((line = br.readLine()) != null) {
                result.add(splitStringByCommas(line));
            }
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: File not found " + filename);
        } catch (IOException e) {
            System.err.println("ERROR: Could not read " + filename);
        }
        return result;
    }


    /** This function helps to preserve commas stated within double-quotation marks.
     * Only commas outside of quotation marks would be split into String array elements.
     *
     * @param input the String to be split by commas
     * @return a String array that stores the splitted substrings from input
     */
    public static String[] splitStringByCommas(String input) {
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (c == '\"') {
                inQuotes = !inQuotes;
                sb.append(c);
            } else if (c == ',' && !inQuotes) {
                result.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        if (sb.length() > 0) {
            result.add(sb.toString().trim());
        }
        return result.toArray(new String[0]);
    }




    /** This function reads a json file from the designated folder "..\\umr-data-integration-project\\0_datasets".
     *
     * @param filename should contain the exact file name including ".json" e.g. "ufo1_nuforc_reports.json"
     * @param header has to be an array of Strings which contain the corresponding headers of the dataset
     * @return a list of arrays of String extracted directly from the given file
     */

    // /umr-data-integration-project/0_datasets/bigfoot1_reports.csv

    public static List<String[]> read_json_file(String filename, String[] header) {
        BufferedReader br = null;
        JSONParser parser = new JSONParser();
        String filepath = "../umr-data-integration-project/0_datasets/" + filename;
        List<String[]> result = new ArrayList<>();
        result.add(header);
        try {
            br = new BufferedReader(new FileReader(filepath));
            String line;
            while ((line = br.readLine()) != null) {
                Object obj;
                try {
                    obj = parser.parse(line);
                    JSONObject jsonObject = (JSONObject) obj;

                    String[] array = new String[header.length];
                    for (int i = 0; i < header.length; i++) {
                        array[i] = (String) jsonObject.get(header[i]);
                    }
                    result.add(array);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    /** This function reads an xlsx file from the designated folder "..\\umr-data-integration-project\\0_datasets".
     *
     * @param filename should contain the exact file name including ".xlsx" e.g. "bigfoot4_DataDNA_Dataset_Challenge-February_2023.xlsx"
     * @return a list of arrays of String extracted directly from the given file
     */
    public static List<String[]> read_xlsx_file(String filename) {
        String filepath = "../umr-data-integration-project/0_datasets/" + filename;
        List<String[]> result = new ArrayList<>();
        Boolean firstRow = true;
        int columnsCount = 0;
        try {
            FileInputStream fis = new FileInputStream(new File(filepath));
            XSSFWorkbook wb = new XSSFWorkbook(fis);
            XSSFSheet sheet = wb.getSheetAt(0);
            Iterator<Row> itr = sheet.iterator();
            while (itr.hasNext()) {
                Row row = itr.next();
                String[] array = {};
                if (!firstRow) {
                    array = new String[columnsCount];
                }
                Iterator<Cell> cellIterator = row.cellIterator();
                int index = 0;
                while (cellIterator.hasNext()) {
                    if (firstRow) {
                        array = Arrays.copyOf(array, ++columnsCount);
                    }
                    Cell cell = cellIterator.next();
                    switch (cell.getCellTypeEnum()) {
                        case STRING:
                            array[index++] = cell.getStringCellValue();
                            break;
                        case NUMERIC:
                            array[index++] = String.valueOf(cell.getNumericCellValue());
                            break;
                        default:
                    }
                }
                result.add(array);
                if (firstRow) {firstRow = false;}
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public static void main(String[] args) throws IOException {
        // define headers for json files
        String[] header_ufo1 = {"text", "stats", "date_time", "report_link", "city", "state", "country", "shape", "duration", "summary", "posted"};
        String[] header_bigfoot2 = {"YEAR", "SEASON", "MONTH", "DATE", "STATE", "COUNTY", "LOCATION_DETAILS", "NEAREST_TOWN", "NEAREST_ROAD", "OBSERVED", "ALSO_NOTICED", "OTHER_WITNESSES", "OTHER_STORIES", "TIME_AND_CONDITIONS", "ENVIRONMENT", "REPORT_NUMBER", "REPORT_CLASS"};

        // read files
        List<String[]> file_bigfoot1 = read_csv_file("bigfoot1_reports.csv");
        List<String[]> file_bigfoot2_locations = read_csv_file("bigfoot2_bfro_locations.csv");
        List<String[]> file_bigfoot2_reports = read_json_file("bigfoot2_bfro_reports.json", header_bigfoot2);
        List<String[]> file_bigfoot2_reports_geocoded = read_csv_file("bigfoot2_bfro_reports_geocoded.csv");
        List<String[]> file_bigfoot3 = read_csv_file("bigfoot3_Bigfoot_Sightings.csv");
        List<String[]> file_bigfoot4 = read_xlsx_file("bigfoot4_DataDNA_Dataset_Challenge-February_2023.xlsx");
        List<String[]> file_ufo1_csv = read_csv_file("ufo1_nuforc_reports.csv");
        List<String[]> file_ufo1_json = read_json_file("ufo1_nuforc_reports.json", header_ufo1);
        List<String[]> file_ufo2 = read_csv_file("ufo2_ufo_sighting_data.csv");
        List<String[]> file_ufo3 = read_csv_file("ufo3_nuforc_reports.csv");

        // check sizes
        System.out.println("bigfoot1 size: " + file_bigfoot1.size());
        System.out.println("bigfoot2_locations size: " + file_bigfoot2_locations.size());
        System.out.println("bigfoot2_reports size: " + file_bigfoot2_reports.size());
        System.out.println("bigfoot2_reports_geocoded size: " + file_bigfoot2_reports_geocoded.size());
        System.out.println("bigfoot3 size: " + file_bigfoot3.size());
        System.out.println("bigfoot4 size: " + file_bigfoot4.size());
        System.out.println("ufo1_csv size: " + file_ufo1_csv.size());
        System.out.println("ufo1_json size: " + file_ufo1_json.size());
        System.out.println("ufo2 size: " + file_ufo2.size());
        System.out.println("ufo3 size: " + file_ufo3.size());

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            int report_id = 10000000, weather_id = 20000000, location_id = 30000000, ufo_id = 40000000, bigfoot_id = 50000000;
            PreparedStatement statement_report = connection.prepareStatement("INSERT INTO report VALUES (?, ?, ?, ?, ?, ?)");
            PreparedStatement statement_ufo = connection.prepareStatement("INSERT INTO ufo_sighting VALUES (?, ?, ?, ?, ?)");
            PreparedStatement statement_bigfoot = connection.prepareStatement("INSERT INTO bigfoot_sighting VALUES (?, ?, ?, ?, ?)");
            PreparedStatement statement_weather = connection.prepareStatement("INSERT INTO weather VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            PreparedStatement statement_location = connection.prepareStatement("INSERT INTO location VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

            // import bigfoot1 to SQL
            for (int i = 1; i < file_bigfoot1.size(); i++) {
                String[] record = file_bigfoot1.get(i);
                statement_report.setInt(1, report_id);
//                statement_report.setTimestamp(2, Timestamp.valueOf(record[3])); // Fehler
                statement_report.setTimestamp(2, null); // zuerst null setzen
                statement_report.setString(3, record[22]);
                statement_report.setString(4, record[0]);
                statement_report.setString(5, record[4]);
                statement_report.setString(6, record[13]);
                statement_report.executeUpdate();

                statement_weather.setInt(1, weather_id);
                for (int j = 2; j < 10; j++) {
                    if (j != 6)
                        statement_weather.setBigDecimal(j, null);
                }
                statement_weather.setArray(6, null);
                statement_weather.executeUpdate();

                statement_location.setInt(1, location_id);
                statement_location.setBigDecimal(2, null);
                statement_location.setBigDecimal(3, null);
                statement_location.setString(4, null);
                statement_location.setString(5, record[9]);
                statement_location.setString(6, record[8]);
                statement_location.setString(7, record[11]);
                statement_location.setString(8, record[12]);
                statement_location.setString(9, record[10]);
                statement_location.executeUpdate();

                statement_bigfoot.setInt(1, bigfoot_id);
                if (record[2].length() == 0)
                    statement_bigfoot.setString(2, null);
                else
                    statement_bigfoot.setString(2, record[2].substring(record[2].length()-1));
                statement_bigfoot.setInt(3, report_id);
                statement_bigfoot.setInt(4, weather_id);
                statement_bigfoot.setInt(5, location_id);
                statement_bigfoot.executeUpdate();

                report_id++;
                weather_id++;
                location_id++;
                bigfoot_id++;
            }

            // import bigfoot2_locations to SQL
            for (int i = 1; i < file_bigfoot2_locations.size(); i++) {
                String[] record = file_bigfoot2_locations.get(i);
                statement_report.setInt(1, report_id);
//                statement_report.setTimestamp(2, Timestamp.valueOf(record[3]));
                statement_report.setTimestamp(2, null);
                statement_report.setString(3, null);
                statement_report.setString(4, null);
                statement_report.setString(5, record[1]);
                statement_report.setString(6, null);
                statement_report.executeUpdate();

                statement_weather.setInt(1, weather_id);
                for (int j = 2; j < 10; j++) {
                    if (j != 6)
                        statement_weather.setBigDecimal(j, null);
                }
                statement_weather.setArray(6, null);
                statement_weather.executeUpdate();

                statement_location.setInt(1, location_id);
                statement_location.setDouble(2, Double.valueOf(record[5]));
                statement_location.setDouble(3, Double.valueOf(record[4]));
                for (int j = 4; j < 10; j++) {
                    statement_location.setString(j, null);
                }
                statement_location.executeUpdate();

                statement_bigfoot.setInt(1, bigfoot_id);
                statement_bigfoot.setString(2, record[2].substring(record[2].length()-1));
                statement_bigfoot.setInt(3, report_id);
                statement_bigfoot.setInt(4, weather_id);
                statement_bigfoot.setInt(5, location_id);
                statement_bigfoot.executeUpdate();

                report_id++;
                weather_id++;
                location_id++;
                bigfoot_id++;
            }

            // import bigfoot2_reports to SQL
//            for (int i = 1; i < file_bigfoot2_reports.size(); i++) {
//                String[] record = file_bigfoot2_reports.get(i);
//                statement_report.setInt(1, report_id);
//
//
//                report_id++;
//                location_id++;
//            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        try {
            Connection connection = DriverManager.getConnection(url, username, password);

            // Create a statement object
            Statement statement = connection.createStatement();

            // Execute a query
            ResultSet resultSet = statement.executeQuery("SELECT * FROM report ORDER BY report_id DESC LIMIT 5");

            // Process the results
            while (resultSet.next()) {
                // Access the data from the result set
                int id = resultSet.getInt("report_id");
                String headline = resultSet.getString("headline");

                // Do something with the retrieved data
                System.out.println("report_id: " + id + "; headline: " + headline);
            }

            // Close the result set, statement, and connection
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            // Handle any exceptions
            e.printStackTrace();
        }






    }
}
