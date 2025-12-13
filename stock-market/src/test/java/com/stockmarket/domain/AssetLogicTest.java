package com.stockmarket.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

class AssetLogicTest {

    @Test
    void shareShouldReduceValueByHandlingFee() {
        // Akcja warta 100, opłata 5.
        // 1 sztuka powinna być warta 95.
        Share share = new Share("TestShare", new BigDecimal("100.00"));
        assertThat(share.calculateRealValue(1))
                .isEqualByComparingTo(new BigDecimal("95.00"));
    }

    @Test
    void commodityShouldReduceValueByStorageCost() {
        // Surowiec 100, koszt mag. 10.
        // 2 sztuki: (100*2) - (10*2) = 200 - 20 = 180.
        Commodity gold = new Commodity("Gold", new BigDecimal("100.00"), new BigDecimal("10.00"));
        assertThat(gold.calculateRealValue(2))
                .isEqualByComparingTo(new BigDecimal("180.00"));
    }

    @Test
    void currencyShouldReduceValueBySpread() {
        // Waluta 5.00, spread 0.20.
        // Cena Bid = 4.80.
        // 100 sztuk = 480.00.
        Currency euro = new Currency("EUR", new BigDecimal("5.00"), new BigDecimal("0.20"));
        assertThat(euro.calculateRealValue(100))
                .isEqualByComparingTo(new BigDecimal("480.00"));
    }


    // Sprawdzenie kosztu zakupu dla Akcji (Cena + Opłata)
    @Test
    void shareShouldIncludeFeeInPurchaseCost() {
        Share share = new Share("Apple", new BigDecimal("100.00"));
        // Koszt zakupu = 100 + 5 (opłata) = 105
        assertThat(share.calculatePurchaseCost(1))
                .isEqualByComparingTo(new BigDecimal("105.00"));
    }

    // Zabezpieczenie przed ujemną wartością Akcji (gdy opłata > ceny)
    @Test
    void shareShouldNotHaveNegativeValue() {
        Share pennyStock = new Share("Cheap", new BigDecimal("2.00"));
        // 2.00 - 5.00 = -3.00 -> Powinno zwrócić 0
        assertThat(pennyStock.calculateRealValue(1))
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    // Sprawdzenie kosztu zakupu dla Surowca (Bez dodatkowych opłat przy zakupie)
    @Test
    void commodityPurchaseCostShouldBeStandard() {
        Commodity oil = new Commodity("Oil", new BigDecimal("50.00"), new BigDecimal("2.00"));
        // Koszt zakupu to czysta cena * ilość (50 * 10 = 500)
        assertThat(oil.calculatePurchaseCost(10))
                .isEqualByComparingTo(new BigDecimal("500.00"));
    }

    // Zabezpieczenie Surowca przed ujemną wartością (wysoki magazyn)
    @Test
    void commodityShouldReturnZeroIfStorageCostIsHigh() {
        Commodity waste = new Commodity("Waste", new BigDecimal("10.00"), new BigDecimal("20.00"));
        // 10 - 20 = -10 -> Powinno zwrócić 0
        assertThat(waste.calculateRealValue(1))
                .isEqualByComparingTo(BigDecimal.ZERO);
    }

    // Sprawdzenie kosztu zakupu Waluty
    @Test
    void currencyPurchaseCostShouldBeStandard() {
        Currency usd = new Currency("USD", new BigDecimal("4.00"), new BigDecimal("0.10"));
        // 100 * 4.00 = 400.00
        assertThat(usd.calculatePurchaseCost(100))
                .isEqualByComparingTo(new BigDecimal("400.00"));
    }

    // Zabezpieczenie Waluty (gdy spread > kursu)
    @Test
    void currencyShouldReturnZeroIfSpreadIsHuge() {
        Currency weak = new Currency("Weak", new BigDecimal("0.10"), new BigDecimal("0.50"));
        // 0.10 - 0.50 = -0.40 -> Powinno zwrócić 0
        assertThat(weak.calculateRealValue(100))
                .isEqualByComparingTo(BigDecimal.ZERO);
    }
}
