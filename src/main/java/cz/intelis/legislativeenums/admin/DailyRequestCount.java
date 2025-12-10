package cz.intelis.legislativeenums.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyRequestCount {
    private LocalDate date;
    private Long count;
}
