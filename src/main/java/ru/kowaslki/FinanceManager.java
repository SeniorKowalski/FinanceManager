package ru.kowaslki;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
        // если категория уже есть в списке категорий добавляем к её сумме стоимость покупки:
        if (maxCategoryMap.containsKey(purchase.getCategory())) {
            Long sum = maxCategoryMap.get(purchase.getCategory());
            maxCategoryMap.put(purchase.getCategory(), sum + purchase.getSum());
        } else {
            maxCategoryMap.put(purchase.getCategory(), purchase.getSum());
        }
    }

    public Map<String, Long> getMaxCategoryMap() {
        return maxCategoryMap;
    }

    // метод ищет максимальную по абсолютным тратам категорию за весь период и возвращает её в виде json-строки
    public JsonObject maxCategory(Map<String, Long> argumentMap) {

        Long maxValue = argumentMap.entrySet().stream()
                .max((val1, val2) -> val1.getValue() > val2.getValue() ? 1 : -1)
                .get().getValue();
        String maxCategory = argumentMap.entrySet().stream()
                .max((val1, val2) -> val1.getValue() > val2.getValue() ? 1 : -1)
                .get().getKey();

        String jsonString = "{'maxCategory': {'category': " + maxCategory + ",'sum': " + maxValue + "}}";

        return (JsonObject) JsonParser.parseString(jsonString);

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
        List<Purchase> purchasesForLoad = new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(binFile))) {
            purchasesForLoad = (List<Purchase>) in.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        try {
            for (Purchase purchase :
                    purchasesForLoad) {
                addToPurchaseList(purchase);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}

