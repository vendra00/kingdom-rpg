package t1tanic.kingdomrpg.engine.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.config.GameProperties;
import t1tanic.kingdomrpg.domain.character.Npc;
import t1tanic.kingdomrpg.domain.character.NpcConversation;
import t1tanic.kingdomrpg.domain.character.NpcTrust;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.domain.character.enums.NpcConversationRole;
import t1tanic.kingdomrpg.domain.character.enums.NpcFaction;
import t1tanic.kingdomrpg.domain.character.enums.Ability;
import t1tanic.kingdomrpg.engine.AbilityCheckService;
import t1tanic.kingdomrpg.engine.ai.NpcAiService;
import t1tanic.kingdomrpg.engine.ai.NpcTrustService;
import t1tanic.kingdomrpg.engine.enums.MarkupTag;
import t1tanic.kingdomrpg.repository.NpcConversationRepository;
import t1tanic.kingdomrpg.repository.NpcRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Command allowing the player to speak with an NPC present in their current room.
 * <p>Syntax: {@code talk <name> [message]}</p>
 * <ul>
 *   <li>Without a message — reminds the player how to speak.</li>
 *   <li>With a message — calls the LLM with the NPC's personality, full conversation history,
 *       and current trust level; then persists the exchange.</li>
 *   <li>Hostile NPCs refuse all dialogue regardless of trust.</li>
 *   <li>Falls back to the static greeting when the API key is absent.</li>
 * </ul>
 *
 * @author t1tanic
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class TalkCommand implements Command {

    private static final Set<String> INTENT_KEYWORDS = Set.of(
            "neutral", "intimidate", "convince", "deceive", "negotiate", "bribe", "sense"
    );

    private final GameProperties             props;
    private final NpcRepository             npcRepository;
    private final NpcConversationRepository conversationRepository;
    private final NpcAiService              npcAiService;
    private final NpcTrustService           npcTrustService;
    private final AbilityCheckService       abilityCheckService;

    @Override
    public String execute(Player player, String[] args) {
        if (args.length == 0) {
            return "Talk to whom? (type: talk [intent] <name> <message>)";
        }

        long roomId = player.getCurrentRoom().getId();
        List<Npc> roomNpcs = npcRepository.findByCurrentRoomIdAndVisibleTrue(roomId);

        // Strip optional explicit intent declaration from the front of args
        Optional<Ability> playerIntent = Optional.empty();
        String[] npcArgs = args;
        if (args.length > 0 && INTENT_KEYWORDS.contains(args[0].toLowerCase())) {
            String intentStr = args[0].toLowerCase();
            if (!"neutral".equals(intentStr)) {
                playerIntent = Ability.fromInput(intentStr);
            }
            npcArgs = Arrays.copyOfRange(args, 1, args.length);
        }

        Npc    npc     = null;
        String message = null;
        for (int i = npcArgs.length; i >= 1; i--) {
            String candidate = String.join(" ", Arrays.copyOfRange(npcArgs, 0, i));
            Npc match = roomNpcs.stream()
                    .filter(n -> n.getName().toLowerCase().contains(candidate.toLowerCase()))
                    .findFirst().orElse(null);
            if (match != null) {
                npc     = match;
                message = i < npcArgs.length
                        ? String.join(" ", Arrays.copyOfRange(npcArgs, i, npcArgs.length))
                        : null;
                break;
            }
        }

        if (npc == null) return "There's no one by that name here.";

        String coloredName = MarkupTag.color(npc.getFaction().cssColor(), npc.getName());

        if (npc.getFaction() == NpcFaction.HOSTILE) {
            return coloredName + " glares at you menacingly. They have no interest in conversation.";
        }

        if (message == null || message.isBlank()) {
            return coloredName + " is here. What do you say? (type: talk " +
                   npc.getName().toLowerCase() + " <message>)";
        }

        NpcTrust trust = npcTrustService.getOrCreate(player, npc);
        int trustBefore = trust.getTrustLevel();

        List<NpcConversation> history = conversationRepository
                .findByPlayerIdAndNpcIdOrderByCreatedAtAsc(player.getId(), npc.getId());
        int historyLimit = props.getNpc().getConversationHistoryLimit();
        if (history.size() > historyLimit) {
            history = new ArrayList<>(history.subList(history.size() - historyLimit, history.size()));
        }

        NpcAiService.ChatResult result = npcAiService.chat(npc, player, message, history, trustBefore, playerIntent);
        String aiReply;
        int trustDelta = 0;
        if (result != null && !result.reply().isBlank()) {
            aiReply    = result.reply();
            trustDelta = result.trustDelta();
        } else {
            aiReply = npc.getGreeting() != null ? npc.getGreeting() : "...";
        }

        saveMessage(player, npc, NpcConversationRole.USER,      message);
        saveMessage(player, npc, NpcConversationRole.ASSISTANT, aiReply);

        // Declared player intent takes priority; fall back to LLM-detected attempt
        Optional<Ability> attempt = playerIntent.isPresent()
                ? playerIntent
                : (result != null ? result.attempt() : Optional.empty());
        AbilityCheckService.CheckResult checkResult = attempt
                .map(a -> abilityCheckService.resolve(player, a))
                .orElse(null);
        int checkDelta = checkResult != null ? checkResult.trustDelta() : 0;

        // Apply combined trust delta (tone + check outcome)
        int totalDelta = trustDelta + checkDelta;
        int trustAfter = trustBefore;
        if (totalDelta != 0) {
            trust      = npcTrustService.adjustTrust(trust, totalDelta);
            trustAfter = trust.getTrustLevel();
        }

        String trustLine = totalDelta != 0
                ? "Trust: " + trustBefore + " → " + trustAfter + "/100 ("
                  + (trustAfter > trustBefore ? "+" : "") + (trustAfter - trustBefore) + ")"
                : "Trust: " + trustAfter + "/100";

        StringBuilder sb = new StringBuilder();

        // Implicit ability check block (nerd roll + verdict in main output)
        if (checkResult != null && attempt.isPresent()) {
            Ability ability = attempt.get();
            String verdict = checkResult.crit()   ? MarkupTag.EXIT.wrap("EXCEPTIONAL SUCCESS")
                           : checkResult.fumble() ? MarkupTag.ITEM.wrap("SPECTACULAR FAILURE")
                           : checkResult.success() ? MarkupTag.EXIT.wrap("SUCCESS")
                           : "FAILURE";
            sb.append(MarkupTag.NERD.wrap(ability.displayName() + ": " + checkResult.rollLine())).append("\n");
            sb.append(verdict).append("  ").append(checkResult.narrative()).append("\n");
        }

        sb.append(MarkupTag.NERD.wrap(trustLine)).append("\n");
        sb.append(coloredName).append(" says:\n");
        sb.append(MarkupTag.NARRATE.wrap("\"" + aiReply + "\""));
        return sb.toString();
    }

    private void saveMessage(Player player, Npc npc, NpcConversationRole role, String content) {
        NpcConversation msg = new NpcConversation();
        msg.setPlayer(player);
        msg.setNpc(npc);
        msg.setRole(role);
        msg.setContent(content);
        conversationRepository.save(msg);
    }
}
