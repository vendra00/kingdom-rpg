package t1tanic.kingdomrpg.engine.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import t1tanic.kingdomrpg.domain.character.Npc;
import t1tanic.kingdomrpg.domain.character.NpcConversation;
import t1tanic.kingdomrpg.domain.character.NpcTrust;
import t1tanic.kingdomrpg.domain.character.Player;
import t1tanic.kingdomrpg.domain.character.enums.NpcConversationRole;
import t1tanic.kingdomrpg.domain.character.enums.NpcFaction;
import t1tanic.kingdomrpg.engine.ai.NpcAiService;
import t1tanic.kingdomrpg.engine.ai.NpcTrustService;
import t1tanic.kingdomrpg.engine.enums.MarkupTag;
import t1tanic.kingdomrpg.repository.NpcConversationRepository;
import t1tanic.kingdomrpg.repository.NpcRepository;

import java.util.Arrays;
import java.util.List;

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

    private static final int MAX_HISTORY = 20;

    private final NpcRepository             npcRepository;
    private final NpcConversationRepository conversationRepository;
    private final NpcAiService              npcAiService;
    private final NpcTrustService           npcTrustService;

    @Override
    public String execute(Player player, String[] args) {
        if (args.length == 0) {
            return "Talk to whom? (type: talk <name> <message>)";
        }

        long roomId = player.getCurrentRoom().getId();
        List<Npc> roomNpcs = npcRepository.findByCurrentRoomId(roomId);

        Npc    npc     = null;
        String message = null;
        for (int i = args.length; i >= 1; i--) {
            String candidate = String.join(" ", Arrays.copyOfRange(args, 0, i));
            Npc match = roomNpcs.stream()
                    .filter(n -> n.getName().toLowerCase().contains(candidate.toLowerCase()))
                    .findFirst().orElse(null);
            if (match != null) {
                npc     = match;
                message = i < args.length
                        ? String.join(" ", Arrays.copyOfRange(args, i, args.length))
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
        int trustLevel = trust.getTrustLevel();

        List<NpcConversation> history = conversationRepository
                .findByPlayerIdAndNpcIdOrderByCreatedAtAsc(player.getId(), npc.getId());
        if (history.size() > MAX_HISTORY) {
            history = history.subList(history.size() - MAX_HISTORY, history.size());
        }

        String aiReply = npcAiService.chat(npc, player, message, history, trustLevel);
        if (aiReply == null || aiReply.isBlank()) {
            aiReply = npc.getGreeting() != null ? npc.getGreeting() : "...";
        }

        saveMessage(player, npc, NpcConversationRole.USER,      message);
        saveMessage(player, npc, NpcConversationRole.ASSISTANT, aiReply);

        return MarkupTag.NERD.wrap("Trust: " + trustLevel + "/100") + "\n" +
               coloredName + " says:\n" +
               MarkupTag.NARRATE.wrap("\"" + aiReply + "\"");
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
