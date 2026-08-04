package rs.fon.probniispit.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import rs.fon.probniispit.model.Korisnik;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtServis {

    private final SecretKey kljuc;
    private final long trajanje;

    public JwtServis(@Value("${jwt.tajni-kljuc}") String tajniKljuc,
                     @Value("${jwt.trajanje-ms}") long trajanje) {
        this.kljuc = Keys.hmacShaKeyFor(tajniKljuc.getBytes(StandardCharsets.UTF_8));
        this.trajanje = trajanje;
    }

    public String napraviToken(Korisnik korisnik) {
        Date sada = new Date();
        Date istice = new Date(sada.getTime() + trajanje);

        return Jwts.builder()
                .subject(korisnik.getEmail())
                .claim("uloga", korisnik.getUloga().name())
                .claim("korisnikId", korisnik.getId())
                .issuedAt(sada)
                .expiration(istice)
                .signWith(kljuc)
                .compact();
    }

    public String procitajEmail(String token) {
        return procitajSadrzaj(token).getSubject();
    }

    public Integer procitajKorisnikId(String token) {
        return procitajSadrzaj(token).get("korisnikId", Integer.class);
    }

    public String procitajUlogu(String token) {
        return procitajSadrzaj(token).get("uloga", String.class);
    }

    public boolean jeValidan(String token) {
        try {
            procitajSadrzaj(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims procitajSadrzaj(String token) {
        return Jwts.parser()
                .verifyWith(kljuc)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}