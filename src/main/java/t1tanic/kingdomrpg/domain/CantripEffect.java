package t1tanic.kingdomrpg.domain;

public enum CantripEffect {

    DAMAGE ("You focus your energy and shape the arcane threads of %s..."),
    DEBUFF ("Dark energy coils around your fingers as you invoke %s..."),
    BUFF   ("A warm shimmer envelops your hands as you channel %s..."),
    UTILITY("You weave the subtle patterns of %s..."),
    HEALING("Soft light flows through you as you call upon %s...");

    private final String channelFmt;

    CantripEffect(String channelFmt) {
        this.channelFmt = channelFmt;
    }

    public String channel(String spellName) {
        return channelFmt.formatted(spellName);
    }
}
