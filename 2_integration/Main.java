package org.example;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

public class Main {

    //TODO Data Cleaning
    //TODO 

    public static void main(String[] args) throws SQLException, IOException, IllegalAccessException, ParseException {

        IntegrateData dataIntegration = new IntegrateData(List.of(
                //"src/main/resources/cleaned_bigfoot1_reports.csv" //TODO Datensatz hat Fehler mit Delimitern
                "src/main/resources/cleaned_bigfoot2_bfro_locations.csv", //Ja
                "src/main/resources/cleaned_bigfoot2_bfro_reports_geocoded.csv", //Ja
                "src/main/resources/cleaned_bigfoot3_bigfoot_sightings.csv", //Ja
                "src/main/resources/cleaned_ufo1_nuforc_reports.csv", //Ja
                "src/main/resources/cleaned_ufo2_ufo_sighting_data.csv", //Ja
                "src/main/resources/cleaned_ufo3_nuforc_reports.csv" //Ja
                ));
        dataIntegration.start();

    }
}