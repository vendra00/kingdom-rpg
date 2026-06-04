package t1tanic.kingdomrpg.domain;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Abstract base class for domain entities, providing common auditing properties and an identifier.
 * * <p>This class uses Spring Data JPA auditing to automatically track creation and modification
 * metadata, as well as optimistic locking via a version field. It is designed to be extended
 * by concrete entity classes within the application.</p>
 * * @author t1tanic
 * @version 1.0
 */
@MappedSuperclass
@Getter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    /**
     * The primary key identifier for the entity.
     * Generates values automatically using the identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The date and time when the entity was first created.
     * This field is populated automatically upon persistence and cannot be updated.
     */
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * The date and time when the entity was last modified.
     * This field is updated automatically whenever the entity is changed.
     */
    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * The identifier (e.g., username or system ID) of the user who created the entity.
     * This field is populated automatically upon persistence and cannot be updated.
     */
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    /**
     * The identifier (e.g., username or system ID) of the user who last modified the entity.
     * This field is updated automatically whenever the entity is changed.
     */
    @LastModifiedBy
    private String updatedBy;

    /**
     * The version number used for optimistic locking control.
     * Ensures that concurrent updates do not overwrite each other silently.
     */
    @Version
    private Long version;
}
