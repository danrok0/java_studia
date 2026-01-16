package com.stockmarket.domain;

import java.math.BigDecimal;

public class Share extends Asset {
    private static final BigDecimal HANDLING_FEE = new BigDecimal("5.00");

    public Share(String ticker, BigDecimal currentPrice) {
        super(ticker, currentPrice, AssetType.SHARE);
    }

    @Override
    public BigDecimal calculateRealValue(int quantity) {
        BigDecimal rawValue = getCurrentPrice().multiply(BigDecimal.valueOf(quantity));
        return rawValue.subtract(HANDLING_FEE).max(BigDecimal.ZERO);
    }
}