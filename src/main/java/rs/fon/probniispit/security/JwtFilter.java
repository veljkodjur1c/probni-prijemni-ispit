package rs.fon.probniispit.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtServis jwtServis;

    public JwtFilter(JwtServis jwtServis) {
        this.jwtServis = jwtServis;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest zahtev,
                                    HttpServletResponse odgovor,
                                    FilterChain lanac) throws ServletException, IOException {

        String zaglavlje = zahtev.getHeader("Authorization");

        if (zaglavlje != null && zaglavlje.startsWith("Bearer ")) {
            String token = zaglavlje.substring(7);

            if (jwtServis.jeValidan(token)) {
                UlogovaniKorisnik korisnik = new UlogovaniKorisnik(
                        jwtServis.procitajKorisnikId(token),
                        jwtServis.procitajEmail(token),
                        jwtServis.procitajUlogu(token)
                );

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                korisnik, null, korisnik.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        lanac.doFilter(zahtev, odgovor);
    }
}