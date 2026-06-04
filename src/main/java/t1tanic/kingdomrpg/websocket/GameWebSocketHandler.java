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

@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(GameWebSocketHandler.class);

    private final GameEngine   gameEngine;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, String> sessionToPlayer = new ConcurrentHashMap<>();

    public GameWebSocketHandler(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

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

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String name = sessionToPlayer.remove(session.getId());
        if (name != null) {
            setContext(name, session.getId());
            log.info("Player disconnected — status {}", status.getCode());
            clearContext();
        }
    }

    private void setContext(String player, String sessionId) {
        MDC.put("player",  player);
        MDC.put("session", sessionId.substring(0, Math.min(8, sessionId.length())));
        AuditorContext.set(player);
    }

    private void clearContext() {
        MDC.clear();
        AuditorContext.clear();
    }

    private void send(WebSocketSession session, String type, String text) throws Exception {
        String json = objectMapper.writeValueAsString(Map.of("type", type, "text", text));
        session.sendMessage(new TextMessage(json));
    }
}
