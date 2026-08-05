package rs.fon.probniispit.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CenovnikDTO(
        Integer id,
        String vrstaIspita,
        BigDecimal cena,
        LocalDate vaziOd
) {}