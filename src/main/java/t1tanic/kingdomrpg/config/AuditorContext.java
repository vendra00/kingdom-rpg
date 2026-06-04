package t1tanic.kingdomrpg.config;

public final class AuditorContext {

    private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();

    private AuditorContext() {}

    public static void set(String auditor) { CURRENT.set(auditor); }
    public static String get()             { return CURRENT.get(); }
    public static void clear()             { CURRENT.remove(); }
}
