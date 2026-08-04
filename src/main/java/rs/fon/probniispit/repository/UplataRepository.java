package rs.fon.probniispit.repository;

import rs.fon.probniispit.model.Uplata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UplataRepository extends JpaRepository<Uplata, Integer> {
    Optional<Uplata> findByPrijavaId(Integer prijavaId);
}