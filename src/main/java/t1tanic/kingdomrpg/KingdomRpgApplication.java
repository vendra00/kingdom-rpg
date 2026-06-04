package t1tanic.kingdomrpg;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The primary entry point configuration class for the Kingdom RPG application backend.
 * <p>Enables Spring Boot automatic component scanning, structural auto-configuration loops,
 * and configuration property bindings via the {@link SpringBootApplication} annotation.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@SpringBootApplication
public class KingdomRpgApplication {
    /**
     * Bootstraps the application context, launching the embedded application server,
     * database connections, and real-time execution layers.
     *
     * @param args runtime command-line execution parameters passed down to the application context
     */
    public static void main(String[] args) {
        SpringApplication.run(KingdomRpgApplication.class, args);
    }
}
