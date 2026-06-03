package t1tanic.kingdomrpg.engine;

public enum MarkupTag {

    ROOM("room"),
    EXIT("exit"),
    ITEM("item"),
    NARRATE("narrate");

    private final String tag;

    MarkupTag(String tag) {
        this.tag = tag;
    }

    public String wrap(String text) {
        return "[" + tag + "]" + text + "[/" + tag + "]";
    }
}
