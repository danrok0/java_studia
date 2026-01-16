package com.stockmarket.domain;

import java.math.BigDecimal;

public class Commodity extends Asset {
    private final BigDecimal storageCostPerUnit;

    public Commodity(String ticker, BigDecimal currentPrice, BigDecimal storageCostPerUnit) {
        super(ticker, currentPrice, AssetType.COMMODITY);
        this.storageCostPerUnit = storageCostPerUnit;
    }

    @Override
    public BigDecimal calculateRealValue(int quantity) {
        BigDecimal rawValue = getCurrentPrice().multiply(BigDecimal.valueOf(quantity));
        BigDecimal storageCost = storageCostPerUnit.multiply(BigDecimal.valueOf(quantity));
        return rawValue.subtract(storageCost).max(BigDecimal.ZERO);
    }
}