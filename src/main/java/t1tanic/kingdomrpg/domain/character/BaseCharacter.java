package t1tanic.kingdomrpg.domain.character;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import t1tanic.kingdomrpg.domain.BaseEntity;
import t1tanic.kingdomrpg.domain.world.Room;

import static t1tanic.kingdomrpg.domain.character.enums.Attribute.*;

/**
 * Abstract base mapped superclass shared by all character types (players and NPCs).
 * <p>Extracts the common identity fields — name, attributes, resources, equipment, and current location —
 * so that both {@link Player} and {@code Npc} tables inherit them without JPA inheritance overhead.
 * Dynamic resource maxima (health, mana, stamina) are derived here from the embedded attribute scores.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public abstract class BaseCharacter extends BaseEntity {

    /**
     * The display name of the character used in game output and entity lookups.
     */
    private String name;

    /**
     * Embedded core physical and mental attribute scores (Strength, Dexterity, etc.).
     */
    @Embedded
    private CharacterAttributes attributes = new CharacterAttributes();

    /**
     * Embedded tracking for current vital metrics (health, mana, stamina, carry weight).
     */
    @Embedded
    private CharacterResources resources = new CharacterResources();

    /**
     * The current world location where this character is stationed.
     * Loaded eagerly to ensure availability during immediate processing.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_room_id")
    private Room currentRoom;

    /**
     * Currently equipped items across the three body slots (main hand, off hand, body).
     * Getter is manually defined because Hibernate sets this to null when all FK columns are NULL.
     */
    @Embedded
    private Equipment equipment = new Equipment();

    /** Null-safe getter — Hibernate sets @Embedded to null when all FK columns are NULL. */
    public Equipment getEquipment() {
        if (equipment == null) equipment = new Equipment();
        return equipment;
    }

    /** @return {@code 50 + (CON modifier × 10)} */
    public int getMaxHealth() {
        return 50 + attributes.modifier(CONSTITUTION) * 10;
    }

    /** @return {@code 30 + ((INT modifier + WIS modifier) × 8)} */
    public int getMaxMana() {
        return 30 + (attributes.modifier(INTELLIGENCE) + attributes.modifier(WISDOM)) * 8;
    }

    /** @return {@code 40 + ((STR modifier + DEX modifier + CON modifier) × 6)} */
    public int getMaxStamina() {
        return 40 + (attributes.modifier(STRENGTH) + attributes.modifier(DEXTERITY) + attributes.modifier(CONSTITUTION)) * 6;
    }
}
