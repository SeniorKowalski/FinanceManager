package ru.kowaslki;

import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
public class Purchase {

    private String title;
    private LocalDate parsedDate;
    private Long sum;
    private String category;
    private String date;

    public Purchase(String title, String date, Long sum) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        this.title = title;
        this.date = date;
        this.sum = sum;
        this.parsedDate = LocalDate.from(formatter.parse(date));
    }

}
