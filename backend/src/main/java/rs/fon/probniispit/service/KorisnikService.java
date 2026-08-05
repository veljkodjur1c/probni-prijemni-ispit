package rs.fon.probniispit.service;

import rs.fon.probniispit.exception.KonfliktException;
import rs.fon.probniispit.model.Korisnik;
import rs.fon.probniispit.model.Uloga;
import rs.fon.probniispit.repository.KorisnikRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class KorisnikService {

    private final KorisnikRepository korisnikRepository;
    private final PasswordEncoder passwordEncoder;

    public KorisnikService(KorisnikRepository korisnikRepository, PasswordEncoder passwordEncoder) {
        this.korisnikRepository = korisnikRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Korisnik registruj(String ime, String prezime, String email, String lozinka) {
        if (korisnikRepository.findByEmail(email).isPresent()) {
            throw new KonfliktException("Korisnik sa ovim email-om već postoji");
        }

        Korisnik korisnik = new Korisnik();
        korisnik.setIme(ime);
        korisnik.setPrezime(prezime);
        korisnik.setEmail(email);
        korisnik.setLozinkaHash(passwordEncoder.encode(lozinka));
        korisnik.setUloga(Uloga.KANDIDAT);

        return korisnikRepository.save(korisnik);
    }

    public Optional<Korisnik> pronadjiPoEmail(String email) {
        return korisnikRepository.findByEmail(email);
    }

    public Optional<Korisnik> pronadjiPoId(Integer id) {
        return korisnikRepository.findById(id);
    }

    public boolean validirajLozinku(String unetaLozinka, String hesovanaLozinka) {
        return passwordEncoder.matches(unetaLozinka, hesovanaLozinka);
    }
}