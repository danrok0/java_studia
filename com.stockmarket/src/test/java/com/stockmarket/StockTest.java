package com.stockmarket;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StockTest {

    @Test
    void testStockCreationAndGetters() {
        Stock stock = new Stock("CDR", "CD Projekt", 150.0);
        assertEquals("CDR", stock.getSymbol());
        assertEquals("CD Projekt", stock.getName());
        assertEquals(150.0, stock.getInitialPrice());
    }

    @Test
    void testEqualsAndHashCodeSameSymbol() {
        Stock s1 = new Stock("CDR", "CD Projekt", 100);
        Stock s2 = new Stock("CDR", "CD Projekt Red", 200);
        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
    }

    @Test
    void testNotEqualDifferentSymbol() {
        Stock s1 = new Stock("CDR", "CD Projekt", 100);
        Stock s2 = new Stock("TEN", "Ten Square Games", 100);
        assertNotEquals(s1, s2);
    }

    @Test
    void testInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> new Stock("", "Name", 10));
        assertThrows(IllegalArgumentException.class, () -> new Stock("SYM", "", 10));
        assertThrows(IllegalArgumentException.class, () -> new Stock("SYM", "Name", 0));
    }
    @Test
    void testSymbolAndNameTrimming() {
        Stock s = new Stock("  CDR  ", "  CD Projekt  ", 100.0);
        assertEquals("  CDR  ", s.getSymbol());
        assertEquals("  CD Projekt  ", s.getName());
    }

    @Test
    void testEqualsItself() {
        Stock stock = new Stock("TEN", "Ten Square Games", 100);
        assertEquals(stock, stock);
    }

    @Test
    void testHashCodeConsistency() {
        Stock s1 = new Stock("ABC", "Company ABC", 10.0);
        int hash1 = s1.hashCode();
        int hash2 = s1.hashCode();
        assertEquals(hash1, hash2);
    }

    @Test
    void testEqualsWithNullAndOtherType() {
        Stock s1 = new Stock("QWE", "Test Name", 12.1);
        assertNotEquals(null, s1);
        assertNotEquals(s1, "QWE");
    }
}
