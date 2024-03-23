package fr.utc.picasso.jcapuchinov3.services;


import fr.utc.picasso.jcapuchinov3.services.Topics.TopicService;
import fr.utc.picasso.jcapuchinov3.threads.CardReaderThread;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class CardReader {
    Logger logger = org.slf4j.LoggerFactory.getLogger(CardReader.class);
    Thread cardReaderThread;

    final TopicService topicService;

    public CardReader(TopicService topicService) {
        try {
            cardReaderThread = new Thread(new CardReaderThread(topicService));
            cardReaderThread.start();
            logger.info("Card reader thread started");
        } catch (Exception e) {
            logger.error("Error while starting card reader thread", e);
        }
        this.topicService = topicService;
    }

}
