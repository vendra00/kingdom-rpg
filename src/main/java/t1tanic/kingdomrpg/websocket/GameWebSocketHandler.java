package t1tanic.kingdomrpg.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import t1tanic.kingdomrpg.engine.GameEngine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    private final GameEngine gameEngine;
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
                String welcome = gameEngine.joinGame(name, payload);
                send(session, "system", welcome);
                send(session, "output", gameEngine.processCommand(name, "look"));

            } else if ("command".equals(type)) {
                String name = sessionToPlayer.get(session.getId());
                if (name == null) {
                    send(session, "error", "Session expired — please refresh.");
                    return;
                }
                send(session, "output", gameEngine.processCommand(name, (String) payload.get("text")));
            }
        } catch (Exception e) {
            send(session, "error", "Something went wrong: " + e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionToPlayer.remove(session.getId());
    }

    private void send(WebSocketSession session, String type, String text) throws Exception {
        String json = objectMapper.writeValueAsString(Map.of("type", type, "text", text));
        session.sendMessage(new TextMessage(json));
    }
}
