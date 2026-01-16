package com.stockmarket.logic;

import java.math.BigDecimal;

public class Order implements Comparable<Order> {
    public enum Type { BUY, SELL }

    private final String ticker;
    private final Type type;
    private final BigDecimal priceLimit;
    private final int quantity;

    public Order(String ticker, Type type, BigDecimal priceLimit, int quantity) {
        this.ticker = ticker;
        this.type = type;
        this.priceLimit = priceLimit;
        this.quantity = quantity;
    }

    // Logika priorytetów:
    // BUY: Im drożej chcesz kupić, tym lepiej (na górę)
    // SELL: Im taniej chcesz sprzedać, tym lepiej (na górę)
    @Override
    public int compareTo(Order other) {
        if (this.type != other.type) {
            return 0;
        }
        if (this.type == Type.BUY) {
            return other.priceLimit.compareTo(this.priceLimit); // Malejąco
        } else {
            return this.priceLimit.compareTo(other.priceLimit); // Rosnąco
        }
    }

    public String getTicker() { return ticker; }
    public BigDecimal getPriceLimit() { return priceLimit; }
}