package fr.utc.picasso.jcapuchinov3.websockets;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import fr.utc.picasso.jcapuchinov3.Constants;
import fr.utc.picasso.jcapuchinov3.models.CardControllerState;
import fr.utc.picasso.jcapuchinov3.services.Topics.TopicService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class WebsocketCards implements WebSocketHandler {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(WebsocketCards.class);

    @Autowired
    TopicService topicService;

    @PostConstruct
    public void init() {
        topicService.addChannel(Constants.TOPIC_CARD_READ, this::onCardMessage);
        topicService.addChannel(Constants.TOPIC_CONTROLLER_STATE, this::onStateMessage);
    }

    static final List<WebSocketSession> sessions = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        logger.info("New client connected" + session.getId());
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        //Doing nothing for now
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        sessions.remove(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        sessions.remove(session);
        logger.info("Client disconnected");
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public void onCardMessage(Object o) {
        String cardUID = (String) o;
        SendWebSocketMessage<String> message = new SendWebSocketMessage<>("card", cardUID);
        sendToAllSessions(message);
    }

    public void onStateMessage(Object o) {
        SendWebSocketMessage<Map<String, String>> message = new SendWebSocketMessage<>("state", ((CardControllerState) o).toMap());
        sendToAllSessions(message);
    }

    private void sendToAllSessions(SendWebSocketMessage<?> message) {
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new org.springframework.web.socket.TextMessage(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(message)));
            } catch (Exception e) {
                sessions.remove(session);
                logger.error("Error while sending message to client" + e.getMessage());
            }
        }
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    static class SendWebSocketMessage<T> {
        String type;
        T payload;

        public SendWebSocketMessage(String type, T payload) {
            this.type = type;
            this.payload = payload;
        }
    }

    //interval method
    @Scheduled(fixedRate = 5000)
    public void sendPing() {
        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new PingMessage());
            } catch (Exception e) {
                sessions.remove(session);
                logger.error("Error while sending message to client" + e.getMessage());
            }
        }
    }
}
