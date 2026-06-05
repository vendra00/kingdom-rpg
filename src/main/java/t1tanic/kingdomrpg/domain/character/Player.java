package t1tanic.kingdomrpg.domain.character;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import t1tanic.kingdomrpg.domain.magic.Cantrip;

import java.util.HashSet;
import java.util.Set;

import static t1tanic.kingdomrpg.domain.character.enums.Attribute.*;

/**
 * Represents a playable character within the RPG domain.
 * <p>Extends {@link BaseCharacter} for shared character fields and adds player-specific data:
 * narrative identity, learned cantrips, carry capacity, and per-room scan history.
 * Dynamic carrying capacity is derived here based on Strength and Constitution modifiers.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Player extends BaseCharacter {

    /**
     * Embedded profile details specifying the character's narrative identity (race, class, etc.).
     */
    @Embedded
    private CharacterIdentity identity = new CharacterIdentity();

    /**
     * The collection of cantrips (basic spells) this player has unlocked.
     * Eagerly loaded. Excluded from {@code equals}, {@code hashCode}, and {@code toString}
     * to prevent cyclic dependency issues.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "player_cantrips",
        joinColumns = @JoinColumn(name = "player_id"),
        inverseJoinColumns = @JoinColumn(name = "cantrip_id")
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Cantrip> learnedCantrips = new HashSet<>();

    /**
     * Room IDs where this player has already performed a perception scan.
     * Once a room is scanned (regardless of result), it cannot be scanned again.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "player_scanned_rooms", joinColumns = @JoinColumn(name = "player_id"))
    @Column(name = "room_id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Long> scannedRoomIds = new HashSet<>();

    /**
     * Computes the maximum carrying capacity weight limit for the player.
     * <p>Formula: {@code 20,000 + (STR modifier × 4,000) + (CON modifier × 2,000)}</p>
     *
     * @return the maximum carry weight capacity in grams
     */
    public int getMaxCarryWeight() {
        return 20_000 + getAttributes().modifier(STRENGTH) * 4_000 + getAttributes().modifier(CONSTITUTION) * 2_000;
    }
}
