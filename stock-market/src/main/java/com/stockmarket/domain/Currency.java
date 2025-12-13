package com.stockmarket.domain;

import java.math.BigDecimal;

public class Currency extends Asset {
    private final BigDecimal spread;

    public Currency(String name, BigDecimal basePrice, BigDecimal spread) {
        super(name, basePrice);
        this.spread = spread;
    }

    
   @Override
public BigDecimal calculateRealValue(int quantity) {
    BigDecimal bidPrice = getBasePrice().subtract(spread);
    // Wartość to (Cena Rynkowa - Spread) * ilość
    return bidPrice.multiply(BigDecimal.valueOf(quantity)).max(BigDecimal.ZERO);
}

}
