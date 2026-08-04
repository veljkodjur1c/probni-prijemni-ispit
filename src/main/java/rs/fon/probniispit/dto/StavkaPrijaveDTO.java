package rs.fon.probniispit.dto;

import java.math.BigDecimal;

public record StavkaPrijaveDTO(
        TerminDTO termin,
        BigDecimal cena
) {}