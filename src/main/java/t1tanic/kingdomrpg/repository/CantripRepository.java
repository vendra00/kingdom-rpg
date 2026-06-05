package t1tanic.kingdomrpg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import t1tanic.kingdomrpg.domain.magic.Cantrip;

import java.util.List;

/**
 * Data access repository layer interface handling persistent management for {@link Cantrip} spell assets.
 * <p>Extends standard {@link JpaRepository} capabilities to offer built-in CRUD operations and
 * infrastructure bindings against string-identified entity states.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
public interface CantripRepository extends JpaRepository<Cantrip, String> {
    /**
     * Queries and filters the collection of persistent cantrips to find items whose allowed classes
     * criteria contains the specified archetype token.
     * <p>Because class assignments are stored as a delimited string block matching layout rules
     * (e.g., {@code "mage,rogue"}), this method translates to an SQL {@code LIKE %className%} evaluation block.</p>
     *
     * @param className the lowercase class designator string to search for (e.g., "mage")
     * @return a {@link List} containing all matching {@link Cantrip} entities found in storage
     */
    @Query(value = "SELECT * FROM cantrip WHERE allowed_classes LIKE CONCAT('%', :className, '%')", nativeQuery = true)
    List<Cantrip> findByAllowedClassesContaining(@Param("className") String className);
}
