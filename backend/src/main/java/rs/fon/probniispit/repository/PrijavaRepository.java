package rs.fon.probniispit.repository;

import rs.fon.probniispit.model.Prijava;
import rs.fon.probniispit.model.StatusPrijave;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<Prijava> findByStatus(StatusPrijave status, Pageable pageable);

    @Query("""
            SELECT p FROM Prijava p
            WHERE (:status IS NULL OR p.status = :status)
              AND (:pretraga IS NULL OR
                   LOWER(CONCAT(p.korisnik.ime, ' ', p.korisnik.prezime)) LIKE LOWER(CONCAT('%', :pretraga, '%')))
            """)
    Page<Prijava> pretrazi(@Param("status") StatusPrijave status,
                           @Param("pretraga") String pretraga,
                           Pageable pageable);
}