package com.stockmarket;
import java.util.Objects;

public class Stock {
    private final String symbol;
    private final String name;
    private final double initialPrice;

    public Stock(String symbol, String name, double initialPrice) {
        if (symbol == null || symbol.isBlank())
            throw new IllegalArgumentException("Symbol cannot be empty");
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name cannot be empty");
        if (initialPrice <= 0)
            throw new IllegalArgumentException("Initial price must be positive");

        this.symbol = symbol;
        this.name = name;
        this.initialPrice = initialPrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return name;
    }

    public double getInitialPrice() {
        return initialPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stock)) return false;
        Stock stock = (Stock) o;
        return symbol.equals(stock.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }
}
