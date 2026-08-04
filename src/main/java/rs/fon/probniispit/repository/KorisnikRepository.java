package rs.fon.probniispit.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.fon.probniispit.model.Korisnik;

public interface KorisnikRepository extends JpaRepository<Korisnik, Integer> {
    
    Optional<Korisnik> findByEmail(String email);
}
