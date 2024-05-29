package org.example.service;

import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.example.configuration.FinnhubConfig;
import org.example.dto.MarketResponse;
import org.example.dto.StockBuyRequest;
import org.example.dto.StockSellRequest;
import org.example.exceptions.ResourceNotFoundException;
import org.example.model.stocks.StockData;
import org.example.model.stocks.StockPending;
import org.example.model.stocks.StockStatus;
import org.example.model.user.User;
import org.example.repository.StockDataRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class StockDataService {
    private static final Logger LOGGER = Logger.getLogger(StockDataService.class.getName());
    private final StockDataRepository stockDataRepository;
    private final AuthenticationService authenticationService;
    private final FinnhubConfig finnhubConfig;
    private final String COMPANIES_FILE_PATH = "D:\\Facultate\\Licenta\\TradingApplicationBackend\\src\\main\\java\\org\\example\\file\\nasdaq_screener_1715711046717.csv";
    private final String FINNHUB_QUOTE = "https://finnhub.io/api/v1/quote?symbol=";
    private final String TOKEN_URL = "&token=";

    @Async
    @Scheduled(initialDelay = 60000, fixedRate = 900000)
    public void getRealTimeDataForStocks() {
        try {
            LOGGER.info("Getting real-time data for stocks...");
            String csvFile = COMPANIES_FILE_PATH;
            LocalDateTime timestamp = LocalDateTime.now();
            List<CSVRecord> records = readCSV(csvFile);
            for (CSVRecord record : records) {
                processStockData(record, timestamp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<CSVRecord> readCSV(String filePath) throws IOException {
        try (Reader reader = new FileReader(filePath);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            return csvParser.getRecords();
        }
    }

    private void processStockData(CSVRecord record, LocalDateTime timestamp) {
        String symbol = record.get(0);
        JsonObject jsonResponse = fetchStockData(symbol);
        if (jsonResponse != null) {
            StockData stockData = buildStockData(jsonResponse, symbol, timestamp);
            LOGGER.info("Saving stock data for symbol: " + symbol);
            stockDataRepository.save(stockData);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
        }
    }

    private JsonObject fetchStockData(String symbol) {
        try {
            URL url = new URL( FINNHUB_QUOTE + symbol + TOKEN_URL + finnhubConfig.getSecretKey());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return JsonParser.parseString(response.toString()).getAsJsonObject();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private StockData buildStockData(JsonObject jsonResponse, String symbol, LocalDateTime timestamp) {
        double c = jsonResponse.get("c").getAsDouble();
        double d = jsonResponse.get("d").getAsDouble();
        double dp = jsonResponse.get("dp").getAsDouble();
        double h = jsonResponse.get("h").getAsDouble();
        double l = jsonResponse.get("l").getAsDouble();
        double o = jsonResponse.get("o").getAsDouble();
        double pc = jsonResponse.get("pc").getAsDouble();

        return StockData.builder()
                .symbol(symbol)
                .currentPrice(c)
                .changeDay(d)
                .percentChangeDay(dp)
                .highestPriceDay(h)
                .lowestPriceDay(l)
                .openPriceDay(o)
                .previousClosePrice(pc)
                .timestamp(timestamp)
                .build();
    }

    public StockData getLastStockValue(String symbol) {
        List<StockData> stockDataList = stockDataRepository.findBySymbol(symbol);
        stockDataList.sort((stockData1, stockData2) -> stockData2.getTimestamp().compareTo(stockData1.getTimestamp()));
        return stockDataList.get(0);
    }
}
