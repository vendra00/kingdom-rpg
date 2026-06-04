package t1tanic.kingdomrpg.engine.commands;

import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.character.enums.Attribute;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.engine.enums.MarkupTag;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Command implementation responsible for auditing and rendering a comprehensive character sheet layout.
 * <p>This command pulls the character's primary identity information, dynamic resource vitals (Health, Mana, Stamina),
 * current carrying capacity, and core ability attributes. It then generates visual progress bars and formatted data column
 * metrics wrapped inside terminal markup descriptors.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Component
public class StatusCommand implements Command {

    /**
     * {@inheritDoc}
     * <p>Assembles character attributes and structural parameters into an explicit multi-line profile interface display.
     * Handles string token capitalization normalization and calculates relative resource bar fill fractions.</p>
     *
     * @param player the active player character querying their sheet status
     * @param args   trailing command arguments (unused by this command block)
     * @return a multi-line character overview sheet detailing metadata, structural resource indicators, and attribute modifiers
     */
    @Override
    public String execute(Player player, String[] args) {
        var id    = player.getIdentity();
        var attrs = player.getAttributes();
        var res   = player.getResources();

        String title = MarkupTag.ROOM.wrap("%s  ·  %s %s".formatted(
            player.getName(),
            cap(id.getRace()),
            cap(id.getCharacterClass())
        ));
        String subline = "Background: %s  ·  %s  ·  %s".formatted(
            cap(id.getBackground()),
            player.getCurrentRoom().getName(),
            cap(id.getGender())
        );
        String attrSection = Arrays.stream(Attribute.values())
            .map(attr -> "  %-14s%2d  (%+d)".formatted(
                cap(attr.key()), attrs.get(attr), attrs.modifier(attr)))
            .collect(Collectors.joining("\n"));

        return """
            %s
            %s

            HP       %3d / %-3d  %s
            Mana     %3d / %-3d  %s
            Stamina  %3d / %-3d  %s
            Carry    %.2f / %.2f kg  %s

            ── Attributes ────────────────────
            %s""".formatted(
            title, subline,
            res.getHealth(),  player.getMaxHealth(),  bar(res.getHealth(),  player.getMaxHealth()),
            res.getMana(),    player.getMaxMana(),    bar(res.getMana(),    player.getMaxMana()),
            res.getStamina(), player.getMaxStamina(), bar(res.getStamina(), player.getMaxStamina()),
            res.getCarryWeight()       / 1000.0,
            player.getMaxCarryWeight() / 1000.0,
            bar(res.getCarryWeight(),  player.getMaxCarryWeight()),
            attrSection
        );
    }

    /**
     * Constructs an ascii representation progress track meter utilizing filled and shaded Unicode character blocks.
     *
     * @param current the current quantity metric total
     * @param max     the maximum capacity constraint metric total
     * @return a 10-character wide string layout containing comparative visual block indicators
     */
    private String bar(int current, int max) {
        int filled = max > 0 ? Math.max(0, Math.min(10, Math.round((float) current / max * 10))) : 0;
        return "█".repeat(filled) + "░".repeat(10 - filled);
    }

    /**
     * Standardizes a raw token phrase by transforming the initial boundary character to uppercase and the remainder to lowercase.
     *
     * @param s the target text string requiring character modification
     * @return the transformed string line or a fallback dash signature if the target context evaluates as empty
     */
    private String cap(String s) {
        if (s == null || s.isBlank()) return "—";
        return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
    }
}