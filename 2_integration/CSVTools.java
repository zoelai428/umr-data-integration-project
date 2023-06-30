package org.example;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

public class CSVTools {

    public static Iterable<CSVRecord> readCSV(String source) throws IOException {

        Reader reader = new FileReader(source);

//        CSVFormat csvFormat = CSVFormat.Builder.create().setDelimiter(";")
//                                .setHeader()
//                                .setSkipHeaderRecord(false)
//                                .build();

        CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter(';').builder()
                                .setHeader()
                                .setSkipHeaderRecord(false)
                                .build();

        CSVParser csvParser = new CSVParser(reader, csvFormat);

        return csvParser.getRecords();
    }

    public static List<String> getHeader(String source) throws IOException {

        Reader reader = new FileReader(source);

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(false)
                .build();

        CSVParser csvParser = new CSVParser(reader, csvFormat);

        return csvParser.getHeaderNames();
    }

}
