package rs.fon.probniispit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rs.fon.probniispit.model.Katalog;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface KatalogRepository extends JpaRepository<Katalog, Integer> {

    List<Katalog> findAllByOrderByVaziOdDesc();

    @Query("SELECT k FROM Katalog k WHERE k.vaziOd <= :datum ORDER BY k.vaziOd DESC LIMIT 1")
    Optional<Katalog> pronadjiVazeci(@Param("datum") LocalDate datum);
}