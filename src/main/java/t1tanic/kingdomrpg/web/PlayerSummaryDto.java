package t1tanic.kingdomrpg.web;

public record PlayerSummaryDto(
    String name,
    String race,
    String characterClass,
    String lastPlayed
) {}
