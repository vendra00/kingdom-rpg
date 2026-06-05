package t1tanic.kingdomrpg.engine.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import t1tanic.kingdomrpg.config.GameProperties;
import t1tanic.kingdomrpg.domain.character.Npc;
import t1tanic.kingdomrpg.domain.character.NpcConversation;
import t1tanic.kingdomrpg.domain.character.Player;

import t1tanic.kingdomrpg.domain.character.enums.Ability;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Drives NPC dialogue by calling the Anthropic Messages API with the NPC's personality system prompt
 * and the full persisted conversation history, returning a contextually aware response.
 * <p>If no API key is configured the service returns {@code null}, letting callers fall back gracefully.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Slf4j
@Service
public class NpcAiService {

    private static final String  ANTHROPIC_URL     = "https://api.anthropic.com";
    private static final String  ANTHROPIC_VERSION = "2023-06-01";
    private static final Pattern TRUST_PATTERN     = Pattern.compile("\\[TRUST:([+-]?\\d+)]", Pattern.CASE_INSENSITIVE);
    private static final Pattern ATTEMPT_PATTERN   = Pattern.compile("\\[ATTEMPT:(\\w+)]", Pattern.CASE_INSENSITIVE);

    /** Carries the NPC's dialogue, any trust adjustment, and an optional implicit ability check. */
    public record ChatResult(String reply, int trustDelta, Optional<Ability> attempt) {}

    private final RestClient   restClient;
    private final GameProperties props;
    private final String       apiKey;
    private final String       model;
    private final int          maxTokens;

    public NpcAiService(
            GameProperties props,
            @Value("${anthropic.api-key:}")    String apiKey,
            @Value("${anthropic.model:claude-haiku-4-5-20251001}") String model,
            @Value("${anthropic.max-tokens:200}") int maxTokens) {
        this.props      = props;
        this.apiKey     = apiKey;
        this.model      = model;
        this.maxTokens  = maxTokens;
        this.restClient = RestClient.builder().baseUrl(ANTHROPIC_URL).build();
    }

    /**
     * Sends a player message to the Anthropic API and returns the NPC's generated reply.
     *
     * @param npc         the NPC whose personality and backstory frame the system prompt
     * @param player      the player speaking (name used for personalisation)
     * @param userMessage the new message from the player
     * @param history     previous turns of this conversation, oldest first
     * @param trustLevel  current trust level (0–100) used to control what the NPC will share
     * @return the NPC's reply text, or {@code null} if the API key is absent or the call fails
     */
    public ChatResult chat(Npc npc, Player player, String userMessage, List<NpcConversation> history, int trustLevel, Optional<Ability> playerIntent) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Anthropic API key not configured — NPC AI unavailable");
            return null;
        }

        List<ApiMessage> messages = new ArrayList<>();
        for (NpcConversation turn : history) {
            messages.add(new ApiMessage(turn.getRole().value(), turn.getContent()));
        }
        messages.add(new ApiMessage("user", userMessage));

        ApiRequest request = new ApiRequest(model, maxTokens, buildSystemPrompt(npc, player, trustLevel, playerIntent), messages);

        try {
            ApiResponse response = restClient.post()
                    .uri("/v1/messages")
                    .header("x-api-key", apiKey)
                    .header("anthropic-version", ANTHROPIC_VERSION)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(ApiResponse.class);

            if (response == null) return null;

            String raw = response.getText();

            int trustDelta = 0;
            Matcher tm = TRUST_PATTERN.matcher(raw);
            if (tm.find()) {
                try { trustDelta = Math.max(props.getNpc().getTrustDeltaMin(),
                                           Math.min(props.getNpc().getTrustDeltaMax(),
                                                    Integer.parseInt(tm.group(1)))); }
                catch (NumberFormatException ignored) {}
            }

            // If the player declared an explicit intent, skip LLM attempt detection
            Optional<Ability> attempt;
            if (playerIntent.isPresent()) {
                attempt = playerIntent;
            } else {
                attempt = Optional.empty();
                Matcher am = ATTEMPT_PATTERN.matcher(raw);
                if (am.find()) attempt = Ability.fromInput(am.group(1));
            }

            String reply = TRUST_PATTERN.matcher(ATTEMPT_PATTERN.matcher(raw).replaceAll(""))
                                        .replaceAll("").strip();
            return new ChatResult(reply, trustDelta, attempt);

        } catch (Exception e) {
            log.error("Anthropic API call failed: {}", e.getMessage());
            return null;
        }
    }

    private String buildSystemPrompt(Npc npc, Player player, int trustLevel, Optional<Ability> playerIntent) {
        String factionBehavior = switch (npc.getFaction()) {
            case FRIENDLY -> "Be warm, helpful, and willing to share knowledge about the castle and its history.";
            case NEUTRAL  -> "Be curt and guarded. You don't trust strangers easily but remain civil unless provoked.";
            case HOSTILE  -> "You are aggressive. Speak only in short threatening fragments. Never cooperate.";
        };

        String backstory = npc.getPersonality() != null && !npc.getPersonality().isBlank()
                ? npc.getPersonality()
                : npc.getDescription();

        String trustContext  = buildTrustContext(npc, player, trustLevel);
        String intentContext = playerIntent.map(a ->
                "The player has declared their intent: " + a.displayName() + ". " +
                "React to this approach naturally. Do NOT emit [ATTEMPT:] — the intent is already registered."
        ).orElse("");

        return """
                You are %s, a character living inside an ancient, crumbling castle in a dark fantasy world.

                About you: %s

                Disposition: %s

                %s

                %s

                Rules — follow them strictly:
                - Respond in 1–3 short sentences maximum. This is a text RPG; brevity is essential.
                - Stay in character at all times. Never acknowledge being an AI.
                - React naturally to tone: if the player is rude or threatening, become cold or hostile.
                - If the player repeats the same phrase or question, show mild irritation — vary your response.
                - The player's name is %s. You are currently in %s.
                - After your in-character dialogue, on a NEW LINE, write exactly one trust marker: [TRUST:N]
                  where N is an integer chosen from the player's latest message tone ONLY (not cumulative):
                  direct threats or demands → -10 to -15 | insults or rudeness → -3 to -8 | neutral → 0
                  polite or curious → +2 to +5 | genuine warmth or meaningful sharing → +5 to +8
                  This marker is a game-engine instruction — do NOT include it inside quotes or spoken text.
                - If the player is ACTIVELY attempting a deliberate persuasion action AND no intent was
                  declared above, also add [ATTEMPT:ability] on the same line as [TRUST:N]. Use EXACTLY
                  one of these — read the definitions carefully, they are distinct:
                  · intimidate — explicit threat of physical harm or violence ("I'll kill you", "do it or I hurt you").
                                 Arrogance, demands, or insults alone are NOT intimidate — only direct harm threats.
                  · deceive    — the player states something FALSE to manipulate ("I am the king", "the lord sent me",
                                 "I already paid"). Any lie about identity, status, authority, or past events.
                  · convince   — reasoned argument or appeal to self-interest with no false claims ("you'd be safer if...",
                                 "think about what you gain").
                  · negotiate  — proposing a specific mutual exchange of terms ("I'll do X if you do Y").
                  · bribe      — explicitly offering coin, goods, or a reward ("here is gold", "I'll pay you").
                  · sense      — the player is actively probing the NPC's true feelings, loyalty, or honesty.
                  Only emit [ATTEMPT:] when the action is unmistakably deliberate. Rude or aggressive tone without
                  a specific persuasion goal does NOT trigger it — handle those through [TRUST:N] only.
                """.formatted(
                npc.getName(),
                backstory,
                factionBehavior,
                trustContext,
                intentContext,
                player.getName(),
                npc.getCurrentRoom().getName()
        );
    }

    private String buildTrustContext(Npc npc, Player player, int trustLevel) {
        StringBuilder ctx = new StringBuilder();
        ctx.append("Trust with ").append(player.getName()).append(": ").append(trustLevel).append("/100.\n");

        if (trustLevel < 30) {
            ctx.append("You are very guarded. Deflect personal questions and share nothing sensitive.");
        } else if (trustLevel < 60) {
            ctx.append("You are cautiously open. Discuss general topics but deflect anything deeply personal.");
        } else {
            ctx.append("You are comfortable with this person and speak more openly.");
        }

        if (npc.getSecret() != null && !npc.getSecret().isBlank()) {
            if (trustLevel >= npc.getSecretThreshold()) {
                ctx.append("\nThis player has earned enough trust. If the topic arises naturally, you may reveal your secret: \"")
                   .append(npc.getSecret()).append("\"");
            } else {
                ctx.append("\nYou hold a personal secret you will NOT share until you fully trust someone ")
                   .append("(needs ").append(npc.getSecretThreshold()).append("/100). ")
                   .append("If pressed, say it is not their business — firmly but without revealing anything.");
            }
        }

        return ctx.toString();
    }

    // ── API DTOs ──────────────────────────────────────────────────────────────

    public record ApiRequest(String model, int max_tokens, String system, List<ApiMessage> messages) {}

    public record ApiMessage(String role, String content) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ApiResponse(List<ContentBlock> content) {
        String getText() {
            if (content == null) return "";
            return content.stream()
                    .filter(b -> "text".equals(b.type()))
                    .map(ContentBlock::text)
                    .findFirst()
                    .orElse("");
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public record ContentBlock(String type, String text) {}
    }
}
