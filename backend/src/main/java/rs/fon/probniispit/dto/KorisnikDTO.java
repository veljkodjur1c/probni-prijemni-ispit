package rs.fon.probniispit.dto;

public record KorisnikDTO(
        Integer id,
        String ime,
        String prezime,
        String email,
        String uloga
) {}