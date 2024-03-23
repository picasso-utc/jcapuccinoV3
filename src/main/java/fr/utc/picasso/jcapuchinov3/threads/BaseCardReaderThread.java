package fr.utc.picasso.jcapuchinov3.threads;

import fr.utc.picasso.jcapuchinov3.models.CardControllerState;
import fr.utc.picasso.jcapuchinov3.services.Topics.TopicService;
import org.slf4j.Logger;

import static fr.utc.picasso.jcapuchinov3.Constants.TOPIC_CARD_READ;
import static fr.utc.picasso.jcapuchinov3.Constants.TOPIC_CONTROLLER_STATE;

abstract public class BaseCardReaderThread extends Thread {

    private final TopicService topicService;
    private boolean isRunning = true;

    private int cardReaderConnectionAttempts = 0;
    Logger logger = org.slf4j.LoggerFactory.getLogger(BaseCardReaderThread.class);

    public BaseCardReaderThread(TopicService topicService) {
        this.topicService = topicService;
    }

    public void run() {
        while (isRunning) {
            try {
                ensureReaderConnection();
                String message = handleCardsListening();
                if (message != null) {
                    topicService.publish(TOPIC_CARD_READ, message);
                }
                waitCardAbsent();
            } catch (Exception e) {
                logger.error("Error while handling cards listening", e);
            }
        }
    }
    private boolean lastIsReaderConnected = true;
    private void ensureReaderConnection() throws InterruptedException {
        if (isReaderConnected()) {
            if (!lastIsReaderConnected) {
                lastIsReaderConnected = true;
                topicService.publish(TOPIC_CONTROLLER_STATE, CardControllerState.connected());
            }
            return;
        }
        do {
            tryConnectReader();
            cardReaderConnectionAttempts++;
            Thread.sleep(1000);
            if (cardReaderConnectionAttempts > 10) {
                logger.warn("Card reader not connected after 10 attempts, waiting 10 seconds before retrying");
                Thread.sleep(10000);
                cardReaderConnectionAttempts = 0;
            }
            topicService.publish(TOPIC_CONTROLLER_STATE, CardControllerState.notConnected());
        } while (!isReaderConnected());
        topicService.publish(TOPIC_CONTROLLER_STATE, CardControllerState.connected());
    }

    abstract boolean isReaderConnected();

    abstract void tryConnectReader();

    abstract String handleCardsListening();

    abstract void waitCardAbsent();

    public void stopThread() {
        isRunning = false;
    }
}
