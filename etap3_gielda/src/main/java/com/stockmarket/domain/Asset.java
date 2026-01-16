package com.stockmarket.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Asset {
    private final String ticker;
    private BigDecimal currentPrice;
    private final AssetType type;

    // Historia zakupów
    private final List<Lot> lots = new ArrayList<>();

    public Asset(String ticker, BigDecimal currentPrice, AssetType type) {
        this.ticker = ticker;
        this.currentPrice = currentPrice;
        this.type = type;
    }

    public abstract BigDecimal calculateRealValue(int quantity);

    public void addLot(Lot lot) {
        this.lots.add(lot);
    }

    public List<Lot> getLots() {
        return lots;
    }

    public String getTicker() {
        return ticker;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public AssetType getType() {
        return type;
    }

    // Sumowanie
    public int getTotalQuantity() {
        int sum = 0;
        for (Lot lot : lots) {
            sum += lot.getQuantity();
        }
        return sum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asset asset = (Asset) o;
        return Objects.equals(ticker, asset.ticker);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticker);
    }
}