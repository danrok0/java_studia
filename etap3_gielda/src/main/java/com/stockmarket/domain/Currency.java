package com.stockmarket.domain;

import java.math.BigDecimal;

public class Currency extends Asset {
    private final BigDecimal spread;

    public Currency(String ticker, BigDecimal currentPrice, BigDecimal spread) {
        super(ticker, currentPrice, AssetType.CURRENCY);
        this.spread = spread;
    }

    @Override
    public BigDecimal calculateRealValue(int quantity) {
        BigDecimal bidPrice = getCurrentPrice().subtract(spread);
        return bidPrice.multiply(BigDecimal.valueOf(quantity)).max(BigDecimal.ZERO);
    }
}