package fr.utc.picasso.jcapuchinov3.services.Topics;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class TopicService {
    ConcurrentHashMap<String, ConcurrentLinkedQueue<Channel>> topicChannels = new ConcurrentHashMap<>();

    public TopicService() {
    }

    public void addChannel(String topic, Channel channel) {
        if (!topicChannels.containsKey(topic)) {
            topicChannels.put(topic, new ConcurrentLinkedQueue<>());
        }
        topicChannels.get(topic).add(channel);
    }

    public void removeChannel(String topic, Channel channel) {
        if (topicChannels.containsKey(topic)) {
            topicChannels.get(topic).remove(channel);
        }
    }

    public void publish(String topic, Object message) {
        if (topicChannels.containsKey(topic)) {
            for (Channel channel : topicChannels.get(topic)) {
                channel.onMessage(message);
            }
        }
    }
}
