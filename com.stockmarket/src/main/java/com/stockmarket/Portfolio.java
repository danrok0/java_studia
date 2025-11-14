package com.stockmarket;

public class Portfolio {
    private double cash;
    private final StockHolding[] holdings;
    private int holdingsCount;

    private static final int MAX_HOLDINGS = 10;

    private static class StockHolding {
        private final Stock stock;
        private int quantity;

        private StockHolding(Stock stock, int quantity) {
            this.stock = stock;
            this.quantity = quantity;
        }
    }

    public Portfolio(double initialCash) {
        if (initialCash < 0)
            throw new IllegalArgumentException("Initial cash cannot be negative");
        this.cash = initialCash;
        this.holdings = new StockHolding[MAX_HOLDINGS];
        this.holdingsCount = 0;
    }

    public double getCash() {
        return cash;
    }

    public int getHoldingsCount() {
        return holdingsCount;
    }

    public void addStock(Stock stock, int quantity) {
        if (quantity <= 0)
            throw new IllegalArgumentException("Quantity must be positive");
        if (stock == null)
            throw new IllegalArgumentException("Stock cannot be null");

        for (int i = 0; i < holdingsCount; i++) {
            if (holdings[i].stock.equals(stock)) {
                holdings[i].quantity += quantity;
                return;
            }
        }

        if (holdingsCount >= MAX_HOLDINGS)
            throw new IllegalStateException("Portfolio is full");

        holdings[holdingsCount++] = new StockHolding(stock, quantity);
    }

    public int getStockQuantity(Stock stock) {
        for (int i = 0; i < holdingsCount; i++) {
            if (holdings[i].stock.equals(stock)) {
                return holdings[i].quantity;
            }
        }
        return 0;
    }

    public double calculateStockValue() {
        double total = 0;
        for (int i = 0; i < holdingsCount; i++) {
            total += holdings[i].stock.getInitialPrice() * holdings[i].quantity;
        }
        return total;
    }

    public double calculateTotalValue() {
        return cash + calculateStockValue();
    }
}
