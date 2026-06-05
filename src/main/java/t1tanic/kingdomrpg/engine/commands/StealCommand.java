package t1tanic.kingdomrpg.engine.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.character.Npc;
import t1tanic.kingdomrpg.domain.character.NpcTrust;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.domain.character.enums.Ability;
import t1tanic.kingdomrpg.domain.item.Item;
import t1tanic.kingdomrpg.engine.AbilityCheckService;
import t1tanic.kingdomrpg.engine.ai.NpcTrustService;
import t1tanic.kingdomrpg.engine.enums.MarkupTag;
import t1tanic.kingdomrpg.repository.ItemRepository;
import t1tanic.kingdomrpg.repository.NpcRepository;
import t1tanic.kingdomrpg.repository.PlayerRepository;

import java.util.Arrays;
import java.util.List;

/**
 * Attempts to steal gold or a named item from an NPC using a Pickpocket ability check.
 * <p>Syntax: {@code steal <npc> [item]}</p>
 * <ul>
 *   <li>No item argument — steals all gold from the NPC's purse.</li>
 *   <li>With item argument — steals a specific named item from the NPC's inventory.</li>
 *   <li>On failure: trust with the NPC is reduced; on critical failure, the NPC catches you.</li>
 * </ul>
 *
 * @author t1tanic
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class StealCommand implements Command {

    private final NpcRepository       npcRepository;
    private final ItemRepository      itemRepository;
    private final PlayerRepository    playerRepository;
    private final NpcTrustService     npcTrustService;
    private final AbilityCheckService abilityCheckService;

    @Override
    public String execute(Player player, String[] args) {
        if (args.length == 0) {
            return "Steal from whom?  (steal <npc> [item])";
        }

        long roomId = player.getCurrentRoom().getId();
        List<Npc> roomNpcs = npcRepository.findByCurrentRoomIdAndVisibleTrue(roomId);

        // Greedy NPC name match, optional item name after
        Npc    npc        = null;
        String itemTarget = null;
        for (int i = args.length; i >= 1; i--) {
            String candidate = String.join(" ", Arrays.copyOfRange(args, 0, i));
            Npc match = roomNpcs.stream()
                    .filter(n -> n.getName().toLowerCase().contains(candidate.toLowerCase()))
                    .findFirst().orElse(null);
            if (match != null) {
                npc        = match;
                itemTarget = i < args.length
                        ? String.join(" ", Arrays.copyOfRange(args, i, args.length))
                        : null;
                break;
            }
        }

        if (npc == null) return "There's no one by that name here.";

        AbilityCheckService.CheckResult check = abilityCheckService.resolve(player, Ability.PICKPOCKET);
        String coloredName = MarkupTag.color(npc.getFaction().cssColor(), npc.getName());

        StringBuilder sb = new StringBuilder();
        String verdict = check.crit()    ? MarkupTag.EXIT.wrap("EXCEPTIONAL SUCCESS")
                       : check.fumble()  ? MarkupTag.ITEM.wrap("SPECTACULAR FAILURE")
                       : check.success() ? MarkupTag.EXIT.wrap("SUCCESS")
                       : "FAILURE";

        sb.append(MarkupTag.NERD.wrap("Pickpocket: " + check.rollLine())).append("\n");
        sb.append(verdict).append("  ").append(check.narrative()).append("\n");

        if (check.success()) {
            if (itemTarget == null || itemTarget.isBlank()) {
                stealGold(player, npc, coloredName, sb);
            } else {
                stealItem(player, npc, itemTarget, coloredName, sb);
            }
        } else {
            int penalty = check.fumble() ? -20 : -8;
            NpcTrust trust = npcTrustService.getOrCreate(player, npc);
            npcTrustService.adjustTrust(trust, penalty);
            if (check.fumble()) {
                sb.append(coloredName).append(" catches your hand in the act. Their eyes go cold.");
            } else {
                sb.append(coloredName).append(" notices a furtive movement and takes a step back, watching you.");
            }
        }

        return sb.toString();
    }

    private void stealGold(Player player, Npc npc, String coloredName, StringBuilder sb) {
        int gold = npc.getResources().getGold();
        if (gold <= 0) {
            sb.append("Your fingers find ").append(coloredName).append("'s purse — it's empty.");
            return;
        }
        npc.getResources().setGold(0);
        player.getResources().setGold(player.getResources().getGold() + gold);
        npcRepository.save(npc);
        playerRepository.save(player);
        sb.append("You slip ").append(MarkupTag.color("#ffd700", gold + " gold"))
          .append(" from ").append(coloredName).append("'s purse without a sound.");
        sb.append("\n").append(MarkupTag.NERD.wrap("Gold: you +" + gold));
    }

    private void stealItem(Player player, Npc npc, String itemTarget, String coloredName, StringBuilder sb) {
        List<Item> npcItems = itemRepository.findByNpcIdOrderByNameAsc(npc.getId());
        Item item = npcItems.stream()
                .filter(i -> i.getName().toLowerCase().contains(itemTarget.toLowerCase()))
                .findFirst().orElse(null);

        if (item == null) {
            sb.append("You can't find a '").append(itemTarget).append("' on them.");
            return;
        }

        int newCarry = player.getResources().getCarryWeight() + item.getWeightGrams();
        if (newCarry > player.getMaxCarryWeight()) {
            sb.append("The ").append(item.getName()).append(" is too heavy to take without being noticed.");
            return;
        }

        item.setNpc(null);
        item.setPlayer(player);
        itemRepository.save(item);
        player.getResources().setCarryWeight(newCarry);
        playerRepository.save(player);
        sb.append("You slip the ").append(MarkupTag.ITEM.wrap(item.getName()))
          .append(" from ").append(coloredName).append(". Clean and quiet.");
    }
}
