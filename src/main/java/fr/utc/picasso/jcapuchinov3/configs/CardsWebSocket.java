package fr.utc.picasso.jcapuchinov3.configs;

import fr.utc.picasso.jcapuchinov3.JcapuchinoV3Application;
import fr.utc.picasso.jcapuchinov3.websockets.WebsocketCards;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@ComponentScan(basePackageClasses = JcapuchinoV3Application.class)
public class CardsWebSocket implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebsocketCards(), "/cards/listen").setAllowedOrigins("*");
    }
}
