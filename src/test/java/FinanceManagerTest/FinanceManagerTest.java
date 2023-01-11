package FinanceManagerTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.kowaslki.FinanceManager;
import ru.kowaslki.Purchase;

import java.util.*;

public class FinanceManagerTest {

    FinanceManager manager = new FinanceManager();
    Map<String, Long> categoryMap = new HashMap<>();
    List<Purchase> purchaseList = new ArrayList<>();

    @BeforeEach
    void addCategory() {
        categoryMap.put("еда", 1400L);
        categoryMap.put("одежда", 800L);
        categoryMap.put("развлечения", 1500L);
        categoryMap.put("транспорт", 300L);
        categoryMap.put("разное", 1800L);
    }

    @Test
    @DisplayName("Max category true")
    public void findMaxCategoryTest() {
        String res = "{\"maxCategory\":{\"category\":\"разное\",\"sum\":1800}}";
        String exp = manager.maxCategory(categoryMap).toString();
        Assertions.assertEquals(res, exp);
    }

    @Test
    @DisplayName("Max category false")
    public void maxCategoryFalseTest() {
        String res = "{\"maxCategory\":{\"category\":\"развлечения\",\"sum\":1500}}";
        String exp = manager.maxCategory(categoryMap).toString();
        Assertions.assertFalse(Objects.deepEquals(res, exp));
    }

    @Test
    @DisplayName("add to purchase list")
    public void addToPurchaseListTest() {
        manager.addToPurchaseList(new Purchase("пирожок", "2022.10.08", 50L));
        Assertions.assertTrue(purchaseList != null);
    }

}
