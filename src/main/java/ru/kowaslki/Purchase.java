package ru.kowaslki;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Month;

@Data
public class Purchase implements Serializable {

    private String title;
    private Long sum;
    private String category;
    private String date;
    private LocalDate parsedDate;

    public Purchase(String title, String date, Long sum) {
        this.title = title;
        this.date = date;
        this.sum = sum;
    }

    public int getPurchaseYear() {
        this.date = date.replace('.', '-');
        this.parsedDate = LocalDate.parse(date);
        return parsedDate.getYear();
    }

    public Month getPurchaseMonth() {
        this.date = date.replace('.', '-');
        this.parsedDate = LocalDate.parse(date);
        return this.parsedDate.getMonth();
    }

    public int getPurchaseDay() {
        this.date = date.replace('.', '-');
        this.parsedDate = LocalDate.parse(date);
        return parsedDate.getDayOfMonth();
    }
}
