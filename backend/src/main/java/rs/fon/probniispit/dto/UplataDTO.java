package rs.fon.probniispit.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UplataDTO(
        Integer id,
        Integer prijavaId,
        BigDecimal iznos,
        LocalDateTime datumEvidentiranja,
        String evidentiraoAdmin
) {}