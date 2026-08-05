package rs.fon.probniispit.repository;

import rs.fon.probniispit.model.Cenovnik;
import rs.fon.probniispit.model.VrstaIspita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CenovnikRepository extends JpaRepository<Cenovnik, Integer> {
    
    @Query("SELECT c FROM Cenovnik c WHERE c.vrstaIspita = :vrsta AND c.vaziOd <= :datum ORDER BY c.vaziOd DESC LIMIT 1")
    Optional<Cenovnik> findValidPrice(@Param("vrsta") VrstaIspita vrsta, @Param("datum") LocalDate datum);
}