package com.myproject.orderbook.objects.stats;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class LimitBreakDownStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    BigDecimal limitPrice;
    Long LimitQuantity;
    @ManyToOne
    @JoinColumn(name = "order_book_stats_id")
    private OrderBookStats orderBookStats;

    public LimitBreakDownStats() {
    }

    public LimitBreakDownStats(OrderBookStats orderBookStats) {
        this.orderBookStats = orderBookStats;
    }

    public BigDecimal getLimitPrice() {
        return limitPrice;
    }

    public void setLimitPrice(BigDecimal limitPrice) {
        this.limitPrice = limitPrice;
    }

    public Long getLimitQuantity() {
        return LimitQuantity;
    }

    public void setLimitQuantity(Long limitQuantity) {
        LimitQuantity = limitQuantity;
    }
}
