package com.stockmarket.logic;

import com.stockmarket.domain.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PortfolioTest {


    @Test
    void polymorphismTest_assetsWithSameBasePriceShouldHaveDifferentRealValues() {
        // given: 3 różne aktywa o tej samej cenie 100 PLN
        BigDecimal price = new BigDecimal("100.00");
        Share share = new Share("S", price);             // 100 - 5 = 95
        Commodity commodity = new Commodity("C", price, new BigDecimal("10")); // 100 - 10 = 90
        Currency currency = new Currency("M", price, new BigDecimal("1"));     // 99

        // when
        BigDecimal v1 = share.calculateRealValue(1);
        BigDecimal v2 = commodity.calculateRealValue(1);
        BigDecimal v3 = currency.calculateRealValue(1);

        // then: Każde musi dać inny wynik
        assertThat(v1).isNotEqualByComparingTo(v2);
        assertThat(v2).isNotEqualByComparingTo(v3);
        assertThat(v1).isNotEqualByComparingTo(v3);
    }

    @Test
    void shouldThrowExceptionWhenBuyingTooMuch() {
        // given: portfel z 100 PLN
        Portfolio p = new Portfolio(new BigDecimal("100.00"));
        Share expensive = new Share("Drogie", new BigDecimal("200.00"));

        // then: próba zakupu musi rzucić wyjątek
        assertThatThrownBy(() -> p.addAsset(expensive, 1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Niewystarczające środki");
    }


    // Sprawdzenie, czy gotówka maleje po zakupie
    @Test
    void shouldReduceCashAfterPurchase() {
        Portfolio p = new Portfolio(new BigDecimal("1000.00"));
        Share share = new Share("Apple", new BigDecimal("100.00"));
        
        // Koszt zakupu: 100 + 5 (opłata) = 105
        p.addAsset(share, 1);

        // 1000 - 105 = 895
        assertThat(p.getCash()).isEqualByComparingTo(new BigDecimal("895.00"));
    }

    // Sprawdzenie, czy dokupienie tego samego aktywa sumuje ilość
    @Test
    void shouldAccumulateQuantityWhenAddingSameAsset() {
        Portfolio p = new Portfolio(new BigDecimal("1000.00"));
        Share share = new Share("Orlen", new BigDecimal("10.00"));

        p.addAsset(share, 5);
        p.addAsset(share, 5); // Łącznie 10 sztuk

        // Wartość aktywów: 10 sztuk * 10 PLN = 100 PLN.
        // Wartość realna (minus opłata 5 PLN): 95 PLN.
        // Gotówka: 1000 - (15 + 15) = 970 PLN.
        // Razem: 970 + 95 = 1065 PLN.
        assertThat(p.calculateTotalValue()).isEqualByComparingTo(new BigDecimal("1065.00"));
    }

    // Sprawdzenie pustego portfela
    @Test
    void shouldReturnOnlyCashForEmptyPortfolio() {
        Portfolio p = new Portfolio(new BigDecimal("500.00"));
        assertThat(p.calculateTotalValue()).isEqualByComparingTo(new BigDecimal("500.00"));
    }

    // Test mieszany (różne typy aktywów w jednym portfelu)
    @Test
    void shouldCalculateTotalValueForMixedPortfolio() {
        Portfolio p = new Portfolio(new BigDecimal("1000.00"));
        
        // Kupujemy po 1 sztuce każdego typu (zakładamy bazową cenę 100)
        p.addAsset(new Share("S", new BigDecimal("100.00")), 1);
        p.addAsset(new Commodity("C", new BigDecimal("100.00"), new BigDecimal("10.00")), 1);
        p.addAsset(new Currency("U", new BigDecimal("100.00"), new BigDecimal("1.00")), 1);

        // Koszty zakupu: 105 + 100 + 100 = 305.
        // Gotówka: 1000 - 305 = 695.
        
        // Wartości realne: 95 + 90 + 99 = 284.
        
        // Razem: 695 + 284 = 979.
        assertThat(p.calculateTotalValue()).isEqualByComparingTo(new BigDecimal("979.00"));
    }

    // Wyjątek przy ujemnej ilości
    @Test
    void shouldThrowExceptionForNegativeQuantity() {
        Portfolio p = new Portfolio(new BigDecimal("1000.00"));
        Share s = new Share("S", new BigDecimal("10.00"));

        assertThatThrownBy(() -> p.addAsset(s, -5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // Wyjątek przy ilości równej 0
    @Test
    void shouldThrowExceptionForZeroQuantity() {
        Portfolio p = new Portfolio(new BigDecimal("1000.00"));
        Share s = new Share("S", new BigDecimal("10.00"));

        assertThatThrownBy(() -> p.addAsset(s, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // Zakup za całą dostępną gotówkę (do zera)
    @Test
    void shouldAllowPurchaseForExactAmountOfCash() {
        Portfolio p = new Portfolio(new BigDecimal("105.00"));
        Share s = new Share("S", new BigDecimal("100.00")); // Koszt 100 + 5 = 105

        p.addAsset(s, 1);

        assertThat(p.getCash()).isEqualByComparingTo(BigDecimal.ZERO);
    }
}


