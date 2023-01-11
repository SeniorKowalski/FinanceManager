package ru.kowaslki;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FinanceManager {

    private final String FILE_NAME = "categories.tsv";
    File file = new File(FILE_NAME);
    List<Purchase> purchaseList = new ArrayList<>();
    private Map<String, String> categoryMap;
    public Map<String, Long> maxCategoryMap = new HashMap<>();

    public void addToPurchaseList(Purchase purchase) {
        setPurchaseCategory(file);
        String category = categoryMap.get(purchase.getTitle());
        purchase.setCategory(category == null ? "разное" : category);
        purchaseList.add(purchase);
        if (maxCategoryMap.containsKey(purchase.getCategory())) {
            Long sum = maxCategoryMap.get(purchase.getCategory());
            maxCategoryMap.put(purchase.getCategory(), sum + purchase.getSum());
        } else {
            maxCategoryMap.put(purchase.getCategory(), purchase.getSum());
        }
    }

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

    public void setPurchaseCategory(File categoriesFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(categoriesFile))) {
            categoryMap = reader.lines().map(a -> a.split("\t")).collect(Collectors.toMap(a -> a[0], a -> a[1]));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
