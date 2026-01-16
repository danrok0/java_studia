package com.stockmarket.logic;

import com.stockmarket.domain.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PortfolioTest {

    // --- METODA POMOCNICZA (SETUP) ---
    private Portfolio setupPortfolioWithLots() {
        Portfolio p = new Portfolio(new BigDecimal("20000"));
        Share xyz = new Share("XYZ", new BigDecimal("100"));
        p.trackAsset(xyz);
        xyz.addLot(new Lot(LocalDate.of(2023, 1, 1), 10, new BigDecimal("100")));
        xyz.addLot(new Lot(LocalDate.of(2023, 2, 1), 10, new BigDecimal("120")));
        return p;
    }

    // --- SEKCJA 1: ALGORYTM FIFO (ZYSK) ---

    @Test
    void fifo_ShouldCalculateCorrectProfit_ForSingleLotSale() {
        Portfolio p = new Portfolio(new BigDecimal("10000"));
        Share s = new Share("FIFO", new BigDecimal("100"));
        p.trackAsset(s);
        s.addLot(new Lot(LocalDate.now(), 10, new BigDecimal("100")));

        BigDecimal profit = p.sell("FIFO", 10, new BigDecimal("150"));
        assertThat(profit).isEqualByComparingTo(new BigDecimal("500"));
    }

    @Test
    void fifo_ShouldCalculateCorrectProfit_ForMultiLotSale() {
        Portfolio p = setupPortfolioWithLots();
        BigDecimal profit = p.sell("XYZ", 15, new BigDecimal("150"));
        assertThat(profit).isEqualByComparingTo(new BigDecimal("650"));
    }

    // --- SEKCJA 2: ALGORYTM FIFO (ILOŚĆ I PARTIE) ---

    @Test
    void fifo_ShouldReduceTotalQuantity_AfterSale() {
        Portfolio p = setupPortfolioWithLots();
        p.sell("XYZ", 15, new BigDecimal("150"));
        assertThat(p.getAsset("XYZ").getTotalQuantity()).isEqualTo(5);
    }

    @Test
    void fifo_ShouldRemoveEmptyLot_AfterFullSale() {
        Portfolio p = setupPortfolioWithLots();
        p.sell("XYZ", 15, new BigDecimal("150"));
        assertThat(p.getAsset("XYZ").getLots()).hasSize(1);
    }

    @Test
    void fifo_ShouldReduceQuantityInRemainingLot() {
        Portfolio p = setupPortfolioWithLots();
        p.sell("XYZ", 15, new BigDecimal("150"));
        assertThat(p.getAsset("XYZ").getLots().get(0).getQuantity()).isEqualTo(5);
    }

    @Test
    void fifo_ShouldKeepCorrectPriceInRemainingLot() {
        Portfolio p = setupPortfolioWithLots();
        p.sell("XYZ", 15, new BigDecimal("150"));
        assertThat(p.getAsset("XYZ").getLots().get(0).getPurchasePrice()).isEqualByComparingTo(new BigDecimal("120"));
    }

    // --- SEKCJA 3: OBSŁUGA BŁĘDÓW ---

    @Test
    void exception_ShouldThrow_WhenSellingMoreThanOwned() {
        Portfolio p = new Portfolio(BigDecimal.ZERO);
        Share s = new Share("S", BigDecimal.TEN);
        p.trackAsset(s);
        assertThatThrownBy(() -> p.sell("S", 5, BigDecimal.TEN)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void exception_ShouldThrow_WhenBuyingWithoutFunds() {
        Portfolio p = new Portfolio(new BigDecimal("10.00"));
        Share s = new Share("S", new BigDecimal("100.00"));
        p.trackAsset(s);
        assertThatThrownBy(() -> p.buy("S", 1, new BigDecimal("100.00"))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void exception_ShouldThrow_WhenAssetUnknown() {
        Portfolio p = new Portfolio(BigDecimal.ZERO);
        assertThatThrownBy(() -> p.buy("UNKNOWN", 1, BigDecimal.ONE)).isInstanceOf(IllegalArgumentException.class);
    }

    // --- SEKCJA 4: PERSYSTENCJA (ZAPIS/ODCZYT) ---

    @Test
    void persistence_ShouldLoadCorrectCash(@TempDir Path tempDir) {
        File file = tempDir.resolve("cash.txt").toFile();
        Portfolio source = new Portfolio(new BigDecimal("5000.50"));
        source.saveToFile(file.getAbsolutePath());

        Portfolio target = new Portfolio(BigDecimal.ZERO);
        target.loadFromFile(file.getAbsolutePath());

        assertThat(target.getCash()).isEqualByComparingTo(new BigDecimal("5000.50"));
    }

    @Test
    void persistence_ShouldLoadAssetExistence(@TempDir Path tempDir) {
        // given
        File file = tempDir.resolve("assets.txt").toFile();
        // POPRAWKA: Dajemy 10000 PLN na start, żeby starczyło na zakup
        Portfolio source = new Portfolio(new BigDecimal("10000"));
        Share apple = new Share("AAPL", new BigDecimal("150.00"));
        source.trackAsset(apple);
        source.buy("AAPL", 5, new BigDecimal("100.00"));
        source.saveToFile(file.getAbsolutePath());

        // when
        Portfolio target = new Portfolio(BigDecimal.ZERO);
        target.loadFromFile(file.getAbsolutePath());

        // then
        assertThat(target.getAsset("AAPL")).isNotNull();
    }

    @Test
    void persistence_ShouldLoadAssetQuantity(@TempDir Path tempDir) {
        // given
        File file = tempDir.resolve("qty.txt").toFile();
        //  Dajemy 10000 PLN na start
        Portfolio source = new Portfolio(new BigDecimal("10000"));
        Share apple = new Share("AAPL", new BigDecimal("150.00"));
        source.trackAsset(apple);
        source.buy("AAPL", 5, new BigDecimal("100.00"));
        source.saveToFile(file.getAbsolutePath());

        // when
        Portfolio target = new Portfolio(BigDecimal.ZERO);
        target.loadFromFile(file.getAbsolutePath());

        // then
        assertThat(target.getAsset("AAPL").getTotalQuantity()).isEqualTo(5);
    }

    @Test
    void persistence_ShouldLoadAssetType(@TempDir Path tempDir) {
        // given
        File file = tempDir.resolve("type.txt").toFile();
        //  Dajemy 10000 PLN na start
        Portfolio source = new Portfolio(new BigDecimal("10000"));
        Commodity gold = new Commodity("GOLD", new BigDecimal("1000"), new BigDecimal("50"));
        source.trackAsset(gold);
        source.buy("GOLD", 1, new BigDecimal("1000"));
        source.saveToFile(file.getAbsolutePath());

        // when
        Portfolio target = new Portfolio(BigDecimal.ZERO);
        target.loadFromFile(file.getAbsolutePath());

        // then
        assertThat(target.getAsset("GOLD").getType()).isEqualTo(AssetType.COMMODITY);
    }

    @Test
    void persistence_ShouldThrowOnCorruptedData(@TempDir Path tempDir) throws IOException {
        File badFile = tempDir.resolve("corrupted.txt").toFile();
        Files.writeString(badFile.toPath(), "LOT|2023-01-01|10|100");

        Portfolio p = new Portfolio(BigDecimal.ZERO);
        assertThatThrownBy(() -> p.loadFromFile(badFile.getAbsolutePath())).isInstanceOf(DataIntegrityException.class);
    }
}