package t1tanic.kingdomrpg.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

import static t1tanic.kingdomrpg.domain.Attribute.*;

@Entity
@Data
@NoArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Embedded
    private CharacterIdentity identity = new CharacterIdentity();

    @Embedded
    private CharacterAttributes attributes = new CharacterAttributes();

    @Embedded
    private CharacterResources resources = new CharacterResources();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_room_id")
    private Room currentRoom;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "player_cantrips",
        joinColumns = @JoinColumn(name = "player_id"),
        inverseJoinColumns = @JoinColumn(name = "cantrip_id")
    )
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Cantrip> learnedCantrips = new HashSet<>();

    // ── Derived maximums ──
    public int getMaxHealth() {
        return 50 + attributes.modifier(CONSTITUTION) * 10;
    }

    public int getMaxMana() {
        return 30 + (attributes.modifier(INTELLIGENCE) + attributes.modifier(WISDOM)) * 8;
    }

    public int getMaxStamina() {
        return 40 + (attributes.modifier(STRENGTH) + attributes.modifier(DEXTERITY) + attributes.modifier(CONSTITUTION)) * 6;
    }
}
