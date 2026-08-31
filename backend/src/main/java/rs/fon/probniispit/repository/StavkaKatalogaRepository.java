package rs.fon.probniispit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.fon.probniispit.model.StavkaKataloga;
import rs.fon.probniispit.model.VrstaIspita;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface StavkaKatalogaRepository extends JpaRepository<StavkaKataloga, Integer> {

    @Query("""
            SELECT s FROM StavkaKataloga s
            WHERE s.vrstaIspita = :vrsta
              AND s.katalog.vaziOd <= :datum
            ORDER BY s.katalog.vaziOd DESC
            LIMIT 1
            """)
    Optional<StavkaKataloga> pronadjiVazecuCenu(@Param("vrsta") VrstaIspita vrsta,
                                                @Param("datum") LocalDate datum);
}