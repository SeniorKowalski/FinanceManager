
import lombok.Data;

import java.time.format.DateTimeFormatter;
import java.util.Date;

@Data
public class Purchase {

    private String title;
    private String stringDate;
    private Long sum;
    private String category;
    private String date;

    public Purchase(String title, Date date, Long sum) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        this.title = title;
        this.date = formatter.format(date.toInstant());
        this.sum = sum;
    }

}
