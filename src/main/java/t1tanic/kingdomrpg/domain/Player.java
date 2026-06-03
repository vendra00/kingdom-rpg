package t1tanic.kingdomrpg.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    // ── Base attributes (D&D default: 10 = no modifier) ──
    private int strength = 10;
    private int dexterity = 10;
    private int constitution = 10;
    private int intelligence = 10;
    private int wisdom = 10;
    private int charisma = 10;

    // ── Current resource values (persisted) ──
    private int health;
    private int mana;
    private int stamina;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_room_id")
    private Room currentRoom;

    // ── D&D modifier: floor((attr - 10) / 2) ──
    public int modifier(int attribute) {
        return (int) Math.floor((attribute - 10) / 2.0);
    }

    // ── Derived maximums ──
    // HP    driven by Constitution
    public int getMaxHealth() {
        return 50 + modifier(constitution) * 10;
    }

    // Mana  driven by Intelligence + Wisdom
    public int getMaxMana() {
        return 30 + (modifier(intelligence) + modifier(wisdom)) * 8;
    }

    // Stamina driven by Strength + Dexterity + Constitution
    public int getMaxStamina() {
        return 40 + (modifier(strength) + modifier(dexterity) + modifier(constitution)) * 6;
    }
}
