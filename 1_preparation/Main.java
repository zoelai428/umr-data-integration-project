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


public class Main {

    private final static String url = "jdbc:postgresql://localhost:5432/dataintegration";
    private final static String username = "postgres";
    private final static String password = "";

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
                result.add(line.split(","));
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
                    switch (cell.getCellType()) {
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



        try {
            Connection connection = DriverManager.getConnection(url, username, password);

            // Create a statement object
            Statement statement = connection.createStatement();

            // Execute a query
            ResultSet resultSet = statement.executeQuery("SELECT * FROM report");

            // Process the results
            while (resultSet.next()) {
                // Access the data from the result set
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");

                // Do something with the retrieved data
                System.out.println("ID: " + id + ", Name: " + name);
            }

            // Close the result set, statement, and connection
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            // Handle any exceptions
            e.printStackTrace();
        }




        /** Test reading csv files*/
        List<String[]> file_bigfoot1 = read_csv_file("bigfoot1_reports.csv");

        System.out.println(Arrays.toString(file_bigfoot1.get(0)));
        System.out.println(Arrays.toString(file_bigfoot1.get(10)));

        List<String[]> file_bigfoot2_locations = read_csv_file("bigfoot2_bfro_locations.csv");
        List<String[]> file_bigfoot2_reports_geocoded = read_csv_file("bigfoot2_bfro_reports_geocoded.csv");
        List<String[]> file_bigfoot3 = read_csv_file("bigfoot3_Bigfoot_Sightings.csv");
        List<String[]> file_ufo1_csv = read_csv_file("ufo1_nuforc_reports.csv");
        List<String[]> file_ufo2 = read_csv_file("ufo2_ufo_sighting_data.csv");
        List<String[]> file_ufo3 = read_csv_file("ufo3_nuforc_reports.csv");


        /** Test reading json files*/
        String[] header_ufo1 = {"text", "stats", "date_time", "report_link", "city", "state", "country", "shape", "duration", "summary", "posted"};
        List<String[]> file_ufo1_json = read_json_file("ufo1_nuforc_reports.json", header_ufo1);
        System.out.println(Arrays.toString(file_ufo1_json.get(0)));
        System.out.println(Arrays.toString(file_ufo1_json.get(3)));

        /** For checking purpose: 3rd entry is as below:*/
//        {"text": "Group of several orange lights, seemingly circular.  Lights did not blink.   \n \nObject appeared to be like a plane, but the color and speed wasn\u2019t correct, and it seemed closer and lower than a plane.  Slow and steady course.   \n \nAfter it disappeared into the clouds, the same exact thing happened about 1-2 minutes later (a second identical object on what seemed like the same flight path). \n \n \n((NUFORC Note:  Time indicated by witness may have been flawed.  We have amended it, in order to indicate a nighttime sighting.  Witness elects to remain totally anonymous; provides no contact information, so we are unable to confirm the time.  PD))",
//        "stats": "Occurred : 6/20/2019 23:28  (Entered as : 06/20/19 11:28) Reported: 6/20/2019 8:36:51 PM 20:36 Posted: 6/27/2019 Location: Charlottesville, VA Shape: Circle Duration: 15 seconds Characteristics: There were lights on the object",
//        "date_time": "6/20/19 23:28",
//        "report_link": "http://www.nuforc.org/webreports/reports/146/S146944.html",
//        "city": "Charlottesville",
//        "state": "VA",
//        "country": "USA",
//        "shape": "Circle",
//        "duration": "15 seconds",
//        "summary": "Group of several orange lights, seemingly circular.  Lights did not blink.   ((anonymous report))",
//        "posted": "6/27/19"}

        /** Output: */
//[Group of several orange lights, seemingly circular.  Lights did not blink.
//
//        Object appeared to be like a plane, but the color and speed wasn�t correct, and it seemed closer and lower than a plane.  Slow and steady course.
//
//        After it disappeared into the clouds, the same exact thing happened about 1-2 minutes later (a second identical object on what seemed like the same flight path).
//
//
//        ((NUFORC Note:  Time indicated by witness may have been flawed.  We have amended it, in order to indicate a nighttime sighting.  Witness elects to remain totally anonymous; provides no contact information, so we are unable to confirm the time.  PD)), Occurred : 6/20/2019 23:28  (Entered as : 06/20/19 11:28) Reported: 6/20/2019 8:36:51 PM 20:36 Posted: 6/27/2019 Location: Charlottesville, VA Shape: Circle Duration: 15 seconds Characteristics: There were lights on the object, 6/20/19 23:28, http://www.nuforc.org/webreports/reports/146/S146944.html, Charlottesville, VA, USA, Circle, 15 seconds, Group of several orange lights, seemingly circular.  Lights did not blink.   ((anonymous report)), 6/27/19]

        String[] header_bigfoot2 = {"YEAR", "SEASON", "MONTH", "DATE", "STATE", "COUNTY", "LOCATION_DETAILS", "NEAREST_TOWN", "NEAREST_ROAD", "OBSERVED", "ALSO_NOTICED", "OTHER_WITNESSES", "OTHER_STORIES", "TIME_AND_CONDITIONS", "ENVIRONMENT", "REPORT_NUMBER", "REPORT_CLASS"};
        List<String[]> file_bigfoot2_reports = read_json_file("bigfoot2_bfro_reports.json", header_bigfoot2);
        System.out.println(Arrays.toString(file_bigfoot2_reports.get(0)));
        System.out.println(Arrays.toString(file_bigfoot2_reports.get(8)));



        /** Test reading xlsx files*/

        List<String[]> file_bigfoot4 = read_xlsx_file("bigfoot4_DataDNA_Dataset_Challenge-February_2023.xlsx");
        System.out.println(Arrays.toString(file_bigfoot4.get(0)));
        System.out.println(Arrays.toString(file_bigfoot4.get(3)));
        System.out.println(Arrays.toString(file_bigfoot4.get(4)));
    }
}
