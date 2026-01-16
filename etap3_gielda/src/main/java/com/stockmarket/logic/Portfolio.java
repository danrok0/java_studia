package com.stockmarket.logic;

import com.stockmarket.domain.*;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Portfolio {
    private BigDecimal cash;

    // O(1) Access - mapa aktywów
    private final Map<String, Asset> assets = new HashMap<>();

    // Priority Queue - kolejka zleceń
    private final PriorityQueue<Order> orderQueue = new PriorityQueue<>();

    public Portfolio(BigDecimal initialCash) {
        this.cash = initialCash;
    }

    public void trackAsset(Asset asset) {
        assets.put(asset.getTicker(), asset);
    }

    public Asset getAsset(String ticker) {
        return assets.get(ticker);
    }

    // --- KUPNO (Dodawanie nowej partii) ---
    public void buy(String ticker, int quantity, BigDecimal price) {
        if (!assets.containsKey(ticker)) {
            throw new IllegalArgumentException("Nieznane aktywo: " + ticker);
        }
        Asset asset = assets.get(ticker);

        BigDecimal cost = price.multiply(BigDecimal.valueOf(quantity));
        if (cash.compareTo(cost) < 0) {
            throw new IllegalStateException("Niewystarczające środki");
        }

        cash = cash.subtract(cost);

        // Dodanie nowej partii (LOT)
        Lot newLot = new Lot(LocalDate.now(), quantity, price);
        asset.addLot(newLot);
    }

    // --- SPRZEDAŻ (Algorytm FIFO) ---
    public BigDecimal sell(String ticker, int quantityToSell, BigDecimal currentMarketPrice) {
        if (!assets.containsKey(ticker)) {
            throw new IllegalArgumentException("Nieznane aktywo");
        }
        Asset asset = assets.get(ticker);
        if (asset.getTotalQuantity() < quantityToSell) {
            throw new IllegalStateException("Nie masz wystarczającej liczby akcji");
        }

        BigDecimal totalProfit = BigDecimal.ZERO;
        int remaining = quantityToSell;

        Iterator<Lot> iterator = asset.getLots().iterator();

        while (iterator.hasNext() && remaining > 0) {
            Lot lot = iterator.next();
            int soldFromThisLot;

            if (lot.getQuantity() <= remaining) {
                // Cała partia zużyta
                soldFromThisLot = lot.getQuantity();
                iterator.remove();
            } else {
                // Część partii zużyta
                soldFromThisLot = remaining;
                lot.setQuantity(lot.getQuantity() - remaining);
            }

            BigDecimal buyPrice = lot.getPurchasePrice();
            BigDecimal profitPerUnit = currentMarketPrice.subtract(buyPrice);
            BigDecimal lotProfit = profitPerUnit.multiply(BigDecimal.valueOf(soldFromThisLot));

            totalProfit = totalProfit.add(lotProfit);

            // Przychód ze sprzedaży trafia do gotówki
            BigDecimal revenue = currentMarketPrice.multiply(BigDecimal.valueOf(soldFromThisLot));
            cash = cash.add(revenue);

            remaining -= soldFromThisLot;
        }

        return totalProfit;
    }

    // --- OBSŁUGA ZLECEŃ ---
    public void addOrder(Order order) {
        orderQueue.add(order);
    }

    public Order peekNextOrder() {
        return orderQueue.peek();
    }

    // --- PERSYSTENCJA (Zapis do pliku) ---
    public void saveToFile(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("HEADER|CASH|" + cash);
            for (Asset asset : assets.values()) {
                if (asset.getLots().isEmpty()) continue;
                // Zapisujemy: Typ|Ticker
                writer.println("ASSET|" + asset.getType() + "|" + asset.getTicker());
                for (Lot lot : asset.getLots()) {
                    // Zapisujemy: Data|Ilość|Cena
                    writer.println("LOT|" + lot.getPurchaseDate() + "|" + lot.getQuantity() + "|" + lot.getPurchasePrice());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Błąd zapisu pliku", e);
        }
    }

    // --- PERSYSTENCJA (Odczyt z pliku) ---
    public void loadFromFile(String filename) {
        File file = new File(filename);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            Asset currentAsset = null;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                String recordType = parts[0];

                switch (recordType) {
                    case "HEADER":
                        this.cash = new BigDecimal(parts[2]);
                        break;
                    case "ASSET":
                        String typeStr = parts[1];
                        String ticker = parts[2];

                        // Odtwarzamy obiekt Asset (cena rynkowa resetowana do 0 przy wczytaniu, bo to stan historyczny)
                        if (typeStr.equals("SHARE")) currentAsset = new Share(ticker, BigDecimal.ZERO);
                        else if (typeStr.equals("COMMODITY")) currentAsset = new Commodity(ticker, BigDecimal.ZERO, BigDecimal.ZERO);
                        else if (typeStr.equals("CURRENCY")) currentAsset = new Currency(ticker, BigDecimal.ZERO, BigDecimal.ZERO);

                        if (currentAsset != null) trackAsset(currentAsset);
                        break;
                    case "LOT":
                        if (currentAsset == null) throw new DataIntegrityException("LOT bez ASSET");
                        // Dodajemy partię do ostatnio wczytanego aktywa
                        currentAsset.addLot(new Lot(LocalDate.parse(parts[1]), Integer.parseInt(parts[2]), new BigDecimal(parts[3])));
                        break;
                    default:
                        throw new DataIntegrityException("Nieznany typ rekordu");
                }
            }
        } catch (Exception e) {
            throw new DataIntegrityException("Błąd odczytu pliku: " + e.getMessage());
        }
    }

    // --- RAPORTOWANIE ---
    public String generateReport() {
        List<Asset> assetList = new ArrayList<>(assets.values());

        // Sortowanie
        Collections.sort(assetList, new Comparator<Asset>() {
            @Override
            public int compare(Asset a1, Asset a2) {
                // 1. Sortowanie po typie
                int typeCompare = a1.getType().compareTo(a2.getType());
                if (typeCompare != 0) return typeCompare;

                // 2. Sortowanie po wartości rynkowej malejąco
                BigDecimal val1 = a1.calculateRealValue(a1.getTotalQuantity());
                BigDecimal val2 = a2.calculateRealValue(a2.getTotalQuantity());
                return val2.compareTo(val1);
            }
        });

        StringBuilder report = new StringBuilder();
        report.append("CASH: ").append(cash).append("\n");
        for (Asset a : assetList) {
            if (a.getTotalQuantity() > 0) {
                report.append(a.getTicker()).append(" Qty: ").append(a.getTotalQuantity()).append("\n");
            }
        }
        return report.toString();
    }

    public BigDecimal getCash() { return cash; }
    public Map<String, Asset> getAssets() { return assets; }
}