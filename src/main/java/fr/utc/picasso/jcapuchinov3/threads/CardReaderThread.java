package fr.utc.picasso.jcapuchinov3.threads;

import fr.utc.picasso.jcapuchinov3.services.Topics.TopicService;
import org.springframework.beans.factory.annotation.Value;

import javax.smartcardio.*;
import java.util.List;

public class CardReaderThread extends BaseCardReaderThread {

    @Value("${cardreader.allowAndroid:false}")
    boolean allowAndroid;
    CardTerminal terminal;

    public CardReaderThread(TopicService topicService) {
        super(topicService);
    }

    @Override
    boolean isReaderConnected() {
        return terminal != null;
    }

    @Override
    void tryConnectReader() {
        try {
            TerminalFactory factory = TerminalFactory.getDefault();
            CardTerminals terminals = factory.terminals();
            //get the first terminal
            List<CardTerminal> cardTerminals = terminals.list();
            if (cardTerminals.isEmpty()) {
                logger.warn("No terminal found");
                terminal = null;
                return;
            }
            terminal = cardTerminals.get(0);
            logger.info("Terminal found : " + terminal.getName());
        } catch (Exception e) {
            logger.error("Error while listing terminals");
        }
    }


    @Override
    String handleCardsListening() {
        try {
            if (!terminal.waitForCardPresent(200)) return null;
            return getUuid(terminal);
        } catch (CardException e) {
            logger.error("Error while checking if card is present");
            return null;
        }
    }

    private String getUuid(CardTerminal cardTerminal) throws CardException {
        Card card = cardTerminal.connect("*");
        CardChannel channel = card.getBasicChannel();
        ResponseAPDU CardApduResponse = channel.transmit(new CommandAPDU(new byte[]{(byte) 0xFF, (byte) 0xCA, 0x00, 0x00, 0x00}));
        byte[] CardApduResponseData = CardApduResponse.getData();
        //to hex representation
        StringBuilder sb = new StringBuilder();
        for (byte b : CardApduResponseData) {
            sb.append(String.format("%02X", b));
        }
        String output = sb.toString();
        logger.info("Card detected : " + output.substring(0, 2) + "....");
        //if the card is an android phone (start with 08 and 4 bits long)
        if (output.startsWith("08") && output.length() == 8 && allowAndroid) {
            logger.info("Android phone detected, trying to get UID");
            //try to get true payutc uid card by sending a command to the card
            CardApduResponse = channel.transmit(new CommandAPDU(new byte[]{(byte) 0xFF, (byte) 0xCA, 0x00, 0x00, 0x04}));
            CardApduResponseData = CardApduResponse.getData();
            //data is string format : pay:<card_uid>
            output = new String(CardApduResponseData);
            if (output.startsWith("pay:")) {
                output = output.substring(4);
            } else {
                logger.warn("Error while getting card UID, data is not in the expected format");
                return null;
            }
        }
        card.disconnect(true);
        return output;
    }

    @Override
    void waitCardAbsent() {
        try {
            terminal.waitForCardAbsent(0);
        } catch (CardException e) {
            logger.error("Error while waiting for card to be absent", e);
        }
    }
}
