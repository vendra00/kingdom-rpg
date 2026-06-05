package t1tanic.kingdomrpg.domain.character.enums;

/**
 * Represents which side of the dialogue produced a given {@code NpcConversation} message.
 * Maps directly to the role string expected by the Anthropic Messages API.
 *
 * @author t1tanic
 * @version 1.0
 */
public enum NpcConversationRole {
    USER("user"),
    ASSISTANT("assistant");

    private final String value;

    NpcConversationRole(String value) { this.value = value; }

    /** The lowercase string expected by the Anthropic API ({@code "user"} or {@code "assistant"}). */
    public String value() { return value; }
}
