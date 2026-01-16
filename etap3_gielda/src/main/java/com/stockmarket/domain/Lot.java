package com.stockmarket.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Lot {
    private final LocalDate purchaseDate;
    private int quantity;
    private final BigDecimal purchasePrice;

    public Lot(LocalDate purchaseDate, int quantity, BigDecimal purchasePrice) {
        this.purchaseDate = purchaseDate;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    @Override
    public String toString() {
        return purchaseDate + "|" + quantity + "|" + purchasePrice;
    }
}