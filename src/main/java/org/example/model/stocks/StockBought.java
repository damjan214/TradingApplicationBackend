package org.example.model.stocks;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dto.StockBoughtDto;
import org.example.model.Portfolio;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Data
@Entity
@NoArgsConstructor
@Table(name = "stocks_bought")
public class StockBought {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stockId")
    private Long id;

    @Column(name = "uuid", unique = true)
    private String uuid;

    @Column(name = "name")
    private String name;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "balance_invested")
    private Double balanceInvested;

    @Column(name = "quantity")
    private Double quantity;

    @Column(name = "open_price_day")
    private Double openPriceDay;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "portfolioId")
    private Portfolio portfolio;

    @PrePersist
    public void prePersist() {
        this.uuid = java.util.UUID.randomUUID().toString();
    }

    public StockBoughtDto toDto() {
        return StockBoughtDto.builder()
                .uuid(this.uuid)
                .name(this.name)
                .symbol(this.symbol)
                .balanceInvested(this.balanceInvested)
                .quantity(this.quantity)
                .openPriceDay(this.openPriceDay)
                .timestamp(this.timestamp.toString())
                .build();
    }

}
