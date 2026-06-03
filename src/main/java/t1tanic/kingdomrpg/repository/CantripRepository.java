package t1tanic.kingdomrpg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import t1tanic.kingdomrpg.domain.Cantrip;

import java.util.List;

public interface CantripRepository extends JpaRepository<Cantrip, String> {
    List<Cantrip> findByAllowedClassesContaining(String className);
}
