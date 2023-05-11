import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    /** This function reads a csv file from the designated folder "..\\umr-data-integration-project\\0_datasets".
     *
     * @param filename should contain the exact file name including ".csv" e.g. "bigfoot1_reports.csv"
     * @return a list of arrays of String extracted directly from the given file
     * @throws IOException
     */
    public static List<String[]> read_csv_file(String filename) throws IOException {
        String filepath = "..\\umr-data-integration-project\\0_datasets\\" + filename;
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

    public static void main(String[] args) throws IOException {
        List<String[]> file_bigfoot1 = read_csv_file("bigfoot1_reports.csv");

        System.out.println(Arrays.toString(file_bigfoot1.get(0)));
        System.out.println(Arrays.toString(file_bigfoot1.get(10)));

        List<String[]> file_bigfoot2_locations = read_csv_file("bigfoot2_bfro_locations.csv");
        List<String[]> file_bigfoot2_reports_geocoded = read_csv_file("bigfoot2_bfro_reports_geocoded.csv");
        List<String[]> file_bigfoot3 = read_csv_file("bigfoot3_Bigfoot_Sightings.csv");
        List<String[]> file_ufo1_csv = read_csv_file("ufo1_nuforc_reports.csv");
        List<String[]> file_ufo2 = read_csv_file("ufo2_ufo_sighting_data.csv");
        List<String[]> file_ufo3 = read_csv_file("ufo3_nuforc_reports.csv");

//        List<String[]> file_bigfoot2_reports = read_json_file("bigfoot2_bfro_reports.json");
//        List<String[]> file_bigfoot4 = read_xlsx_file("bigfoot4_DataDNA_Dataset_Challenge-February_2023.xlsx");
//        List<String[]> file_ufo1_json = read_json_file("ufo1_nuforc_reports.json");


    }
}
