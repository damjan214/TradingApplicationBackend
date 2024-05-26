package org.example.model.stocks;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Data
@Entity
@NoArgsConstructor
@Table(name = "stocks_data")
public class StockData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stockDataId")
    private Long id;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "current_price")
    private Double currentPrice;

    @Column(name = "change_day")
    private Double changeDay;

    @Column(name = "percent_change_day")
    private Double percentChangeDay;

    @Column(name = "highest_price_day")
    private Double highestPriceDay;

    @Column(name = "lowest_price_day")
    private Double lowestPriceDay;

    @Column(name = "open_price_day")
    private Double openPriceDay;

    @Column(name = "previous_close_price")
    private Double previousClosePrice;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;
}
