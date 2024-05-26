package org.example.model.stocks;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.dto.StockPendingDto;
import org.example.model.Portfolio;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Data
@Entity
@NoArgsConstructor
@Table(name = "stocks_pending")
public class StockPending {

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

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private StockStatus stockStatus;

    @ManyToOne
    @JoinColumn(name = "portfolioId")
    private Portfolio portfolio;

    @PrePersist
    public void prePersist() {
        this.uuid = java.util.UUID.randomUUID().toString();
    }

    public StockPendingDto toDto() {
        return StockPendingDto.builder()
                .uuid(this.uuid)
                .name(this.name)
                .symbol(this.symbol)
                .balanceInvested(this.balanceInvested)
                .quantity(this.quantity)
                .timestamp(this.timestamp.toString())
                .stockStatus(this.stockStatus.toString())
                .build();
    }
}
