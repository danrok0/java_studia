package com.stockmarket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PortfolioTest {

    private Portfolio portfolio;
    private Stock cdr, ten;

    @BeforeEach
    void setUp() {
        portfolio = new Portfolio(1000.0);
        cdr = new Stock("CDR", "CD Projekt", 150.0);
        ten = new Stock("TEN", "Ten Square Games", 100.0);
    }

    @Test
    void testEmptyPortfolio() {
        assertEquals(1000.0, portfolio.getCash());
        assertEquals(0, portfolio.getHoldingsCount());
        assertEquals(0, portfolio.calculateStockValue());
        assertEquals(1000.0, portfolio.calculateTotalValue());
    }

    @Test
    void testAddNewStock() {
        portfolio.addStock(cdr, 2);
        assertEquals(1, portfolio.getHoldingsCount());
        assertEquals(2, portfolio.getStockQuantity(cdr));
    }

    @Test
    void testAddSameStockIncreasesQuantity() {
        portfolio.addStock(cdr, 2);
        portfolio.addStock(cdr, 3);
        assertEquals(1, portfolio.getHoldingsCount());
        assertEquals(5, portfolio.getStockQuantity(cdr));
    }

    @Test
    void testAddDifferentStocksCreatesSeparateHoldings() {
        portfolio.addStock(cdr, 2);
        portfolio.addStock(ten, 1);
        assertEquals(2, portfolio.getHoldingsCount());
    }

    @Test
    void testCalculateStockAndTotalValue() {
        portfolio.addStock(cdr, 2);  // 2 * 150 = 300
        portfolio.addStock(ten, 3);  // 3 * 100 = 300
        assertEquals(600.0, portfolio.calculateStockValue());
        assertEquals(1600.0, portfolio.calculateTotalValue());
    }

    @Test
    void testAddStockWhenFullThrowsException() {
        for (int i = 0; i < 10; i++) {
            portfolio.addStock(new Stock("SYM" + i, "Company" + i, 10.0), 1);
        }
        assertThrows(IllegalStateException.class, () ->
                portfolio.addStock(new Stock("OVER", "Overflow", 5.0), 1));
    }

    @Test
    void testInvalidInputs() {
        assertThrows(IllegalArgumentException.class, () -> portfolio.addStock(null, 1));
        assertThrows(IllegalArgumentException.class, () -> portfolio.addStock(cdr, 0));
    }
     @Test
    void testInitialCashNegativeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> new Portfolio(-1.0));
    }

    @Test
    void testGetStockQuantityNotPresent() {
        assertEquals(0, portfolio.getStockQuantity(new Stock("ZZZ", "Not Present", 10.0)));
    }

    @Test
    void testAddStockNullStockThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> portfolio.addStock(null, 5));
    }

    @Test
    void testPortfolioIsFullEdgeCase() {
        for (int i = 0; i < 10; i++) {
            portfolio.addStock(new Stock("SYM" + i, "Company" + i, 10.0), 1);
        }
        // The portfolio should be full
        assertEquals(10, portfolio.getHoldingsCount());
    }

    @Test
    void testCalculateStockValueNoStocks() {
        Portfolio emptyPortfolio = new Portfolio(200.0);
        assertEquals(0.0, emptyPortfolio.calculateStockValue());
        assertEquals(200.0, emptyPortfolio.calculateTotalValue());
    }
}
