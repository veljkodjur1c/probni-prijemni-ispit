package rs.fon.probniispit.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import rs.fon.probniispit.model.Korisnik;

import java.util.Collection;
import java.util.List;

public class UlogovaniKorisnik implements UserDetails {

    private final Integer id;
    private final String email;
    private final String lozinkaHash;
    private final String uloga;

    public UlogovaniKorisnik(Korisnik korisnik) {
        this.id = korisnik.getId();
        this.email = korisnik.getEmail();
        this.lozinkaHash = korisnik.getLozinkaHash();
        this.uloga = korisnik.getUloga().name();
    }

    public UlogovaniKorisnik(Integer id, String email, String uloga) {
        this.id = id;
        this.email = email;
        this.lozinkaHash = null;
        this.uloga = uloga;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + uloga));
    }

    @Override
    public String getPassword() {
        return lozinkaHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public boolean jeAdmin() {
        return "ADMIN".equals(uloga);
    }
}