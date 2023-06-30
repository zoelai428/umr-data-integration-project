package stage3;

import org.example.DatabaseConnection;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Cleaning_Main {

    public static void main(String[] args) throws SQLException {

        // initialize SQL relations mapped with their corresponding attribute names
        stage3.Relation report = new stage3.Relation("report", DatabaseConnection.returnColumnNames("report"));
        stage3.Relation weather = new stage3.Relation("weather", DatabaseConnection.returnColumnNames("weather"));
        stage3.Relation location = new stage3.Relation("location", DatabaseConnection.returnColumnNames("location"));
        stage3.Relation ufo_sighting = new stage3.Relation("ufo_sighting", DatabaseConnection.returnColumnNames("ufo_sighting"));
        stage3.Relation bigfoot_sighting = new stage3.Relation("bigfoot_sighting", DatabaseConnection.returnColumnNames("bigfoot_sighting"));
        List<stage3.Relation> relationList = Arrays.asList(report, weather, location, ufo_sighting, bigfoot_sighting);

        // special handling for "weather" table
        Set<String> empty_but_not_null = new HashSet<>(Arrays.asList("precip_type", "summary", "conditions"));


        /** Before Cleaning*/
        System.out.println("*** Before Cleaning ***");
        // retrieve the number of records in each SQL table
        DatabaseConnection.printNumRecords(relationList);
        // retrieve the number of null/empty RECORDS in each table
        DatabaseConnection.printNumEmptyRecords(relationList, empty_but_not_null);

        System.out.println();

        /**Output:

         TOTAL NUMBER OF RECORDS
         -----------------------
         # report: 300675
         # weather: 5082
         # location: 207892
         # ufo_sighting: 359611
         # bigfoot_sighting: 13098

         NUMBER OF NULL/EMPTY RECORDS
         ----------------------------
         # report: 0
         # weather: 978
         # location: 0
         # ufo_sighting: 1173
         # bigfoot_sighting: 0

         */



        /**Cleaning*/
        System.out.println("*** Cleaning ***");
        // remove empty records from tables weather and ufo_sighting
        DatabaseConnection.removeEmptyRecords(Arrays.asList(ufo_sighting, weather), empty_but_not_null);

        // remove duplicates in 'weather'
        DatabaseConnection.removeDuplicatesWeather(); // no duplicates found...



        System.out.println();

    }
}
