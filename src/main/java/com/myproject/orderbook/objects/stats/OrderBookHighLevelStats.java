package com.myproject.orderbook.objects.stats;

public class OrderBookHighLevelStats extends AbstractOrderBookStats {

    private Long totalOrders;

    public OrderBookHighLevelStats(OrderBookStats orderBookStats) {
        super(orderBookStats);
        this.totalOrders = orderBookStats.getTotalOrders();
    }

    public OrderBookHighLevelStats() {
        super();
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }


}
