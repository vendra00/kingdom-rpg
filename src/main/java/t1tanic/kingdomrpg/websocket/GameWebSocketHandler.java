package t1tanic.kingdomrpg.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import t1tanic.kingdomrpg.config.AuditorContext;
import t1tanic.kingdomrpg.engine.GameEngine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * High-performance WebSocket handler managing real-time, bi-directional gameplay sessions.
 * <p>Extends {@link TextWebSocketHandler} to handle asynchronous incoming text frames, routing raw JSON structures
 * down to business engines. This component maps ephemeral communication sockets to concrete player contexts,
 * injects structured diagnostic tracking signatures into the Mapped Diagnostic Context (MDC), and sets up auditing snapshots
 * before processing incoming command operations.</p>
 *
 * @author t1tanic
 * @version 1.0
 */
@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(GameWebSocketHandler.class);

    private final GameEngine   gameEngine;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, String> sessionToPlayer = new ConcurrentHashMap<>();

    public GameWebSocketHandler(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    /**
     * Intercepts incoming client network payloads and acts as the runtime traffic dispatcher.
     * <p>Parses inbound frames into two core operational blocks:</p>
     * <ul>
     * <li>{@code "join"}: Binds the connection address to a persistent username handle, initializes internal states,
     * and auto-triggers an environmental lookup view to orient the player.</li>
     * <li>{@code "command"}: Verifies routing bounds and feeds the text payload stream forward into execution blocks.</li>
     * </ul>
     *
     * @param session the unique network pipeline instance managing the stateful socket channel
     * @param message the raw outbound text frame wrapper holding string characters
     * @throws Exception if network writing triggers serialization failures or infrastructure crashes occur
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            Map<String, Object> payload = objectMapper.readValue(message.getPayload(), Map.class);
            String type = (String) payload.get("type");

            if ("join".equals(type)) {
                String name = ((String) payload.get("name")).trim();
                if (name.isBlank()) {
                    send(session, "error", "Name cannot be empty.");
                    return;
                }
                sessionToPlayer.put(session.getId(), name);
                setContext(name, session.getId());
                log.info("Player joined");
                String welcome = gameEngine.joinGame(name, payload);
                send(session, "system", welcome);
                send(session, "output", gameEngine.processCommand(name, "look"));

            } else if ("command".equals(type)) {
                String name = sessionToPlayer.get(session.getId());
                if (name == null) {
                    send(session, "error", "Session expired — please refresh.");
                    return;
                }
                String text = (String) payload.get("text");
                setContext(name, session.getId());
                log.debug("Command received: {}", text);
                send(session, "output", gameEngine.processCommand(name, text));
            }

        } catch (Exception e) {
            log.error("Error handling message", e);
            send(session, "error", "Something went wrong: " + e.getMessage());
        } finally {
            clearContext();
        }
    }

    /**
     * Cleans up transient identity footprints when a network socket terminates.
     *
     * @param session the specific socket pipeline tracking link being severed
     * @param status  the formal socket termination structural status indicator
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String name = sessionToPlayer.remove(session.getId());
        if (name != null) {
            setContext(name, session.getId());
            log.info("Player disconnected — status {}", status.getCode());
            clearContext();
        }
    }

    /**
     * Hydrates diagnostic logging matrices and audit reference nodes with contextual execution metadata.
     *
     * @param player    the current player character profile string
     * @param sessionId the raw communication channel reference key
     */
    private void setContext(String player, String sessionId) {
        MDC.put("player",  player);
        MDC.put("session", sessionId.substring(0, Math.min(8, sessionId.length())));
        AuditorContext.set(player);
    }

    /**
     * Purges thread-local state tracking registers to guarantee cross-session defensive isolation.
     */
    private void clearContext() {
        MDC.clear();
        AuditorContext.clear();
    }

    /**
     * Helper routine packaging payload parameters into standard structured communication envelopes.
     *
     * @param session the outbound socket pipeline target reference
     * @param type    the structural classification type identifier (e.g., "output", "system", "error")
     * @param text    the raw payload string text block being returned to the client application
     * @throws Exception if conversion mechanisms stumble or streaming components throw exceptions
     */
    private void send(WebSocketSession session, String type, String text) throws Exception {
        String json = objectMapper.writeValueAsString(Map.of("type", type, "text", text));
        session.sendMessage(new TextMessage(json));
    }
}
