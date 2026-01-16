package com.stockmarket.logic;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.PriorityQueue;
import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @Test
    void buyOrder_ShouldPrioritizeHigherPrice() {
        Order lowBid = new Order("XYZ", Order.Type.BUY, new BigDecimal("100.00"), 10);
        Order highBid = new Order("XYZ", Order.Type.BUY, new BigDecimal("105.00"), 10);
        PriorityQueue<Order> queue = new PriorityQueue<>();

        queue.add(lowBid);
        queue.add(highBid);

        assertThat(queue.peek()).isEqualTo(highBid);
    }

    @Test
    void sellOrder_ShouldPrioritizeLowerPrice() {
        Order cheapOffer = new Order("ABC", Order.Type.SELL, new BigDecimal("100.00"), 5);
        Order expensiveOffer = new Order("ABC", Order.Type.SELL, new BigDecimal("105.00"), 5);
        PriorityQueue<Order> queue = new PriorityQueue<>();

        queue.add(expensiveOffer);
        queue.add(cheapOffer);

        assertThat(queue.peek()).isEqualTo(cheapOffer);
    }

    @Test
    void compareTo_ShouldReturnZeroForSamePriceAndType() {
        Order o1 = new Order("A", Order.Type.BUY, new BigDecimal("100"), 1);
        Order o2 = new Order("A", Order.Type.BUY, new BigDecimal("100"), 1);
        assertThat(o1.compareTo(o2)).isZero();
    }
}