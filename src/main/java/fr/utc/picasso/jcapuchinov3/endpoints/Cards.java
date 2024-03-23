package fr.utc.picasso.jcapuchinov3.endpoints;

import fr.utc.picasso.jcapuchinov3.Constants;
import fr.utc.picasso.jcapuchinov3.models.CardControllerState;
import fr.utc.picasso.jcapuchinov3.services.SseManager;
import fr.utc.picasso.jcapuchinov3.services.Topics.TopicService;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@RestController
@RequestMapping("/cards")
public class Cards {

    private final SseManager sseManager;
    private final TopicService topicService;

    private CardControllerState state = null;

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(Cards.class);

    public Cards(SseManager sseManager, TopicService topicService) {
        this.sseManager = sseManager;
        this.topicService = topicService;
        this.topicService.addChannel(Constants.TOPIC_CONTROLLER_STATE, this::onControllerState);
    }

    private void onControllerState(Object o) {
        state = (CardControllerState) o;
    }

    @GetMapping("/events")
    public SseEmitter listen() {
        logger.info("[SSE] New client connected");
        return sseManager.getNewEmitter();
    }

    @GetMapping("/controller")
    public Map<String, String> controller() {
        if (state == null) {
            return CardControllerState.unknown().toMap();
        }
        return state.toMap();
    }
}
