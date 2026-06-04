package t1tanic.kingdomrpg.web;

/**
 * A lightweight, immutable Data Transfer Object (DTO) representing a high-level summary of a player character.
 * <p>This record serves as a read-only projection layer designed to transmit character directory metadata over
 * network boundaries to client dashboards or API consumers, filtering out heavy domain relationships and sensitive
 * internal database keys.</p>
 *
 * @param name           the unique nickname or account handle of the player character
 * @param race           the descriptive fantasy race identifier (e.g., "Elf", "Human")
 * @param characterClass the professional combat or roleplay class archetype (e.g., "Mage", "Warrior")
 * @param lastPlayed     an ISO-8601 formatted date-time string representing the last recorded session modification
 * @author t1tanic
 * @version 1.0
 */
public record PlayerSummaryDto(
    String name,
    String race,
    String characterClass,
    String lastPlayed
) {}
