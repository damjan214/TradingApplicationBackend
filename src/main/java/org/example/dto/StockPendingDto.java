package org.example.dto;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.stocks.StockPending;
import org.example.model.stocks.StockStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class StockPendingDto {
    private String uuid;
    private String name;
    private String symbol;
    private Double balanceInvested;
    private Double quantity;

    private Double currentPrice;
    private String timestamp;
    private String stockStatus;

    public StockPending fromDto() {
        return StockPending.builder()
                .uuid(this.uuid)
                .name(this.name)
                .symbol(this.symbol)
                .balanceInvested(this.balanceInvested)
                .quantity(this.quantity)
                .timestamp(LocalDateTime.parse(this.timestamp))
                .stockStatus(StockStatus.valueOf(this.stockStatus))
                .build();
    }
}
