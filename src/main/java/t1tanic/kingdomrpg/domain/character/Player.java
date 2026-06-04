package t1tanic.kingdomrpg.domain.character;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import t1tanic.kingdomrpg.domain.BaseEntity;
import t1tanic.kingdomrpg.domain.magic.Cantrip;
import t1tanic.kingdomrpg.domain.world.Room;

import java.util.HashSet;
import java.util.Set;

import static t1tanic.kingdomrpg.domain.character.enums.Attribute.*;

/**
 * Represents a playable character within the RPG domain.
 * <p>The {@code Player} class encapsulates a character's core details including identity,
 * base attributes, current vital resources, geographical location in the game world, and
 * learned spells. It inherits fundamental auditing and identifier properties from {@link BaseEntity}.</p>
 * * <p>Dynamic statistical resource calculations (such as Health, Mana, and Stamina maxima)
 * are derived here based on the character's base attributes and corresponding modifiers.</p>
 * * @author t1tanic
 * @version 1.0
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Player extends BaseEntity {

    /**
     * The unique username or character name of the player.
     */
    @Column(unique = true)
    private String name;

    /**
     * Embedded profile details specifying the character's narrative identity (e.g., race, class).
     */
    @Embedded
    private CharacterIdentity identity = new CharacterIdentity();

    /**
     * Embedded core physical and mental attribute scores (Strength, Dexterity, etc.).
     */
    @Embedded
    private CharacterAttributes attributes = new CharacterAttributes();

    /**
     * Embedded tracking for current vital metrics (e.g., current health, mana, stamina).
     */
    @Embedded
    private CharacterResources resources = new CharacterResources();

    /**
     * The current world location (Room) where the player is stationed.
     * Loaded eagerly to ensure availability during immediate player processing.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_room_id")
    private Room currentRoom;

    /**
     * The collection of cantrips (basic spells) this player has unlocked.
     * <p>Mapped via a many-to-many join table {@code player_cantrips}. Eagerly loaded.
     * Excluded from standard {@code equals}, {@code hashCode}, and {@code toString}
     * evaluations to prevent cyclic dependency issues with the relationship.</p>
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
     * Currently equipped items across the three body slots (main hand, off hand, body).
     * The items remain in the player's inventory — this just tracks which slot each occupies.
     * Getter is manually defined because Hibernate sets this to null when all FK columns are NULL.
     */
    @Embedded
    private Equipment equipment = new Equipment();

    public Equipment getEquipment() {
        if (equipment == null) equipment = new Equipment();
        return equipment;
    }

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
     * Computes the maximum health pool of the player.
     * <p>Formula: {@code 50 + (Constitution Modifier * 10)}</p>
     *
     * @return the maximum health value as an integer
     */
    public int getMaxHealth() {
        return 50 + attributes.modifier(CONSTITUTION) * 10;
    }

    /**
     * Computes the maximum mana pool of the player.
     * <p>Formula: {@code 30 + ((Intelligence Modifier + Wisdom Modifier) * 8)}</p>
     *
     * @return the maximum mana value as an integer
     */
    public int getMaxMana() {
        return 30 + (attributes.modifier(INTELLIGENCE) + attributes.modifier(WISDOM)) * 8;
    }

    /**
     * Computes the maximum stamina pool of the player.
     * <p>Formula: {@code 40 + ((Strength Modifier + Dexterity Modifier + Constitution Modifier) * 6)}</p>
     *
     * @return the maximum stamina value as an integer
     */
    public int getMaxStamina() {
        return 40 + (attributes.modifier(STRENGTH) + attributes.modifier(DEXTERITY) + attributes.modifier(CONSTITUTION)) * 6;
    }

    /**
     * Computes the maximum carrying capacity weight limit for the player.
     * <p>Formula: {@code 20,000 + (Strength Modifier * 4,000) + (Constitution Modifier * 2,000)}</p>
     *
     * @return the maximum carrying weight capacity as an integer unit
     */
    public int getMaxCarryWeight() {
        return 20_000 + attributes.modifier(STRENGTH) * 4_000 + attributes.modifier(CONSTITUTION) * 2_000;
    }
}
