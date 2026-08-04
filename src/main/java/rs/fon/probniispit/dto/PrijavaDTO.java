package rs.fon.probniispit.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PrijavaDTO(
        Integer id,
        Integer korisnikId,
        String imeKandidata,
        LocalDateTime datumPrijave,
        BigDecimal ukupnaCena,
        String status,
        List<StavkaPrijaveDTO> stavke
) {}