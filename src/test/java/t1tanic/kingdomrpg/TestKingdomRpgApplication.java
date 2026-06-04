package t1tanic.kingdomrpg;

import org.springframework.boot.SpringApplication;

/**
 * Runs the application locally with a real PostgreSQL container —
 * no docker-compose needed for development.
 */
public class TestKingdomRpgApplication {

    public static void main(String[] args) {
        SpringApplication.from(KingdomRpgApplication::main)
                         .with(TestcontainersConfiguration.class)
                         .run(args);
    }
}
