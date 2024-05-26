package org.example.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReadFileService {

    private final String COMPANIES_FILE_PATH = "D:\\Facultate\\Licenta\\TradingApplicationBackend\\src\\main\\java\\org\\example\\file\\nasdaq_screener_1715711046717.csv";

    public Map<String, String> getSymbolsAndNames() {
        String csvFile = COMPANIES_FILE_PATH;
        List<String> column = new ArrayList<>();
        Map<String, String> symbolsAndNames = new HashMap<>();
        try (Reader reader = new FileReader(csvFile);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord csvRecord : csvParser) {
                symbolsAndNames.put(csvRecord.get(0), csvRecord.get(1));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return symbolsAndNames;
    }
}
