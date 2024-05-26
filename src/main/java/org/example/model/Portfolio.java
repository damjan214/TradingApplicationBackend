package org.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.stocks.StockBought;
import org.example.model.stocks.StockPending;
import org.example.model.user.User;

import java.util.List;

@AllArgsConstructor
@Builder
@Data
@Entity
@NoArgsConstructor
@Table(name = "portfolios")
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "portfolioId")
    private Long id;

    @Column(name = "uuid", unique = true)
    private String uuid;

    @OneToOne
    @JoinColumn(name = "userId")
    private User user;

    @Column(name = "balance_available")
    private Double balanceAvailable;

    @Column(name = "currency")
    private String currency;

    @OneToMany(mappedBy = "portfolio", fetch = FetchType.EAGER)
    private List<StockBought> stockBoughts;

    @OneToMany(mappedBy = "portfolio", fetch = FetchType.EAGER)
    private List<StockPending> stockPendings;

    @PrePersist
    public void prePersist() {
        this.uuid = java.util.UUID.randomUUID().toString();
    }
}
