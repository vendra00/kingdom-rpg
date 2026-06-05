package t1tanic.kingdomrpg.engine.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.character.CharacterAttributes;
import t1tanic.kingdomrpg.domain.character.Npc;
import t1tanic.kingdomrpg.domain.character.NpcTrust;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.domain.character.enums.Attribute;
import t1tanic.kingdomrpg.domain.character.enums.NpcFaction;
import t1tanic.kingdomrpg.engine.ai.NpcTrustService;
import t1tanic.kingdomrpg.engine.dice.Dice;
import t1tanic.kingdomrpg.engine.dice.DiceRoll;
import t1tanic.kingdomrpg.engine.enums.MarkupTag;
import t1tanic.kingdomrpg.repository.NpcRepository;

import java.util.List;

/**
 * Command that lets the player attempt to persuade an NPC, rolling {@code d20 + CHA modifier}
 * against the NPC's {@code persuadeDc}.
 * <p>Outcomes:</p>
 * <ul>
 *   <li><b>Exceeds DC by 5+</b>: trust +20 — the NPC is genuinely moved.</li>
 *   <li><b>Meets DC</b>: trust +10 — mild warming.</li>
 *   <li><b>Misses DC by less than 10</b>: trust −3 — unconvincing.</li>
 *   <li><b>Misses DC by 10+</b>: trust −10 — the NPC is offended.</li>
 * </ul>
 *
 * @author t1tanic
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class PersuadeCommand implements Command {

    private final NpcRepository   npcRepository;
    private final NpcTrustService npcTrustService;

    @Override
    public String execute(Player player, String[] args) {
        if (args.length == 0) {
            return "Persuade whom? (type: persuade <name>)";
        }

        String target = String.join(" ", args).toLowerCase();
        long roomId   = player.getCurrentRoom().getId();
        List<Npc> roomNpcs = npcRepository.findByCurrentRoomId(roomId);

        Npc npc = roomNpcs.stream()
                .filter(n -> n.getName().toLowerCase().contains(target))
                .findFirst().orElse(null);

        if (npc == null) return "There's no one by that name here.";

        String coloredName = MarkupTag.color(npc.getFaction().cssColor(), npc.getName());

        if (npc.getFaction() == NpcFaction.HOSTILE) {
            return coloredName + " will not be swayed by words.";
        }

        int chaMod = CharacterAttributes.modifier(player.getAttributes().get(Attribute.CHARISMA));
        DiceRoll roll  = Dice.D20.roll().plus(chaMod);
        int total = roll.total();
        int dc    = npc.getPersuadeDc();

        NpcTrust trust = npcTrustService.getOrCreate(player, npc);
        int before = trust.getTrustLevel();

        StringBuilder sb = new StringBuilder();
        sb.append(MarkupTag.NERD.wrap("Persuasion: " + roll.format() + " vs DC " + dc)).append("\n");

        if (total >= dc + 5) {
            trust = npcTrustService.adjustTrust(trust, 20);
            sb.append(coloredName).append(" seems genuinely moved by your words.");
        } else if (total >= dc) {
            trust = npcTrustService.adjustTrust(trust, 10);
            sb.append(coloredName).append(" warms to you slightly.");
        } else if (total < dc - 9) {
            trust = npcTrustService.adjustTrust(trust, -10);
            sb.append(coloredName).append(" looks offended. You've only made things worse.");
        } else {
            trust = npcTrustService.adjustTrust(trust, -3);
            sb.append(coloredName).append(" doesn't seem convinced.");
        }

        int after = trust.getTrustLevel();
        String delta = after > before ? "+" + (after - before) : String.valueOf(after - before);
        sb.append("\n").append(MarkupTag.NERD.wrap("Trust: " + before + " → " + after + "/100 (" + delta + ")"));

        return sb.toString();
    }
}
