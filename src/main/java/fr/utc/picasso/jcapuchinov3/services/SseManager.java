package fr.utc.picasso.jcapuchinov3.services;

import fr.utc.picasso.jcapuchinov3.services.Topics.Channel;
import fr.utc.picasso.jcapuchinov3.services.Topics.TopicService;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;

import static fr.utc.picasso.jcapuchinov3.Constants.TOPIC_CARD_READ;

@Service
public class SseManager implements Channel {
    Logger logger = org.slf4j.LoggerFactory.getLogger(SseManager.class);

    final
    TopicService topicService;
    List<SseEmitter> emitters = new ArrayList<>();

    public SseManager(TopicService topicService) {
        this.topicService = topicService;
        topicService.addChannel(TOPIC_CARD_READ, this);
    }

    public SseEmitter getNewEmitter() {
        SseEmitter emitter = new SseEmitter();
        emitters.add(emitter);
        emitter.onCompletion(() -> {
            emitters.remove(emitter);
            logger.info("[SSE] Client disconnected");
        });
        emitter.onTimeout(() -> {
            emitters.remove(emitter);
            emitter.complete();
            logger.info("[SSE] Client timeout");
        });
        emitter.onError((e) -> {
            logger.error("[SSE] Client error", e);
        });
        return emitter;
    }

    public void publish(String event, String message) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name(event).data(message));
            } catch (Exception e) {
                emitters.remove(emitter);
                logger.error("Error while sending message to client");
            }
        }
    }

    @Override
    public void onMessage(Object o) {
        //on new card
        publish(TOPIC_CARD_READ, (String) o);
    }
}
