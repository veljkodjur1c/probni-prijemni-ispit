package rs.fon.probniispit.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import rs.fon.probniispit.model.Termin;
import rs.fon.probniispit.model.VrstaIspita;

public interface TerminRepository extends JpaRepository<Termin, Integer> {

    List<Termin> findByVrstaIspita(VrstaIspita vrstaIspita);
    List<Termin> findByDatumGreaterThanEqualOrderByDatumAsc(LocalDate datum);

}
