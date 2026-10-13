package cz.cuni.mff.simekjan.importer;

import cz.cuni.mff.simekjan.ConfigLoader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class CsvParser {

    public static CSVParser getParser(String filename) throws IOException {
        Path csvPath = ConfigLoader.getCsvDir();

        Reader reader = Files.newBufferedReader(csvPath.resolve(filename));

        return CSVFormat.DEFAULT
            .builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .get()
            .parse(reader);
    }

}
