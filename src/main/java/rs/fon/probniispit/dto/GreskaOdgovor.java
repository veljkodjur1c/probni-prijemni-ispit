package rs.fon.probniispit.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record GreskaOdgovor(
        LocalDateTime vreme,
        int status,
        String poruka,
        Map<String, String> greskePolja
) {
    public GreskaOdgovor(int status, String poruka) {
        this(LocalDateTime.now(), status, poruka, null);
    }

    public GreskaOdgovor(int status, String poruka, Map<String, String> greskePolja) {
        this(LocalDateTime.now(), status, poruka, greskePolja);
    }
}