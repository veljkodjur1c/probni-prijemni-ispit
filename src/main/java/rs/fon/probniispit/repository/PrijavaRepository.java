package rs.fon.probniispit.repository;

import rs.fon.probniispit.model.Prijava;
import rs.fon.probniispit.model.StatusPrijave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrijavaRepository extends JpaRepository<Prijava, Integer> {
    List<Prijava> findByKorisnikId(Integer korisnikId);
    List<Prijava> findByStatus(StatusPrijave status);
    
    @Query("SELECT p FROM Prijava p WHERE p.id = :prijavaId AND p.korisnik.id = :korisnikId")
    java.util.Optional<Prijava> findByIdAndKorisnikId(@Param("prijavaId") Integer prijavaId, @Param("korisnikId") Integer korisnikId);
}