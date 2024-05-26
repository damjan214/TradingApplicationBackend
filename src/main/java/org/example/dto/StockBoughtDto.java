package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.stocks.StockBought;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class StockBoughtDto {
        private String uuid;
        private String name;
        private String symbol;

        private Double currentPrice;
        private Double balanceInvested;
        private Double quantity;

        private Double profitOrLoss;

        private Double profitOrLossPercentage;
        private Double openPriceDay;
        private String timestamp;

        public StockBought fromDto() {
            return StockBought.builder()
                    .uuid(this.uuid)
                    .name(this.name)
                    .symbol(this.symbol)
                    .balanceInvested(this.balanceInvested)
                    .quantity(this.quantity)
                    .openPriceDay(this.openPriceDay)
                    .timestamp(LocalDateTime.parse(this.timestamp))
                    .build();
        }
}
