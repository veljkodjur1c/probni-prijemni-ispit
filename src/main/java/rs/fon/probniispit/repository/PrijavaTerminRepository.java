package rs.fon.probniispit.repository;

import rs.fon.probniispit.model.PrijavaTermin;
import rs.fon.probniispit.model.PrijavaTerminId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrijavaTerminRepository extends JpaRepository<PrijavaTermin, PrijavaTerminId> {
    List<PrijavaTermin> findByPrijavaId(Integer prijavaId);
    List<PrijavaTermin> findByTerminId(Integer terminId);
}