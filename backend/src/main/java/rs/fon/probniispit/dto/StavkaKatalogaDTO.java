package rs.fon.probniispit.dto;

import java.math.BigDecimal;

public record StavkaKatalogaDTO(
        Integer id,
        String vrstaIspita,
        BigDecimal cena
) {}