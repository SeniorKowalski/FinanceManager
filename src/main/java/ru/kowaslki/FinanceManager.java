package ru.kowaslki;

import com.google.gson.JsonObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FinanceManager {

    private final String FILE_NAME = "categories.tsv";
    private final File file = new File(FILE_NAME);
    private final List<Purchase> purchaseList = new ArrayList<>();
    private final Map<String, Long> maxCategoryMap = new HashMap<>();

    // Добавление покупки в список покупок
    public void addToPurchaseList(Purchase purchase) {
        setPurchaseCategory(file, purchase);
        purchaseList.add(purchase);
        maxCategoryInMap(purchase, maxCategoryMap);
    }

    // если категория уже есть в списке категорий добавляем к её сумме стоимость покупки:
    private void maxCategoryInMap(Purchase purchase, Map<String, Long> map) {
        if (map.containsKey(purchase.getCategory())) {
            Long sum = map.get(purchase.getCategory());
            map.put(purchase.getCategory(), sum + purchase.getSum());
        } else {
            map.put(purchase.getCategory(), purchase.getSum());
        }
    }

    public List<Purchase> getPurchaseList() {
        return purchaseList;
    }

    // метод считает все максимальные категории по датам и собирает их в json объект
    public JsonObject maxPeriodCategory(List<Purchase> purchaseList) {

        Map<String, Long> maxPeriodCategoryMap = new HashMap<>();
        Purchase lastPurchase = purchaseList.get(purchaseList.size() - 1);
        JsonObject res = new JsonObject();

        res.add("maxCategory", maxCategory(maxCategoryMap));

        purchaseList.stream()
                .filter(pur -> pur.getPurchaseYear() == lastPurchase.getPurchaseYear())
                .forEach((val) -> maxCategoryInMap(val, maxPeriodCategoryMap));
        res.add("maxYearCategory", maxCategory(maxPeriodCategoryMap));
        maxPeriodCategoryMap.clear();

        purchaseList.stream()
                .filter(pur -> pur.getPurchaseYear() == lastPurchase.getPurchaseYear())
                .filter(pur -> pur.getPurchaseMonth() == lastPurchase.getPurchaseMonth())
                .forEach((val) -> maxCategoryInMap(val, maxPeriodCategoryMap));
        res.add("maxMonthCategory", maxCategory(maxPeriodCategoryMap));
        maxPeriodCategoryMap.clear();

        purchaseList.stream()
                .filter(pur -> pur.getPurchaseYear() == lastPurchase.getPurchaseYear())
                .filter(pur -> pur.getPurchaseMonth() == lastPurchase.getPurchaseMonth())
                .filter(pur -> pur.getPurchaseDay() == lastPurchase.getPurchaseDay())
                .forEach((val) -> maxCategoryInMap(val, maxPeriodCategoryMap));
        res.add("maxDayCategory", maxCategory(maxPeriodCategoryMap));
        maxPeriodCategoryMap.clear();

        return res;
    }

    // метод ищет максимальную по абсолютным тратам категорию и возвращает её в виде json-строки
    public JsonObject maxCategory(Map<String, Long> argumentMap) {

        Long maxValue = argumentMap.entrySet().stream()
                .max((val1, val2) -> val1.getValue() > val2.getValue() ? 1 : -1)
                .get().getValue();
        String maxCategory = argumentMap.entrySet().stream()
                .max((val1, val2) -> val1.getValue() > val2.getValue() ? 1 : -1)
                .get().getKey();

        JsonObject cat = new JsonObject();
        cat.addProperty("category", maxCategory);
        cat.addProperty("sum", maxValue);
        return cat;
    }


    // парсинг файла с категориями и назначение правильной категории новой покупке
    public void setPurchaseCategory(File categoriesFile, Purchase purchase) {
        try (BufferedReader reader = new BufferedReader(new FileReader(categoriesFile))) {
            Map<String, String> categoryMap = reader.lines()
                    .map(a -> a.split("\t"))
                    .collect(Collectors.toMap(a -> a[0], a -> a[1]));
            String category = categoryMap.get(purchase.getTitle());
            purchase.setCategory(category == null ? "разное" : category);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // сохраняем в bin-файл список покупок при каждой новой покупке
    public void saveToBin(File binFile, Purchase purchase) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(binFile))) {
            addToPurchaseList(purchase);
            out.writeObject(purchaseList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // при старте сервера загружаем из bin-файла список покупок и получаем актуальную статистику по категориям
    public void loadFromBin(File binFile) {
        List<Purchase> purchasesForLoad;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(binFile))) {
            purchasesForLoad = (List<Purchase>) in.readObject();
            for (Purchase purchase :
                    purchasesForLoad) {
                addToPurchaseList(purchase);
            }
        } catch (ClassNotFoundException | IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }
}

