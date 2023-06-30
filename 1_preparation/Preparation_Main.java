import com.opencsv.CSVReader;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Preparation_Main {

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

        try (CSVReader reader = new CSVReader(new FileReader(filepath))) {
            String[] line;

            while ((line = reader.readNext()) != null) {
                result.add(line);
            }
        } catch (FileNotFoundException e) {
            System.err.println("ERROR: File not found " + filename);
        } catch (IOException e) {
            System.err.println("ERROR: Could not read " + filename);
        }

        return result;
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

//    public static void create_report (String[] record, PreparedStatement statement_report, Map<Integer, Integer> map) throws SQLException {
//        statement_report.setInt(1, map.get(1));
////                statement_report.setTimestamp(2, Timestamp.valueOf(record[map.get(2)])); // Fehler
//        statement_report.setTimestamp(2, null); // zuerst null setzen
//        statement_report.setString(3, record[map.get(3)]);
//        statement_report.setString(4, record[map.get(4)]);
//        statement_report.setString(5, record[map.get(5)]);
//        statement_report.setString(6, record[map.get(1)]);
//        statement_report.executeUpdate();
//    }

    public static void set_duration(String s, PreparedStatement statement) throws SQLException {
        Duration duration;
        try {
            String[] parts = s.split(" ");
            long amount = Long.parseLong(parts[0]);
            String unit = parts[1];

            if (unit.endsWith("s")) {
                unit = unit.substring(0, unit.length() - 1);
            }
            switch (unit.toLowerCase()) {
                case "hour":
                    duration = Duration.ofHours(amount);
                    break;
                case "minute":
                    duration = Duration.ofMinutes(amount);
                    break;
                case "second":
                    duration = Duration.ofSeconds(amount);
                    break;
                default:
                    // Handle unsupported units or provide a default behavior
                    duration = Duration.ZERO;
                    break;
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            // Handle the exception and return Duration.ZERO as the default behavior
            duration = Duration.ZERO;
        }
        statement.setObject(2, duration, Types.OTHER);
    }

    public static Duration convertSecondsToDuration(String seconds) {
        try {
            long amount = Long.parseLong(seconds);
            return Duration.ofSeconds(amount);
        } catch (NumberFormatException e) {
            // Handle the exception and return Duration.ZERO as the default behavior
            return Duration.ZERO;
        }
    }


    public static Timestamp create_timestamp_numeric(String s) {
        Timestamp timestamp = null;
        try {
            double dateTimeValue = Double.parseDouble(s);

            int datePart = (int) dateTimeValue; // Extract the integer part as the date value
            double timePart = (dateTimeValue - datePart) * 24 * 60 * 60 * 1000; // Extract the decimal part as the time value

            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.set(1899, Calendar.DECEMBER, 30);
            calendar.add(Calendar.DAY_OF_YEAR, datePart);
            calendar.add(Calendar.MILLISECOND, (int) timePart);

            timestamp = new Timestamp(calendar.getTimeInMillis());
        } catch (Exception e) {
            timestamp = Timestamp.valueOf(LocalDateTime.of(0, 1, 1, 0, 0, 0));
        }
        return timestamp;
    }

    public static void create_timestamp_strings(PreparedStatement statement, int index, String year, String month, String day) throws SQLException {
        try {
            int yearValue = Integer.parseInt(year);
            int monthValue = parseMonth(month);
            int dayValue = Integer.parseInt(day);

            LocalDate date = LocalDate.of(yearValue, monthValue, dayValue);
            LocalDateTime dateTime = date.atStartOfDay();
            Timestamp timestamp = Timestamp.valueOf(dateTime);

            statement.setTimestamp(index, timestamp);
        } catch (Exception e) {
            // Handle the exceptions and set a default timestamp with all zeros
            statement.setTimestamp(index, Timestamp.valueOf(LocalDateTime.of(0, 1, 1, 0, 0, 0)));
        }
    }

    private static int parseMonth(String month) {
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM");
        return Month.from(monthFormatter.parse(month)).getValue();
    }

    public static void create_timestamp_datetimestring(PreparedStatement statement, int index, String s) throws SQLException {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            LocalDateTime dateTime = LocalDateTime.parse(s, formatter);
            Timestamp timestamp = Timestamp.valueOf(dateTime);

            statement.setTimestamp(index, timestamp);
        } catch (Exception e) {
            // Handle the exception and set a default timestamp with all zeros
            statement.setTimestamp(index, Timestamp.valueOf(LocalDateTime.of(0, 1, 1, 0, 0, 0)));
        }
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
        System.out.println();

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            int report_id, weather_id = 20000000, location_id = 30000000, ufo_id = 40000000, bigfoot_id = 50000000;
            PreparedStatement statement_report = connection.prepareStatement("INSERT INTO report VALUES (?, ?, ?, ?, ?, ?)");
            PreparedStatement statement_ufo = connection.prepareStatement("INSERT INTO ufo_sighting VALUES (?, ?, ?, ?, ?)");
            PreparedStatement statement_bigfoot = connection.prepareStatement("INSERT INTO bigfoot_sighting VALUES (?, ?, ?, ?, ?)");
            PreparedStatement statement_weather = connection.prepareStatement("INSERT INTO weather VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            PreparedStatement statement_location = connection.prepareStatement("INSERT INTO location VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");


            // import bigfoot1 to database
            report_id = 10000000;
            for (int i = 1; i < file_bigfoot1.size(); i++) {
                String[] record = file_bigfoot1.get(i);
                statement_report.setInt(1, report_id);
                create_timestamp_strings(statement_report, 2, record[5], record[7], record[21]);
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
//                statement_bigfoot.setNull(4, java.sql.Types.INTEGER); // cannot be null
                statement_bigfoot.setInt(4, weather_id);
                statement_bigfoot.setInt(5, location_id);
                statement_bigfoot.executeUpdate();

                report_id++;
                weather_id++;
                location_id++;
                bigfoot_id++;
            }


            // import bigfoot2_locations to database
            report_id = 11000000;
            for (int i = 1; i < file_bigfoot2_locations.size(); i++) {
                String[] record = file_bigfoot2_locations.get(i);
                statement_report.setInt(1, report_id);
//                statement_report.setTimestamp(2, null);
                create_timestamp_datetimestring(statement_report, 2, record[3]);
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


            // import bigfoot2_reports to database
            report_id = 12000000;
            for (int i = 1; i < file_bigfoot2_reports.size(); i++) {
                String[] record = file_bigfoot2_reports.get(i);
                statement_report.setInt(1, report_id);
                create_timestamp_strings(statement_report, 2, record[0], record[2], record[3]);
                statement_report.setString(3, null);
                statement_report.setString(4, null);
                statement_report.setString(5, null);
                statement_report.setString(6, record[9]);
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
                statement_location.setString(5, record[5]);
                statement_location.setString(6, record[4]);
                statement_location.setString(7, record[7]);
                statement_location.setString(8, record[8]);
                statement_location.setString(9, record[6]);
                statement_location.executeUpdate();

                statement_bigfoot.setInt(1, bigfoot_id);
                if (record[16] == null)
                    statement_bigfoot.setString(2, null);
                else
                    statement_bigfoot.setString(2, record[16].substring(record[16].length()-1));
                statement_bigfoot.setInt(3, report_id);
                statement_bigfoot.setInt(4, weather_id);
                statement_bigfoot.setInt(5, location_id);
                statement_bigfoot.executeUpdate();

                report_id++;
                weather_id++;
                location_id++;
                bigfoot_id++;
            }


            // import file_bigfoot2_reports_geocoded into database
            report_id = 13000000;
            for (int i = 1; i < file_bigfoot2_reports_geocoded.size(); i++) {
                String[] record = file_bigfoot2_reports_geocoded.get(i);
                statement_report.setInt(1, report_id);
                if (record[8] == null || record[8] == "")
                    statement_report.setTimestamp(2, null);
                else
                    statement_report.setTimestamp(2, create_timestamp_numeric(record[8]));
                statement_report.setString(3, null);
                statement_report.setString(4, null);
                statement_report.setString(5, record[5]);
                statement_report.setString(6, record[0]);
                statement_report.executeUpdate();

                statement_weather.setInt(1, weather_id);
                statement_weather.setObject(2, !record[13].isEmpty() ? Double.valueOf(record[13]) : null);
                statement_weather.setObject(3, !record[26].isEmpty() ? Double.valueOf(record[26]) : null);
                statement_weather.setObject(4, !record[16].isEmpty() ? Double.valueOf(record[16]) : null);
                statement_weather.setObject(5, !record[19].isEmpty() ? Double.valueOf(record[19]) : null);
                statement_weather.setObject(6, !record[21].isEmpty() ? connection.createArrayOf("text", new String[]{record[21]}) : null);
                statement_weather.setObject(7, !record[17].isEmpty() ? Double.valueOf(record[17]) : null);
                statement_weather.setObject(8, !record[25].isEmpty() ? Double.valueOf(record[25]) : null);
                statement_weather.setObject(9, !record[18].isEmpty() ? Double.valueOf(record[18]) : null);
                statement_weather.executeUpdate();

                statement_location.setInt(1, location_id);
                statement_location.setObject(2, !record[7].isEmpty() ? Double.valueOf(record[7]) : null);
                statement_location.setObject(3, !record[6].isEmpty() ? Double.valueOf(record[6]) : null);
                statement_location.setString(4, null);
                statement_location.setString(5, record[2]);
                statement_location.setString(6, record[3]);
                statement_location.setString(7, null);
                statement_location.setString(8, null);
                statement_location.setString(9, record[1]);
                statement_location.executeUpdate();

                statement_bigfoot.setInt(1, bigfoot_id);
                if (record[10] == null)
                    statement_bigfoot.setString(2, null);
                else
                    statement_bigfoot.setString(2, record[10].substring(record[10].length()-1));
                statement_bigfoot.setInt(3, report_id);
                statement_bigfoot.setInt(4, weather_id);
                statement_bigfoot.setInt(5, location_id);
                statement_bigfoot.executeUpdate();

                report_id++;
                weather_id++;
                location_id++;
                bigfoot_id++;
            }


            // import file_bigfoot3 into database
             report_id = 14000000;
            for (int i = 1; i < file_bigfoot3.size(); i++) {
                String[] record = file_bigfoot3.get(i);
                statement_report.setInt(1, report_id);
                if (record[5] == null || record[5] == "")
                    statement_report.setTimestamp(2, null);
                else
                    statement_report.setTimestamp(2, create_timestamp_numeric(record[5]));
                statement_report.setString(3, null);
                statement_report.setString(4, null);
                statement_report.setString(5, record[4]);
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
                statement_location.setDouble(2, Double.valueOf(record[1]));
                statement_location.setDouble(3, Double.valueOf(record[0]));
                statement_location.setString(4, null);
                statement_location.setString(5, null);
                statement_location.setString(6, null);
                statement_location.setString(7, null);
                statement_location.setString(8, null);
                statement_location.setString(9, null);
                statement_location.executeUpdate();

                statement_bigfoot.setInt(1, bigfoot_id);
                if (record[10] == null || record[10] == "")
                    statement_bigfoot.setString(2, null);
                else
                    statement_bigfoot.setString(2, record[10].substring(record[10].length()-1));
                statement_bigfoot.setInt(3, report_id);
                statement_bigfoot.setInt(4, weather_id);
                statement_bigfoot.setInt(5, location_id);
                statement_bigfoot.executeUpdate();

                report_id++;
                weather_id++;
                location_id++;
                bigfoot_id++;
            }


            // import file_bigfoot4 into database
            report_id = 15000000;
            for (int i = 1; i < file_bigfoot4.size(); i++) {
                String[] record = file_bigfoot4.get(i);
                statement_report.setInt(1, report_id);
                if (record[7] == null || record[7] == "")
                    statement_report.setTimestamp(2, null);
                else
                    try {
                        statement_report.setTimestamp(2, create_timestamp_numeric(record[7]));
                    } catch (NumberFormatException e) {
                        statement_report.setTimestamp(2, null);
                    }
                statement_report.setString(3, null);
                statement_report.setString(4, null);
                statement_report.setString(5, record[4]);
                statement_report.setString(6, record[0]);
                statement_report.executeUpdate();

                statement_weather.setInt(1, weather_id);
                try {
                    statement_weather.setObject(2, (record[11] != null && !record[11].isEmpty()) ? Double.valueOf(record[11]) : null);
                    statement_weather.setObject(3, (record[23] != null && !record[23].isEmpty()) ? Double.valueOf(record[23]) : null);
                    statement_weather.setObject(4, (record[14] != null && !record[14].isEmpty()) ? Double.valueOf(record[14]) : null);
                    statement_weather.setObject(5, (record[17] != null && !record[17].isEmpty()) ? Double.valueOf(record[17]) : null);
                    statement_weather.setObject(6, (record[19] != null && !record[19].isEmpty()) ? connection.createArrayOf("text", new String[]{record[19]}) : null);
                    statement_weather.setObject(7, (record[15] != null && !record[15].isEmpty()) ? Double.valueOf(record[15]) : null);
                    statement_weather.setObject(8, (record[22] != null && !record[22].isEmpty()) ? Double.valueOf(record[22]) : null);
                    statement_weather.setObject(9, (record[16] != null && !record[16].isEmpty()) ? Double.valueOf(record[16]) : null);
                } catch (NumberFormatException e) {
                    statement_weather.setObject(2, null);
                    statement_weather.setObject(3, null);
                    statement_weather.setObject(4, null);
                    statement_weather.setObject(5, null);
                    statement_weather.setObject(6, null);
                    statement_weather.setObject(7, null);
                    statement_weather.setObject(8, null);
                    statement_weather.setObject(9, null);
                }
                statement_weather.executeUpdate();

                statement_location.setInt(1, location_id);
                statement_location.setObject(2, (record[6] != null && !record[6].isEmpty()) ? Double.valueOf(record[6]) : null);
                try {
                    statement_location.setObject(3, (record[5] != null && !record[5].isEmpty()) ? Double.valueOf(record[5]) : null);
                } catch (NumberFormatException e) {
                    statement_location.setObject(3, null);
                }
                statement_location.setString(4, null);
                statement_location.setString(5, record[1]);
                statement_location.setString(6, record[2]);
                statement_location.setString(7, null);
                statement_location.setString(8, null);
                statement_location.setString(9, null);
                statement_location.executeUpdate();

                statement_bigfoot.setInt(1, bigfoot_id);
                if (record[9] == null || record[9] == "")
                    statement_bigfoot.setString(2, null);
                else
                    statement_bigfoot.setString(2, record[9].substring(record[9].length()-1));
                statement_bigfoot.setInt(3, report_id);
                statement_bigfoot.setInt(4, weather_id);
                statement_bigfoot.setInt(5, location_id);
                statement_bigfoot.executeUpdate();

                report_id++;
                weather_id++;
                location_id++;
                bigfoot_id++;
            }


            // import file_ufo1_csv to database
            report_id = 16000000;
            for (int i = 1; i < file_ufo1_csv.size(); i++) {
                String[] record = file_ufo1_csv.get(i);
                statement_report.setInt(1, report_id);
                try {
                    create_timestamp_datetimestring(statement_report, 2, record[4] + "Z");
                } catch (Exception e) {
                    statement_report.setTimestamp(2, null);
                }
                statement_report.setString(3, null);
                statement_report.setString(4, null);
                statement_report.setString(5, record[0]);
                statement_report.setString(6, record[9]);
                statement_report.executeUpdate();

                statement_weather.setInt(1, weather_id);
                for (int j = 2; j < 10; j++) {
                    if (j != 6)
                        statement_weather.setBigDecimal(j, null);
                }
                statement_weather.setArray(6, null);
                statement_weather.executeUpdate();

                statement_location.setInt(1, location_id);
                try {
                    statement_location.setObject(2, !record[12].isEmpty() ? Double.valueOf(record[12]) : null);
                    statement_location.setObject(3, !record[11].isEmpty() ? Double.valueOf(record[11]) : null);
                } catch (NumberFormatException e) {
                    statement_location.setObject(2, null);
                    statement_location.setObject(3, null);
                }
                statement_location.setString(4, record[1]);
                statement_location.setString(5, record[2]);
                statement_location.setString(6, record[3]);
                statement_location.setString(7, null);
                statement_location.setString(8, null);
                statement_location.setString(9, null);
                statement_location.executeUpdate();

                statement_ufo.setInt(1, ufo_id);
                if (record[6] == null || record[6] == "")
                    statement_ufo.setObject(2, null);
                else
                    set_duration(record[6], statement_ufo);
                statement_ufo.setInt(3, report_id);
                statement_ufo.setInt(4, weather_id);
                statement_ufo.setInt(5, location_id);
                statement_ufo.executeUpdate();

                report_id++;
                weather_id++;
                location_id++;
                ufo_id++;
            }


            // import file_ufo1_json to database
            report_id = 17000000;
            for (int i = 1; i < file_ufo1_json.size(); i++) {
                String[] record = file_ufo1_json.get(i);
                statement_report.setInt(1, report_id);
                try {
                    create_timestamp_datetimestring(statement_report, 2, record[2] + "Z");
                } catch (Exception e) {
                    statement_report.setTimestamp(2, null);
                }
                statement_report.setString(3, null);
                statement_report.setString(4, null);
                statement_report.setString(5, record[9]);
                statement_report.setString(6, record[0]);
                statement_report.executeUpdate();

                statement_weather.setInt(1, weather_id);
                for (int j = 2; j < 10; j++) {
                    if (j != 6)
                        statement_weather.setBigDecimal(j, null);
                }
                statement_weather.setArray(6, null);
                statement_weather.executeUpdate();

                statement_location.setInt(1, location_id);
                statement_location.setObject(2, null);
                statement_location.setObject(3, null);
                statement_location.setString(4, record[6]);
                statement_location.setString(5, record[5]);
                statement_location.setString(6, record[4]);
                statement_location.setString(7, null);
                statement_location.setString(8, null);
                statement_location.setString(9, null);
                statement_location.executeUpdate();

                statement_ufo.setInt(1, ufo_id);
                if (record[8] == null || record[8] == "")
                    statement_ufo.setObject(2, null);
                else
                    set_duration(record[8], statement_ufo);
                statement_ufo.setInt(3, report_id);
                statement_ufo.setInt(4, weather_id);
                statement_ufo.setInt(5, location_id);
                statement_ufo.executeUpdate();

                report_id++;
                weather_id++;
                location_id++;
                ufo_id++;
            }


            // import file_ufo2 to database
            report_id = 18000000;
            for (int i = 1; i < file_ufo2.size(); i++) {
                String[] record = file_ufo2.get(i);
                statement_report.setInt(1, report_id);
                if (record[0] == null || record[0] == "")
                    statement_report.setTimestamp(2, null);
                else
                    try {
                        statement_report.setTimestamp(2, create_timestamp_numeric(record[0]));
                    } catch (NumberFormatException e) {
                        statement_report.setTimestamp(2, null);
                    }
                statement_report.setString(3, null);
                statement_report.setString(4, null);
                statement_report.setString(5, null);
                statement_report.setString(6, record[7]);
                statement_report.executeUpdate();

                statement_weather.setInt(1, weather_id);
                for (int j = 2; j < 10; j++) {
                    if (j != 6)
                        statement_weather.setBigDecimal(j, null);
                }
                statement_weather.setArray(6, null);
                statement_weather.executeUpdate();

                statement_location.setInt(1, location_id);
                statement_location.setObject(2, !record[10].isEmpty() ? Double.valueOf(record[10]) : null);
                try {
                    statement_location.setObject(3, !record[9].isEmpty() ? Double.valueOf(record[9]) : null);
                } catch (NumberFormatException e) {
                    statement_location.setObject(3, null);
                }
                try {
                    statement_location.setString(4, record[6]);
                } catch (NumberFormatException e) {
                    statement_location.setString(4, null);
                }
                statement_location.setString(5, record[1]);
                statement_location.setString(6, record[2]);
                statement_location.setString(7, null);
                statement_location.setString(8, null);
                statement_location.setString(9, null);
                statement_location.executeUpdate();

                statement_ufo.setInt(1, ufo_id);
                if (record[5] == null || record[5] == "")
                    statement_ufo.setObject(2, null);
                else {
                    Duration duration = convertSecondsToDuration(record[5]);
                    set_duration(duration.toString(), statement_ufo);
                }
                statement_ufo.setInt(3, report_id);
                statement_ufo.setInt(4, weather_id);
                statement_ufo.setInt(5, location_id);
                statement_ufo.executeUpdate();

                report_id++;
                weather_id++;
                location_id++;
                ufo_id++;
            }


            // import file_ufo3 to database
            report_id = 19000000;
            for (int i = 1; i < file_ufo2.size(); i++) {
                String[] record = file_ufo2.get(i);
                statement_report.setInt(1, report_id);
                if (record[4] == null || record[4] == "")
                    statement_report.setTimestamp(2, null);
                else
                    try {
                        statement_report.setTimestamp(2, create_timestamp_numeric(record[4]));
                    } catch (NumberFormatException e) {
                        statement_report.setTimestamp(2, null);
                    }
                statement_report.setString(3, null);
                statement_report.setString(4, null);
                try {
                    statement_report.setString(5, record[11]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    statement_report.setString(5,null);
                }
                statement_report.setString(6, record[2]);
                statement_report.executeUpdate();

                statement_weather.setInt(1, weather_id);
                for (int j = 2; j < 10; j++) {
                    if (j != 6)
                        statement_weather.setBigDecimal(j, null);
                }
                statement_weather.setArray(6, null);
                statement_weather.executeUpdate();

                statement_location.setInt(1, location_id);
                statement_location.setObject(2,null);
                statement_location.setObject(3, null);
                statement_location.setString(4, record[8]);
                statement_location.setString(5, record[6]);
                statement_location.setString(6, record[7]);
                statement_location.setString(7, null);
                statement_location.setString(8, null);
                statement_location.setString(9, null);
                statement_location.executeUpdate();

                statement_ufo.setInt(1, ufo_id);
                if (record[10] == null || record[10] == "")
                    statement_ufo.setObject(2, null);
                else
                    set_duration(record[10], statement_ufo);
                statement_ufo.setInt(3, report_id);
                statement_ufo.setInt(4, weather_id);
                statement_ufo.setInt(5, location_id);
                statement_ufo.executeUpdate();


                report_id++;
                weather_id++;
                location_id++;
                ufo_id++;
            }



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        try {
            Connection connection = DriverManager.getConnection(url, username, password);

            // Create a statement object
            Statement statement = connection.createStatement();

            // Execute a query
            ResultSet resultSet = statement.executeQuery("SELECT * FROM report WHERE date IS NOT NULL LIMIT 10");

            // Process the results
            while (resultSet.next()) {
                // Access the data from the result set
                int id = resultSet.getInt("report_id");
                Timestamp timestamp = resultSet.getTimestamp("date");
                String headline = resultSet.getString("headline");
                String desc = resultSet.getString("description");
                desc = desc.replace("/n", "");

                // Do something with the retrieved data
                System.out.println("report_id: " + id
                        + "; Timestamp: " + timestamp
                        + "; headline: " + (headline != null ? headline.substring(0, Math.min(headline.length(), 50)) + "..." : "null")
                        + "; description: " + (desc != null ? desc.substring(0, Math.min(desc.length(), 100)) + "..." : "null"));
            }

            System.out.println();

            resultSet = statement.executeQuery("SELECT * FROM location LIMIT 10");
            while (resultSet.next()) {
                // Access the data from the result set
                String country = resultSet.getString("country");
                String city = resultSet.getString("county");
                String state = resultSet.getString("state");
                Double latitude = resultSet.getDouble("latitude");
                Double longitude = resultSet.getDouble("longtitude");

                // Do something with the retrieved data
                System.out.println("Country: " + country
                        + "; City: " + city
                        + "; State: " + state
                        + "; Latitude: " + latitude
                        + "; Longitude: " + longitude);
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
